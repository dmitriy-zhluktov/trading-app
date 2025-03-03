package ru.arc.socket;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;

@Slf4j
public class ReconnectingWebSocketConnectionManager extends WebSocketConnectionManager {
    private boolean reconnectIfNeed = true;
    private ReconnectingTask reconnectingTask;
    public ReconnectingWebSocketConnectionManager(WebSocketClient client, WebSocketHandler webSocketHandler, String uriTemplate, Object... uriVariables) {
        super(client, webSocketHandler, uriTemplate, uriVariables);
    }

    @Override
    protected void openConnection() {
        super.openConnection();
        if (reconnectingTask == null) {
            reconnectingTask = new ReconnectingTask(this);
            reconnectingTask.run();
        }
    }

    @Override
    protected void closeConnection() throws Exception {
        reconnectIfNeed = false;
        super.closeConnection();
    }

    @RequiredArgsConstructor
    private static class ReconnectingTask implements Runnable {
        private final ReconnectingWebSocketConnectionManager connectionManager;

        @SneakyThrows
        @Override
        public void run() {
            while (connectionManager.reconnectIfNeed) {
                Thread.sleep(5000L);
                if (!connectionManager.isConnected()) {
                    log.info("reconnecting");
                    connectionManager.openConnection();
                }
            }
        }
    }
}
