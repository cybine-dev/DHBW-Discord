package de.cybine.dhbw.discordbot.api.external;

import com.google.gson.Gson;
import de.cybine.dhbw.discordbot.config.StuvApiConfig;
import de.cybine.dhbw.discordbot.data.schedule.LectureDto;
import de.cybine.dhbw.discordbot.data.schedule.ScheduleSyncDetailDto;
import de.cybine.dhbw.discordbot.data.schedule.ScheduleSyncDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

@DisplayName("StuvAPI-Test")
@SpringBootTest(classes = { StuvApiConfig.class, StuvAPI.class, Gson.class })
class StuvAPITest
{
    @Autowired
    private StuvAPI api;

    @Test
    @DisplayName("Fetch all courses")
    void testFetchAllCourses( )
    {
        Collection<LectureDto> lectures = Assertions.assertDoesNotThrow(( ) -> this.api.getLectures(false));
    }

    @Test
    @DisplayName("Fetch a course")
    void testFetchCourse( )
    {
        Collection<LectureDto> lectures = Assertions.assertDoesNotThrow(( ) -> this.api.getLectures(false,
                "MGH-TINF21"));
    }

    @Test
    @DisplayName("Fetch latest syncs")
    void testLatestSyncs( )
    {
        Collection<ScheduleSyncDto> latestSyncs = Assertions.assertDoesNotThrow(( ) -> this.api.getLatestSyncs(10, 0));

    }
    @Test
    @DisplayName("Fetch sync details")
    void testGetSyncDetails( )
    {
        ScheduleSyncDetailDto syncDetails = Assertions.assertDoesNotThrow(( ) -> this.api.getSyncDetails(2542));

    }

}