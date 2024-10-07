package ru.practicum.shareit.common.exception;

public class ActionNotAllowedException extends RuntimeException {

    public ActionNotAllowedException(final String message) {
        super(message);
    }
}
