package de.cybine.dhbw.discordbot.service.stuvapi.event.response;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Getter
@NoArgsConstructor
@CloudEventInfo(name = "stuv-api-lecture-response")
public class StuvApiLectureResponseEvent implements IStuvApiResponseEvent
{
    private boolean includeArchived;
    private String  course;

    private LocalDateTime executedAt;

    private Collection<LectureDto> lectures;

    private StuvApiLectureResponseEvent(boolean includeArchived, String course, LocalDateTime executedAt,
            Collection<LectureDto> lectures)
    {
        this.includeArchived = includeArchived;
        this.course = course;
        this.executedAt = executedAt;
        this.lectures = lectures;
    }

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
