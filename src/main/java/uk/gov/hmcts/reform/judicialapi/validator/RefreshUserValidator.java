package uk.gov.hmcts.reform.judicialapi.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;

import java.util.List;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.ATLEAST_ONE_PARAMETER_REQUIRED;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.COMMA_SEPARATED_AND_ALL_NOT_ALLOWED;
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
            boolean personalCodes = isListNotEmptyOrNotNull(refreshRoleRequest.getPersonalCodes());

            if (ccdServiceNames ? isOneTrue(objectIds, sidamIds, personalCodes) :
                    isTwoTrue(objectIds, sidamIds, personalCodes)) {
                throw new InvalidRequestException(ONLY_ONE_PARAMETER_REQUIRED);
            }
            if (ccdServiceNames && (refreshRoleRequest.getCcdServiceNames().split(",").length > 1
                    || refreshRoleRequest.getCcdServiceNames().trim().equalsIgnoreCase("ALL"))) {
                throw new InvalidRequestException(COMMA_SEPARATED_AND_ALL_NOT_ALLOWED);
            }
            if (!ccdServiceNames && !objectIds && !sidamIds && !personalCodes) {
                throw new InvalidRequestException(ATLEAST_ONE_PARAMETER_REQUIRED);
            }
        }
    }

    private boolean isTwoTrue(boolean objectIds, boolean sidamIds, boolean personalCodes) {
        return objectIds ? (sidamIds || personalCodes) : (sidamIds && personalCodes);
    }

    private boolean isOneTrue(boolean objectIds, boolean sidamIds, boolean personalCodes) {
        return objectIds || sidamIds || personalCodes;
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
            values = values.stream().filter(this::isStringNotEmptyOrNotNull).toList();
        }
        return values;
    }


}
