package se.six.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSector.class)
@JsonDeserialize(as = ImmutableSector.class)
public interface Sector
{
    String code();
    String description();
}
