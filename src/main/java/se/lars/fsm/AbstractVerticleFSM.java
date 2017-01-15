package se.lars.fsm;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

import static se.lars.fsm.FSM.when;

//import static se.lars.FSM.when;

public class AbstractVerticleFSM<TState>
    extends AbstractVerticle
{
    protected static Logger log = LoggerFactory.getLogger(AbstractVerticleFSM.class);

    private List<MessageConsumer<?>> _consumers = new ArrayList<>();
    private TState _currentState;

    protected void init(TState state)
    {
        _currentState = state;
    }

    @SuppressWarnings({"unchecked", "varargs"})
    @SafeVarargs
    final void receive(String address, FSM.State<TState>... stateBuilders)
    {
        //List<FSM.State<TState>> states = stateBuilders.collect(Collectors.toList());
        vertx.eventBus().consumer(address, event -> {
            Optional<FSM.Match<TState, Object>> x =
                Stream.of(stateBuilders)
                      .filter(state -> state.state() == _currentState)
                      .flatMap(state -> state.matchers())
                      .filter(match -> match.type().equals(event.body().getClass()))
                      .findFirst();

            if (x.isPresent()) {
                _currentState = x.get().handler().apply(event);
            } else {
                log.warn("Message dropped {} in State {}", event.body().getClass(), _currentState);
            }
        });
    }


    //void consume(String address, Stream<FSM.Match> matchers)
    //{
    //    List<FSM.Match> matches = matchers.collect(Collectors.toList());
    //    vertx.eventBus().consumer(address, event -> {
    //        matches.stream()
    //              .map(match -> match)
    //              .filter(match -> match.type().equals(event.body().getClass()))
    //              .findFirst()
    //              .ifPresent(match -> {
    //                  _currentState = match.handler().apply(event);
    //              });
    //    });
    //}
}

class MyStatefulVerticle
    extends AbstractVerticleFSM<MyStatefulVerticle.State>
{
    enum State
    {
        Init, Started
    }


    @Override
    public void start()
        throws Exception
    {
        init(State.Init);

        receive("1234",
                when(State.Init)
                    .match(Integer.class, (message) -> {
                        System.out.println("INIT: received: " + message.body());
                        return State.Init;
                    })
                    .match(String.class, (message) -> {
                        System.out.println("INIT: received: " + message.body());
                        return State.Started;
                    }),
                when(State.Started)
                    .match(Integer.class, (message) -> {
                        System.out.println("STARTED: received: " + message.body());
                        return State.Started;
                    })
                    .match(String.class, (message) -> {
                        System.out.println("STARTED: received: " + message.body());
                        return State.Init;
                    }));

    }

    public static void main(String[] args)
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyStatefulVerticle(), event -> {
            vertx.eventBus().send("1234", 1);
            vertx.eventBus().send("1234", "forward");
            vertx.eventBus().send("1234", 2);
            vertx.eventBus().send("1234", "forward");
            vertx.eventBus().send("1234", 124324d);
        });
    }
}