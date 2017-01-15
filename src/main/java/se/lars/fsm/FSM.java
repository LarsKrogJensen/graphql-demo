package se.lars.fsm;

import io.vertx.core.eventbus.Message;
import javaslang.API;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class FSM
{

    public static <TState> State<TState> when(TState state)
    {
        return new State<>(state);
    }


    public static <TState> State<TState> when(TState state, API.Match<TState> ... match)
        {
            return new State<>(state);
        }

    public static class State<TState>
    {
        private TState _state;
        private List<Match<TState, ? super Object>> _matchers = new ArrayList<>();

        public State(TState state)
        {
            _state = state;
        }

        public TState state()
        {
            return _state;
        }

        public <TValue> State<TState> match(Class<TValue> type,
                                            Function<Message<TValue>, TState> handler)
        {
            //noinspection unchecked
            _matchers.add(new Match(type, handler));
            return this;
        }

        public Stream<Match<TState, Object>> matchers()
        {
            return _matchers.stream();
        }
    }

    public static class Match<TState, TValue>
    {
        private Class<TValue> _type;
        private Function<Message<TValue>, TState> _handler;

        public Match(Class<TValue> type, Function<Message<TValue>, TState> handler)
        {
            _type = type;
            _handler = handler;
        }

        public Class<TValue> type()
        {
            return _type;
        }

        public Function<Message<TValue>, TState> handler()
        {
            return _handler;
        }
    }
}
