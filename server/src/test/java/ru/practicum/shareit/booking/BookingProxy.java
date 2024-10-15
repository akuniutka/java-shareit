package ru.practicum.shareit.booking;

import java.util.Objects;

class BookingProxy extends Booking {

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
        final BookingProxy other = (BookingProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getItem(), other.getItem())
                && Objects.equals(this.getBooker(), other.getBooker())
                && Objects.equals(this.getStart(), other.getStart())
                && Objects.equals(this.getEnd(), other.getEnd())
                && Objects.equals(this.getStatus(), other.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getItem(), this.getBooker(), this.getStart(), this.getEnd(),
                this.getStatus());
    }
}
