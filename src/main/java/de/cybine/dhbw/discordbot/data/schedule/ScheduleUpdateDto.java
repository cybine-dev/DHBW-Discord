package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ScheduleUpdateDto
{
    private long id;

    private LectureDto lecture;

    private Collection<ScheduleUpdateDetailDto> changeInfo;
}
