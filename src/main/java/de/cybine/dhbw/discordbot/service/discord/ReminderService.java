package de.cybine.dhbw.discordbot.service.discord;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.RoomDto;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class ReminderService
{
    private final BotConfig            botConfig;
    private final GatewayDiscordClient gateway;

    private final ILectureDao lectureRepository;

    public Collection<LectureDto> postWeeklySchedule( )
    {
        LocalDate date = LocalDate.now().plus(1, ChronoUnit.WEEKS);
        LocalDateTime beginOfWeek = date.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = date.with(DayOfWeek.SUNDAY).plus(1, ChronoUnit.DAYS).atStartOfDay();

        Collection<LectureDto> lectures = this.lectureRepository.findByStartDate(beginOfWeek, endOfWeek);
        if (lectures.isEmpty())
            return lectures;

        Map<LocalDate, Collection<LectureDto>> dailyLectures = new HashMap<>();
        for (LectureDto lecture : lectures)
        {
            LocalDate day = lecture.getStartsAt().toLocalDate();
            if (!dailyLectures.containsKey(day))
                dailyLectures.put(day, new ArrayList<>());

            dailyLectures.get(day).add(lecture);
        }

        this.gateway.getChannelById(this.botConfig.notificationChannelId())
                .subscribe(channel -> dailyLectures.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entries -> ((MessageChannel) channel).createMessage(MessageCreateSpec.builder()
                                .content(String.format("**%s**",
                                        entries.getKey()
                                                .getDayOfWeek()
                                                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMAN)))
                                .embeds(this.lecturesToEmbeds(entries.getValue()))
                                .build()).block()));

        return lectures;
    }

    private Collection<EmbedCreateSpec> lecturesToEmbeds(Collection<LectureDto> lectures)
    {
        return lectures.stream().map(this::lectureToEmbed).toList();
    }

    private EmbedCreateSpec lectureToEmbed(LectureDto lectureDto)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                .title(lectureDto.getName())
                .addField("Beginn", lectureDto.getStartsAt().format(formatter), true)
                .addField("Ende", lectureDto.getEndsAt().format(formatter), true)
                .timestamp(Instant.now())
                .author("Cybine",
                        null,
                        "https://cdn.discordapp.com/avatars/801905875543392267/13f8dd94bc23e5ad3525addad54345b6.webp")
                .footer("Powered by StuvAPI", null);

        switch (lectureDto.getType())
        {
            case ONLINE -> builder.color(Color.BLUE);
            case HYBRID -> builder.color(Color.DEEP_LILAC);
            default -> builder.color(Color.ORANGE);
        }

        if (!lectureDto.getRooms().isEmpty())
            builder.addField("Räume",
                    lectureDto.getRooms().stream().map(RoomDto::getName).collect(Collectors.joining("\n")),
                    false);

        return builder.build();
    }
}
