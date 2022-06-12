package de.cybine.dhbw.discordbot.api.rest.v1.timed;

import de.cybine.dhbw.discordbot.api.rest.v1.timed.result.WeeklyScheduleResult;
import de.cybine.dhbw.discordbot.config.SwaggerConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Validated
@RequestMapping("/api/v1/timed")
@Api(tags = SwaggerConfig.TIMED_ENDPOINT_TAG,
     protocols = "http, https",
     consumes = "application/json",
     produces = "application/json")
public interface IWeeklyScheduleEndpointController
{
    @PostMapping("/weekly/schedule")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Post weekly schedule overview")
    @ApiResponses({ @ApiResponse(code = 200, message = "Schedule posted", response = WeeklyScheduleResult.class),
                    @ApiResponse(code = 400, message = "Could not post schedule") })
    ResponseEntity<WeeklyScheduleResult> postWeeklySchedule( );
}
