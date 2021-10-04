package uk.gov.hmcts.reform.judicialapi.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;

import java.util.Collection;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.ONLY_ONE_PARAMETER_REQUIRED;

@Component
@Slf4j
@NoArgsConstructor
public class RefreshUserValidator {


    public void shouldContainOnlyOneInputParameter(RefreshRoleRequest refreshRoleRequest) {
        if (null != refreshRoleRequest) {
            boolean ccdServiceNames = isCcdServiceNamesNotEmptyOrNull(refreshRoleRequest.getCcdServiceNames());
            boolean objectIds = isNotEmptyOrNull(refreshRoleRequest.getObjectIds());
            boolean sidamIds = isNotEmptyOrNull(refreshRoleRequest.getSidamIds());

            if (ccdServiceNames ? (objectIds || sidamIds) : (objectIds && sidamIds)) {
                throw new InvalidRequestException(ONLY_ONE_PARAMETER_REQUIRED);
            }
        }
    }

    public boolean isCcdServiceNamesNotEmptyOrNull(String ccdServiceNames) {
        return StringUtils.isNotEmpty(ccdServiceNames);
    }

    public boolean isNotEmptyOrNull(Collection<?> collection) {
        if (collection != null) {
            collection.removeIf(item -> item == null || "".equals(item));
            return !collection.isEmpty();
        }
        return false;
    }

}
