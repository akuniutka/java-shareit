package ru.practicum.shareit.user;

import java.util.Objects;

class UserCreateDtoProxy extends UserCreateDto {

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
        final UserCreateDtoProxy other = (UserCreateDtoProxy) obj;
        return Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getEmail());
    }
}
