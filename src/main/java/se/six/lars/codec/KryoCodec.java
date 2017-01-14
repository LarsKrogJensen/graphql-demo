package se.six.lars.codec;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.*;
import com.google.common.reflect.ClassPath;
import io.vertx.core.buffer.Buffer;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

public class KryoCodec<T>
    implements MessageCodec<T, T>
{
    private static ThreadLocal<Kryo> kryo =
        ThreadLocal.withInitial(Kryo::new);


    private static final ThreadLocal<Output> out =
        ThreadLocal.withInitial(() -> new UnsafeOutput(new ByteArrayOutputStream()));

    private Class<T> _type;

    public KryoCodec(Class<T> type)
    {
        _type = type;
    }


    @Override
    public void encodeToWire(Buffer buffer, T obj)
    {
        Output output = out.get();
        output.setPosition(0); // reset output
        kryo.get().writeObject(output, obj);
        byte[] bytes = output.toBytes();
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer)
    {
        int length = buffer.getInt(pos);
        pos += 4;
        byte[] encoded = buffer.getBytes(pos, pos + length);

        try (Input input = new UnsafeInput(encoded)) {
            return kryo.get().readObject(input, _type);
        }
    }

    @Override
    public T transform(T obj)
    {
        return obj;
    }

    @Override
    public String name()
    {
        return _type.getName();
    }

    @Override
    public byte systemCodecID()
    {
        return -1;
    }

    public static Stream<? extends Class> resolveKryoAwareClasses(final String... packages)
        throws IOException
    {
        ClassPath classPath = ClassPath.from(KryoCodec.class.getClassLoader());

        return Stream.of(packages)
                     .flatMap(packageName -> classPath.getTopLevelClasses(packageName).stream())
                     .map(ClassPath.ClassInfo::load)
                     .filter(type -> !type.isInterface())
                     .filter(type -> type.isAnnotationPresent(KryoCodecAware.class));
    }

    //public static void registerKryoAwareMessages(final EventBus bus, final String... packages)
    //    throws IOException
    //{
    //    ClassPath classPath = ClassPath.from(KryoCodec.class.getClassLoader());
    //
    //    Stream.of(packages)
    //          .flatMap(packageName -> classPath.getTopLevelClasses(packageName).stream())
    //          .map(ClassPath.ClassInfo::load)
    //          //.map(Class::getDeclaringClass)
    //          .filter(type -> !type.isInterface())
    //          .filter(type -> type.isAnnotationPresent(KryoCodecAware.class))
    //          .forEach(type -> bus.registerDefaultCodec(type, new KryoCodec<>(type)));
    //}
}

