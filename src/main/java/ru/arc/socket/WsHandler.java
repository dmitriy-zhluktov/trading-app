package ru.arc.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.arc.service.TradeService;
import ru.arc.socket.model.CurrencyUpdate;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class WsHandler extends StompSessionHandlerAdapter {
    private final TradeService tradeService;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        final var currencyUpdate = (CurrencyUpdate) payload;
        tradeService.performAction(currencyUpdate.getCoin(), currencyUpdate.getDirection(), currencyUpdate.getCurrentPrice());
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
        return CurrencyUpdate.class;
    }
}

