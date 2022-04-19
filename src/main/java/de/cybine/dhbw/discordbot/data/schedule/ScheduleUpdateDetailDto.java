package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ScheduleUpdateDetailDto
{
    private long id;

    private String fieldName;
    private String fieldType;

    private String previousValue;
    private String newValue;
}
