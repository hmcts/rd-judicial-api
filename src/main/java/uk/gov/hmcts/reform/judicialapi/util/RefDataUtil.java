package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Objects.isNull;

@Slf4j
@Getter
public class RefDataUtil {

    private RefDataUtil() {
    }

    private static int defaultPageSize;


    public static Pageable createPageableObject(Integer page, Integer size) {
        if (isNull(size)) {
            size = defaultPageSize;
        }
        return PageRequest.of(page, size);
    }

    @Value("${defaultPageSize}")
    public static void setDefaultPageSize(int defaultPageSize) {
        RefDataUtil.defaultPageSize = defaultPageSize;
    }
}
