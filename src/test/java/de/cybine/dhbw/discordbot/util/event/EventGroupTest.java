package de.cybine.dhbw.discordbot.util.event;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SpringBootTest(classes = { EventManager.class })
class EventGroupTest
{
    private EventManager eventManager;

    @BeforeEach
    void setup( )
    {
        this.eventManager = new EventManager();
    }

    static Stream<Arguments> provideCreationData( )
    {
        return Stream.of(Arguments.of(IEvent.class,
                        (EventGroup.Executor<IEvent>) (event, handlers, manager) -> event,
                        null),
                Arguments.of(null,
                        (EventGroup.Executor<IEvent>) (event, handlers, manager) -> event,
                        IllegalArgumentException.class),
                Arguments.of(IEvent.class, null, IllegalArgumentException.class));
    }

    @ParameterizedTest
    @DisplayName("Creation")
    @MethodSource("provideCreationData")
    <T> void testCreation(Class<T> superclass, EventGroup.Executor<T> executor, Class<? extends Throwable> exception)
    {
        if (exception == null)
        {
            Assertions.assertDoesNotThrow(( ) -> new EventGroup<>(superclass, executor));
            return;
        }

        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> new EventGroup<>(superclass, executor));
    }

    static Stream<Arguments> provideHandleData( )
    {
        return Stream.of(Arguments.of(ICancelableEvent.class, new CancelableEvent(), true),
                Arguments.of(ICancelableEvent.class, new Event(), false),
                Arguments.of(ICancelableEvent.class, null, false));
    }

    @ParameterizedTest
    @DisplayName("Handle check")
    @MethodSource("provideHandleData")
    void testHandled(Class<?> superclass, Object event, boolean shouldBeHandled)
    {
        Assertions.assertEquals(shouldBeHandled, this.getEventGroup(superclass).isHandled(event));
    }

    @Test
    @DisplayName("Equals")
    void testEquals( )
    {
        Assertions.assertEquals(this.getEventGroup(Event.class), this.getEventGroup(Event.class));
    }

    @Test
    @DisplayName("Comparison")
    void testComparison( )
    {
        List<EventGroup<?>> unorderedGroups = Lists.list(this.getEventGroup(ICancelableEvent.class),
                this.getEventGroup(IEvent.class),
                this.getEventGroup(CancelableEvent.class));

        List<EventGroup<?>> orderedGroups = Lists.list(this.getEventGroup(CancelableEvent.class),
                this.getEventGroup(ICancelableEvent.class),
                this.getEventGroup(IEvent.class));

        unorderedGroups.sort(new EventGroup.PrecisionComparator());
        Assertions.assertEquals(orderedGroups, unorderedGroups);
    }

    @Test
    @DisplayName("Handle")
    void testHandle( )
    {
        ICancelableEvent cancelableEvent = new CancelableEvent();
        EventGroup<ICancelableEvent> eventGroup = this.getEventGroup(ICancelableEvent.class,
                (event) -> event.setCanceled(true));

        Assertions.assertTrue(eventGroup.handle(cancelableEvent, this.eventManager).isCanceled());
    }

    private <T> EventGroup<T> getEventGroup(Class<T> clazz)
    {
        return this.getEventGroup(clazz, null);
    }

    private <T> EventGroup<T> getEventGroup(Class<T> clazz, Consumer<T> manipulator)
    {
        return new EventGroup<>(clazz, (event, handlers, manager) ->
        {
            if (manipulator != null)
                manipulator.accept(event);

            return event;
        });
    }

    static class CancelableEvent implements ICancelableEvent
    {
        private boolean canceled;

        @Override
        public boolean isCanceled( )
        {
            return this.canceled;
        }

        @Override
        public void setCanceled(boolean canceled)
        {
            this.canceled = canceled;
        }
    }

    static class Event implements IEvent
    { }
}