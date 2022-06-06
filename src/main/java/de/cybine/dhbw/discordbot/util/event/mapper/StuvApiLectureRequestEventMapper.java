package de.cybine.dhbw.discordbot.util.event.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cybine.dhbw.discordbot.service.stuvapi.event.request.StuvApiLectureRequestEvent;
import de.cybine.dhbw.discordbot.util.event.custom.CloudEventMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

public class StuvApiLectureRequestEventMapper
{
    public static CloudEventMapper.MapperInfo<StuvApiLectureRequestEvent> getMapper(ObjectMapper mapper)
    {
        return new CloudEventMapper.MapperInfo<>(StuvApiLectureRequestEvent.class,
                data -> StuvApiLectureRequestEventMapper.toEvent(data, mapper),
                event -> StuvApiLectureRequestEventMapper.toData(event, mapper));
    }

    private static StuvApiLectureRequestEvent toEvent(CloudEvent data, ObjectMapper mapper)
    {
        if (data.getData() == null)
            throw new IllegalArgumentException();

        PojoCloudEventData<StuvApiLectureRequestEvent> pojoData = CloudEventUtils.mapData(data,
                PojoCloudEventDataMapper.from(mapper, StuvApiLectureRequestEvent.class));

        if (pojoData == null)
            throw new IllegalStateException();

        return pojoData.getValue();
    }

    private static CloudEventData toData(StuvApiLectureRequestEvent event, ObjectMapper mapper)
    {
        return JsonCloudEventData.wrap(mapper.valueToTree(event));
    }
}
