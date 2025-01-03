package ru.arc.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.arc.service.TradeService;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class WsHandler extends StompSessionHandlerAdapter {
    private final TradeService tradeService;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println(new String((byte[]) payload));
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/greetings", this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }
}

