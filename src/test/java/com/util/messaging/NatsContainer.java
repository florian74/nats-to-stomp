package com.util.messaging;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class NatsContainer {

    public final static GenericContainer<?> natsContainer;


    static {
        natsContainer = new GenericContainer<>(DockerImageName.parse("nats:alpine")).withExposedPorts(4222);

        natsContainer.start();
    }

    public static String getNatsURI() {
        return "nats://" + natsContainer.getHost() + ":" + natsContainer.getMappedPort(4222);
    }
}
