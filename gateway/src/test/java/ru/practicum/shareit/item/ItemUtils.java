package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class ItemUtils {

    private ItemUtils() {
    }

    static ItemCreateDto makeTestItemCreateDto() {
        final ItemCreateDto dto = new ItemCreateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        dto.setRequestId(42L);
        return dto;
    }

    static void assertItemCreateDtoEqual(final ItemCreateDto expected, final ItemCreateDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getAvailable(), actual.getAvailable()),
                () -> assertEquals(expected.getRequestId(), actual.getRequestId())
        );
    }

    static CommentCreateDto makeTestCommentCreateDto() {
        final CommentCreateDto dto = new CommentCreateDto();
        dto.setText("This is first comment");
        return dto;
    }

    static void assertCommentCreateDtoEqual(final CommentCreateDto expected, final CommentCreateDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getText(), actual.getText())
        );
    }

    static ItemUpdateDto makeTestItemUpdateDto() {
        final ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        return dto;
    }

    static void assertItemUpdateDtoEqual(final ItemUpdateDto expected, final ItemUpdateDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getAvailable(), actual.getAvailable())
        );
    }
}
