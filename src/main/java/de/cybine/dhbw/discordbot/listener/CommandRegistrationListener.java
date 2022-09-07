package de.cybine.dhbw.discordbot.listener;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.service.discord.event.CommandRegistrationEvent;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import discord4j.core.DiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandRegistrationListener implements IEventListener
{
    private final Long applicationId;

    private final BotConfig     config;
    private final DiscordClient client;

    public CommandRegistrationListener(@Qualifier("applicationId") Long applicationId, BotConfig config,
            DiscordClient client)
    {
        this.applicationId = applicationId;
        this.config = config;
        this.client = client;
    }

    @EventHandler
    public void onCommandRegistration(CommandRegistrationEvent event)
    {
        new Thread(( ) ->
        {
            List<ApplicationCommandData> commandData = this.client.getApplicationService()
                    .getGlobalApplicationCommands(this.applicationId)
                    .cache()
                    .collectList()
                    .block();

            if (commandData != null && commandData.stream()
                    .anyMatch(data -> data.name().equals(event.request().name())))
                return;

            this.client.getApplicationService()
                    .createGuildApplicationCommand(this.applicationId, this.config.guildId().asLong(), event.request())
                    .subscribe();
        }).start();
    }
}
