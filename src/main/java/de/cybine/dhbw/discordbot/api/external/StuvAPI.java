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
    @NonNull
    private final BotConfig config;
}
