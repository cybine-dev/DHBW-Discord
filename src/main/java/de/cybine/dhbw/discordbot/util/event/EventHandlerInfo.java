package de.cybine.dhbw.discordbot.util.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public record EventHandlerInfo(IEventListener listener, Method method)
{
    public EventHandlerInfo
    {
        if (method.getParameterCount() != 1)
            throw new IllegalArgumentException("Method must have exactly 1 parameter!");

        if (!method.isAnnotationPresent(EventHandler.class))
            throw new IllegalArgumentException("Method must be annotated with EventHandler!");
    }

    public String name( )
    {
        return String.format(this.info().name(),
                this.listener().getClass().getSimpleName(),
                this.method().getName(),
                this.type().getSimpleName());
    }

    public Class<?> type( )
    {
        return this.method().getParameterTypes()[ 0 ];
    }

    public Integer priority( )
    {
        return this.info().priority();
    }

    public boolean matchExact( )
    {
        return this.info().matchExact();
    }

    public void call(Object event) throws InvocationTargetException, IllegalAccessException
    {
        if (event == null)
            throw new IllegalArgumentException(String.format(
                    "Failed calling event handler for %s (handler %s): The provided event must not be null!",
                    this.type().getSimpleName(),
                    this.name()));

        this.method().invoke(this.listener(), event);
    }

    public boolean isHandled(Object event)
    {
        if (event == null)
            return false;

        Class<?> clazz = event.getClass();
        if (this.matchExact())
            return this.type() == clazz;

        return this.type().isAssignableFrom(clazz);
    }

    private EventHandler info( )
    {
        return this.method.getAnnotation(EventHandler.class);
    }

    public static class PriorityComparator implements Comparator<EventHandlerInfo>
    {
        @Override
        public int compare(EventHandlerInfo element, EventHandlerInfo other)
        {
            return element.priority().compareTo(other.priority());
        }
    }
}
