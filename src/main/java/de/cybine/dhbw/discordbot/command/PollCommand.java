package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.data.polls.PollsDto;
import de.cybine.dhbw.discordbot.data.schedule.ScheduleSyncDto;
import de.cybine.dhbw.discordbot.repository.stuvapi.IPollsDao;
import de.cybine.dhbw.discordbot.service.stuvapi.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PollCommand
{
    private final GatewayDiscordClient gateway;
    private final EventManager         eventManager;
    private final IPollsDao pollsDao;

    public void register( )
    {
        this.eventManager.handle(manager -> new CommandRegistrationEvent(ApplicationCommandRequest.builder()
                .name("poll")
                .description("Makes a poll")
                .type(1)
                .addOption(ApplicationCommandOptionData.builder()
                        .name("message")
                        .description("Make an anonymous poll")
                        .type(3)
                        .required(true)
                        .build())
                .build()));

        this.gateway.on(ChatInputInteractionEvent.class).subscribe(this::onCommand);
    }

    private void onCommand(ChatInputInteractionEvent event)
    {
        if (!event.getCommandName().equals("poll"))
            return;

        event.reply(String.format("%s",
                        event.getOption("message")
                                .flatMap(ApplicationCommandInteractionOption::getValue)
                                .map(ApplicationCommandInteractionOptionValue::asString)
                                .orElse("no message")))
                .withComponents(ActionRow.of(
                        Button.primary("yes", "Yay"),
                        Button.danger("No", "Nay")))
                .subscribe();

        this.gateway.on(ButtonInteractionEvent.class).subscribe(this::onReaction);
    }

    private void onReaction(ButtonInteractionEvent event)
    {
        PollsDto pollsDto = new PollsDto( );


        //Makes changes to database
        PollsDto.PollsDtoBuilder builder = PollsDto.builder();
        builder.id(pollsDto.getId()+1);
        builder.name(String.valueOf(event.getMessage()));
        builder.createdAt(LocalDateTime.from(Instant.now()));
        builder.startedAt(LocalDateTime.from(Instant.now()));
//        builder.endsAt(LocalDateTime.from(Instant.now()));
        if(event.getCustomId().equals("No")){
            builder.answerNo(pollsDto.getAnswerNo()+1);
        } else if(event.getCustomId().equals("Yes")) {
            builder.answerYes(pollsDto.getAnswerYes() + 1);
        } else if(event.getCustomId().equals("Maybe")){
            builder.answerMaybe(pollsDto.getAnswerMaybe()+1);
        } else if(event.getCustomId().equals("Abstain")){
            builder.answerAbstain(pollsDto.getAnswerAbstain()+1);
        }
        builder.isActive(true);

//        List<PollsDto> polls = this.pollsDao.findByName(String.valueOf(event.getMessage()));
        List<PollsDto> polls = new ArrayList<>();

        polls.add(builder.build());
        event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .embeds(Collections.singleton(createEmbed(polls.get(0))))
                .build().withEphemeral(false)).block();
    }


    private EmbedCreateSpec createEmbed(PollsDto polls)
    {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                .title(polls.getName())
                .addField("Voted for", String.valueOf(polls.getAnswerYes()), true)
                .addField("Voted against", String.valueOf(polls.getAnswerNo()), true)
                .addField("Total voter", String.valueOf(polls.getAnswerTotal()), true)
                .timestamp(Instant.now())
                .author("Cybine",
                        null,
                        "https://cdn.discordapp.com/avatars/801905875543392267/13f8dd94bc23e5ad3525addad54345b6.webp");
        return builder.build();
    }
}