package de.cybine.dhbw.discordbot.util.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark method as event handler
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler
{
    /**
     * The lower the priority the earlier it gets executed.
     *
     * @return execution priority
     */
    int priority( ) default 100;

    /**
     * @return true if event should ignore cancel state
     */
    boolean ignoreCanceled( ) default false;

    /**
     * @return true if only events of the exact class should be handled otherwise the handler reacts to all events
     *         implementing from the given class too
     */
    boolean matchExact( ) default false;

    /**
     * @return identifier for log outputs
     */
    String name( ) default "%1$s-%2$s";
}
