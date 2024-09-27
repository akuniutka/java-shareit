package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
interface BookingMapper {

    @Mapping(source = "itemId", target = "item.id")
    Booking mapToBooking(BookingCreateDto dto);

    BookingRetrieveDto mapToDto(Booking booking);

    List<BookingRetrieveDto> mapToDto(List<Booking> bookings);
}
