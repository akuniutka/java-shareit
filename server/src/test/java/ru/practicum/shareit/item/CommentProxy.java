package ru.practicum.shareit.item;

import java.util.Objects;

class CommentProxy extends Comment {

    CommentProxy withId(final Long id) {
        super.setId(id);
        return this;
    }

    CommentProxy withAuthorName(final String authorName) {
        super.getAuthor().setName(authorName);
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CommentProxy other = (CommentProxy) obj;
        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getItem(), other.getItem())
                && Objects.equals(this.getAuthor(), other.getAuthor())
                && (Objects.isNull(this.getAuthor())
                        || Objects.equals(this.getAuthor().getName(), other.getAuthor().getName()))
                && Objects.equals(this.getText(), other.getText())
                && Objects.equals(this.getCreated(), other.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getItem(), this.getAuthor(), this.getText(), this.getCreated());
    }
}
