package de.cybine.dhbw.discordbot.listener;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.Connection;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordListener implements IEventListener
{
    private final Connection           connection;
    private final CloudEventMapper     eventMapper;

    private final BotConfig     botConfig;
    private final StuvApiConfig stuvApiConfig;

    @EventHandler
    public void onGuildCreate(GuildCreateEvent event)
    {
        if (event.getGuild().getId().asLong() == this.botConfig.guildId().asLong())
            this.publishEvent(StuvApiLectureRequestEvent.of(false, this.stuvApiConfig.courseName()));
    }

    private void publishEvent(Object event)
    {
        this.eventMapper.toCloudEvent(event)
                .ifPresent(eventData -> this.connection.publish(NatsMessage.builder()
                        .subject(this.botConfig.getNatsChannel())
                        .data(EventFormatProvider.getInstance()
                                .resolveFormat(JsonFormat.CONTENT_TYPE)
                                .serialize(eventData))
                        .build()));
    }
}
