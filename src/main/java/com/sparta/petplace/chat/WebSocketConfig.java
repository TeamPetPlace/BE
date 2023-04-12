package com.sparta.petplace.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker // Stomp를 사용하기위해 선언하는 어노테이션
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/place").setAllowedOrigins("*");
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 내장된 메세지 브로커를 사용해 Client에게 Subscriptions, Broadcasting 기능을 제공한다. 해당 경로로 SimpleBroker를 등록. SimpleBroker는 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행
        config.setApplicationDestinationPrefixes("/pub"); // /pub 경로로 시작하는 STOMP 메세지의 "destination" 헤더는@MessageMapping 메서드로 라우팅 된다. (Client에서 SEND 요청을 처리)

    }
}

