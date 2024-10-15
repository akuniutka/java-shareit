package ru.practicum.shareit.item;

import java.util.Objects;

class ItemCreateDtoProxy extends ItemCreateDto {

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
        final ItemCreateDtoProxy other = (ItemCreateDtoProxy) obj;
        return Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getDescription(), other.getDescription())
                && Objects.equals(this.getAvailable(), other.getAvailable())
                && Objects.equals(this.getRequestId(), other.getRequestId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getDescription(), this.getAvailable(), this.getRequestId());
    }
}
