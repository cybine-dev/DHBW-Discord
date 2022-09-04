package de.cybine.dhbw.discordbot.api.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class StuvAPIRelay
{
    private final StuvApiConfig config;
    private final ObjectMapper  objectMapper;

    @SuppressWarnings("unchecked")
    public List<LectureDto> fetchLectures(String course, LocalDateTime from, LocalDateTime until)
            throws IOException, InterruptedException
    {
        URI uri = UriComponentsBuilder.fromUriString(this.config.stuvApiRelayUrl())
                .path("/api/v1/lecture")
                .queryParam("course", Optional.ofNullable(course))
                .queryParam("from", Optional.ofNullable(from))
                .queryParam("until", Optional.ofNullable(until))
                .build()
                .toUri();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != HttpURLConnection.HTTP_OK)
            throw new IllegalStateException(String.format("Could not query data. (%s)", uri));

        return this.objectMapper.readValue(response.body(), List.class)
                .stream()
                .map(data -> this.parseLecture((Map<String, Object>) data))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public LectureDto fetchLecture(UUID id) throws IOException, InterruptedException
    {
        URI uri = UriComponentsBuilder.fromUriString(this.config.stuvApiRelayUrl())
                .path(String.format("/api/v1/lecture/%s", id))
                .build()
                .toUri();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != HttpURLConnection.HTTP_OK)
            throw new IllegalStateException(String.format("Could not query data. (%s)", uri));

        return this.parseLecture(this.objectMapper.readValue(response.body(), Map.class));
    }

    @SuppressWarnings("unchecked")
    public List<ScheduleSyncDto> fetchSyncs( ) throws IOException, InterruptedException
    {
        URI uri = UriComponentsBuilder.fromUriString(this.config.stuvApiRelayUrl())
                .path("/api/v1/sync/all")
                .build()
                .toUri();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != HttpURLConnection.HTTP_OK)
            throw new IllegalStateException(String.format("Could not query data. (%s)", uri));

        return this.objectMapper.readValue(response.body(), List.class)
                .stream()
                .map(data -> this.parseScheduleSync((Map<String, Object>) data))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private LectureDto parseLecture(Map<String, Object> data)
    {
        return LectureDto.builder()
                .id(UUID.fromString((String) data.get("id")))
                .createdAt(ZonedDateTime.parse((String) data.get("createdAt"), DateTimeFormatter.ISO_DATE_TIME))
                .updatedAt(ZonedDateTime.parse((String) data.get("updatedAt"), DateTimeFormatter.ISO_DATE_TIME))
                .startsAt(ZonedDateTime.parse((String) data.get("startsAt"), DateTimeFormatter.ISO_DATE_TIME))
                .endsAt(ZonedDateTime.parse((String) data.get("endsAt"), DateTimeFormatter.ISO_DATE_TIME))
                .name((String) data.get("name"))
                .course((String) data.get("course"))
                .lecturer((String) data.get("lecturer"))
                .type(LectureDto.Type.valueOf((String) data.get("type")))
                .rooms(((List<?>) data.get("rooms")).stream()
                        .map(roomData -> this.parseRoom((Map<String, Object>) roomData))
                        .toList())
                .build();
    }

    private RoomDto parseRoom(Map<String, Object> data)
    {
        return RoomDto.builder()
                .id(UUID.fromString((String) data.get("id")))
                .name((String) data.get("name"))
                .displayName((String) data.get("displayName"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private ScheduleSyncDto parseScheduleSync(Map<String, Object> data)
    {
        return ScheduleSyncDto.builder()
                .id(UUID.fromString((String) data.get("id")))
                .startedAt(ZonedDateTime.parse((String) data.get("startedAt"), DateTimeFormatter.ISO_DATE_TIME))
                .finishedAt(ZonedDateTime.parse((String) data.get("finishedAt"), DateTimeFormatter.ISO_DATE_TIME))
                .data(((List<?>) data.get("data")).stream()
                        .map(lectureSync -> this.parseLectureSync((Map<String, Object>) lectureSync))
                        .toList())
                .build();
    }

    @SuppressWarnings("unchecked")
    private LectureSyncDto parseLectureSync(Map<String, Object> data)
    {
        return LectureSyncDto.builder()
                .id(UUID.fromString((String) data.get("id")))
                .syncId(UUID.fromString((String) data.get("syncId")))
                .lecture(this.parseLecture((Map<String, Object>) data.get("lecture")))
                .type(UpdateType.valueOf((String) data.get("type")))
                .details(((List<?>) data.get("details")).stream()
                        .map(details -> this.parseLectureSyncDetail((Map<String, Object>) details))
                        .toList())
                .build();
    }

    private LectureSyncDetailDto parseLectureSyncDetail(Map<String, Object> data)
    {
        return LectureSyncDetailDto.builder()
                .description((String) data.get("description"))
                .fieldName((String) data.get("fieldName"))
                .previousValue((String) data.get("previousValue"))
                .currentValue((String) data.get("currentValue"))
                .build();
    }
}
