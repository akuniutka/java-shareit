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
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .name(name)
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenNameLengthExceeds255() {
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .name("a".repeat(256))
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "not_an_email"})
    void shouldViolateConstraintWhenEmailNullOrBlankOrMalformed(final String email) {
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .email(email)
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenEmailLengthExceed255() {
        final String longEmail = "a".repeat(64) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .email(longEmail)
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldNotViolateConstraintWhenCorrectUserCreateDto() {
        final UserCreateDto dto = makeTestUserCreateDto();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotViolateConstraintWhenNameLengthIs255() {
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .name("a".repeat(255))
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotViolateConstraintWhenEmailLengthIs255() {
        final String longEmail = "a".repeat(63) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        final UserCreateDto dto = makeTestUserCreateDto().toBuilder()
                .email(longEmail)
                .build();

        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    private UserCreateDto makeTestUserCreateDto() {
        return UserCreateDto.builder()
                .name("John Doe")
                .email("john_doe@mail.com")
                .build();
    }
}