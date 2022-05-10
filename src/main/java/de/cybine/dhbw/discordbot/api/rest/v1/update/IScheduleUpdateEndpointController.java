package de.cybine.dhbw.discordbot.api.rest.v1.update;

import de.cybine.dhbw.discordbot.api.rest.v1.update.result.ScheduleUpdateResult;
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
@RequestMapping("/api/v1/update")
@Api(tags = SwaggerConfig.UPDATE_ENDPOINT_TAG,
     protocols = "http, https",
     consumes = "application/json",
     produces = "application/json")
public interface IScheduleUpdateEndpointController
{
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Apply new schedule updates")
    @ApiResponses({ @ApiResponse(code = 200,
                                 message = "New updates have been applied",
                                 response = ScheduleUpdateResult.class),
                    @ApiResponse(code = 304, message = "No updates to apply") })
    ResponseEntity<ScheduleUpdateResult> updateLectures( );
}
