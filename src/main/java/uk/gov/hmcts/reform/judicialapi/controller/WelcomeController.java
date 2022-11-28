package uk.gov.hmcts.reform.judicialapi.controller;

import feign.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.feign.ElinksFeignClient;

import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(
    value = "/",
    produces = APPLICATION_JSON_VALUE
)

@RestController
public class WelcomeController {


    @Autowired
    private ElinksFeignClient elinksFeignClient;

    private static final Logger LOG = getLogger(WelcomeController.class);
    private static final String INSTANCE_ID = UUID.randomUUID().toString();
    private static final String MESSAGE = "Welcome to the Judicial API";

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
     * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
     * @return Welcome message from the service.
     */
    @ApiOperation("Welcome message for the Judicial API")
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Welcome message",
            response = String.class
        )
    })
    @GetMapping(
        path = "/",
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<String> welcome() {

        LOG.info("Welcome message '{}' from running instance: {}", MESSAGE, INSTANCE_ID);
        Response response = elinksFeignClient.getLocal();
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .body("{\"message\": \"" + MESSAGE + "\"}");

    }

    @ApiOperation("testing api")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Welcome message",
                    response = String.class
            )
    })
    @GetMapping(
            path = "/api",
            produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<String> apiKey(@RequestHeader Map<String, String> headers) {
        return ResponseEntity
                .status(200)
                .body("");

    }

}
