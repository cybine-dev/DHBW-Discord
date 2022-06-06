package de.cybine.dhbw.discordbot.service.event;

import de.cybine.dhbw.discordbot.util.event.EventHandlerInfo;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import io.cloudevents.core.v1.CloudEventV1;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class CloudEventHandler
{
    public static CloudEventV1 handle(CloudEventV1 event, Collection<EventHandlerInfo> handlers,
            EventManager eventManager)
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
