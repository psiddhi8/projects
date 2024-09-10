#import "@preview/polylux:0.3.1": *
#import themes.simple: *

#let title = "Secure Messenger Design"
#let author = "Yifei Sun, Yiting Wu"
#let date = datetime(year: 2024, month: 3, day: 15)

#set document(title: title, author: author, date: date)
#set page(paper: "presentation-16-9")

#show: simple-theme.with(footer: [CY 6740 - PS 4 - #author])

#slide[
  = #title

  #set text(16pt)

  By #author

  *Architecture*: KDC + Clients

  *Assumptions*: KDC have preregisted list of usernames and corresponding Argon2id
  hashed SHA256 hashed passwords

  *Workflow*: Password $arrow$ SHA256(Password) $arrow$ Argon2id $arrow$ RSA4096 $arrow$ Client-Server
  Key Exchange $arrow$ Client-Client Key Exchange -> Encrypted Com

  *Services*: Login (w/ username), List, Encrypted Messaging, Logout
  - Clients and servers generate new RSA key pair for every new "server session".
  - Encrypted communications between clients have signature of the communication
    content using RSA4096.

  *Login/Message/Logout Protocols*
  - Login: send identity (host + port), encrypted password, key exchange
  - List: between client and server, encrypted with session key
  - Message: client to client, first do key exchange, then encrypted with session
    key
  - Logout: reset client public keys and session keys on server side, must redo
    handshake to reestablish connection
]

#slide[
  == What Server Has at the Beginning of Each Session

  $P$: a password of arbitrary length provided by client

  $c_t$: time cost factor for Argon2id KDF (int)

  $c_m$: memory cost factor for Argon2id KDF (int)

  $r$: salt

  $K = "Argon2id"("SHA256"(P), c_t, c_m, r)$

  The server has a dictionary of usernames and corresponding $K$ values
]

#slide[
  == Client-Server Ephemeral Session Key Generation

  Assumption:

  - KDC (server) generates a long-lived public/private key pair

  - The key pair will stay the same for entire lifetime of the server (a new one
    will be generated if the server dies)

  - Server and each client will have a randomly generated RSA4096 public/private key
    pair
]

#slide[
  == Client-Server Ephemeral Session Key Generation

  Step 1: A $arrow.r.long$ S: $K_A$, $T_1$

  Step 2: S $arrow.r.long$ A: $K_S$, ${T_1, T_2}_K_A$ // T_1, T_2 are timestamps, A and S must verify the current time does not drift too far off from T_1, T_2

  Step 3: A $arrow.r.long$ S: ${A, P_A, K_"AS", T_2, T_3}_K_S$ $P_A$ is the hashed
  password // K_"AS" is the session key

  Step 4: S $arrow.r.long$ A: ${"Op"(T_3)}_K_"AS"$ // Op(T_3) meaning the result of some simple operation performed on T_3

  From 6 and on: A $arrow.r.long$ S: ${"type": "list", N}_K_"AS"$ or ${"type": "logout", N}_K_"AS"$ ...

  Replies will also be encrypted with the session key + nonce
]

#slide[
  == Client-Client Ephemeral Session Key Generation

  Step 1: A $arrow.r.long$ B: A, $K_A$, $T_1$

  Step 2: B $arrow.r.long$ A: $K_B$, ${T_1, T_2}_K_A$

  Step 3: A $arrow.r.long$ S: ${K_"AB", T_2, T_3}_K_B$

  Step 4: S $arrow.r.long$ A: ${"Op"(T_3)}_K_"AB"$

  From 6 and on: A $arrow.r.long$ S: ${"type": "message", N}_K_"AB"$

  Replies will also be encrypted with the session key + nonce
]

#slide[
  == Client-Client Ephemeral Session Key Generation

  Originally, this was supposed to be a modified version of Kerberos, but the
  final implementation is simplified to a modified version of TLS handshake
]

#slide[
  == Summary

  #set text(18pt)

  // https://argon2-cffi.readthedocs.io/en/stable/index.html
  *Argon2id KDF*
  - Memory hard / Long execution time
  - Prevents on-/off- line dictionary attacks

  *Perfect Forward Secrecy*:
  - Ephemeral session keys
  - Server does not know the session keys between two clients

  *Denial of Service Attacks*: Spawn more KDCs

  *End-points Hiding*
  - usernames are not exposed in the client-server communication
  - Only relatively anonymous public keys are exposed in plaintext
]
