package de.cybine.dhbw.discordbot.util.event;

public interface ICancelableEvent extends IEvent
{
    boolean isCanceled( );

    void setCanceled(boolean canceled);
}
