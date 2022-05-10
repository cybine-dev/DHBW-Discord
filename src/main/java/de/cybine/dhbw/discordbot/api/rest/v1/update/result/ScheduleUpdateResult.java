package de.cybine.dhbw.discordbot.api.rest.v1.update.result;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.UpdateType;

import java.util.Collection;
import java.util.Map;

public record ScheduleUpdateResult(Map<UpdateType, Collection<LectureDto>> update)
{ }
