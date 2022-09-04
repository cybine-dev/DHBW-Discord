package de.cybine.dhbw.discordbot.data.schedule;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder(builderClassName = "Builder")
public class LectureSyncDto
{
    private final UUID id;
    private final UUID syncId;

    private final LectureDto lecture;

    private final UpdateType type;

    private final List<LectureSyncDetailDto> details;
}
