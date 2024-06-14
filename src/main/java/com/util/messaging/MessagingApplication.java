package com.util.messaging;


import io.nats.spring.boot.autoconfigure.NatsAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


// main spring boot class, to start an embedded tomcat server with all functionalities
@SpringBootApplication()
@Import(NatsAutoConfiguration.class)
public class MessagingApplication {
  
    public static void main(String[] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }
}
