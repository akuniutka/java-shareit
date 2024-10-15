package ru.practicum.shareit.booking;

import java.util.Objects;

class BookingCreateDtoProxy extends BookingCreateDto {

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final BookingCreateDtoProxy other = (BookingCreateDtoProxy) obj;
        return Objects.equals(this.getItemId(), other.getItemId())
                && Objects.equals(this.getStart(), other.getStart())
                && Objects.equals(this.getEnd(), other.getEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getItemId(), this.getStart(), this.getEnd());
    }
}
