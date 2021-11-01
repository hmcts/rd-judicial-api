package uk.gov.hmcts.reform.judicialapi.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class RoleDetail {
    @JsonProperty("code")
    private String code = null;

    public RoleDetail() {
    }

    public RoleDetail code(String code) {
        this.code = code;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = "the role code"
    )
    @NotNull
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            RoleDetail roleDetail = (RoleDetail)o;
            return Objects.equals(this.code, roleDetail.code);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.code});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RoleDetail {\n");
        sb.append("    code: ").append(this.toIndentedString(this.code)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
