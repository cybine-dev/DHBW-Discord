package de.cybine.dhbw.discordbot.data.schedule;

import javax.persistence.*;

@Entity
@Table(name = "lecture_room_relations")
public class LectureRoomRelation
{
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "lecture_id")
    private long lectureId;

    @Column(name = "room_name")
    private long roomName;
}
