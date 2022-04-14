package de.cybine.dhbw.discordbot.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@AllArgsConstructor
@PropertySource("classpath:/application.properties")
public class BotConfig
{
    private final Environment env;

    public String botToken( )
    {
        return this.env.getRequiredProperty("bot.token", String.class);
    }
}
