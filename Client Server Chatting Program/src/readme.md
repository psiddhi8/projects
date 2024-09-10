# PS4 Implementation

In the implementation, we assumed that the server has a pre-configured list of users and their hashed passwords.
The `credentials.txt` file contains of users: `user1` and `user2` and `user3` with their hashed passwords.

```txt
User: user1
Password: password1

User: user2
Password: password2

User: user3
Password: password3
```

To run the clients and server, you need to have the following installed:

- argon2-cffi
- cryptography

During development, we used Python 3.11.7, with the following versions of the libraries:

```txt
argon2-cffi==23.1.0
cryptography==42.0.5
```

You can install them with: `pip install -r requirements.txt`.

To run the server, you can use the following command:

```shell
python server.py -sh 0.0.0.0 -sp 22222
```

This binds the server socket to 0.0.0.0 and port 22222.

To run the client, you can use the following command:

```shell
python client.py -sh 127.0.0.1 -sp 22222 -u user1 -p password1
```

This connects the client to the server at 127.0.0.1 and port 22222 with the username `user1` and password `password1`. The instructions for other clients are similar, just change the username and password accordingly.

You can change the binding host and port, just make sure that the client and server are using the matching ones.

Or alternatively, you can pass "-c" or "--config" to client.py and server.py to use startup client and server with specified host and port:

```shell
python server.py -c config.json
```

```shell
python client.py -c config.json
-> username: user1
-> password: password1
...
```

The client will immediately connect to the server and try for authentication after getting the username and password from the config file or CLI arguments.
