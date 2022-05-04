package de.cybine.dhbw.discordbot.service.stuvapi;

import de.cybine.dhbw.discordbot.api.external.StuvAPI;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.repository.stuvapi.IRoomDao;
import de.cybine.dhbw.discordbot.repository.stuvapi.IScheduleSyncDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.service.stuvapi.event.response.StuvApiLectureResponseEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ScheduleService
{
    private final StuvAPI      stuvAPI;
    private final EventManager eventManager;

    private final IRoomDao         roomRepository;
    private final ILectureDao      lectureRepository;
    private final IScheduleSyncDao scheduleSyncRepository;

    public void updateLectures(String course)
    {
        StuvApiLectureRequestEvent event = this.eventManager.handle(manager -> StuvApiLectureRequestEvent.of(false,
                course));

        if (event.isCanceled())
            return;

        Collection<LectureDto> lectures = event.getCourse()
                .map(courseName -> this.stuvAPI.getLectures(event.isIncludeArchived(), courseName))
                .orElseGet(( ) -> this.stuvAPI.getLectures(event.isIncludeArchived()));

        this.lectureRepository.saveAll(lectures);

        StuvApiLectureResponseEvent response = StuvApiLectureResponseEvent.of(LocalDateTime.now(),
                lectures,
                event.isIncludeArchived(),
                event.getCourse().orElse(null));

        this.eventManager.handle(manager -> response);
    }
}
