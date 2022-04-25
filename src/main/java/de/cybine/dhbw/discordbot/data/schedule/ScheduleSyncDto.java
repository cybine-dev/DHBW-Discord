package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ScheduleSyncDto
{
    private long id;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String  status;
    private boolean hasChanges;

    private int updateCount;
    private int newCount;
    private int removeCount;
}
