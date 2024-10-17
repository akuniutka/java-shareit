package ru.practicum.shareit.user;

import java.util.Objects;

class UserProxy extends User {

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
        final UserProxy other = (UserProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getEmail());
    }
}
