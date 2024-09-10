import argparse
import sys
import socket
import socketserver
import time

from cryptography.hazmat.primitives import serialization

from common import (
    sha256,
    argon2_verify,
    aes256_encrypt,
    aes256_decrypt,
    rsa4096_generate,
    rsa4096_encrypt,
    rsa4096_decrypt,
    send,
    recv,
    read_client_credentials,
)

clients = {}
credentials = read_client_credentials("credentials.txt") # user1: password1, ..., user3: password3
rsa_key = rsa4096_generate()


def sign_in(sock: socket.socket, step1: dict) -> None:
    assert step1["type"] == "sign-in"
    t1 = step1["payload"]["t1"]
    t2 = str(time.time())
    pub_key = serialization.load_pem_public_key(step1["payload"]["public_key"].encode())
    print(f"+> sign-in step 1 received from {sock.getpeername()}")

    # step 2
    send(sock, {
        "type": "sign-in",
        "payload": {
            "step": 2,
            "public_key": rsa_key.public_key().public_bytes(
                encoding = serialization.Encoding.PEM,
                format = serialization.PublicFormat.SubjectPublicKeyInfo
            ).decode(),
        "t1": rsa4096_encrypt(pub_key, t1.encode()).hex(),
        "t2": rsa4096_encrypt(pub_key, t2.encode()).hex(),
        },
    })
    print(f"+> sign-in step 2 completed at t2: {t2}")

    # step 3
    step3 = recv(sock)
    assert step3["type"] == "sign-in"
    assert step3["payload"]["step"] == 3
    username = rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["username"])).decode()
    hashed_password = rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["hashed_password"])).decode()
    session_key = rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["session_key"]))
    iv = rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["iv"]))
    assert t2 == rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["t2"])).decode()
    t3 = rsa4096_decrypt(rsa_key, bytes.fromhex(step3["payload"]["t3"])).decode()
    assert argon2_verify(hashed_password, credentials[username])
    print(f"+> sign-in step 3 completed, user {username} passed password verification")
    user_host = aes256_decrypt(session_key, iv, bytes.fromhex(step3["payload"]["host"])).decode()
    user_port = aes256_decrypt(session_key, iv, bytes.fromhex(step3["payload"]["port"])).decode()

    # step 4
    send(sock, {
        "type": "sign-in",
        "payload": {
            "step": 4,
            "challenge": aes256_encrypt(session_key, iv, sha256(t3).encode()).hex(),
        },
    })
    print("+> sign-in step 4 completed, client passed challenge")

    clients[username] = {
        "public_key": pub_key,
        "session_key": session_key,
        "iv": iv,
        "host": user_host,
        "port": user_port,
    }


def sign_out(sock: socket.socket, data: dict) -> None:
    assert data["type"] == "sign-out"
    print(f"+> sign-out request received from {sock.getpeername()} for {data['username']}")
    clients.pop(data["username"])
    send(sock, {"type": "sign-out", "payload": "success"})


def list_users(sock: socket.socket, req: dict) -> None:
    assert req["type"] == "list"
    session_key = clients[req["username"]]["session_key"]
    iv = clients[req["username"]]["iv"]
    print(f"+> list request received from {sock.getpeername()}, response has {len(clients)-1} users")
    send(sock, {
        "type": "list",
        "payload": {
            "users": [
                # for each user, encrypt name pubkey, host and port
                {
                    "nonce": str(time.time()),
                    "username": aes256_encrypt(session_key, iv, user.encode()).hex(),
                    "host": aes256_encrypt(session_key, iv, clients[user]["host"].encode()).hex(),
                    "port": aes256_encrypt(session_key, iv, clients[user]["port"].encode()).hex(),
                    "public_key": clients[user]["public_key"].public_bytes(
                        encoding = serialization.Encoding.PEM,
                        format = serialization.PublicFormat.SubjectPublicKeyInfo
                    ).decode(),
                } for user in clients if user != req["username"]
            ],
        },
    })


def main() -> None:
    # parse -sp
    parser = argparse.ArgumentParser()
    parser.add_argument("-sh", "--server-host", help="server host", type=str)
    parser.add_argument("-sp", "--server-port", help="server port", type=int)
    parser.add_argument("-c", "--config", help="config file", type=str)
    parser.parse_args(args=None if sys.argv[1:] else ["--help"])
    args = parser.parse_args()

    if args.config:
        import json
        with open(args.config, "r") as f:
            config = json.load(f)
            if "server_host" in config:
                args.server_host = config["server_host"]
            else:
                raise ValueError("server_host not found in config")
            if "server_port" in config:
                args.server_port = config["server_port"]
            else:
                raise ValueError("server_port not found in config")
    else:
        if not args.server_host:
            raise ValueError("server_host not provided")
        if not args.server_port:
            raise ValueError("server_port not provided")
    class ChatServer(socketserver.BaseRequestHandler):
        def handle(self) -> None:
            data = recv(self.request)
            match data["type"]:
                case "sign-in":
                    sign_in(self.request, data)
                case "sign-out":
                    sign_out(self.request, data)
                case "list":
                    list_users(self.request, data)
                case _:
                    print(f"+> invalid message type: {data['type']}")
    try:
        with socketserver.ThreadingTCPServer((args.server_host, args.server_port), ChatServer) as server:
            print(f"+> server started on {args.server_host}:{args.server_port}")
            server.serve_forever()
    except KeyboardInterrupt:
        print("\n+> keyboard interrupt")
    except Exception as e:
        print(f"+> server error: {e}")
    

if __name__ == "__main__":
    raise SystemExit(main())
