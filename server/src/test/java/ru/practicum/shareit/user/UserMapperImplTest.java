package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.shareit.user.UserUtils.makeTestUser;
import static ru.practicum.shareit.user.UserUtils.makeTestUserPatch;
import static ru.practicum.shareit.user.UserUtils.makeTestUserRetrieveDto;
import static ru.practicum.shareit.user.UserUtils.makeUserCreateDtoProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserUpdateDtoProxy;
import static ru.practicum.shareit.user.UserUtils.samePropertyValuesAs;

public class UserMapperImplTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapperImpl();
    }

    @Test
    void testMapToUser() {
        final User expected = makeTestUser();

        final User actual = mapper.mapToUser(makeUserCreateDtoProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToUserWhenNull() {
        final User actual = mapper.mapToUser(null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToPatch() {
        final UserPatch expected = makeTestUserPatch();

        final UserPatch actual = mapper.mapToPatch(expected.getUserId(), makeUserUpdateDtoProxy());

        assertThat(expected, samePropertyValuesAs(actual));
    }

    @Test
    void testMapToPatchWhenNull() {
        final UserPatch actual = mapper.mapToPatch(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleUser() {
        final UserRetrieveDto expected = makeTestUserRetrieveDto();

        final UserRetrieveDto actual = mapper.mapToDto(makeUserProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleUserNull() {
        final UserRetrieveDto actual = mapper.mapToDto((User) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenUserList() {
        final UserRetrieveDto expected = makeTestUserRetrieveDto();

        final List<UserRetrieveDto> actual = mapper.mapToDto(List.of(makeUserProxy()));

        assertThat(actual, contains(samePropertyValuesAs(expected)));
    }

    @Test
    void testMapToDtoWhenUserListNull() {
        final List<UserRetrieveDto> actual = mapper.mapToDto((List<User>) null);

        assertThat(actual, nullValue());
    }
}
