package se.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.*;


@Value.Immutable
@JsonSerialize(as = ImmutableListing.class)
@JsonDeserialize(as = ImmutableListing.class)
public interface Listing
{
    String id();
    Optional<String> name();
    Optional<String> type();
    Optional<String> currencyCode();
    Optional<String> longName();
    Optional<String> isinCode();
    Optional<String> micCode();
    Optional<Date> listingDate();
    OptionalInt roundLot();
    Optional<Reference> getIssuer();
}
