package se.lars.chat;

import org.immutables.value.Value;
import se.lars.codec.KryoCodecAware;

@Value.Immutable
@MessageStyle
@KryoCodecAware
public interface IncomingMessage
{
    @Value.Parameter
    String message();
}
