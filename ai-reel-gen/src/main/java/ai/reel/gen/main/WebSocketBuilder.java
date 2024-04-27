package ai.reel.gen.main;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import commons.marcandreher.Commons.Flogger;
import commons.marcandreher.Commons.Flogger.Prefix;
import spark.Session;
import spark.Spark;

@WebSocket
public class WebSocketBuilder {
    Flogger logger = Flogger.instance;

    public WebSocketBuilder() {
        Spark.webSocket("/ws", this);
       
    }

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        logger.log(Prefix.INFO, "A user joined ", 0);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        logger.log(Prefix.INFO, "A user left ", 0);
    }

    @OnWebSocketMessage
    public void onMessage(org.eclipse.jetty.websocket.api.Session user, String message) {
        logger.log(Prefix.INFO, "A message  " + message, 0);
    }
}
