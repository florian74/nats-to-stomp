package com.util.messaging.service;

import com.util.messaging.yaml.Config;
import com.util.messaging.yaml.Redirection;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mvnsearch.spring.boot.nats.core.NatsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class NatsToStomp {
    
    // nats connector
    NatsTemplate natsTemplate;
    
    // publish to websocket (for tests display in web)
    SimpMessagingTemplate stompSender;
    
    // yaml mapping configuration
    Config config;

    public NatsToStomp(@Autowired SimpMessagingTemplate stompSender, @Autowired NatsTemplate natsTemplate, @Autowired Config config) throws IOException {
        this.stompSender = stompSender;
        this.natsTemplate = natsTemplate;
        this.config = config;
    }
    
    
    // read config and set up listeners
    @PostConstruct
    public void init() throws IOException {

        // Option 1: read config and redirect
        for (Redirection redirection : config.getConfigs()) {
            redirectQueueToStomp(redirection.getSource(), redirection.getTarget());
        }

        // Option 2: put it there by name
        //redirectQueueToStomp("nats.queue", "/stomp/queue");
    }
    
    
    // forward bytes from a destination to another
    public void redirectQueueToStomp(String natsQueue, String stompQueue) {
        natsTemplate.subscribe(natsQueue)
                .subscribe(payload -> {

                    Map<String, Object> header = new HashMap<>();
                    header.put("content-length", payload.getData().length);
                    header.put("content-type", "application/octet-stream");
                    header.put("destination", stompQueue);
                    
                    Message message = MessageBuilder.createMessage(payload.getData(), new MessageHeaders(header));
                    
                    stompSender.send(stompQueue, message );
                });
        log.info("redirecting nats queue: " + natsQueue + " , to stomp topic: " + stompQueue);
    }
    
    
    /*
    // Option 3: use an annotation
    @NatsSubscriber(subject = "nats.queue")
    public void forwardTrackLabel(Message message){
    
        // you may need to set header again here
        
        stompSender.send("/stomp/queue", message);
     
    }
    */
    
    




}
