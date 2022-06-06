package de.cybine.dhbw.discordbot.service.stuvapi.event.request;

import de.cybine.dhbw.discordbot.util.event.custom.CloudEventInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@NoArgsConstructor
@CloudEventInfo(name = "stuv-api-lecture-request")
public class StuvApiLectureRequestEvent implements IStuvApiRequestEvent
{
    private boolean includeArchived;
    private String  course;

    @Setter
    private boolean canceled;

    private StuvApiLectureRequestEvent(boolean includeArchived, String course)
    {
        this.includeArchived = includeArchived;
        this.course = course;
    }

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
