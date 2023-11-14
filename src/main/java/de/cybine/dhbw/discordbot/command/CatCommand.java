package de.cybine.dhbw.discordbot.command;

import de.cybine.dhbw.discordbot.service.discord.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CatCommand {

    private final GatewayDiscordClient gateway;
    private final EventManager eventManager;

    public void register () {
        this.eventManager.handle(manager -> new CommandRegistrationEvent(ApplicationCommandRequest.builder()
                .name("cat")
                .description("It's a cat")
                .type(1)
                .build()));

        this.gateway.on(ChatInputInteractionEvent.class).subscribe(this::onCommand);
    }

    private void onCommand(ChatInputInteractionEvent event){
        if (!event.getCommandName().equals("cat"))
            return;

        event.reply("meow").subscribe();
    }
}
