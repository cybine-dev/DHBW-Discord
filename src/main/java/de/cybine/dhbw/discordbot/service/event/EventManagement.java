package de.cybine.dhbw.discordbot.service.event;

import de.cybine.dhbw.discordbot.listener.CommandRegistrationListener;
import de.cybine.dhbw.discordbot.listener.NatsListener;
import de.cybine.dhbw.discordbot.listener.ScheduleUpdateListener;
import de.cybine.dhbw.discordbot.util.event.EventGroup;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import de.cybine.dhbw.discordbot.util.event.IEvent;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import discord4j.core.event.domain.Event;
import io.cloudevents.core.v1.CloudEventV1;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@Component
public class EventManagement
{
    @Getter
    private final EventManager eventManager;

    private final Collection<IEventListener> listeners;

    public EventManagement(EventManager eventManager, CommandRegistrationListener commandRegistrationListener,
            ScheduleUpdateListener scheduleUpdateListener, NatsListener natsListener)
    {
        this.eventManager = eventManager;
        this.listeners = new ArrayList<>();
        this.listeners.add(commandRegistrationListener);
        this.listeners.add(scheduleUpdateListener);
        this.listeners.add(natsListener);

        this.setup();
    }

    private void setup( )
    {
        this.registerGroups();
        this.registerHandlers();
    }

    private void registerGroups( )
    {
        this.eventManager.registerEventGroup(manager -> new EventGroup<>(IEvent.class, CustomEventHandler::handle));
        this.eventManager.registerEventGroup(manager -> new EventGroup<>(Event.class, DiscordEventHandler::handle));
        this.eventManager.registerEventGroup(manager -> new EventGroup<>(CloudEventV1.class,
                CloudEventHandler::handle));
    }

    private void registerHandlers( )
    {
        this.listeners.forEach(listener -> this.eventManager.registerHandlers(manager -> listener));
    }
}
