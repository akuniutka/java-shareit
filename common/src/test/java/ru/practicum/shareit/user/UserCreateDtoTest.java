package ru.practicum.shareit.user;

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

class UserCreateDtoTest {

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
    void shouldViolateConstraintWhenNameNullOrBlank(final String name) {
        final UserCreateDto dto = getRandomUserCreateDto();
        dto.setName(name);

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenNameLengthExceeds255() {
        final UserCreateDto dto = getRandomUserCreateDto();
        dto.setName("a".repeat(256));

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "not_an_email"})
    void shouldViolateConstraintWhenEmailNullOrBlankOrMalformed(final String email) {
        final UserCreateDto dto = getRandomUserCreateDto();
        dto.setEmail(email);

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenEmailLengthExceed255() {
        final UserCreateDto dto = getRandomUserCreateDto();
        final String longEmail = "a".repeat(64) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        dto.setEmail(longEmail);

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldNotViolateConstraintWhenCorrectUserCreateDto() {
        final UserCreateDto dto = getRandomUserCreateDto();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }


    @Test
    void shouldNotViolateConstraintWhenNameLengthIs255() {
        final UserCreateDto dto = getRandomUserCreateDto();
        dto.setName("a".repeat(255));

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }


    @Test
    void shouldNotViolateConstraintWhenEmailLengthIs255() {
        final UserCreateDto dto = getRandomUserCreateDto();
        final String longEmail = "a".repeat(63) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        dto.setEmail(longEmail);

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    private UserCreateDto getRandomUserCreateDto() {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }
}