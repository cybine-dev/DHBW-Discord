package de.cybine.dhbw.discordbot.service.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.cybine.dhbw.discordbot.listener.CommandRegistrationListener;
import de.cybine.dhbw.discordbot.listener.DiscordListener;
import de.cybine.dhbw.discordbot.listener.NatsListener;
import de.cybine.dhbw.discordbot.listener.stuvapi.ScheduleUpdateListener;
import de.cybine.dhbw.discordbot.util.event.EventGroup;
import de.cybine.dhbw.discordbot.util.event.EventManager;
import de.cybine.dhbw.discordbot.util.event.IEvent;
import de.cybine.dhbw.discordbot.util.event.IEventListener;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import de.cybine.dhbw.discordbot.util.event.mapper.StuvApiLectureRequestEventMapper;
import de.cybine.dhbw.discordbot.util.event.mapper.StuvApiLectureResponseEventMapper;
import discord4j.core.event.domain.Event;
import io.cloudevents.core.v1.CloudEventV1;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@Component
public class EventManagement
{
    @Getter
    private final EventManager     eventManager;
    private final CloudEventMapper eventMapper;

    private final Collection<IEventListener> listeners;

    public EventManagement(EventManager eventManager, CloudEventMapper eventMapper,
            CommandRegistrationListener commandRegistrationListener, ScheduleUpdateListener scheduleUpdateListener,
            DiscordListener discordListener, NatsListener natsListener)
    {
        this.eventManager = eventManager;
        this.eventMapper = eventMapper;
        this.listeners = new ArrayList<>();
        this.listeners.add(commandRegistrationListener);
        this.listeners.add(scheduleUpdateListener);
        this.listeners.add(discordListener);
        this.listeners.add(natsListener);

        this.setup();
    }

    private void setup( )
    {
        this.registerGroups();
        this.registerHandlers();
        this.registerMappings();
    }

    private void registerGroups( )
    {
        this.eventManager.registerEventGroup(eventManager -> new EventGroup<>(IEvent.class,
                CustomEventHandler::handle));
        this.eventManager.registerEventGroup(eventManager -> new EventGroup<>(Event.class,
                DiscordEventHandler::handle));
        this.eventManager.registerEventGroup(eventManager -> new EventGroup<>(CloudEventV1.class,
                CloudEventHandler::handle));
    }

    private void registerHandlers( )
    {
        this.listeners.forEach(listener -> this.eventManager.registerHandlers(manager -> listener));
    }

    private void registerMappings( )
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());

        this.eventMapper.addTypeMapping(StuvApiLectureRequestEventMapper.getMapper(objectMapper));
        this.eventMapper.addTypeMapping(StuvApiLectureResponseEventMapper.getMapper(objectMapper));
    }
}
