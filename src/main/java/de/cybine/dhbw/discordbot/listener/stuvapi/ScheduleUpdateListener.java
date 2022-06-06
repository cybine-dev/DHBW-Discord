package de.cybine.dhbw.discordbot.listener.stuvapi;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.RoomDto;
import de.cybine.dhbw.discordbot.data.schedule.UpdateType;
import de.cybine.dhbw.discordbot.service.stuvapi.event.ScheduleUpdateEvent;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleUpdateListener implements IEventListener
{
    private final GatewayDiscordClient gateway;

    private final BotConfig botConfig;

    @EventHandler
    public void onScheduleUpdate(ScheduleUpdateEvent event)
    {
        this.gateway.getChannelById(this.botConfig.notificationChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format("<@&%s>\nEs gibt neue Änderungen im Vorlesungsplan:",
                                this.botConfig.notificationRoleId().asString()))
                        .embeds(event.getUpdatedLectures()
                                .entrySet()
                                .stream()
                                .map(update -> this.buildUpdateEmbeds(update.getKey(), update.getValue()))
                                .flatMap(List::stream)
                                .toList())
                        .build()).block());
    }

    private List<EmbedCreateSpec> buildUpdateEmbeds(UpdateType type, Collection<LectureDto> lectures)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
        List<EmbedCreateSpec> embeds = new ArrayList<>();
        for (LectureDto lecture : lectures)
        {
            EmbedCreateSpec.Builder builder = this.insertUpdateTypeSpecificEmbedData(EmbedCreateSpec.builder(), type)
                    .title(lecture.getName())
                    .addField("Beginn", lecture.getStartsAt().format(formatter), true)
                    .addField("Ende", lecture.getEndsAt().format(formatter), true)
                    .timestamp(Instant.now())
                    .author("Cybine",
                            null,
                            "https://cdn.discordapp.com/avatars/801905875543392267/13f8dd94bc23e5ad3525addad54345b6.webp")
                    .footer("Powered by StuvAPI", null);

            if (!lecture.getRooms().isEmpty())
                builder.addField("Räume",
                        lecture.getRooms().stream().map(RoomDto::getName).collect(Collectors.joining("\n")),
                        false);

            embeds.add(builder.build());
        }

        return embeds;
    }

    private EmbedCreateSpec.Builder insertUpdateTypeSpecificEmbedData(EmbedCreateSpec.Builder builder, UpdateType type)
    {
        return switch (type)
                {
                    case CREATED -> builder.color(Color.GREEN).addField("Status", "Neu", false);
                    case UPDATED -> builder.color(Color.ORANGE).addField("Status", "Update", false);
                    case DELETED -> builder.color(Color.RED).addField("Status", "Entfernt", false);
                };
    }
}
