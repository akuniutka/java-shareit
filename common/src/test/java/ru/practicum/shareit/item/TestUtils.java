package ru.practicum.shareit.item;

final class TestUtils {

    private TestUtils() {
    }

    static ItemCreateDto makeTestItemCreateDto() {
        final ItemCreateDto dto = new ItemCreateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        dto.setRequestId(42L);
        return dto;
    }
}
