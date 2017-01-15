package se.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableOrderLevel.class)
@JsonDeserialize(as = ImmutableOrderLevel.class)
@Value.Immutable
public interface OrderLevel
{
    int level();
    double askPrice();
    double bidPrice();
    double askVolume();
    double bidVolume();
    double askOrders();
    double bidOrders();

}
