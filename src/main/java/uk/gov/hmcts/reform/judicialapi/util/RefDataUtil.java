package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
public class RefDataUtil {

    private RefDataUtil() {
    }

    public static Pageable createPageableObject(Integer page, Integer size, Integer defaultPageSize) {

        if (isNull(size)) {
            size = defaultPageSize;
        }
        page = nonNull(page) ? page : 0;
        return PageRequest.of(page, size);
    }

}
