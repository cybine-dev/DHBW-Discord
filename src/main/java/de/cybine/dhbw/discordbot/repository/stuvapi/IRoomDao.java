package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.RoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Collection;

@Transactional
public interface IRoomDao extends JpaRepository<RoomDto, Long>
{
    Collection<RoomDto> findAllByName(@Param("name") String name);
}
