package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRequest implements Serializable {
   @JsonProperty("user_ids")
    private List<String> userIds;
}