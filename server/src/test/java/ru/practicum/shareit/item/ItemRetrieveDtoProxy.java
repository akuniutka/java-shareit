package ru.practicum.shareit.item;

import java.util.Objects;

class ItemRetrieveDtoProxy extends ItemRetrieveDto {

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
        final ItemRetrieveDtoProxy other = (ItemRetrieveDtoProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getDescription(), other.getDescription())
                && Objects.equals(this.getAvailable(), other.getAvailable())
                && Objects.equals(this.getRequestId(), other.getRequestId())
                && Objects.equals(this.getLastBooking(), other.getLastBooking())
                && Objects.equals(this.getNextBooking(), other.getNextBooking())
                && Objects.equals(this.getComments(), other.getComments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getDescription(), this.getAvailable(),
                this.getRequestId(), this.getLastBooking(), this.getNextBooking(), this.getComments());
    }
}
