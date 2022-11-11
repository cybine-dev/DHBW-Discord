package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.api.external.StuvAPIRelay;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.service.discord.event.CommandRegistrationEvent;
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

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
public class DisplayScheduleCommand
{
    private final GatewayDiscordClient gateway;
    private final EventManager         eventManager;

    private final StuvApiConfig config;
    private final StuvAPIRelay  stuvAPIRelay;

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
            ZonedDateTime fromDate = LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime untilDate = fromDate.plus(1, ChronoUnit.DAYS);

            List<LectureDto> lectures = this.stuvAPIRelay.fetchLectures(this.config.courseName(),
                    fromDate.plus(2, ChronoUnit.HOURS),
                    untilDate.minus(2, ChronoUnit.HOURS));
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
        catch (IOException exception)
        {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .content("An error occurred while fetching data.")
                    .ephemeral(true)
                    .build()).subscribe();
        }
        catch (InterruptedException exception)
        {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .content("An error occurred while fetching data.")
                    .ephemeral(true)
                    .build()).subscribe();

            Thread.currentThread().interrupt();
        }
    }

    private EmbedCreateSpec lectureToString(LectureDto lectureDto)
    {
        EmbedCreateSpec.Builder builder = lectureDto.toEmbedBuilder();
        switch (lectureDto.getType())
        {
            case ONLINE -> builder.color(Color.BLUE);
            case HYBRID -> builder.color(Color.DEEP_LILAC);
            default -> builder.color(Color.ORANGE);
        }

        return builder.build();
    }
}
