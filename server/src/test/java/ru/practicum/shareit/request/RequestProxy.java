package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;

class RequestProxy extends Request {

    RequestProxy withNoId() {
        super.setId(null);
        return this;
    }

    RequestProxy withNoRequester() {
        super.setRequester(null);
        return this;
    }

    RequestProxy withNoDescription() {
        super.setDescription(null);
        return this;
    }

    RequestProxy withCreated(final LocalDateTime created) {
        super.setCreated(created);
        return this;
    }

    RequestProxy withEmptyItems() {
        super.setItems(new HashSet<>());
        return this;
    }

    RequestProxy withNoItems() {
        super.setItems(null);
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Request other)) {
            return false;
        }
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
