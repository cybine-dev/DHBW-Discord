package de.cybine.dhbw.discordbot;

import de.cybine.dhbw.discordbot.command.DisplayScheduleCommand;
import de.cybine.dhbw.discordbot.command.EchoCommand;
import de.cybine.dhbw.discordbot.repository.stuvapi.ILectureDao;
import de.cybine.dhbw.discordbot.service.event.EventManagement;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = { "de.cybine.dhbw.discordbot" })
public class DiscordBotApplication
{
    public static void main(String[] args)
    {
        ConfigurableApplicationContext context = SpringApplication.run(DiscordBotApplication.class, args);
        context.getBean(GatewayDiscordClient.class).onDisconnect().block();
    }

    private final EventManagement eventManagement;

    private final GatewayDiscordClient gateway;

    private final ILectureDao lectureDao;

    @PostConstruct
    private void startBot( )
    {
        if (this.gateway == null)
            return;

        this.gateway.on(Event.class)
                .subscribe(event -> this.eventManagement.getEventManager().handle(manager -> event));

        new EchoCommand(this.gateway, this.eventManagement.getEventManager()).register();
        new DisplayScheduleCommand(this.gateway, this.eventManagement.getEventManager(), lectureDao).register();
    }
}
