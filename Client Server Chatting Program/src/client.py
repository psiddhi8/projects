import argparse
import socket
import sys
import time
import threading
from cryptography.hazmat.primitives import serialization

from common import (
    sha256,
    aes256_generate,
    aes256_encrypt,
    aes256_decrypt,
    rsa4096_generate,
    rsa4096_encrypt,
    rsa4096_decrypt,
    rsa4096_sign,
    rsa4096_verify,
    send,
    recv,
)


class Client:
    def __init__(self, server_host: str, server_port: str, username: str, password: str):
        self.sock = None
        self.listening = None

        self.server_host = server_host
        self.server_port = server_port
        self.server_public_key = None

        self.username = username
        self.password = password
        self.rsa_key = rsa4096_generate()
        self.session_key = None
        self.iv = None

        self.users = {}
        self.users_credentials = {}


    def setup(self):
        self.listening = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.listening.bind(("0.0.0.0", 0))
        self.listening.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        threading.Thread(target=self.listen).start()


    def listen(self):
        try:
            self.listening.listen()
            while True:
                conn, _ = self.listening.accept()
                threading.Thread(target=self.handle_connection, args=(conn,)).start()
        except ConnectionAbortedError: # closed by exit
            sys.exit(0)

    
    def handle_connection(self, conn: socket.socket):
        data = recv(conn)
        match data["type"]:
            case "handshake":
                self.handle_peer_handshake(conn, data)
            case "message":
                self.handle_message(data)
            case _:
                print(f"+> unknown message type: {data['type']}")


    def connect(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((self.server_host, self.server_port))


    def authenticate(self):
        # step 1
        t1 = str(time.time())
        send(self.sock, {
            "type": "sign-in",
            "payload": {
                "step": 1,
                "t1": t1,
                "public_key": self.rsa_key.public_key().public_bytes(
                    encoding = serialization.Encoding.PEM,
                    format = serialization.PublicFormat.SubjectPublicKeyInfo
                ).decode(),
            },
        })
        print(f"+> sign-in step 1 completed at t1: {t1}")

        # step 2
        step2 = recv(self.sock)
        assert step2["type"] == "sign-in"
        assert step2["payload"]["step"] == 2
        assert t1 == rsa4096_decrypt(self.rsa_key, bytes.fromhex(step2["payload"]["t1"])).decode()
        t2 = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step2["payload"]["t2"])).decode()
        self.server_public_key = serialization.load_pem_public_key(step2["payload"]["public_key"].encode())
        print(f"+> sign-in step 2 completed at t2: {t2}")

        # step 3
        t3 = str(time.time())
        self.session_key, self.iv = aes256_generate()
        send(self.sock, {
            "type": "sign-in",
            "payload": {
                "step": 3,
                "username": rsa4096_encrypt(self.server_public_key, self.username.encode()).hex(),
                "hashed_password": rsa4096_encrypt(self.server_public_key, sha256(self.password).encode()).hex(),
                "session_key": rsa4096_encrypt(self.server_public_key, self.session_key).hex(),
                "iv": rsa4096_encrypt(self.server_public_key, self.iv).hex(),
                "t2": rsa4096_encrypt(self.server_public_key, t2.encode()).hex(),
                "t3": rsa4096_encrypt(self.server_public_key, t3.encode()).hex(),
                "host": aes256_encrypt(self.session_key, self.iv, self.listening.getsockname()[0].encode()).hex(),
                "port": aes256_encrypt(self.session_key, self.iv, str(self.listening.getsockname()[1]).encode()).hex(),
            },
        })
        print(f"+> sign-in step 3 completed, session key and iv generated at t3: {t3}")

        # step 4
        step4 = recv(self.sock)
        assert step4["type"] == "sign-in"
        assert step4["payload"]["step"] == 4
        assert sha256(t3) == aes256_decrypt(self.session_key, self.iv, bytes.fromhex(step4["payload"]["challenge"])).decode()
        print("+> sign-in step 4 completed, server passed challenge")


    def handle_list(self):
        self.users = {}
        result = self.send_recv_and_return({"type": "list", "username": self.username})["payload"]["users"]
        for user in result:
            username = aes256_decrypt(self.session_key, self.iv, bytes.fromhex(user["username"])).decode()
            key = serialization.load_pem_public_key(user["public_key"].encode())
            host = aes256_decrypt(self.session_key, self.iv, bytes.fromhex(user["host"])).decode()
            port = aes256_decrypt(self.session_key, self.iv, bytes.fromhex(user["port"])).decode()
            self.users[username] = {"public_key": key, "host": host, "port": port}


    def handle_send(self, username: str, message: str):
        if username not in self.users_credentials:
            print("+> peer handshake required")
            self.peer_handshake(username)
            print("+> peer handshake completed")
        
        session_key = self.users_credentials[username]["session_key"]
        iv = self.users_credentials[username]["iv"]

        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.users[username]["host"], int(self.users[username]["port"])))
        signature = rsa4096_sign(self.rsa_key, sha256(message).encode())
        send(sock, {
            "type": "message",
            "payload": {
                "nonce": str(time.time()),
                "username": rsa4096_encrypt(self.users_credentials[username]["public_key"], self.username.encode()).hex(),
                "message": aes256_encrypt(session_key, iv, message.encode()).hex(),
                "signature": signature.hex(),
            },
        })
        sock.close()
        print(f"+> message sent to {username}")


    def handle_message(self, data: dict):
        username = rsa4096_decrypt(self.rsa_key, bytes.fromhex(data["payload"]["username"])).decode()
        session_key = self.users_credentials[username]["session_key"]
        iv = self.users_credentials[username]["iv"]
        peer_pub_key = self.users_credentials[username]["public_key"]

        message = aes256_decrypt(session_key, iv, bytes.fromhex(data["payload"]["message"])).decode()
        signature = bytes.fromhex(data["payload"]["signature"])
        if not rsa4096_verify(peer_pub_key, sha256(message).encode(), signature):
            print("+> signature verification failed, the message may have been tampered with")
        print(f"+> message from {username}: {message}\n+> ", end="")


    def peer_handshake(self, username: str):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.users[username]["host"], int(self.users[username]["port"])))

        # step 1
        t1 = str(time.time())
        send(sock, {
            "type": "handshake",
            "payload": {
                "step": 1,
                "t1": t1,
                "public_key": self.rsa_key.public_key().public_bytes(
                    encoding = serialization.Encoding.PEM,
                    format = serialization.PublicFormat.SubjectPublicKeyInfo
                ).decode(),
            },
        })
        print(f"+> peer handshake step 1 completed at t1: {t1}")

        # step 2
        step2 = recv(sock)
        assert step2["type"] == "handshake"
        assert step2["payload"]["step"] == 2
        assert t1 == rsa4096_decrypt(self.rsa_key, bytes.fromhex(step2["payload"]["t1"])).decode()
        t2 = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step2["payload"]["t2"])).decode()
        peer_pub_key = serialization.load_pem_public_key(step2["payload"]["public_key"].encode())
        print(f"+> peer handshake step 2 completed at t2: {t2}")

        # step 3
        t3 = str(time.time())
        session_key, iv = aes256_generate()
        send(sock, {
            "type": "handshake",
            "payload": {
                "step": 3,
                "username": rsa4096_encrypt(peer_pub_key, self.username.encode()).hex(),
                "session_key": rsa4096_encrypt(peer_pub_key, session_key).hex(),
                "iv": rsa4096_encrypt(peer_pub_key, iv).hex(),
                "t2": rsa4096_encrypt(peer_pub_key, t2.encode()).hex(),
                "t3": rsa4096_encrypt(peer_pub_key, t3.encode()).hex(),
            },
        })
        print(f"+> peer handshake step 3 completed, session key and iv generated at t3: {t3}")

        # step 4
        step4 = recv(sock)
        assert step4["type"] == "handshake"
        assert step4["payload"]["step"] == 4
        assert sha256(t3) == aes256_decrypt(session_key, iv, bytes.fromhex(step4["payload"]["challenge"])).decode()
        print("+> peer handshake step 4 completed, peer passed challenge")

        self.users_credentials[username] = {"session_key": session_key, "iv": iv, "public_key": peer_pub_key}


    def handle_peer_handshake(self, conn: socket.socket, step1: dict):
        # step 1
        assert step1["type"] == "handshake"
        assert step1["payload"]["step"] == 1
        t1 = step1["payload"]["t1"]
        peer_pub_key = serialization.load_pem_public_key(step1["payload"]["public_key"].encode())
        print(f"\n+> peer handshake step 1 completed at t1: {t1}")

        # step 2
        t2 = str(time.time())
        send(conn, {
            "type": "handshake",
            "payload": {
                "step": 2,
                "t1": rsa4096_encrypt(peer_pub_key, t1.encode()).hex(),
                "t2": rsa4096_encrypt(peer_pub_key, t2.encode()).hex(),
                "public_key": self.rsa_key.public_key().public_bytes(
                    encoding = serialization.Encoding.PEM,
                    format = serialization.PublicFormat.SubjectPublicKeyInfo
                ).decode(),
            },
        })
        print(f"+> peer handshake step 2 completed at t2: {t2}")

        # step 3
        step3 = recv(conn)
        assert step3["type"] == "handshake"
        assert step3["payload"]["step"] == 3
        assert t2 == rsa4096_decrypt(self.rsa_key, bytes.fromhex(step3["payload"]["t2"])).decode()
        username = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step3["payload"]["username"])).decode()
        session_key = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step3["payload"]["session_key"]))
        iv = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step3["payload"]["iv"]))
        t3 = rsa4096_decrypt(self.rsa_key, bytes.fromhex(step3["payload"]["t3"])).decode()
        print(f"+> peer handshake step 3 completed, session key and iv generated at t3: {t3}")

        # step 4
        send(conn, {
            "type": "handshake",
            "payload": {
                "step": 4,
                "challenge": aes256_encrypt(session_key, iv, sha256(t3).encode()).hex(),
            },
        })
        print("+> peer handshake step 4 completed, peer passed challenge")

        self.users_credentials[username] = {"session_key": session_key, "iv": iv, "public_key": peer_pub_key}


    def send_recv_and_return(self, data: dict) -> dict:
        try:
            send(self.sock, data)
            return recv(self.sock)
        except Exception:
            self.connect()
            return self.send_recv_and_return(data)


    def handle_input(self):
        while True:
            data = input("+> ")
            if not data:
                continue
            cmd = data.split(" ")[0].lower()

            match cmd:
                case "exit":
                    self.exit()
                case "list":
                    self.handle_list()
                    print(f"+> {len(self.users)} users online: {', '.join(self.users.keys())}")
                case "send":
                    self.handle_list() # refresh users
                    if len(data.split(" ")) < 3:
                        print("+> usage: send <user> <message>")
                        continue
                    user = data.split(" ")[1]
                    if user not in self.users.keys():
                        print(f"+> user {user} not found, is the other user online?")
                        continue
                    message = " ".join(data.split(" ")[2:])
                    self.handle_send(user, message)
                case _:
                    print("+> usage: exit | list | send <user> <message>")


    def run(self):
        try:
            self.handle_input()
        except KeyboardInterrupt:
            self.exit()
            sys.exit(0)
        except BrokenPipeError:
            print("+> connection closed by server")
            self.connect()
            self.run()


    def exit(self):
        status = self.send_recv_and_return({"type": "sign-out", "username": self.username})
        assert status["type"] == "sign-out" and status["payload"] == "success"
        self.sock.close()
        self.listening.close()        
        sys.exit(0)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument("-sh", "--server-host", help="server host", type=str)
    parser.add_argument("-sp", "--server-port", help="server port", type=int)
    parser.add_argument("-u", "--username", help="user", type=str)
    parser.add_argument("-p", "--password", help="password", type=str)
    parser.add_argument("-c", "--config", help="config file", type=str)
    parser.parse_args(args=None if sys.argv[1:] else ["--help"])
    args = parser.parse_args()

    if args.config: # is a json
        import json
        with open(args.config) as f:
            config = json.load(f)
            if "server_host" in config:
                args.server_host = config["server_host"]
            else:
                raise ValueError("server_host not found in config")
            if "server_port" in config:
                args.server_port = config["server_port"]
            else:
                raise ValueError("server_port not found in config")
            if "username" in config:
                args.username = config["username"]
            if "password" in config:
                args.password = config["password"]
    else:
        if not args.server_host or not args.server_port:
            raise ValueError("missing arguments")
        
    if not args.username:
        args.username = input("+> username: ")
    if not args.password:
        args.password = input("+> password: ")

    client = Client(args.server_host, args.server_port, args.username, args.password)
    try:
        client.connect()
    except Exception:
        print("+> connection to server failed, check the server host and port, or is the server running?")
        sys.exit(1)
    client.setup()
    try:
        client.authenticate()
    except Exception:
        print("+> authentication failed, check your username and password")
        sys.exit(1)
    client.run()
