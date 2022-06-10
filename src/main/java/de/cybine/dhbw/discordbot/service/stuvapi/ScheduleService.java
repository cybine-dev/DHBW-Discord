package de.cybine.dhbw.discordbot.service.stuvapi;

import de.cybine.dhbw.discordbot.api.external.StuvAPI;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.*;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.repository.stuvapi.IRoomDao;
import de.cybine.dhbw.discordbot.repository.stuvapi.IScheduleSyncDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.ScheduleUpdateEvent;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.service.stuvapi.event.response.StuvApiLectureResponseEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class ScheduleService
{
    private final StuvApiConfig stuvApiConfig;
    private final StuvAPI       stuvAPI;
    private final EventManager  eventManager;

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

    public Map<UpdateType, Collection<LectureDto>> applyLectureUpdates( )
    {
        ScheduleService.log.debug("Collecting new lecture updates.");

        Map<UpdateType, Collection<LectureDto>> updatedLectures = new HashMap<>();
        Collection<ScheduleSyncDto> syncs = this.stuvAPI.getLatestSyncs(1, 0);
        for (ScheduleSyncDto sync : syncs)
        {
            if (this.scheduleSyncRepository.existsById(sync.getId()))
                continue;

            this.scheduleSyncRepository.save(sync);

            if (!sync.isHasChanges())
                continue;

            ScheduleService.log.debug("Applying lecture creations.");
            ScheduleSyncDetailDto syncDetails = this.stuvAPI.getSyncDetails(((Long) sync.getId()).intValue());
            updatedLectures.put(UpdateType.CREATED,
                    syncDetails.getNewLectures().stream().filter(this::filterOnlyConfiguredCourse).toList());
            syncDetails.getNewLectures()
                    .stream()
                    .filter(this::filterOnlyConfiguredCourse)
                    .forEach(this.lectureRepository::save);

            ScheduleService.log.debug("Applying lecture removals.");
            updatedLectures.put(UpdateType.DELETED,
                    syncDetails.getRemovedLectures().stream().filter(this::filterOnlyConfiguredCourse).toList());
            syncDetails.getRemovedLectures()
                    .stream()
                    .filter(this::filterOnlyConfiguredCourse)
                    .forEach(this.lectureRepository::delete);

            ScheduleService.log.debug("Applying lecture updates.");
            updatedLectures.put(UpdateType.UPDATED,
                    syncDetails.getUpdatedLectures()
                            .stream()
                            .map(ScheduleUpdateDto::getLecture)
                            .filter(this::filterOnlyConfiguredCourse)
                            .toList());
            syncDetails.getUpdatedLectures()
                    .stream()
                    .map(ScheduleUpdateDto::getLecture)
                    .filter(this::filterOnlyConfiguredCourse)
                    .forEach(this.lectureRepository::save);
        }

        if (updatedLectures.values().stream().anyMatch(updates -> !updates.isEmpty()))
            this.eventManager.handle(manager -> new ScheduleUpdateEvent(updatedLectures));

        ScheduleService.log.debug("Lecture updates applied.");
        return updatedLectures;
    }

    private boolean filterOnlyConfiguredCourse(LectureDto lecture)
    {
        return lecture.getCourse().equals(this.stuvApiConfig.courseName());
    }
}
