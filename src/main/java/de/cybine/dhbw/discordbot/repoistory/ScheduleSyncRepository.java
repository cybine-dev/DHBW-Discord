package de.cybine.dhbw.discordbot.repoistory;

import de.cybine.dhbw.discordbot.data.schedule.ScheduleSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleSyncRepository extends JpaRepository<ScheduleSync, UUID>
{ }
