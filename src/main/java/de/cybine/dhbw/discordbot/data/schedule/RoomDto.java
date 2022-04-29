package de.cybine.dhbw.discordbot.data.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "rooms", indexes = @Index(name = "room_index_name", columnList = "name"))
public class RoomDto
{
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
