package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import javax.validation.Valid;

@Validated
@Data
@Setter
@Getter
public class TestUserRequest {
    @JsonProperty("ssoId")
    private String ssoId = null;
    @JsonProperty("email")
    private String email = null;
    @JsonProperty("forename")
    private String forename = null;
    @JsonProperty("surname")
    private String surname = null;
    @JsonProperty("password")
    private String pass = null;
    @JsonProperty("roles")
    @Valid
    private List<RoleDetail> roles = null;
    @JsonProperty("userGroup")
    private RoleDetail userGroup = null;

}