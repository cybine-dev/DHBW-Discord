package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.service.stuvapi.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EchoCommand
{
    private final GatewayDiscordClient gateway;
    private final EventManager         eventManager;

    public void register( )
    {
        this.eventManager.handle(manager -> new CommandRegistrationEvent(ApplicationCommandRequest.builder()
                .name("echo")
                .description("Repeats your message")
                .type(1)
                .addOption(ApplicationCommandOptionData.builder()
                        .name("message")
                        .description("Your message")
                        .type(3)
                        .required(true)
                        .build())
                .build()));

        this.gateway.on(ChatInputInteractionEvent.class).subscribe(this::onCommand);
    }

    private void onCommand(ChatInputInteractionEvent event)
    {
        if (!event.getCommandName().equals("echo"))
            return;

        event.reply(String.format("%s wrote: %s",
                event.getInteraction().getUser().getMention(),
                event.getOption("message")
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .orElse("no message"))).withEphemeral(true).block();
    }
}
