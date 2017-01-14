package se.six.lars.chat;

import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Lists;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ChatRoomVerticle
    extends AbstractVerticle
{
    public static final String MessagesInChannel = "se.lars.chatroom.message.in";
    public static final String MessagesOutChannel = "se.lars.chatroom.message.out";
    public static final String JoinChannel = "se.lars.chatroom.join";
    public static final String LeaveChannel = "se.lars.chatroom.leave";


    private static Logger _log = LoggerFactory.getLogger(ChatRoomVerticle.class);

    private Set<String> _users = new HashSet<>();
    private List<MessageConsumer<?>> _consumers;

    @Override
    public void start()
        throws Exception
    {
        EventBus eventBus = vertx.eventBus();


        _consumers = Arrays.asList(
            // Routing a message
            eventBus.<IncomingMessage>consumer(MessagesInChannel, event -> {
                _log.info("ChatRoom: Incoming message: " + event.body().message());
                eventBus.publish(MessagesOutChannel,
                                 OutgoingMessageImpl.of(event.body().message()));
            }),
            eventBus.<JoinMessage>consumer(JoinChannel, event -> {
                _log.info("User joined: " + event.body().id());
                _users.add(event.body().id());
                event.reply("OK");
            }),
            eventBus.<LeaveMessage>consumer(LeaveChannel, event -> {
                System.out.println("User left: " + event.body().id());
                _users.remove(event.body().id());
            }));

        _log.info("Chatroom started");
    }

    @Override
    public void stop()
        throws Exception
    {
        _consumers.forEach(MessageConsumer::unregister);
        _consumers = Collections.emptyList();

        _log.info("Chatroom stopped");
    }
}
