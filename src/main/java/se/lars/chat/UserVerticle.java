package se.lars.chat;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


public class UserVerticle
    extends AbstractVerticle
{
    private static Logger _log = LoggerFactory.getLogger(UserVerticle.class);
    private ServerWebSocket _webSocket;
    private List<MessageConsumer<?>> _consumers;

    public UserVerticle(ServerWebSocket webSocket)
    {
        _webSocket = webSocket;
    }

    @Override
    public void start()
        throws Exception
    {
        EventBus eventBus = vertx.eventBus();

        _consumers = Arrays.asList(
            eventBus.<IncomingMessage>consumer(deploymentID(),
                                               event -> {
                                                   _log.info("Incoming message: " + event.body().message());
                                                   eventBus.send(ChatRoomVerticle.MessagesInChannel, event.body());
                                               }),
            eventBus.<OutgoingMessage>consumer(ChatRoomVerticle.MessagesOutChannel,
                                               event -> {
                                                   _log.info("Outgoing message: " + event.body().message());
                                                   _webSocket.writeFinalTextFrame(event.body().message());
                                               }));

        eventBus.send(ChatRoomVerticle.JoinChannel,
                      JoinMessageImpl.of(deploymentID()),
                      replyHandler -> {
                          _log.info("Join response:" + replyHandler.result().body());
                      });

        _log.info("User started");
    }

    @Override
    public void stop(Future<Void> stopFuture)
        throws Exception
    {
        _consumers.forEach(MessageConsumer::unregister);

        EventBus eventBus = vertx.eventBus();
        eventBus.send(ChatRoomVerticle.LeaveChannel, LeaveMessageImpl.of(deploymentID()), event -> {
            stopFuture.complete();
            _log.info("User stopped");
        });
    }


}
