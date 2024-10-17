package ru.practicum.shareit.request;

import java.util.Objects;

class ItemRequestProxy extends ItemRequest {

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
        final ItemRequestProxy other = (ItemRequestProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getRequester(), other.getRequester())
                && Objects.equals(this.getDescription(), other.getDescription())
                && Objects.equals(this.getCreated(), other.getCreated())
                && Objects.equals(this.getItems(), other.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getRequester(), this.getDescription(), this.getCreated(),
                this.getItems());
    }
}
