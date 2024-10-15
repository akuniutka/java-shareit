package ru.practicum.shareit.item;

import java.util.Objects;

class CommentProxy extends Comment {

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
        final CommentProxy other = (CommentProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getItem(), other.getItem())
                && Objects.equals(this.getAuthor(), other.getAuthor())
                && Objects.equals(this.getText(), other.getText())
                && Objects.equals(this.getCreated(), other.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getItem(), this.getAuthor(), this.getText(), this.getCreated());
    }
}
