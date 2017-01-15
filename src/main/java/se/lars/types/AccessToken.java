package se.lars.types;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAccessToken.class)
@JsonDeserialize(as = ImmutableAccessToken.class)
public interface AccessToken
{
    @JsonProperty("access_token")
    String accessToken();
    @JsonProperty("token_type")
    String tokenType();
}


