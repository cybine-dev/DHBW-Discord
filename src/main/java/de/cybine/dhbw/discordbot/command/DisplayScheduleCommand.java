package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DisplayScheduleCommand {
    private final GatewayDiscordClient gateway;

    private final EventManager eventManager;

    private final ILectureDao lectureDao;

    public void register() {
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

        this.gateway.on(ChatInputInteractionEvent.class)
                .subscribe(this::chatInputInteraction
                );

    }

    private void chatInputInteraction(ChatInputInteractionEvent chatInputInteractionEvent) {
        List<LectureDto> currentLectures = lectureDao.findByStartDate(chatInputInteractionEvent.getOption("date")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElseThrow());

        StringBuilder lectures = new StringBuilder();
        for (LectureDto lectureDto : currentLectures) {

            lectures.append(lectureToString(lectureDto)).append("\n");
        }

        chatInputInteractionEvent.reply(String.format(lectures.toString()));
    }

    private String lectureToString(LectureDto lectureDto) {
        return " Name: " + lectureDto.getName() + " Start: " + lectureDto.getStartsAt() + " Ende: " + lectureDto.getEndsAt();
    }
}
