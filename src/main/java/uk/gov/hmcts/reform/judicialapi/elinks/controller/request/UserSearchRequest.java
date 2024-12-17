package uk.gov.hmcts.reform.judicialapi.elinks.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserSearchRequest {

    @JsonProperty("searchString")
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    @Pattern(regexp = "^[(a-zA-Z0-9 )\\p{L}\\p{N}'’-]{3,}", message = "must be at least 3 characters including "
            + "letters, apostrophe, hyphen")
    @Schema(name = "searchString",  example = "string")
    private String searchString;

    @JsonProperty("serviceCode")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "should not be empty or contain special characters")
    @Schema(name = "serviceCode",  example = "string")
    private String serviceCode;

    @JsonProperty("location")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "should not be empty or contain special characters")
    @Schema(name = "location",  example = "string")
    private String location;

    public void setSearchString(String searchString) {
        this.searchString = searchString.trim();
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode != null ? serviceCode.trim().toLowerCase() : null;
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim().toLowerCase() : null;
    }
}
