package de.cybine.dhbw.discordbot.data.schedule;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder(builderClassName = "Builder")
public class LectureSyncDetailDto
{
    private final String description;
    private final String fieldName;

    private final String previousValue;
    private final String currentValue;

    public Optional<String> getDescription( )
    {
        return Optional.ofNullable(this.description);
    }

    public Optional<String> getPreviousValue( )
    {
        return Optional.ofNullable(this.previousValue);
    }
}
