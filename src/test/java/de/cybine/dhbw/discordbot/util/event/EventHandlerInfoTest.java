package de.cybine.dhbw.discordbot.util.event;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(classes = { EventManager.class })
class EventHandlerInfoTest
{
    private static EventListener listener;

    @BeforeAll
    static void setup( )
    {
        EventHandlerInfoTest.listener = new EventListener();
    }

    static Stream<Arguments> provideCreationData( )
    {
        return Stream.of(Arguments.of("onEvent", null),
                Arguments.of("onCancelableEvent", null),
                Arguments.of("onCustomSettings", null),
                Arguments.of("onNoHandler", IllegalArgumentException.class),
                Arguments.of("onTooLessArguments", IllegalArgumentException.class),
                Arguments.of("onTooManyArguments", IllegalArgumentException.class));
    }

    @ParameterizedTest
    @DisplayName("Creation")
    @MethodSource("provideCreationData")
    void testCreation(String methodName, Class<? extends Throwable> exception)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(handler);

        if (exception == null)
        {
            Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));
            return;
        }

        Assertions.assertThrows(IllegalArgumentException.class, ( ) -> new EventHandlerInfo(listener, handler));
    }

    static Stream<Arguments> provideNameData( )
    {
        return Stream.of(Arguments.of("onEvent", "EventListener-onEvent"), Arguments.of("onCustomSettings", "test"));
    }

    @ParameterizedTest
    @DisplayName("Name")
    @MethodSource("provideNameData")
    void testName(String methodName, String name)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        Assertions.assertEquals(name, info.name());
    }

    static Stream<Arguments> provideTypeData( )
    {
        return Stream.of(Arguments.of("onEvent", Event.class),
                Arguments.of("onCancelableEvent", CancelableEvent.class),
                Arguments.of("onCustomSettings", Event.class));
    }

    @ParameterizedTest
    @DisplayName("Type")
    @MethodSource("provideTypeData")
    void testType(String methodName, Class<?> type)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        Assertions.assertEquals(type, info.type());
    }

    static Stream<Arguments> providePriorityData( )
    {
        return Stream.of(Arguments.of("onEvent", 100),
                Arguments.of("onCancelableEvent", 100),
                Arguments.of("onCustomSettings", 10));
    }

    @ParameterizedTest
    @DisplayName("Priority")
    @MethodSource("providePriorityData")
    void testPriority(String methodName, int priority)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        Assertions.assertEquals(priority, info.priority());
    }

    static Stream<Arguments> provideIgnoreCancelData( )
    {
        return Stream.of(Arguments.of("onEvent", false),
                Arguments.of("onCancelableEvent", false),
                Arguments.of("onCustomSettings", true));
    }

    @ParameterizedTest
    @DisplayName("IgnoreCanceled")
    @MethodSource("provideIgnoreCancelData")
    void testIgnoreCancel(String methodName, boolean ignoreCanceled)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));
    }

    static Stream<Arguments> provideMatchData( )
    {
        return Stream.of(Arguments.of("onEvent", false),
                Arguments.of("onCancelableEvent", false),
                Arguments.of("onCustomSettings", true));
    }

    @ParameterizedTest
    @DisplayName("Match")
    @MethodSource("provideMatchData")
    void testMatch(String methodName, boolean matchExact)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        Assertions.assertEquals(matchExact, info.matchExact());
    }

    static Stream<Arguments> provideCallData( )
    {
        return Stream.of(Arguments.of("onEvent", null, IllegalArgumentException.class),
                Arguments.of("onCancelableEvent", null, IllegalArgumentException.class),
                Arguments.of("onCustomSettings", null, IllegalArgumentException.class),
                Arguments.of("onEvent", new Event(), null),
                Arguments.of("onCancelableEvent", new Event(), IllegalArgumentException.class),
                Arguments.of("onCustomSettings", new Event(), null),
                Arguments.of("onEvent", new CancelableEvent(), null),
                Arguments.of("onCancelableEvent", new CancelableEvent(), null),
                Arguments.of("onCustomSettings", new CancelableEvent(), null));
    }

    @ParameterizedTest
    @DisplayName("Call")
    @MethodSource("provideCallData")
    void testCall(String methodName, Object event, Class<? extends Throwable> exception)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        if (exception == null)
        {
            Assertions.assertDoesNotThrow(( ) -> info.call(event));
            return;
        }

        Assertions.assertThrows(exception, ( ) -> info.call(event));
    }

    static Stream<Arguments> provideHandleData( )
    {
        return Stream.of(Arguments.of("onEvent", null, false),
                Arguments.of("onCancelableEvent", null, false),
                Arguments.of("onCustomSettings", null, false),
                Arguments.of("onEvent", new Event(), true),
                Arguments.of("onCancelableEvent", new Event(), false),
                Arguments.of("onCustomSettings", new Event(), true),
                Arguments.of("onEvent", new CancelableEvent(), true),
                Arguments.of("onCancelableEvent", new CancelableEvent(), true),
                Arguments.of("onCustomSettings", new CancelableEvent(), false));
    }

    @ParameterizedTest
    @DisplayName("Handle check")
    @MethodSource("provideHandleData")
    void testHandled(String methodName, Object event, boolean handled)
    {
        Method handler = Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        EventHandlerInfo info = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener, handler));

        Assertions.assertEquals(handled, info.isHandled(event));
    }

    @Test
    @DisplayName("Sort")
    void testSort( )
    {
        EventHandlerInfo highPriority = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener,
                EventListener.class.getMethod("onEvent", Event.class)));

        EventHandlerInfo lowPriority = Assertions.assertDoesNotThrow(( ) -> new EventHandlerInfo(listener,
                EventListener.class.getMethod("onCustomSettings", Event.class)));

        List<EventHandlerInfo> unordered = Lists.list(highPriority, lowPriority);
        List<EventHandlerInfo> ordered = Lists.list(lowPriority, highPriority);

        unordered.sort(new EventHandlerInfo.PriorityComparator());
        Assertions.assertEquals(ordered, unordered);
    }

    @SuppressWarnings("unused")
    static class EventListener implements IEventListener
    {
        @EventHandler
        public void onEvent(Event event)
        { }

        @EventHandler
        public void onCancelableEvent(CancelableEvent event)
        { }

        @EventHandler(name = "test", priority = 10, matchExact = true)
        public void onCustomSettings(Event event)
        { }

        public void onNoHandler(IEvent event)
        { }

        @EventHandler
        public void onTooLessArguments( )
        { }

        @EventHandler
        public void onTooManyArguments( )
        { }
    }


    static class CancelableEvent extends Event implements ICancelableEvent
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