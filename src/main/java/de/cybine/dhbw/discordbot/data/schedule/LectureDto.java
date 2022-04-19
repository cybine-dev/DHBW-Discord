package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureDto
{
    private long id;

    private LocalDateTime createdAt;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    private String name;
    private String course;
    private String lecturer;
    private Type   type;

    private Collection<String> rooms;

    public enum Type
    {
        ONLINE, PRESENCE, HYBRID, INVALID
    }
}