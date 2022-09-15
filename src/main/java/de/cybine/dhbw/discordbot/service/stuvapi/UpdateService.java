package de.cybine.dhbw.discordbot.service.stuvapi;

import de.cybine.dhbw.discordbot.api.external.StuvAPIRelay;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.*;
import de.cybine.dhbw.discordbot.repoistory.ScheduleSyncRepository;
import de.cybine.dhbw.discordbot.service.stuvapi.event.ScheduleUpdateEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateService
{
    private final StuvApiConfig config;
    private final StuvAPIRelay  stuvAPIRelay;
    private final EventManager  eventManager;

    private final ScheduleSyncRepository scheduleSyncRepository;

    private final SessionFactory sessionFactory;

    @Scheduled(cron = "0 */15 * * * *")
    public void processScheduleUpdates( ) throws IOException, InterruptedException
    {
        log.info("Processing schedule updates");
        try (Session session = this.sessionFactory.openSession())
        {
            Transaction transaction = session.beginTransaction();

            List<UUID> persistentIds = this.scheduleSyncRepository.findAll().stream().map(ScheduleSync::getId).toList();
            List<ScheduleSyncDto> remainingSyncs = this.stuvAPIRelay.fetchSyncs()
                    .stream()
                    .filter(sync -> !persistentIds.contains(sync.getId()))
                    .sorted(Comparator.comparing(ScheduleSyncDto::getStartedAt))
                    .toList();

            log.info("Found {} schedule syncs to process", remainingSyncs.size());
            for (ScheduleSyncDto sync : remainingSyncs)
            {
                session.persist(new ScheduleSync(sync.getId()));
                List<LectureSyncDto> lectureSyncs = this.stuvAPIRelay.fetchSyncDetails(sync.getId())
                        .stream()
                        .filter(data -> this.config.courseName().equals(data.getLecture().getCourse().orElse(null)))
                        .toList();

                log.info("Found {} lecture updates for course {} in schedule sync with id {}",
                        lectureSyncs.size(),
                        this.config.courseName(),
                        sync.getId());

                Map<UpdateType, Collection<LectureDto>> updates = new EnumMap<>(UpdateType.class);
                for (LectureSyncDto syncData : lectureSyncs)
                    updates.computeIfAbsent(syncData.getType(), type -> new ArrayList<>()).add(syncData.getLecture());

                if (updates.values().stream().anyMatch(lectures -> !lectures.isEmpty()))
                    this.eventManager.handle(manager -> new ScheduleUpdateEvent(sync.getId(), updates));
            }

            transaction.commit();
        }
    }
}
