package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.shareit.item.ItemUtils.makeCommentCreateDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeCommentProxy;
import static ru.practicum.shareit.item.ItemUtils.makeTestComment;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentRetrieveDto;
import static ru.practicum.shareit.item.ItemUtils.samePropertyValuesAs;

class CommentMapperImplTest {

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 13L;

    private CommentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CommentMapperImpl();
    }

    @Test
    void testMapToComment() {
        final Comment expected = makeTestComment();
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(USER_ID, ITEM_ID, makeCommentCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenUserIdNull() {
        final Comment expected = makeTestComment();
        expected.setAuthor(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(null, ITEM_ID, makeCommentCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenItemIdNull() {
        final Comment expected = makeTestComment();
        expected.setItem(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(USER_ID, null, makeCommentCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenDtoNull() {
        final Comment expected = makeTestComment();
        expected.setText(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(USER_ID, ITEM_ID, null);

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenUserIdNullAndItemIdNull() {
        final Comment expected = makeTestComment();
        expected.setAuthor(null);
        expected.setItem(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(null, null, makeCommentCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenUserIdNullAndDtoNull() {
        final Comment expected = makeTestComment();
        expected.setAuthor(null);
        expected.setText(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(null, ITEM_ID, null);

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenItemIdNullAndDtoNull() {
        final Comment expected = makeTestComment();
        expected.setItem(null);
        expected.setText(null);
        final LocalDateTime from = LocalDateTime.now();

        final Comment actual = mapper.mapToComment(USER_ID, null, null);

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToCommentWhenUserIdNullAndItemIdNullAndDtoNull() {
        final Comment actual = mapper.mapToComment(null, null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleComment() {
        final CommentRetrieveDto expected = makeTestCommentRetrieveDto();

        final CommentRetrieveDto actual = mapper.mapToDto(makeCommentProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleCommentAndAuthorNull() {
        final CommentRetrieveDto expected = makeTestCommentRetrieveDto();
        expected.setAuthorName(null);
        final Comment comment = makeCommentProxy();
        comment.setAuthor(null);

        final CommentRetrieveDto actual = mapper.mapToDto(comment);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleCommentNull() {
        final CommentRetrieveDto actual = mapper.mapToDto((Comment) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenCommentSet() {
        final CommentRetrieveDto expected = makeTestCommentRetrieveDto();

        final Set<CommentRetrieveDto> actual = mapper.mapToDto(Set.of(makeCommentProxy()));

        assertThat(actual, contains(samePropertyValuesAs(expected)));
    }

    @Test
    void testMapToDtoWhenCommentSetNull() {
        final Set<CommentRetrieveDto> actual = mapper.mapToDto((Set<Comment>) null);

        assertThat(actual, nullValue());
    }
}