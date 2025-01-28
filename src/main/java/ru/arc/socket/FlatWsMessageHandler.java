package ru.arc.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import ru.arc.service.TradeService;
import ru.arc.socket.model.CurrencyUpdate;

@RequiredArgsConstructor
public class FlatWsMessageHandler extends AbstractWebSocketHandler {
    private final TradeService tradeService;
    private final ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(
            final WebSocketSession session,
            final TextMessage message
    ) throws Exception {
        final var currencyUpdate = objectMapper.readValue(message.getPayload(), CurrencyUpdate.class);
        System.out.println(currencyUpdate.symbol + " " + currencyUpdate.direction);
        tradeService.performAction(currencyUpdate);
    }
}
