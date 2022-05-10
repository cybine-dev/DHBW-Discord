package de.cybine.dhbw.discordbot.service.event;

import de.cybine.dhbw.discordbot.util.event.EventHandlerInfo;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import discord4j.core.event.domain.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class DiscordEventHandler
{
    public static Event handle(Event event, Collection<EventHandlerInfo> handlers, EventManager eventManager)
    {
        for (EventHandlerInfo handler : handlers)
        {
            try
            {
                handler.call(event);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return event;
    }
}
