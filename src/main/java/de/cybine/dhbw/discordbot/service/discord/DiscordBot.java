package de.cybine.dhbw.discordbot.service.discord;

import de.cybine.dhbw.discordbot.config.BotConfig;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DiscordBot
{
    private final BotConfig     botConfig;

    private       DiscordClient client;
    private GatewayDiscordClient gateway;

    @PostConstruct
    private void setup()
    {
        this.client = DiscordClient.create(this.botConfig.botToken());
        this.gateway = this.client.login().block();
    }

    @Bean
    public DiscordClient getClient( )
    {
        return this.client;
    }

    @Bean
    public GatewayDiscordClient getGateway( )
    {
        return this.gateway;
    }
}
