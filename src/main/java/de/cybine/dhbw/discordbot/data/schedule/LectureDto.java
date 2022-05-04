package de.cybine.dhbw.discordbot.data.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lectures")
@Builder(toBuilder = true)
public class LectureDto
{
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "course")
    private String course;
    @Column(name = "lecturer")
    private String lecturer;
    @Column(name = "type", nullable = false)
    private Type   type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "lecture_rooms",
               joinColumns = { @JoinColumn(name = "lecture_id") },
               inverseJoinColumns = { @JoinColumn(name = "room_name") })
    private Collection<RoomDto> rooms;

    public enum Type
    {
        ONLINE, PRESENCE, HYBRID, INVALID
    }
}