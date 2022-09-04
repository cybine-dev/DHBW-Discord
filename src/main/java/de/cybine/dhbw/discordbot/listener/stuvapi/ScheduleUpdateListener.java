package de.cybine.dhbw.discordbot.listener.stuvapi;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ScheduleUpdateListener implements IEventListener
{
    private final GatewayDiscordClient gateway;

    private final BotConfig botConfig;

    @EventHandler
    public void onScheduleUpdate(ScheduleUpdateEvent event)
    {
        log.info("Processing schedule sync with id {}", event.syncId());
        this.gateway.getChannelById(this.botConfig.notificationChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format("<@&%s>%nEs gibt neue Änderungen im Vorlesungsplan:",
                                this.botConfig.notificationRoleId().asString()))
                        .embeds(event.updatedLectures()
                                .entrySet()
                                .stream()
                                .map(update -> this.buildUpdateEmbeds(update.getKey(), update.getValue()))
                                .flatMap(List::stream)
                                .toList())
                        .build()).block());
    }

    private List<EmbedCreateSpec> buildUpdateEmbeds(UpdateType type, Collection<LectureDto> lectures)
    {
        return lectures.stream()
                .map(lecture -> this.insertUpdateTypeSpecificEmbedData(lecture.toEmbedBuilder(), type).build())
                .toList();
    }

    private EmbedCreateSpec.Builder insertUpdateTypeSpecificEmbedData(EmbedCreateSpec.Builder builder, UpdateType type)
    {
        String statusFieldName = "Status";
        return switch (type)
                {
                    case CREATED -> builder.color(Color.GREEN).addField(statusFieldName, "Neu", false);
                    case UPDATED -> builder.color(Color.ORANGE).addField(statusFieldName, "Update", false);
                    case DELETED -> builder.color(Color.RED).addField(statusFieldName, "Entfernt", false);
                };
    }
}
