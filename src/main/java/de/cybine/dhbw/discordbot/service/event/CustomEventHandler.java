package de.cybine.dhbw.discordbot.service.event;

import de.cybine.dhbw.discordbot.util.event.EventHandlerInfo;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import de.cybine.dhbw.discordbot.util.event.ICancelableEvent;
import de.cybine.dhbw.discordbot.util.event.IEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class CustomEventHandler
{
    public static IEvent handle(IEvent event, Collection<EventHandlerInfo> handlers, EventManager eventManager)
    {
        for (EventHandlerInfo handler : handlers)
        {
            try
            {
                if (event instanceof ICancelableEvent && ((ICancelableEvent) event).isCanceled() && !handler.ignoreCanceled())
                    continue;

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
