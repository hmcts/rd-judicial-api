package uk.gov.hmcts.reform.judicialapi.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    @SafeVarargs
    public static <T> Predicate<T> distinctByKeys(final Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t -> {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

}
