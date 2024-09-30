package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
interface BookingMapper {

    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "userId")
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    Booking mapToBooking(BookingCreateDto dto, Long userId);

    BookingRetrieveDto mapToDto(Booking booking);

    List<BookingRetrieveDto> mapToDto(List<Booking> bookings);
}
