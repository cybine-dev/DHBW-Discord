package de.cybine.dhbw.discordbot.data.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class RoomDto
{
    private UUID id;

    private String name;
    private String displayName;
}
