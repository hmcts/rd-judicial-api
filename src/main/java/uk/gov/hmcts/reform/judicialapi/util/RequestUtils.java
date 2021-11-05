package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ReflectionUtils;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;

import java.lang.reflect.Field;
import java.util.Objects;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.INVALID_FIELD;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.PAGE_NUMBER;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.PAGE_SIZE;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.SORT_DIRECTION;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.SORT_COLUMN;

@Slf4j
@Getter
public class RequestUtils {

    private RequestUtils() {
    }

    public static PageRequest validateAndBuildPaginationObject(Integer pageSize,
                                                               Integer pageNumber,
                                                               String sortDirection,
                                                               String sortColumn,
                                                               int configPageSize,
                                                               String configSortColumn,
                                                               Class<?> entityClass) {

        if (Objects.nonNull(pageNumber) && pageNumber < 0) {
            throw new InvalidRequestException(String.format(INVALID_FIELD, PAGE_NUMBER));
        }
        if (Objects.nonNull(pageSize) && pageSize <= 0) {
            throw new InvalidRequestException(String.format(INVALID_FIELD, PAGE_SIZE));
        }
        if (!StringUtils.isEmpty(sortDirection)) {
            try {
                Sort.Direction.fromString(sortDirection);
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new InvalidRequestException(String.format(INVALID_FIELD, SORT_DIRECTION));
            }
        }
        String finalSortColumn = StringUtils.isBlank(sortColumn) ? configSortColumn : sortColumn;
        if (!isValidSortColumn(finalSortColumn, entityClass)) {
            throw new InvalidRequestException(String.format(INVALID_FIELD, SORT_COLUMN));
        }
        return PageRequest.of(Objects.isNull(pageNumber) ? 0 : pageNumber,
                Objects.isNull(pageSize) ? configPageSize : pageSize,
                StringUtils.isBlank(sortDirection) ? Sort.Direction.ASC : Sort.Direction.fromString(sortDirection),
                finalSortColumn);
    }

    private static boolean isValidSortColumn(String finalSortColumn,
                                             Class<?> entityClass) {
        Field field = ReflectionUtils.findField(entityClass, finalSortColumn);
        return Objects.nonNull(field);
    }




}
