package se.six.lars.chat;

import org.immutables.value.Value;
import se.six.lars.codec.KryoCodecAware;

@Value.Immutable
@MessageStyle
@KryoCodecAware
public interface IncomingMessage
{
    @Value.Parameter
    String message();
}
