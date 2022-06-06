package de.cybine.dhbw.discordbot.util.event.custom;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class CloudEventMapper
{
    private final Map<String, MapperInfo<?>> typeMapping = new HashMap<>();

    public <T> void addTypeMapping(Class<T> type, Function<CloudEvent, T> deserializer,
            Function<T, CloudEventData> serializer)
    {
        this.addTypeMapping(new MapperInfo<>(type, deserializer, serializer));
    }

    public void addTypeMapping(MapperInfo<?> mapperInfo)
    {
        this.typeMapping.put(this.getCloudEventType(mapperInfo.type()), mapperInfo);
    }

    private String getCloudEventType(Class<?> type)
    {
        String name = type.getSimpleName();
        if (type.isAnnotationPresent(CloudEventInfo.class))
            name = type.getAnnotation(CloudEventInfo.class).name();

        return name;
    }

    public <T> Optional<CloudEvent> toCloudEvent(T obj)
    {
        return this.typeMapping.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(this.getCloudEventType(obj.getClass())))
                .map(Map.Entry::getValue)
                .findAny()
                .map(mapper -> this.toCloudEvent(obj, mapper::applyToBuilder));
    }

    public <T> CloudEvent toCloudEvent(T obj, BiConsumer<T, CloudEventBuilder> mapper)
    {
        CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("de.cybine.dhbw.bot"))
                .withType(this.getCloudEventType(obj.getClass()))
                .withTime(OffsetDateTime.now())
                .withDataContentType("application/json");

        mapper.accept(obj, builder);
        return builder.build();
    }

    public Optional<Object> toEvent(CloudEvent event)
    {
        return this.typeMapping.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(event.getType()))
                .map(Map.Entry::getValue)
                .findAny()
                .map(mapper -> this.toEvent(event, mapper::toObj));
    }

    public <T> T toEvent(CloudEvent event, Function<CloudEvent, T> mapper)
    {
        return mapper.apply(event);
    }

    public record MapperInfo<T>(Class<T> type,
                                Function<CloudEvent, T> deserializer,
                                Function<T, CloudEventData> serializer)
    {
        public CloudEventData toData(Object obj)
        {
            return this.serializer().apply(this.type().cast(obj));
        }

        public void applyToBuilder(Object obj, CloudEventBuilder builder)
        {
            builder.withData(this.toData(obj));
        }

        public T toObj(CloudEvent data)
        {
            return this.deserializer().apply(data);
        }
    }
}
