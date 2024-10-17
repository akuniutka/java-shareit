package ru.practicum.shareit.user;

import java.util.Objects;

class UserPatchProxy extends UserPatch {

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
        final UserPatchProxy other = (UserPatchProxy) obj;
        return Objects.equals(this.getUserId(), other.getUserId())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId(), this.getName(), this.getEmail());
    }
}
