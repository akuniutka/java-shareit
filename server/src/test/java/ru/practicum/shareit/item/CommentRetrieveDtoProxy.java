package ru.practicum.shareit.item;

import java.util.Objects;

class CommentRetrieveDtoProxy extends CommentRetrieveDto {

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
        final CommentRetrieveDtoProxy other = (CommentRetrieveDtoProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getAuthorName(), other.getAuthorName())
                && Objects.equals(this.getText(), other.getText())
                && Objects.equals(this.getCreated(), other.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getAuthorName(), this.getText(), this.getCreated());
    }
}
