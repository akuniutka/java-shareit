package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class ItemRequestUtils {

    private ItemRequestUtils() {
    }

    static ItemRequestCreateDto makeTestItemRequestCreateDto() {
        final ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need the thing");
        return dto;
    }

    static void assertItemRequestCreateDtoEqual(final ItemRequestCreateDto expected,
            final ItemRequestCreateDto actual
    ) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getDescription(), actual.getDescription())
        );
    }
}
