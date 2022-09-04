package de.cybine.dhbw.discordbot.data.schedule;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class LectureDto
{
    private UUID id;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime startsAt;
    private ZonedDateTime endsAt;

    private String name;
    private String course;
    private String lecturer;
    private Type   type;

    private Collection<RoomDto> rooms;

    public Optional<String> getCourse( )
    {
        return Optional.ofNullable(this.course);
    }

    public Optional<String> getLecturer( )
    {
        return Optional.ofNullable(this.lecturer);
    }

    public EmbedCreateSpec.Builder toEmbedBuilder( )
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                .title(this.getName())
                .addField("Beginn", this.getStartsAt().format(formatter), true)
                .addField("Ende", this.getEndsAt().format(formatter), true)
                .timestamp(Instant.now())
                .author("Cybine",
                        null,
                        "https://cdn.discordapp.com/avatars/801905875543392267/13f8dd94bc23e5ad3525addad54345b6.webp")
                .footer("Powered by StuvAPI-Relay", null);

        if (!this.getRooms().isEmpty())
            builder.addField("Räume",
                    this.getRooms().stream().map(RoomDto::getName).collect(Collectors.joining("\n")),
                    false);

        return builder;
    }

    public enum Type
    {
        ONLINE, PRESENCE, HYBRID, INVALID
    }
}