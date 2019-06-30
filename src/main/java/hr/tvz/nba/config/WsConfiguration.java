package hr.tvz.nba.config;

import hr.tvz.nba.channel.ForwardingMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.FluxSink;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static reactor.core.publisher.Flux.create;

@Configuration
@Slf4j
public class WsConfiguration {

    @Bean
    @Qualifier("connections")
    Map<String, MessageHandler> connections() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler webSocketHandler) {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(10);
        Map<String, Object> mappings = new HashMap<>();
        mappings.put("/google", webSocketHandler);
        simpleUrlHandlerMapping.setUrlMap(mappings);
        return simpleUrlHandlerMapping;
    }

    @Bean
    WebSocketHandler webSocketHandler(@Qualifier("connections") Map<String, MessageHandler> connections) {
        return session -> session.send(
                create(
                        (Consumer<FluxSink<WebSocketMessage>>) sink -> {
                            System.out.println("Connection " + session.getId());
                            ForwardingMessageHandler handler = new ForwardingMessageHandler(session, sink);

                            connections.put(session.getId(), handler);
                            this.channel().subscribe(connections.get(session.getId()));
                            handler.handleMessage(new GenericMessage<>(session.getId()));
                        }
                )
                        .doFinally(signalType -> {
                                    this.channel().unsubscribe(connections.get(session.getId()));
                                    connections.remove(session.getId());
                                }
                        )
        );
    }

    @Bean
    PublishSubscribeChannel channel() {
        return new PublishSubscribeChannel();
    }
}


