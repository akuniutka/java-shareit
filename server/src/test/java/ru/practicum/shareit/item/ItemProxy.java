package ru.practicum.shareit.item;

import java.util.Objects;

class ItemProxy extends Item {

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
        final ItemProxy other = (ItemProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getOwner(), other.getOwner())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getDescription(), other.getDescription())
                && Objects.equals(this.getAvailable(), other.getAvailable())
                && Objects.equals(this.getLastBooking(), other.getLastBooking())
                && Objects.equals(this.getNextBooking(), other.getNextBooking())
                && Objects.equals(this.getComments(), other.getComments())
                && Objects.equals(this.getRequest(), other.getRequest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getOwner(), this.getName(), this.getDescription(), this.getAvailable(),
                this.getLastBooking(), this.getNextBooking(), this.getComments(), this.getRequest());
    }
}
