package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "schedule_syncs")
public class ScheduleSyncDto
{
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "status")
    private String  status;
    @Column(name = "has_changes")
    private boolean hasChanges;

    @Column(name = "update_count")
    private int updateCount;
    @Column(name = "new_count")
    private int newCount;
    @Column(name = "remove_count")
    private int removeCount;
}
