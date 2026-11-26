package uk.gov.hmcts.reform.judicialapi.elinks.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@com.fasterxml.jackson.databind.annotation.JsonNaming(
    com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
@tools.jackson.databind.annotation.JsonNaming(
    tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ElinkBaseLocationResponse {
    List<BaseLocationResponse> results = new ArrayList<>();
}
