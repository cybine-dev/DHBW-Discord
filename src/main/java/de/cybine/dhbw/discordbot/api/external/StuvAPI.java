package de.cybine.dhbw.discordbot.api.external;

import com.google.gson.Gson;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
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
        try
        {
            URI uri = URI.create(String.format("%s/%s?archived=%s",
                    this.config.stuvApiUrl(),
                    "lectures",
                    includeArchived));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != HttpURLConnection.HTTP_OK)
                throw new IllegalStateException(String.format("Could not query data. (%s)", uri));

            return this.parseLectureResponse(response.body());
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Collection<LectureDto> getLectures(boolean includeArchived, String courseId)
    {
        return Collections.EMPTY_LIST;
    }

    private Collection<LectureDto> parseLectureResponse(String json)
    {
        List<Object> result = this.gson.fromJson(json, List.class);
        List<LectureDto> lectures = new ArrayList<>(result.size());
        for (Object obj : result)
        {
            Map<String, Object> lectureDetails = (Map<String, Object>) obj;

            LectureDto.LectureDtoBuilder builder = LectureDto.builder();

            builder.id(((Double) lectureDetails.getOrDefault("id", -1)).longValue());

            builder.createdAt(LocalDateTime.parse((String) lectureDetails.getOrDefault("date",
                    "1970-01-01T00:00:00.000Z"), DateTimeFormatter.ISO_DATE_TIME));
            builder.startsAt(LocalDateTime.parse((String) lectureDetails.getOrDefault("startTime",
                    "1970-01-01T00:00:00.000Z"), DateTimeFormatter.ISO_DATE_TIME));
            builder.endsAt(LocalDateTime.parse((String) lectureDetails.getOrDefault("endTime",
                    "1970-01-01T00:00:00.000Z"), DateTimeFormatter.ISO_DATE_TIME));

            builder.name((String) lectureDetails.getOrDefault("name", "no data"));
            builder.course((String) lectureDetails.getOrDefault("course", "ABC-DEFG21"));
            builder.lecturer((String) lectureDetails.getOrDefault("lecturer", "no data"));
            builder.type(LectureDto.Type.valueOf((String) lectureDetails.getOrDefault("type",
                    LectureDto.Type.INVALID.name())));

            builder.rooms((List<String>) lectureDetails.getOrDefault("rooms", Collections.EMPTY_LIST));
        }

        return lectures;
    }
}
