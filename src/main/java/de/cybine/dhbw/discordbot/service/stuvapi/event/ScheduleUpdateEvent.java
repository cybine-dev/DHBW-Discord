package de.cybine.dhbw.discordbot.service.stuvapi.event;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.UpdateType;
import de.cybine.dhbw.discordbot.util.event.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ScheduleUpdateEvent implements IEvent
{
    private final Map<UpdateType, Collection<LectureDto>> updatedLectures;
}
