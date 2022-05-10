package de.cybine.dhbw.discordbot.service.event;

import de.cybine.dhbw.discordbot.listener.StuvApiListener;
import de.cybine.dhbw.discordbot.util.event.EventGroup;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import de.cybine.dhbw.discordbot.util.event.IEvent;
import discord4j.core.event.domain.Event;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class EventManagement
{
    @Getter
    private final EventManager         eventManager;

    private final StuvApiListener stuvApiListener;

    public EventManagement(final EventManager eventManager, final StuvApiListener stuvApiListener)
    {
        this.eventManager = eventManager;
        this.stuvApiListener = stuvApiListener;

        this.setup();
    }

    private void setup( )
    {
        this.registerGroups();
        this.registerHandlers();
    }

    private void registerGroups( )
    {
        this.eventManager.registerEventGroup(eventManager -> new EventGroup<>(IEvent.class, CustomEventHandler::handle));
        this.eventManager.registerEventGroup(eventManager -> new EventGroup<>(Event.class, DiscordEventHandler::handle));
    }

    private void registerHandlers( )
    {
        this.eventManager.registerHandlers(eventManager -> this.stuvApiListener);
    }
}
