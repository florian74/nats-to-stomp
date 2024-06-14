package com.util.messaging.spring;

import io.nats.client.Connection;
import io.nats.client.Nats;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsConfig {
    
    @Bean
    Connection connection(@Value("${nats.spring.server}") String natsUrl) throws IOException, InterruptedException {
        if (Strings.isEmpty(natsUrl)) {
            natsUrl = "nats://localhost:4222";
        }
        return Nats.connect(natsUrl);
    }
}
