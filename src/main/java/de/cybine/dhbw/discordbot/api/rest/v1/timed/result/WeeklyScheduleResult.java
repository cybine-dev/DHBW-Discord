package de.cybine.dhbw.discordbot.api.rest.v1.timed.result;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;

import java.util.Collection;

public record WeeklyScheduleResult(Collection<LectureDto> lectures)
{ }
