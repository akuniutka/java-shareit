package ru.practicum.shareit.request;

import java.util.Objects;

class ItemRequestCreateDtoProxy extends ItemRequestCreateDto {

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
        final ItemRequestCreateDtoProxy other = (ItemRequestCreateDtoProxy) obj;
        return Objects.equals(this.getDescription(), other.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDescription());
    }
}
