package ru.arc.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import ru.arc.dao.SignalDao;
import ru.arc.service.TradeService;
import ru.arc.socket.model.CurrencyUpdate;

@RequiredArgsConstructor
@Slf4j
public class FlatWsMessageHandler extends AbstractWebSocketHandler {
    private final TradeService tradeService;
    private final ObjectMapper objectMapper;
    private final SignalDao signalDao;

    @Override
    protected void handleTextMessage(
            final WebSocketSession session,
            final TextMessage message
    ) throws Exception {
        final var currencyUpdate = objectMapper.readValue(message.getPayload(), CurrencyUpdate.class);
        log.info("Message received " + currencyUpdate.symbol + " " + currencyUpdate.direction);
        signalDao.insert(currencyUpdate);
        tradeService.performAction(currencyUpdate);
    }
}
