package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.RoomDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoomDao extends JpaRepository<RoomDto, String>
{ }
