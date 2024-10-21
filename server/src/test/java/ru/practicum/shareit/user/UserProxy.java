package ru.practicum.shareit.user;

import java.util.Objects;

class UserProxy extends User {

    UserProxy withNoId() {
        super.setId(null);
        return this;
    }

    UserProxy withNoName() {
        super.setName(null);
        return this;
    }

    UserProxy withNoEmail() {
        super.setEmail(null);
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
        if (!(obj instanceof User other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getEmail());
    }
}
