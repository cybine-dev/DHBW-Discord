package de.cybine.dhbw.discordbot;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.service.stuvapi.ScheduleService;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@SpringBootApplication
@RequiredArgsConstructor
public class DiscordBotApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DiscordBotApplication.class, args);
    }

    @NonNull
    private final BotConfig botConfig;

    @NonNull
    private final EventManager eventManager;

    @NonNull
    private final ScheduleService scheduleService;

    private DiscordClient        client;
    private GatewayDiscordClient gateway;

    @PostConstruct
    private void startBot( )
    {
        this.client = DiscordClient.create(this.botConfig.botToken());
        this.gateway = this.client.login().block();

        if (this.gateway == null)
            return;

        this.gateway.on(Event.class).subscribe(event -> this.eventManager.handle(manager -> event));
        this.gateway.onDisconnect().block();
    }

    @Bean
    public DiscordClient getClient( )
    {
        return client;
    }

    @Bean
    public GatewayDiscordClient getGateway( )
    {
        return gateway;
    }
}
