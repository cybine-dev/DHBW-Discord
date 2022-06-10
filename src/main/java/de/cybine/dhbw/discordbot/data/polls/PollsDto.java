package de.cybine.dhbw.discordbot.data.polls;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "polls")
@Builder(toBuilder = true)
public class PollsDto {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "answer_yes", nullable = false)
    private int answerYes;
    @Column(name = "answer_no", nullable = false)
    private int answerNo;
    @Column(name = "answer_maybe")
    private int answerMaybe;
    @Column(name = "answer_abstain")
    private int answerAbstain;
    @Column(name = "answer_total", nullable = false)
    private int answerTotal;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
