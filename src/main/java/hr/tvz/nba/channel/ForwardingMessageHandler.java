package hr.tvz.nba.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

public class ForwardingMessageHandler implements MessageHandler {

    private final FluxSink<WebSocketMessage> sink;
    private final WebSocketSession session;
    private final ObjectMapper om = new ObjectMapper();

    public ForwardingMessageHandler(WebSocketSession session, FluxSink<WebSocketMessage> sink) {
        this.sink = sink;
        this.session = session;
    }

    @Override
    public void handleMessage(Message<?> message) {
        try {
            sink.next(session.textMessage(om.writeValueAsString(message.getPayload())));
        } catch (JsonProcessingException e) {
            System.out.println("defd");
        }
    }
}
