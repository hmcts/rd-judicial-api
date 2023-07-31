package uk.gov.hmcts.reform.judicialapi.elinks.validator;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElinksRefreshUserValidatorTest {

    @Test
    void shouldReturnTrueWhenListIsNotEmpty() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertTrue(elinksRefreshUserValidator.isListNotEmptyOrNotNull(List.of("testString")));
    }

    @Test
    void shouldReturnFalseWhenListIsNull() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertFalse(elinksRefreshUserValidator.isListNotEmptyOrNotNull(null));
    }

    @Test
    void shouldReturnFalseWhenListIsEmpty() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertFalse(elinksRefreshUserValidator.isListNotEmptyOrNotNull(Collections.emptyList()));
    }

    @Test
    void shouldReturnTrueWhenStringIsNotEmpty() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertTrue(elinksRefreshUserValidator.isStringNotEmptyOrNotNull("testString"));
    }

    @Test
    void shouldReturnFalseWhenStringIsEmpty() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertFalse(elinksRefreshUserValidator.isStringNotEmptyOrNotNull(""));
    }

    @Test
    void shouldReturnFalseWhenStringIsNull() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertFalse(elinksRefreshUserValidator.isStringNotEmptyOrNotNull(null));
    }

    @Test
    void shouldReturnNullWhenListIsNull() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertNull(elinksRefreshUserValidator.removeEmptyOrNullFromList(null));
    }

    @Test
    void shouldReturnEmptyWhenListIsEmpty() {
        var elinksRefreshUserValidator = new ElinksRefreshUserValidator();

        assertEquals(Collections.emptyList(),
                elinksRefreshUserValidator.removeEmptyOrNullFromList(Collections.emptyList()));
    }



}
