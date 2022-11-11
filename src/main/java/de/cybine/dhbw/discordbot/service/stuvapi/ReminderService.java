package de.cybine.dhbw.discordbot.service.stuvapi;

import de.cybine.dhbw.discordbot.api.external.StuvAPIRelay;
import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class ReminderService
{
    private final BotConfig            botConfig;
    private final GatewayDiscordClient gateway;

    private final StuvApiConfig stuvApiConfig;
    private final StuvAPIRelay  stuvAPIRelay;

    @Scheduled(cron = "0 0 12 * * SUN")
    public void postWeeklySchedule( ) throws IOException, InterruptedException
    {
        ZonedDateTime beginOfWeek = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .with(ChronoField.DAY_OF_WEEK, 1)
                .plus(1, ChronoUnit.WEEKS)
                .plus(2, ChronoUnit.HOURS);

        ZonedDateTime endOfWeek = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .with(ChronoField.DAY_OF_WEEK, 5)
                .plus(1, ChronoUnit.WEEKS)
                .minus(2, ChronoUnit.HOURS);

        Collection<LectureDto> lectures = this.stuvAPIRelay.fetchLectures(this.stuvApiConfig.courseName(),
                beginOfWeek,
                endOfWeek);

        if (lectures.isEmpty())
            return;

        Map<LocalDate, Collection<LectureDto>> dailyLectures = new HashMap<>();
        lectures.forEach(lecture -> dailyLectures.computeIfAbsent(lecture.getStartsAt().toLocalDate(),
                key -> new ArrayList<>()).add(lecture));

        this.gateway.getChannelById(this.botConfig.notificationChannelId())
                .subscribe(channel -> dailyLectures.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entries -> ((MessageChannel) channel).createMessage(MessageCreateSpec.builder()
                                .content(String.format("**%s**",
                                        entries.getKey()
                                                .getDayOfWeek()
                                                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMAN)))
                                .embeds(entries.getValue().stream().map(this::getLectureEmbed).toList())
                                .build()).block()));
    }

    private EmbedCreateSpec getLectureEmbed(LectureDto lecture)
    {
        EmbedCreateSpec.Builder builder = lecture.toEmbedBuilder();
        switch (lecture.getType())
        {
            case ONLINE -> builder.color(Color.BLUE);
            case HYBRID -> builder.color(Color.DEEP_LILAC);
            default -> builder.color(Color.ORANGE);
        }

        return builder.build();
    }
}
