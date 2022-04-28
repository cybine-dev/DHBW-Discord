package de.cybine.dhbw.discordbot.util.event;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Event grouping to define default handling behavior.
 *
 * @param superclass superclass of handled events
 * @param executor   default handling logic
 * @param <T>        handled type
 */
public record EventGroup<T>(Class<T> superclass, Executor<T> executor)
{
    public EventGroup
    {
        if (superclass == null)
            throw new IllegalArgumentException("Handled superclass must not be null!");

        if (executor == null)
            throw new IllegalArgumentException("Executor must not be null!");
    }

    /**
     * @param event event to check if handled
     *
     * @return true if event may be handled
     */
    public boolean isHandled(Object event)
    {
        if (event == null)
            return false;

        return this.superclass().isAssignableFrom(event.getClass());
    }

    /**
     * @param event   event to handle
     * @param manager event manager that initiated the process
     *
     * @return processed event
     */
    public T handle(T event, EventManager manager)
    {
        if (event == null)
            throw new IllegalArgumentException(String.format(
                    "Failed handling event for event group (superclass: %s): The provided event must not be null!",
                    this.superclass().getSimpleName()));

        if (manager == null)
            throw new IllegalArgumentException(String.format(
                    "Failed handling event for event group (superclass: %s): The provided event manager must not be null!",
                    this.superclass().getSimpleName()));

        Comparator<EventHandlerInfo> comparator = new EventHandlerInfo.PriorityComparator();
        List<EventHandlerInfo> handlers = manager.getHandlers()
                .stream()
                .filter(handler -> handler.isHandled(event))
                .sorted(comparator)
                .toList();

        return this.executor().execute(event, handlers, manager);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (!(other instanceof EventGroup<?> that))
            return false;

        return this.superclass().equals(that.superclass());
    }

    @Override
    public int hashCode( )
    {
        return this.superclass().hashCode();
    }

    /**
     * Defines base handling of events that include from given type. This logic must call the handlers of a given
     * event.
     *
     * @param <T> base type of handled events
     */
    @FunctionalInterface
    public interface Executor<T>
    {
        /**
         * Base logic to execute when correlated event is received.
         *
         * @param event    event to process
         * @param handlers list of applicable handlers
         * @param manager  event manager that initiated the process
         *
         * @return modified event
         */
        T execute(T event, Collection<EventHandlerInfo> handlers, EventManager manager);
    }

    /**
     * Compare EventGroups preferring more precise implementations
     */
    public static class PrecisionComparator implements Comparator<EventGroup<?>>
    {
        @Override
        public int compare(EventGroup element, EventGroup other)
        {
            return ((Class<?>) element.superclass()).isAssignableFrom(other.superclass()) ? 0 : -1;
        }
    }
}
