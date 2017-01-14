package se.six.lars.chat;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.lang.System.out;


public class ChatSystemHandler
    implements Handler<RoutingContext>
{
    private static Logger _log = LoggerFactory.getLogger(ChatSystemHandler.class);
    private Vertx _vertx;

    @Inject
    public ChatSystemHandler(Vertx vertx)
    {
        _vertx = vertx;
    }

    @Override
    public void handle(RoutingContext rc)
    {
        ServerWebSocket ws = rc.request().upgrade();

        out.println("path: " + ws.path());
        out.println("uri: " + ws.uri());
        out.println("local address: " + ws.localAddress());
        out.println("remore address: " + ws.remoteAddress());
        out.println("Socket id: " + ws.textHandlerID());

        _vertx.deployVerticle(new UserVerticle(ws), res -> {
            ws.closeHandler(event -> {
                _log.info("Close handler on id: " + ws.textHandlerID());
                _vertx.undeploy(res.result());
            });

            ws.handler(buffer -> {
                String msg = buffer.getString(0, buffer.length());
                _vertx.eventBus().send(res.result(), IncomingMessageImpl.of(msg));

                //out.println(msg);
                //out.println("Socket id: " + ws.textHandlerID());
                //
                //JsonObject json = new JsonObject();
                //json.put("eventType", "SessionAttached");
                //json.put("sessionId", "121212121");
                //String encode = json.encode();
                //ws.writeFinalTextFrame(encode);
                //ws.write(Buffer.buffer().appendString("OK"));
            });
        });

    }
}
