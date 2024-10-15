package ru.practicum.shareit.request;

import java.util.Objects;

class ItemRequestRetrieveDtoProxy extends ItemRequestRetrieveDto {

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
        final ItemRequestRetrieveDtoProxy other = (ItemRequestRetrieveDtoProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getDescription(), other.getDescription())
                && Objects.equals(this.getCreated(), other.getCreated())
                && Objects.equals(this.getItems(), other.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getDescription(), this.getCreated(), this.getItems());
    }
}
