package de.cybine.dhbw.discordbot.service.stuvapi.event;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.UpdateType;
import de.cybine.dhbw.discordbot.util.event.IEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public record ScheduleUpdateEvent(UUID syncId, Map<UpdateType, Collection<LectureDto>> updatedLectures)
        implements IEvent
{ }
