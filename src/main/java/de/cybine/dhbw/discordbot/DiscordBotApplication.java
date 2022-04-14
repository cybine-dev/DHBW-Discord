package de.cybine.dhbw.discordbot;

import de.cybine.dhbw.discordbot.config.BotConfig;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
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

    private DiscordClient        client;
    private GatewayDiscordClient gateway;

    @PostConstruct
    public void startBot( )
    {
        this.client = DiscordClient.create(this.botConfig.botToken());
        this.gateway = this.client.login().block();

        if (this.gateway != null)
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
