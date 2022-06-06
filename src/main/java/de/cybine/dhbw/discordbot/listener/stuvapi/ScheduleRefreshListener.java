package de.cybine.dhbw.discordbot.listener.stuvapi;

import de.cybine.dhbw.discordbot.api.external.StuvAPI;
import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.service.stuvapi.event.response.StuvApiLectureResponseEvent;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.Connection;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleRefreshListener implements IEventListener
{
    private final StuvAPI              stuvAPI;
    private final Connection           connection;
    private final CloudEventMapper     eventMapper;
    private final GatewayDiscordClient gateway;

    private final BotConfig     botConfig;

    private final ILectureDao lectureRepository;

    @EventHandler
    public void onScheduleRequest(StuvApiLectureRequestEvent event)
    {
        this.gateway.getChannelById(this.botConfig.testChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format("Collecting schedule information for course %s.",
                                event.getCourse().orElse("ALL")))
                        .build()).block());
    }

    @EventHandler(priority = 10000)
    public void onScheduleRequestCancel(StuvApiLectureRequestEvent event)
    {
        if (event.isCanceled())
        {
            this.gateway.getChannelById(this.botConfig.testChannelId())
                    .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                            .content(String.format(
                                    "No longer collecting schedule information for course %s. Action canceled.",
                                    event.getCourse().orElse("ALL")))
                            .build()).block());

            return;
        }

        this.publishEvent(StuvApiLectureResponseEvent.of(LocalDateTime.now(),
                event.getCourse()
                        .map(courseName -> this.stuvAPI.getLectures(event.isIncludeArchived(), courseName))
                        .orElseGet(( ) -> this.stuvAPI.getLectures(event.isIncludeArchived())),
                event.isIncludeArchived(),
                event.getCourse().orElse(null)));
    }

    @EventHandler
    public void onScheduleResponse(StuvApiLectureResponseEvent event)
    {
        this.lectureRepository.saveAll(event.getLectures());
        this.gateway.getChannelById(this.botConfig.testChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format(
                                "Collected schedule information for course %s. Got %s scheduled lectures.",
                                event.getCourse().orElse("ALL"),
                                event.getLectures().size()))
                        .build()).block());
    }

    private void publishEvent(Object event)
    {
        this.eventMapper.toCloudEvent(event)
                .ifPresent(eventData -> this.connection.publish(NatsMessage.builder()
                        .subject("bot")
                        .data(EventFormatProvider.getInstance()
                                .resolveFormat(JsonFormat.CONTENT_TYPE)
                                .serialize(eventData))
                        .build()));
    }
}
