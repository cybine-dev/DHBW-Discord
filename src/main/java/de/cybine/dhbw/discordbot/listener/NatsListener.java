package de.cybine.dhbw.discordbot.listener;

import de.cybine.dhbw.discordbot.config.BotConfig;
import de.cybine.dhbw.discordbot.util.event.EventHandler;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
@RequiredArgsConstructor
public class NatsListener implements IEventListener
{
    private final BotConfig botConfig;

    private final EventManager     eventManager;
    private final CloudEventMapper eventMapper;

    private final Connection connection;

    @PostConstruct
    private void setup( )
    {
        this.connection.createDispatcher(message -> this.eventManager.handle(manager -> EventFormatProvider.getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE)
                .deserialize(message.getData()))).subscribe(this.botConfig.getNatsChannel());
    }

    @SneakyThrows
    @EventHandler
    public void onCloudEvent(CloudEvent cloudEvent)
    {
        this.eventMapper.toEvent(cloudEvent).ifPresent(event -> this.eventManager.handle(manager -> event));
    }
}
