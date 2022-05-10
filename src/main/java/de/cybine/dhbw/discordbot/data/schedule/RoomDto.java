package de.cybine.dhbw.discordbot.data.schedule;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoomDto
{
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "room")
    private String room;

    @Column(name = "floor")
    private int floor;
}
