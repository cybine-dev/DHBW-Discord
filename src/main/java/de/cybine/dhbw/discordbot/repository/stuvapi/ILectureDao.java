package de.cybine.dhbw.discordbot.repository.stuvapi;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILectureDao extends JpaRepository<LectureDto, Long>
{ }
