package com.util.messaging.spring;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.messaging.converter.MessageConverter;

import java.util.List;

// stomp websocket config
// for react test platform part - websocket permit to simulate recorder and catch realtime statues.
// send live status via websocket.
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${server.allow.origin}") 
    String allowOrigins;
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        
        if (Strings.isEmpty(allowOrigins)) {
            registry.addEndpoint("/live").setAllowedOriginPatterns("*").withSockJS();
        } else {
            registry.addEndpoint("/live").setAllowedOrigins(allowOrigins.split(",")).withSockJS();
        }
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(200000); // default : 64 * 1024
        registration.setSendTimeLimit(20 * 10000); // default : 10 * 10000
        registration.setSendBufferSizeLimit(3* 512 * 1024); // default : 512 * 1024
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new ByteArrayMessageConverter());
        return false;
    }
}