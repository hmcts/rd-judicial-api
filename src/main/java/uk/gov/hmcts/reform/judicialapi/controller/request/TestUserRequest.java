package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Validated
public class TestUserRequest {
    @JsonProperty("id")
    private String id = null;
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

    public TestUserRequest() {
    }

    public TestUserRequest id(String id) {
        this.id = id;
        return this;
    }

    @ApiModelProperty("The user id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TestUserRequest email(String email) {
        this.email = email;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = "The user email address"
    )
    @NotNull
    @Pattern(
            regexp = "^[^()!&/;%*@]+@[^()!&/;%*@]+\\.[^()!&/;%*@]+$"
    )
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TestUserRequest forename(String forename) {
        this.forename = forename;
        return this;
    }

    @ApiModelProperty("The user forename")
    public String getForename() {
        return this.forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public TestUserRequest surname(String surname) {
        this.surname = surname;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = "The user surname"
    )
    @NotNull
    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public TestUserRequest password(String password) {
        this.password = password;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = "The user password"
    )
    @NotNull
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TestUserRequest roles(List<RoleDetail> roles) {
        this.roles = roles;
        return this;
    }

    public TestUserRequest addRolesItem(RoleDetail rolesItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }

        this.roles.add(rolesItem);
        return this;
    }

    @ApiModelProperty("The list of the roles of the user")
    @Valid
    public List<RoleDetail> getRoles() {
        return this.roles;
    }

    public void setRoles(List<RoleDetail> roles) {
        this.roles = roles;
    }

    public TestUserRequest userGroup(RoleDetail userGroup) {
        this.userGroup = userGroup;
        return this;
    }

    @ApiModelProperty("The user group")
    @Valid
    public RoleDetail getUserGroup() {
        return this.userGroup;
    }

    public void setUserGroup(RoleDetail userGroup) {
        this.userGroup = userGroup;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TestUserRequest testUserRequest = (TestUserRequest)o;
            return Objects.equals(this.id, testUserRequest.id) && Objects.equals(this.email, testUserRequest.email) && Objects.equals(this.forename, testUserRequest.forename) && Objects.equals(this.surname, testUserRequest.surname) && Objects.equals(this.password, testUserRequest.password) && Objects.equals(this.roles, testUserRequest.roles) && Objects.equals(this.userGroup, testUserRequest.userGroup);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.email, this.forename, this.surname, this.password, this.roles, this.userGroup});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestUserRequest {\n");
        sb.append("    id: ").append(this.toIndentedString(this.id)).append("\n");
        sb.append("    email: ").append(this.toIndentedString(this.email)).append("\n");
        sb.append("    forename: ").append(this.toIndentedString(this.forename)).append("\n");
        sb.append("    surname: ").append(this.toIndentedString(this.surname)).append("\n");
        sb.append("    password: ").append(this.toIndentedString(this.password)).append("\n");
        sb.append("    roles: ").append(this.toIndentedString(this.roles)).append("\n");
        sb.append("    userGroup: ").append(this.toIndentedString(this.userGroup)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}