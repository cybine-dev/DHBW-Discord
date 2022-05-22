package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ILectureDao extends JpaRepository<LectureDto, Long>
{
    @Query("SELECT lecture FROM LectureDto lecture WHERE lecture.startsAt >= :from AND lecture.startsAt <= :to")
    List<LectureDto> findByStartDate(LocalDateTime from, LocalDateTime to);
}
