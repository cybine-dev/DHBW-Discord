package de.cybine.dhbw.discordbot.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@AllArgsConstructor
@PropertySource("classpath:/application.properties")
public class StuvApiConfig
{
    private final Environment env;

    public String stuvApiUrl()
    {
        return this.env.getRequiredProperty("api.stuv.url", String.class);
    }

    public String courseName( )
    {
        return this.env.getRequiredProperty("api.stuv.course", String.class);
    }
}
