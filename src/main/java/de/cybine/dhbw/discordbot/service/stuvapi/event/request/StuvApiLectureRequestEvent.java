package de.cybine.dhbw.discordbot.service.stuvapi.event.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StuvApiLectureRequestEvent implements IStuvApiRequestEvent
{
    private final boolean includeArchived;
    private final String  course;

    @Setter
    private boolean canceled;

    public Optional<String> getCourse( )
    {
        return Optional.ofNullable(this.course);
    }

    public static StuvApiLectureRequestEvent of(boolean includeArchived)
    {
        return StuvApiLectureRequestEvent.of(includeArchived, null);
    }

    public static StuvApiLectureRequestEvent of(boolean includeArchived, String course)
    {
        return new StuvApiLectureRequestEvent(includeArchived, course);
    }
}
