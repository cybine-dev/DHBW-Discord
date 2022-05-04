package de.cybine.dhbw.discordbot.listener;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.service.stuvapi.ScheduleService;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.service.stuvapi.event.response.StuvApiLectureResponseEvent;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StuvApiListener implements IEventListener
{
    private final GatewayDiscordClient gateway;

    private final BotConfig       botConfig;
    private final StuvApiConfig   stuvApiConfig;
    private final ScheduleService scheduleService;

    @EventHandler
    public void onGuildCreate(GuildCreateEvent event)
    {
        if (event.getGuild().getId().asLong() == this.botConfig.guildId().asLong())
            this.scheduleService.updateLectures(this.stuvApiConfig.courseName());
    }

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
        if (!event.isCanceled())
            return;

        this.gateway.getChannelById(this.botConfig.testChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format(
                                "No longer collecting schedule information for course %s. Action canceled.",
                                event.getCourse().orElse("ALL")))
                        .build()).block());
    }

    @EventHandler
    public void onScheduleResponse(StuvApiLectureResponseEvent event)
    {
        this.gateway.getChannelById(this.botConfig.testChannelId())
                .subscribe(channel -> ((TextChannel) channel).createMessage(MessageCreateSpec.builder()
                        .content(String.format(
                                "Collected schedule information for course %s. Got %s scheduled lectures.",
                                event.getCourse().orElse("ALL"),
                                event.getLectures().size()))
                        .build()).block());
    }
}
