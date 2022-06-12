package de.cybine.dhbw.discordbot.api.rest.v1.timed;

import de.cybine.dhbw.discordbot.api.rest.v1.timed.result.WeeklyScheduleResult;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.service.discord.ReminderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@AllArgsConstructor
public class WeeklyScheduleEndpointController implements IWeeklyScheduleEndpointController
{
    private final ReminderService reminderService;

    @Override
    public ResponseEntity<WeeklyScheduleResult> postWeeklySchedule( )
    {
        Collection<LectureDto> lectures = this.reminderService.postWeeklySchedule();

        return new ResponseEntity<>(new WeeklyScheduleResult(lectures), HttpStatus.OK);
    }
}
