package uk.gov.hmcts.reform.judicialapi.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.ONLY_ONE_PARAMETER_REQUIRED;

@Component
@Slf4j
@NoArgsConstructor
public class RefreshUserValidator {

    public void shouldContainOnlyOneInputParameter(RefreshRoleRequest refreshRoleRequest) {
        if (null != refreshRoleRequest) {
            boolean ccdServiceNames = isStringNotEmptyOrNotNull(refreshRoleRequest.getCcdServiceNames());
            boolean objectIds = isListNotEmptyOrNotNull(refreshRoleRequest.getObjectIds());
            boolean sidamIds = isListNotEmptyOrNotNull(refreshRoleRequest.getSidamIds());

            if (ccdServiceNames ? (objectIds || sidamIds) : (objectIds && sidamIds)) {
                throw new InvalidRequestException(ONLY_ONE_PARAMETER_REQUIRED);
            }
        }
    }

    public boolean isStringNotEmptyOrNotNull(String value) {
        return ((null != value) && (StringUtils.isNotEmpty(value.trim())));
    }

    public boolean isListNotEmptyOrNotNull(List<String> values) {
        if (values != null) {
            return !removeEmptyOrNullFromList(values).isEmpty();
        }
        return false;
    }

    public List<String> removeEmptyOrNullFromList(List<String> values) {
        if (values != null && !values.isEmpty()) {
            values = values.stream().filter(this::isStringNotEmptyOrNotNull).collect(Collectors.toList());
        }
        return values;
    }


}
