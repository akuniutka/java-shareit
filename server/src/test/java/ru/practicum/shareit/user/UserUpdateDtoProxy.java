package ru.practicum.shareit.user;

import java.util.Objects;

class UserUpdateDtoProxy extends UserUpdateDto {

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
        final UserUpdateDtoProxy other = (UserUpdateDtoProxy) obj;
        return Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getEmail());
    }
}
