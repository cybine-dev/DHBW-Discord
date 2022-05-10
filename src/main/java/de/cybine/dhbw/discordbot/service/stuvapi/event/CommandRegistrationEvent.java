package de.cybine.dhbw.discordbot.service.stuvapi.event;

import de.cybine.dhbw.discordbot.util.event.IEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;

public record CommandRegistrationEvent(ApplicationCommandRequest request) implements IEvent
{ }
