package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.RoomDto;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import lombok.AllArgsConstructor;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DisplayScheduleCommand
{
    private final GatewayDiscordClient gateway;
    private final EventManager         eventManager;

    private final ILectureDao lectureDao;

    public void register( )
    {
        this.eventManager.handle(manager -> new CommandRegistrationEvent(ApplicationCommandRequest.builder()
                .name("schedule")
                .description("Displays schedule for one day")
                .type(1)
                .addOption(ApplicationCommandOptionData.builder()
                        .name("date")
                        .description("Date you want the plan from")
                        .type(3)
                        .required(true)
                        .build())
                .build()));

        this.gateway.on(ChatInputInteractionEvent.class).subscribe(this::onCommand);
    }

    private void onCommand(ChatInputInteractionEvent event)
    {
        if (!event.getCommandName().equals("schedule"))
            return;

        try
        {
            String date = event.getOption("date")
                    .flatMap(ApplicationCommandInteractionOption::getValue)
                    .map(ApplicationCommandInteractionOptionValue::asString)
                    .orElseThrow();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate fromDate = LocalDate.parse(date, formatter);
            LocalDate toDate = fromDate.plus(1, ChronoUnit.DAYS);

            List<LectureDto> lectures = this.lectureDao.findByStartDate(fromDate.atStartOfDay(), toDate.atStartOfDay());
            if (lectures.isEmpty())
            {
                event.reply(String.format("Es wurden keine Vorlesungen für den %s gefunden.", date)).subscribe();
                return;
            }

            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .embeds(lectures.stream().map(this::lectureToString).toList())
                    .build()).subscribe();
        }
        catch (NoSuchElementException exception)
        {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .content("Invalid parameters. Parameter date missing!")
                    .ephemeral(true)
                    .build()).subscribe();
        }
        catch (DateTimeException exception)
        {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .content("Invalid date supplied. Required format: dd.MM.yyyy")
                    .ephemeral(true)
                    .build()).subscribe();
        }
    }

    private EmbedCreateSpec lectureToString(LectureDto lectureDto)
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
