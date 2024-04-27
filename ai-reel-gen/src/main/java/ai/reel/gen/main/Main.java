package ai.reel.gen.main;

import java.io.IOException;

import ai.reel.gen.commands.GenShort;
import commons.marcandreher.Commons.Flogger;
import commons.marcandreher.Commons.WebServer;
import commons.marcandreher.Input.CommandHandler;
import io.github.cdimascio.dotenv.Dotenv;
import spark.Route;
import spark.Spark;

public class Main {

    public static final Dotenv ENV = Dotenv.load();

    public static void main(String[] args) {
        Flogger logger = new Flogger(3);

        WebServer webServer = new WebServer(logger, (short) 0);
        webServer.setThreadPool(3, 6, 3000);

       
        new WebSocketBuilder();

        
        try {
            webServer.ignite(ENV.get("IP"), Integer.parseInt(ENV.get("PORT")));
        } catch (NumberFormatException | IOException e) {
            logger.error(e);
        }

        Spark.get("/", new Route() {
            @Override
            public Object handle(spark.Request request, spark.Response response) throws Exception {
                return "Hello World";
            }
        });

        CommandHandler commandHandler = new CommandHandler(logger);
        commandHandler.registerCommand(new GenShort());
        commandHandler.initialize();
  
    }
    
}
