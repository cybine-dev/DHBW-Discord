package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureDto
{
    private long id;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdAt;
    @Column(name = "started_at", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "course")
    private String course;
    private String lecturer;
    @Column(name = "type", nullable = false)
    private Type   type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "lecture_rooms",
               joinColumns = { @JoinColumn(name = "lecture_id") },
               inverseJoinColumns = { @JoinColumn(name = "room_id") })
    private Collection<RoomDto> rooms;

    public enum Type
    {
        ONLINE, PRESENCE, HYBRID, INVALID
    }
}