package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static java.util.Objects.isNull;

@Slf4j
@Getter
public class RefDataUtil {

    private RefDataUtil() {
    }

    public static Pageable createPageableObject(Integer page, Integer size, Integer defaultPageSize, Sort sort) {
        if (isNull(size)) {
            size = defaultPageSize;
        }
        return PageRequest.of(page, size, sort);
    }

}
