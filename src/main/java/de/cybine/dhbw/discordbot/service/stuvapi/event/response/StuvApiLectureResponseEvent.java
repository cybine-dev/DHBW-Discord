package de.cybine.dhbw.discordbot.service.stuvapi.event.response;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StuvApiLectureResponseEvent implements IStuvApiResponseEvent
{
    private final boolean includeArchived;
    private final String  course;

    private final LocalDateTime executedAt;

    private final Collection<LectureDto> lectures;

    public Optional<String> getCourse( )
    {
        return Optional.ofNullable(this.course);
    }

    public static StuvApiLectureResponseEvent of(LocalDateTime time, Collection<LectureDto> lectures,
            boolean includeArchived)
    {
        return StuvApiLectureResponseEvent.of(time, lectures, includeArchived, null);
    }

    public static StuvApiLectureResponseEvent of(LocalDateTime time, Collection<LectureDto> lectures,
            boolean includeArchived, String course)
    {
        return new StuvApiLectureResponseEvent(includeArchived, course, time, lectures);
    }
}
