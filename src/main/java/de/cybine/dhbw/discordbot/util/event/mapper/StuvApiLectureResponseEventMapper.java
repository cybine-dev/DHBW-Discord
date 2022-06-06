package de.cybine.dhbw.discordbot.util.event.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cybine.dhbw.discordbot.service.stuvapi.event.response.StuvApiLectureResponseEvent;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

public class StuvApiLectureResponseEventMapper
{
    public static CloudEventMapper.MapperInfo<StuvApiLectureResponseEvent> getMapper(ObjectMapper mapper)
    {
        return new CloudEventMapper.MapperInfo<>(StuvApiLectureResponseEvent.class,
                data -> StuvApiLectureResponseEventMapper.toEvent(data, mapper),
                event -> StuvApiLectureResponseEventMapper.toData(event, mapper));
    }

    private static StuvApiLectureResponseEvent toEvent(CloudEvent data, ObjectMapper mapper)
    {
        if (data.getData() == null)
            throw new IllegalArgumentException();

        PojoCloudEventData<StuvApiLectureResponseEvent> pojoData = CloudEventUtils.mapData(data,
                PojoCloudEventDataMapper.from(mapper, StuvApiLectureResponseEvent.class));

        if (pojoData == null)
            throw new IllegalStateException();

        return pojoData.getValue();
    }

    private static CloudEventData toData(StuvApiLectureResponseEvent event, ObjectMapper mapper)
    {
        return JsonCloudEventData.wrap(mapper.valueToTree(event));
    }
}
