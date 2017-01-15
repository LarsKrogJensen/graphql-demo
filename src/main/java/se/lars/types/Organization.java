package se.lars.types;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableOrganization.class)
@JsonDeserialize(as = ImmutableOrganization.class)
public interface Organization
{
    String id();
    Optional<String> name();
    Optional<String> countryCode();
    Optional<Sector> industryClassification();
    Optional<Sector> subIndustryClassification();
    Optional<List<Reference>> listedEquities();
    Optional<Reference> mostLiquidEquity();
}
