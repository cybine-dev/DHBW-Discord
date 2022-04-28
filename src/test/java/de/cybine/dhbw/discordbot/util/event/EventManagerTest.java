package de.cybine.dhbw.discordbot.util.event;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Function;

@SpringBootTest(classes = { EventManager.class })
class EventManagerTest
{
    private EventManager eventManager;

    @BeforeEach
    void setup( )
    {
        this.eventManager = new EventManager();
    }

    @Test
    @DisplayName("Event group - Registration")
    void testEventGroupRegistration( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.registerEventGroup(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                ( ) -> this.eventManager.registerEventGroup(manager -> null));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerEventGroup(this.getEventGroupRegistration()));
        Assertions.assertEquals(1, this.eventManager.getGroups().size());

        Assertions.assertThrows(IllegalStateException.class,
                ( ) -> this.eventManager.registerEventGroup(this.getEventGroupRegistration()));
        Assertions.assertEquals(1, this.eventManager.getGroups().size());
    }

    @Test
    @DisplayName("Event group - Unregistration")
    void testEventGroupUnregistration( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.unregisterEventGroup(null));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.unregisterEventGroup(IEvent.class));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerEventGroup(this.getEventGroupRegistration()));
        Assertions.assertEquals(1, this.eventManager.getGroups().size());

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.unregisterEventGroup(IEvent.class));
        Assertions.assertEquals(0, this.eventManager.getGroups().size());
    }

    @Test
    @DisplayName("Event handler - Registration")
    void testEventHandlerRegistration( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.registerHandlers(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                ( ) -> this.eventManager.registerHandlers(manager -> null));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerHandlers(manager -> new Listener()));
        Assertions.assertEquals(1, this.eventManager.getHandlers().size());

        Assertions.assertThrows(IllegalStateException.class,
                ( ) -> this.eventManager.registerHandlers(manager -> new Listener()));
        Assertions.assertEquals(1, this.eventManager.getHandlers().size());
    }

    @Test
    @DisplayName("Event handler - Unregistration")
    void testEventHandlerUnregistration( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.unregisterHandlers(null));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.unregisterHandlers(Listener.class));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerHandlers(manager -> new Listener()));
        Assertions.assertEquals(1, this.eventManager.getHandlers().size());

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.unregisterHandlers(Listener.class));
        Assertions.assertEquals(0, this.eventManager.getHandlers().size());
    }

    @Test
    @DisplayName("Handle check")
    void testHandleCheck( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.isHandled(null));

        Assertions.assertFalse(this.eventManager.isHandled(new IEvent() { }));
        Assertions.assertFalse(this.eventManager.isHandled(new Event()));

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerEventGroup(this.getEventGroupRegistration()));
        Assertions.assertTrue(this.eventManager.isHandled(new IEvent() { }));
        Assertions.assertTrue(this.eventManager.isHandled(new Event()));
        Assertions.assertFalse(this.eventManager.isHandled(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Handle")
    void testHandle( )
    {
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.handle(null));
        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> this.eventManager.handle(manager -> null));

        Function<EventManager, Event> eventCall = manager -> new Event();

        Event notGroupedEvent = this.eventManager.handle(eventCall);
        Assertions.assertFalse(notGroupedEvent.isGrouped());
        Assertions.assertFalse(notGroupedEvent.isCalled());

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerEventGroup(this.getEventGroupRegistration()));
        Event notHandledEvent = this.eventManager.handle(eventCall);
        Assertions.assertTrue(notHandledEvent.isGrouped());
        Assertions.assertFalse(notHandledEvent.isCalled());

        Assertions.assertDoesNotThrow(( ) -> this.eventManager.registerHandlers(manager -> new Listener()));

        Event event = this.eventManager.handle(eventCall);
        Assertions.assertTrue(event.isGrouped());
        Assertions.assertTrue(event.isCalled());
    }

    private Function<EventManager, EventGroup<?>> getEventGroupRegistration( )
    {
        return manager -> new EventGroup<>(IEvent.class, (event, handlers, eventManager) ->
        {
            for (EventHandlerInfo handler : handlers)
            {
                try
                {
                    handler.call(event);
                }
                catch (InvocationTargetException | IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }

            if(event instanceof Event)
                ((Event) event).setGrouped(true);

            return event;
        });
    }

    @Setter
    @Getter
    private static class Event implements IEvent
    {
        private boolean grouped = false;
        private boolean called  = false;
    }

    @SuppressWarnings("unused")
    private static class Listener implements IEventListener
    {
        @EventHandler
        public void onEvent(IEvent event)
        {
            if (event instanceof Event)
                ((Event) event).setCalled(true);
        }

        @EventHandler
        public void onEvent( )
        { }

        @EventHandler
        public void onEvent(IEvent event, IEvent event2)
        { }

        public void noHandler(IEvent event)
        { }
    }
}