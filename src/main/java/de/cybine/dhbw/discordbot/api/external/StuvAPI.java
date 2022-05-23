package de.cybine.dhbw.discordbot.api.external;

import com.google.gson.Gson;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class StuvAPI
{
    private final StuvApiConfig config;
    private final Gson          gson;

    public Collection<LectureDto> getLectures(boolean includeArchived)
    {
        URI uri = URI.create(String.format("%s/%s/%s?archived=%s",
                this.config.stuvApiUrl(),
                "rapla",
                "lectures",
                includeArchived));

        return this.parseLectureResponse(this.performHTTPRequest(uri).body());
    }

    public Collection<LectureDto> getLectures(boolean includeArchived, String courseId)
    {
        URI uri = URI.create(String.format("%s/%s/%s/%s?archived=%s",
                this.config.stuvApiUrl(),
                "rapla",
                "lectures",
                courseId,
                includeArchived));


        return this.parseLectureResponse(this.performHTTPRequest(uri).body());
    }

    public Collection<ScheduleSyncDto> getLatestSyncs(int amount, int skip)
    {
        URI uri = URI.create(String.format("%s/%s/%s?amount=%s&skip=%s",
                this.config.stuvApiUrl(),
                "sync",
                "latest",
                amount,
                skip));


        List<Object> result = this.gson.fromJson(this.performHTTPRequest(uri).body(), List.class);
        List<ScheduleSyncDto> latestSyncs = new ArrayList<>(result.size());
        for (Object obj : result)
        {
            Map<String, Object> latestSync = (Map<String, Object>) obj;

            ScheduleSyncDto.ScheduleSyncDtoBuilder builder = ScheduleSyncDto.builder();

            builder.id(((Double) latestSync.getOrDefault("id", -1)).intValue());

            builder.startedAt(this.parseLocalDateTime((String) latestSync.get("startTime")));
            builder.endedAt(this.parseLocalDateTime((String) latestSync.get("endTime")));

            builder.status((String) latestSync.getOrDefault("status", "0000"));
            builder.hasChanges((Boolean) latestSync.getOrDefault("hasChanges", false));

            builder.updateCount(((Double) latestSync.getOrDefault("updatedCount", -1.0)).intValue());
            builder.newCount(((Double) latestSync.getOrDefault("newCount", -1.0)).intValue());
            builder.removeCount(((Double) latestSync.getOrDefault("removedCount", -1.0)).intValue());

            latestSyncs.add(builder.build());
        }

        return latestSyncs;
    }

    public ScheduleSyncDetailDto getSyncDetails(int syncID)
    {
        URI uri = URI.create(String.format("%s/%s/%s", this.config.stuvApiUrl(), "sync", syncID));

        Map<String, Object> result = this.gson.fromJson(this.performHTTPRequest(uri).body(), Map.class);

        ScheduleSyncDetailDto.ScheduleSyncDetailDtoBuilder builder = ScheduleSyncDetailDto.builder();

        builder.id(((Double) result.getOrDefault("id", -1)).intValue());

        builder.startedAt(this.parseLocalDateTime((String) result.get("startTime")));
        builder.endedAt(this.parseLocalDateTime((String) result.get("endTime")));

        builder.status((String) result.get("status"));

        builder.newLectures(this.parseLectureResponse((List<Object>) result.get("newLectures")));
        builder.removedLectures(this.parseLectureResponse((List<Object>) result.get("removedLectures")));
        builder.updatedLectures(this.parseUpdatedLectures((List<Object>) result.get("updatedLectures")));

        return builder.build();
    }

    private HttpResponse<String> performHTTPRequest(URI uri)
    {

        try
        {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new IllegalStateException(String.format("Could not query data. (%s)", uri));

            return response;

        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }


    }

    private LocalDateTime parseLocalDateTime(String date)
    {
        if (date == null)
            return null;

        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Collection<LectureDto> parseLectureResponse(String json)
    {
        return this.parseLectureResponse((List<Object>) this.gson.fromJson(json, List.class));
    }

    private Collection<LectureDto> parseLectureResponse(List<Object> lectureData)
    {
        return lectureData.stream().map(this::parseLectureResponse).toList();
    }

    private LectureDto parseLectureResponse(Object lectureData)
    {
        Map<String, Object> lectureDetails = (Map<String, Object>) lectureData;
        LectureDto.LectureDtoBuilder builder = LectureDto.builder();

        builder.id(((Double) lectureDetails.getOrDefault("id", -1)).longValue());

        builder.createdAt(this.parseLocalDateTime((String) lectureDetails.get("date")));
        builder.startsAt(this.parseLocalDateTime((String) lectureDetails.get("startTime")));
        builder.endsAt(this.parseLocalDateTime((String) lectureDetails.get("endTime")));

        builder.name((String) lectureDetails.get("name"));
        builder.course((String) lectureDetails.get("course"));
        builder.lecturer((String) lectureDetails.get("lecturer"));
        builder.type(LectureDto.Type.valueOf((String) lectureDetails.get("type")));

        builder.rooms(((List<String>) lectureDetails.getOrDefault("rooms", Collections.EMPTY_LIST)).stream()
                .map(room -> RoomDto.builder().name(room).build())
                .toList());

        return builder.build();
    }

    private Collection<ScheduleUpdateDetailDto> parseUpdateDetailsResponse(List<Object> changeData)
    {
        List<ScheduleUpdateDetailDto> changeInfos = new ArrayList<>(changeData.size());
        for (Object obj : changeData)
        {
            Map<String, Object> changeInfo = (Map<String, Object>) obj;
            ScheduleUpdateDetailDto.ScheduleUpdateDetailDtoBuilder builder = ScheduleUpdateDetailDto.builder();

            builder.id(((Double) changeInfo.getOrDefault("id", -1.0)).longValue());

            builder.fieldName((String) changeInfo.get("fieldName"));
            builder.fieldType((String) changeInfo.get("fieldType"));

            builder.previousValue((String) changeInfo.get("previousValue"));
            builder.newValue((String) changeInfo.get("value"));

            changeInfos.add(builder.build());
        }

        return changeInfos;
    }

    private Collection<ScheduleUpdateDto> parseUpdatedLectures(List<Object> updatedLecturesData)
    {
        List<ScheduleUpdateDto> updatedLectures = new ArrayList<>(updatedLecturesData.size());

        for (Object obj : updatedLecturesData)
        {
            Map<String, Object> updatedLecture = (Map<String, Object>) obj;
            ScheduleUpdateDto.ScheduleUpdateDtoBuilder builder = ScheduleUpdateDto.builder();

            builder.id(((Double) updatedLecture.getOrDefault("id", -1.0)).longValue());
            builder.lecture(this.parseLectureResponse(updatedLecture.get("lecture")));
            builder.changeInfo(this.parseUpdateDetailsResponse((List<Object>) updatedLecture.get("changeInfos")));

            updatedLectures.add(builder.build());
        }

        return updatedLectures;
    }

}