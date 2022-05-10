package de.cybine.dhbw.discordbot.api.rest.v1.update;

import de.cybine.dhbw.discordbot.api.rest.v1.update.result.ScheduleUpdateResult;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.UpdateType;
import de.cybine.dhbw.discordbot.service.stuvapi.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ScheduleUpdateEndpointController implements IScheduleUpdateEndpointController
{
    private final ScheduleService scheduleService;

    @Override
    public ResponseEntity<ScheduleUpdateResult> updateLectures( )
    {
        Map<UpdateType, Collection<LectureDto>> updatedLectures = this.scheduleService.applyLectureUpdates();
        if (updatedLectures.values().stream().allMatch(Collection::isEmpty))
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        return new ResponseEntity<>(new ScheduleUpdateResult(updatedLectures), HttpStatus.OK);
    }
}
