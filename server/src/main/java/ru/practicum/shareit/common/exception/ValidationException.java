package ru.practicum.shareit.common.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String property;
    private final String violation;

    public ValidationException(final String property, final String violation) {
        super(property + ": " + violation);
        this.property = property;
        this.violation = violation;
    }
}
