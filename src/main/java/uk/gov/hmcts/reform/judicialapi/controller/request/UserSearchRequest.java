package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserSearchRequest {

    @JsonProperty("searchString")
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    @Pattern(regexp = "([a-zA-Z\\-\\s']){3,}+", message = "should contains atleast 3 characters with letters, Apostrophe and"
            + " Hyphen only and white space allowed ")
    private String searchString;

    @JsonProperty("serviceCode")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "should not be empty or contain special characters")
    private String serviceCode;

    @JsonProperty("location")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "should not be empty or contain special characters")
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
