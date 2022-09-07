package de.cybine.dhbw.discordbot.data.schedule;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class ScheduleSyncDto
{
    private UUID id;

    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;

    private final List<LectureSyncDto> data;
}
