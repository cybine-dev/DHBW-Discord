package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.polls.PollsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface IPollsDao extends JpaRepository<PollsDto, Long>
{
    @Query("SELECT poll FROM PollsDto poll WHERE poll.name = :name")
    List<PollsDto> findByName(String name);
}
