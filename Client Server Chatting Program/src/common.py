import json
import secrets
import socket
import hashlib

from argon2 import PasswordHasher
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives.asymmetric.rsa import RSAPublicKey, RSAPrivateKey
from cryptography.hazmat.primitives.asymmetric.padding import MGF1, OAEP
from cryptography.hazmat.primitives import hashes


def sha256(data: str) -> str:
    return hashlib.sha256(data.encode("utf-8")).hexdigest()


def argon2_hash(
        password: str,
        time_cost: int = 3,
        memory_cost: int = 65536,
        salt: bytes = None
) -> str:
    """
    argon2 kdf hash generator
    """
    return PasswordHasher(
        time_cost=time_cost,
        memory_cost=memory_cost,
        salt_len = 16 if not salt else len(salt),
    ).hash(
        password = hashlib.sha256(password.encode("utf-8")).hexdigest(),
        salt = None if not salt else salt
    )


def argon2_verify(
        password: str,
        hash: str
) -> bool:
    """
    argon2 kdf hash verifier
    """
    try:
        return PasswordHasher().verify(hash, password)
    except Exception:
        return False


def rsa4096_generate() -> RSAPrivateKey:
    """
    generate rsa4096 key pair
    """
    return rsa.generate_private_key(
        public_exponent = 65537,
        key_size = 4096,
    )


def rsa4096_encrypt(key: RSAPublicKey, data: bytes) -> bytes:
    return key.encrypt(
        data,
        OAEP(
            mgf = MGF1(hashes.SHA256()),
            algorithm = hashes.SHA256(),
            label = None
        )
    )


def rsa4096_decrypt(key: RSAPrivateKey, data: bytes) -> bytes:
    return key.decrypt(
        data,
        OAEP(
            mgf = MGF1(hashes.SHA256()),
            algorithm = hashes.SHA256(),
            label = None
        )
    )


def rsa4096_sign(key: RSAPrivateKey, data: bytes) -> bytes:
    return key.sign(
        data,
        padding.PSS(
            mgf = padding.MGF1(hashes.SHA256()),
            salt_length = padding.PSS.MAX_LENGTH
        ),
        hashes.SHA256(),
    )


def rsa4096_verify(key: RSAPublicKey, data: bytes, signature: bytes) -> bool:
    try:
        return key.verify(
            signature,
            data,
            padding.PSS(
                mgf=padding.MGF1(hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            hashes.SHA256()
        ) or True
    except Exception:
        return False


def aes256_generate() -> tuple[bytes, bytes]:
    """
    generate random 32 bytes key and iv for aes256 encryption
    """
    return secrets.token_bytes(32), secrets.token_bytes(16)


def aes256_encrypt(key: bytes, iv: bytes, data: bytes) -> bytes:
    cipher = Cipher(algorithms.AES256(key), modes.CFB(iv))
    encryptor = cipher.encryptor()
    return encryptor.update(data) + encryptor.finalize()


def aes256_decrypt(key: bytes, iv: bytes, data: bytes) -> bytes:
    cipher = Cipher(algorithms.AES256(key), modes.CFB(iv))
    decryptor = cipher.decryptor()
    return decryptor.update(data) + decryptor.finalize()


def send(sock: socket.socket, data: dict) -> None:
    sock.sendall(json.dumps(data).encode())


def recv(sock: socket.socket, buff: int = 1024) -> dict:
    data = b""
    while True:
        packet = sock.recv(buff)
        if not packet:
            break
        data += packet
        if len(packet) < buff:
            break
    return json.loads(data)


def read_client_credentials(file: str) -> dict:
    with open(file, "r") as f:
        return {line.split(" ")[0]: line.split(" ")[1].strip() for line in f.readlines()}
