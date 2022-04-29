package de.cybine.dhbw.discordbot.data.schedule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "lecture_room_relations")
public class LectureRoomRelation
{
    @Id
    @Column(name = "lecture_id")
    private long lectureId;

    @Id
    @Column(name = "room_id")
    private long roomId;
}
