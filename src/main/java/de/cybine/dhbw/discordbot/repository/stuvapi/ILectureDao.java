package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ILectureDao extends JpaRepository<LectureDto, Long>
{
    @Query(value = "SELECT * from lectures where left(created_at, 10) = ?1", nativeQuery = true)
    List<LectureDto> findByStartDate(String date);


}
