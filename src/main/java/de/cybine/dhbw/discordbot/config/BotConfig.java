package de.cybine.dhbw.discordbot.config;

import discord4j.common.util.Snowflake;
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

    public Snowflake guildId( )
    {
        return Snowflake.of(this.env.getRequiredProperty("bot.guild", Long.class));
    }

    public Snowflake testChannelId( )
    {
        return Snowflake.of(this.env.getRequiredProperty("bot.channel.test", Long.class));
    }

    public Snowflake notificationChannelId( )
    {
        return Snowflake.of(this.env.getRequiredProperty("bot.channel.notification", Long.class));
    }

    public Snowflake notificationRoleId( )
    {
        return Snowflake.of(this.env.getRequiredProperty("bot.role.notification", Long.class));
    }
}
