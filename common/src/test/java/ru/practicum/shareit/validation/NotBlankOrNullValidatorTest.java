package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotBlankOrNullValidatorTest {

    @Test
    void testIsValidWhenNull() {
        assertTrue(new NotBlankOrNullValidator().isValid(null, null));
    }

    @Test
    void testIsValidWhenEmpty() {
        assertFalse(new NotBlankOrNullValidator().isValid("", null));
    }

    @Test
    void testIsValidWhenBlank() {
        assertFalse(new NotBlankOrNullValidator().isValid(" ", null));
    }

    @Test
    void testIsValidWhenNotBlank() {
        assertTrue(new NotBlankOrNullValidator().isValid("test", null));
    }
}