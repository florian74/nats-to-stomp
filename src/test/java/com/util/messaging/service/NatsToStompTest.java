package com.util.messaging.service;

import com.util.messaging.MessagingApplication;
import com.util.messaging.NatsContainer;
import com.util.messaging.yaml.Config;
import io.nats.client.Connection;
import io.nats.client.Nats;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.shaded.org.bouncycastle.util.Strings;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NatsToStompTest extends NatsContainer {

    public static ApplicationContext context;

    @BeforeAll
    public static void start() {

        List<String> argsList = new ArrayList<>();
        
        argsList.add("--nats.spring.server=" + getNatsURI());

        String[] args = new String[argsList.size()];
        argsList.toArray(args);

        Class[] classes = {MessagingApplication.class};

        
        boolean connected = false;
        while (! connected) {
            try {
               Connection conn = Nats.connect(getNatsURI());
               if (conn.getStatus() == Connection.Status.CONNECTED) {
                   connected = true;
               }
               conn.close();
            } catch (Exception e) {}
        }
        
        context = SpringApplication.run(classes, args);
    }

    @AfterAll
    public static void close() {
        SpringApplication.exit(context);
    }


    Config configuration = context.getBean(Config.class);
    
    @Test
    public void ConfigReadTest() {
        Assert.assertNotNull(configuration);
        Assert.assertNotEquals(configuration.getConfigs().size(), 0);
    }
    
    
    @Test
    public void NatsToStompTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        
        
        String payload = "This is the payload";
        
        Connection conn = Nats.connect(getNatsURI());



        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(20*1024*1024);
        
        WebSocketClient client = new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient(container))));
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new ByteArrayMessageConverter());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        //Calls initialize() after the container applied all property values.
        taskScheduler.afterPropertiesSet();
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setInboundMessageSizeLimit(Integer.MAX_VALUE);

        
        AtomicInteger r = new AtomicInteger(0);
        
        
        CompletableFuture<StompSession> future = stompClient.connectAsync("ws://localhost:8667/api/live", new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("handle error", exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("transport error", exception);
            }
        });
        StompSession session = future.get(10, TimeUnit.SECONDS);
        session.subscribe("/test/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info(Strings.fromByteArray((byte[]) payload));
                r.getAndIncrement();
            }
        });

         
          
        conn.publish("test.1", payload.getBytes(StandardCharsets.UTF_8));
        
        int count = 0;
        while(r.intValue() != 1 && count < 120) {
            Thread.sleep(10);
            count++;
        }
        
        Assert.assertEquals(r.intValue(), 1);
    }
    

}
