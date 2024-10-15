package ru.practicum.shareit.user;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.common.CommonUtils.deepEqualTo;
import static ru.practicum.shareit.user.UserUtils.deepEqualTo;
import static ru.practicum.shareit.common.CommonUtils.makeTestNewUser;
import static ru.practicum.shareit.common.CommonUtils.makeTestSavedUser;
import static ru.practicum.shareit.user.UserUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserPatch;
import static ru.practicum.shareit.user.UserUtils.makeTestUserRetrieveDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserUpdateDto;

class UserControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(UserController.class);

    private static final long USER_ID = 1L;

    @Mock
    private UserService mockService;

    @Mock
    private UserMapper mockMapper;

    @Captor
    private ArgumentCaptor<UserCreateDto> userCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<UserUpdateDto> userUpdateDtoCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<List<User>> usersCaptor;

    @Captor
    private ArgumentCaptor<UserPatch> userPatchCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private InOrder inOrder;

    private UserController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new UserController(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        inOrder = inOrder(mockMapper, mockService);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void testCreateUser() throws JSONException, IOException {
        when(mockMapper.mapToUser(any(UserCreateDto.class))).thenReturn(makeTestNewUser());
        when(mockService.createUser(any(User.class))).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.createUser(makeTestUserCreateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToUser(userCreateDtoCaptor.capture());
        assertThat(userCreateDtoCaptor.getValue(), deepEqualTo(makeTestUserCreateDto()));
        inOrder.verify(mockService).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue(), deepEqualTo(makeTestNewUser()));
        inOrder.verify(mockMapper).mapToDto(userCaptor.capture());
        assertThat(userCaptor.getValue(), deepEqualTo(makeTestSavedUser()));
        assertThat(actual, deepEqualTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void testGetUser() throws JSONException, IOException {
        when(mockService.getUser(USER_ID)).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.getUser(USER_ID, mockHttpRequest);

        inOrder.verify(mockService).getUser(USER_ID);
        inOrder.verify(mockMapper).mapToDto(userCaptor.capture());
        assertThat(userCaptor.getValue(), deepEqualTo(makeTestSavedUser()));
        assertThat(actual, deepEqualTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_user.json", getClass());
    }

    @Test
    void testGetUsers() throws JSONException, IOException {
        when(mockService.getAllUsers()).thenReturn(List.of(makeTestSavedUser()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestUserRetrieveDto()));

        final List<UserRetrieveDto> actual = controller.getUsers(mockHttpRequest);

        inOrder.verify(mockService).getAllUsers();
        inOrder.verify(mockMapper).mapToDto(usersCaptor.capture());
        assertThat(usersCaptor.getValue(), notNullValue());
        assertThat(usersCaptor.getValue().size(), equalTo(1));
        assertThat(usersCaptor.getValue().getFirst(), deepEqualTo(makeTestSavedUser()));
        assertThat(actual, notNullValue());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.getFirst(), deepEqualTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_users.json", getClass());
    }

    @Test
    void testPatchUser() throws JSONException, IOException {
        when(mockMapper.mapToPatch(anyLong(), any(UserUpdateDto.class))).thenReturn(makeTestUserPatch());
        when(mockService.patchUser(any(UserPatch.class))).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.patchUser(USER_ID, makeTestUserUpdateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToPatch(userIdCaptor.capture(), userUpdateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(userUpdateDtoCaptor.getValue(), deepEqualTo(makeTestUserUpdateDto()));
        inOrder.verify(mockService).patchUser(userPatchCaptor.capture());
        assertThat(userPatchCaptor.getValue(), deepEqualTo(makeTestUserPatch()));
        inOrder.verify(mockMapper).mapToDto(userCaptor.capture());
        assertThat(userCaptor.getValue(), deepEqualTo(makeTestSavedUser()));
        assertThat(actual, deepEqualTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "update_users.json", getClass());
    }

    @Test
    void testDeleteUser() throws JSONException, IOException {
        doNothing().when(mockService).deleteUser(USER_ID);

        controller.deleteUser(USER_ID, mockHttpRequest);

        verify(mockService).deleteUser(USER_ID);
        assertLogs(logListener.getEvents(), "delete_users.json", getClass());
    }
}