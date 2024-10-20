package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestCreateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintsWhenDescriptionNullOrBlank(final String description) {
        final RequestCreateDto dto = new RequestCreateDto(description);

        final Set<ConstraintViolation<RequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenDescriptionLengthExceeds2000() {
        final RequestCreateDto dto = new RequestCreateDto("a".repeat(2001));

        final Set<ConstraintViolation<RequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldNotViolateConstraintWhenCorrectRequestCreateDto() {
        final RequestCreateDto dto = new RequestCreateDto("Need the thing");

        final Set<ConstraintViolation<RequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotViolateConstraintWhenDescriptionLengthIs2000() {
        final RequestCreateDto dto = new RequestCreateDto("a".repeat(2000));

        final Set<ConstraintViolation<RequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}