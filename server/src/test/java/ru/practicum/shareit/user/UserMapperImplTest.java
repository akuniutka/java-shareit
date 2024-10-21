package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.user.UserUtils.deepEqualTo;
import static ru.practicum.shareit.user.UserUtils.makeTestUser;
import static ru.practicum.shareit.user.UserUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserPatch;
import static ru.practicum.shareit.user.UserUtils.makeTestUserRetrieveDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserUpdateDto;

public class UserMapperImplTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapperImpl();
    }

    @Test
    void testMapToUser() {
        final User actual = mapper.mapToUser(makeTestUserCreateDto());

        assertThat(actual, deepEqualTo(makeTestUser().withNoId()));
    }

    @Test
    void testMapToUserWhenNull() {
        final User actual = mapper.mapToUser(null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToPatch() {
        final UserPatch actual = mapper.mapToPatch(USER_ID, makeTestUserUpdateDto());

        assertThat(actual, equalTo(makeTestUserPatch()));
    }

    @Test
    void testMapToPatchWhenUserIdNullAndDtoNull() {
        final UserPatch actual = mapper.mapToPatch(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToPatchWhenUserIdNull() {
        final UserPatch expected = makeTestUserPatch();
        expected.setUserId(null);

        final UserPatch actual = mapper.mapToPatch(null, makeTestUserUpdateDto());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void testMapToPatchWheDtoIsNull() {
        final UserPatch expected = new UserPatch();
        expected.setUserId(USER_ID);

        final UserPatch actual = mapper.mapToPatch(USER_ID, null);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void testMapToDtoWhenSingleUser() {
        final UserRetrieveDto actual = mapper.mapToDto(makeTestUser());

        assertThat(actual, equalTo(makeTestUserRetrieveDto()));
    }

    @Test
    void testMapToDtoWhenSingleUserNull() {
        final UserRetrieveDto actual = mapper.mapToDto((User) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenUserList() {
        final List<UserRetrieveDto> actual = mapper.mapToDto(List.of(makeTestUser()));

        assertThat(actual, contains(makeTestUserRetrieveDto()));
    }

    @Test
    void testMapToDtoWhenUserListNull() {
        final List<UserRetrieveDto> actual = mapper.mapToDto((List<User>) null);

        assertThat(actual, nullValue());
    }
}
