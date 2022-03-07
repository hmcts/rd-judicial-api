package uk.gov.hmcts.reform.judicialapi.validator;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


class RefreshUserValidatorTest {

    @Test
    void shouldReturnTrueWhenListIsNotEmpty() {
        var refreshUserValidator = new RefreshUserValidator();

        assertTrue(refreshUserValidator.isListNotEmptyOrNotNull(List.of("testString")));
    }

    @Test
    void shouldReturnFalseWhenListIsNull() {
        var refreshUserValidator = new RefreshUserValidator();

        assertFalse(refreshUserValidator.isListNotEmptyOrNotNull(null));
    }

    @Test
    void shouldReturnFalseWhenListIsEmpty() {
        var refreshUserValidator = new RefreshUserValidator();

        assertFalse(refreshUserValidator.isListNotEmptyOrNotNull(Collections.emptyList()));
    }

    @Test
    void shouldReturnTrueWhenStringIsNotEmpty() {
        var refreshUserValidator = new RefreshUserValidator();

        assertTrue(refreshUserValidator.isStringNotEmptyOrNotNull("testString"));
    }

    @Test
    void shouldReturnFalseWhenStringIsEmpty() {
        var refreshUserValidator = new RefreshUserValidator();

        assertFalse(refreshUserValidator.isStringNotEmptyOrNotNull(""));
    }

    @Test
    void shouldReturnFalseWhenStringIsNull() {
        var refreshUserValidator = new RefreshUserValidator();

        assertFalse(refreshUserValidator.isStringNotEmptyOrNotNull(null));
    }

    @Test
    void shouldReturnNullWhenListIsNull() {
        var refreshUserValidator = new RefreshUserValidator();

        assertNull(refreshUserValidator.removeEmptyOrNullFromList(null));
    }

    @Test
    void shouldReturnEmptyWhenListIsEmpty() {
        var refreshUserValidator = new RefreshUserValidator();

        assertEquals(Collections.emptyList(),
                refreshUserValidator.removeEmptyOrNullFromList(Collections.emptyList()));
    }

}