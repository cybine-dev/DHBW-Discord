package de.cybine.dhbw.discordbot.util.event;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Data

@Getter
@Log4j2
@Component
public class EventManager
{
    private final Collection<EventGroup<?>>    groups   = new HashSet<>();
    private final Collection<EventHandlerInfo> handlers = new HashSet<>();

    @Getter(AccessLevel.NONE)
    private final Collection<Class<? extends IEventListener>> handlerClasses = new HashSet<>();

    public void registerEventGroup(Function<EventManager, EventGroup<?>> registration)
    {
        if (registration == null)
            throw new IllegalArgumentException("Failed registering event group: The registration must not be null!");

        EventGroup<?> eventGroup = registration.apply(this);
        if (eventGroup == null)
            throw new IllegalArgumentException("Failed registering event group: The event group must not be null!");

        if (this.groups.contains(eventGroup))
            throw new IllegalStateException(String.format(
                    "Failed registering event group for superclass %s: An event group for this superclass is already present!",
                    eventGroup.superclass().getSimpleName()));

        this.groups.add(eventGroup);
        EventManager.log.debug("New event group registered! ({})", eventGroup.superclass().getSimpleName());
    }

    public void unregisterEventGroup(Class<?> superclass)
    {
        if (superclass == null)
            throw new IllegalArgumentException("Failed unregistering event group: The superclass must not be null!");

        Iterator<EventGroup<?>> iterator = this.groups.iterator();
        while (iterator.hasNext())
        {
            EventGroup<?> eventGroup = iterator.next();
            if (eventGroup.superclass() != superclass)
                continue;

            iterator.remove();
            EventManager.log.debug("Event group unregistered! ({})", superclass.getSimpleName());
        }
    }

    public void registerHandlers(Function<EventManager, IEventListener> registration)
    {
        if (registration == null)
            throw new IllegalArgumentException("Failed registering event handlers: The registration must not be null!");

        IEventListener eventListener = registration.apply(this);
        if (eventListener == null)
            throw new IllegalArgumentException("Failed registering event handlers: The event listener must not be null!");

        if (this.handlerClasses.contains(eventListener.getClass()))
            throw new IllegalStateException(
                    "Failed registering event handlers: The event listener has already been registered!");

        List<EventHandlerInfo> handlerInfoList = Arrays.stream(eventListener.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .filter(method -> method.getParameterCount() == 1)
                .map(method -> new EventHandlerInfo(eventListener, method))
                .toList();

        this.handlerClasses.add(eventListener.getClass());
        this.handlers.addAll(handlerInfoList);

        EventManager.log.debug("{} new event handlers registered for class {}!",
                handlerInfoList.size(),
                eventListener.getClass().getSimpleName());
    }

    public void unregisterHandlers(Class<?> listenerClass)
    {
        if (listenerClass == null)
            throw new IllegalArgumentException(
                    "Failed unregistering event handlers: The listener class must not be null!");

        if (!this.handlerClasses.removeIf(clazz -> clazz == listenerClass))
            return;

        this.getHandlers().removeIf(handler -> handler.listener().getClass() == listenerClass);

        EventManager.log.debug("Event handlers unregistered! (Listener: {})", listenerClass.getSimpleName());
    }

    public boolean isHandled(Object event)
    {
        if (event == null)
            throw new IllegalArgumentException("Failed checking if event is handled: Event must not be null!");

        return this.getGroups().stream().anyMatch(group -> group.isHandled(event));
    }

    public <T> T handle(Function<EventManager, T> call)
    {
        if (call == null)
            throw new IllegalArgumentException("Failed calling event handlers: Event call must not be null!");

        T event = call.apply(this);
        if (event == null)
            throw new IllegalArgumentException("Failed calling event handlers: Event must not be null!");

        EventManager.log.debug("Received event: {}", event.getClass().getSimpleName());
        Comparator<EventGroup<?>> comparator = new EventGroup.PrecisionComparator();

        //noinspection unchecked
        return this.getGroups()
                .stream()
                .filter(group -> group.isHandled(event))
                .min(comparator)
                .map(group -> (T) ((EventGroup<? super T>) group).handle(event, this))
                .orElse(event);
    }
}
