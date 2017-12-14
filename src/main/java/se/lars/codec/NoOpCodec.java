package se.lars.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import static java.util.Objects.requireNonNull;

public class NoOpCodec<T> implements MessageCodec<T,T> {
    private final Class<T> type;

    public NoOpCodec(Class<T> type) {
        requireNonNull(type);
        this.type = type;
    }

    @Override
    public void encodeToWire(Buffer buffer, T t) {

    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public T transform(T t) {
        return t;
    }

    @Override
    public String name() {
        return type.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
