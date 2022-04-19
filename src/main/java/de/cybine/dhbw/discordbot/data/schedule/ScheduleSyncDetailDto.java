package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ScheduleSyncDetailDto
{
    private long id;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String status;

    private Collection<LectureDto>        newLectures;
    private Collection<LectureDto>        removedLectures;
    private Collection<ScheduleUpdateDto> updatedLectures;
}
