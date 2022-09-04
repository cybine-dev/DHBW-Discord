package de.cybine.dhbw.discordbot.data.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_syncs")
public class ScheduleSync
{
    @Id
    private UUID id;
}
