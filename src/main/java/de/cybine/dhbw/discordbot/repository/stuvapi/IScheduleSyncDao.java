package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.ScheduleSyncDto;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface IScheduleSyncDao extends JpaRepository<ScheduleSyncDto, Long>
{ }
