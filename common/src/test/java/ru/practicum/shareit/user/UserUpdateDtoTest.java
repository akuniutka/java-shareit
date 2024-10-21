package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUpdateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenNameLengthExceeds255() {
        final UserUpdateDto dto = makeTestUserUpdateDto().toBuilder()
                .name("a".repeat(256))
                .build();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenEmailMalformed() {
        final UserUpdateDto dto = makeTestUserUpdateDto().toBuilder()
                .email("not_an_email")
                .build();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenEmailLengthExceed255() {
        final String longEmail = "a".repeat(64) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        final UserUpdateDto dto = makeTestUserUpdateDto().toBuilder()
                .email(longEmail)
                .build();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldNotViolateConstraintWhenCorrectUserUpdateDto() {
        final UserUpdateDto dto = makeTestUserUpdateDto();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotViolateConstraintWhenNameLengthIs255() {
        final UserUpdateDto dto = makeTestUserUpdateDto().toBuilder()
                .name("a".repeat(255))
                .build();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotViolateConstraintWhenEmailLengthIs255() {
        final String longEmail = "a".repeat(63) + "@" + "a".repeat(63) + "." + "a".repeat(63) + "." + "a".repeat(63);
        final UserUpdateDto dto = makeTestUserUpdateDto().toBuilder()
                .email(longEmail)
                .build();

        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    private UserUpdateDto makeTestUserUpdateDto() {
        return UserUpdateDto.builder()
                .name("John Doe")
                .email("john_doe@mail.com")
                .build();
    }
}