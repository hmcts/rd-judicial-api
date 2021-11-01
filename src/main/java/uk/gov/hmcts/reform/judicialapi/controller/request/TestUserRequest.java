package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

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
    private String password = null;
    @JsonProperty("roles")
    @Valid
    private List<RoleDetail> roles = null;
    @JsonProperty("userGroup")
    private RoleDetail userGroup = null;



}