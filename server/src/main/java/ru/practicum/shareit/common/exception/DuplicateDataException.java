package ru.practicum.shareit.common.exception;

public class DuplicateDataException extends RuntimeException {

    public DuplicateDataException(final String message) {
        super(message);
    }
}
