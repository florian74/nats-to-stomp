Nats To Stomp

Design to redirect bytes payload from nats to a stomp ws, so mainly to transfer binary data.

The server is made to deliver data from nats to the outside according to the given configuration.
The other direction is not supported, because I think REST is a better way to provide a command,
but it can be nice to retrieve the data without much effort.

The app registered the configuration.yml file in the classpath and set up the mapping according to it.

Example:
```
configs:
  - source: "test.1"
    target: "/test/1"
  - source: "test.2"
    target: "/test/2"
```

In this example, the nats queue "test.1" is redirected to the stomp subject /test/1
and "test.2" is redirected to the stomp subject /test/2

The wildcard (>) on nats should be possible, but is not supported by STOMP protocol.

nats url can be configured with the env var NATS_URL.

local:
```
mvn install
java -jar .\target\nats-to-stomp-1.0-SNAPSHOT-exec.jar --nats.spring.server=localhost:4222 --server.config.path=file:<path-to>/configuration-override.yml
```

build docker with:

```docker build -t nats-to-stomp:v0.0.1 .   ```

run with:

```docker run nats-to-stomp:v0.0.1```

you can also specified option to docker run

A docker compose demo can be found in the demo directory
after running ```docker-compose up```, connect to localhost:3000

Select the nats queue you want, and a text message, then send it. String are converted to base64 during the process.
Specify the stomp queue to listen and check the message is received on the other side
Notice that every currently connected browser will receive the message