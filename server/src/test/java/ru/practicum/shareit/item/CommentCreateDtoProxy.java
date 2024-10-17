package ru.practicum.shareit.item;

import java.util.Objects;

class CommentCreateDtoProxy extends CommentCreateDto {

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
        final CommentCreateDtoProxy other = (CommentCreateDtoProxy) obj;
        return Objects.equals(this.getText(), other.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getText());
    }
}
