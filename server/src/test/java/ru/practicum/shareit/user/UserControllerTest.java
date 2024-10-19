package ru.practicum.shareit.user;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.user.UserUtils.makeTestUser;
import static ru.practicum.shareit.user.UserUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserPatch;
import static ru.practicum.shareit.user.UserUtils.makeTestUserRetrieveDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserUpdateDto;

class UserControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(UserController.class);

    @Mock
    private UserService mockService;

    @Mock
    private UserMapper mockMapper;

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
        when(mockMapper.mapToUser(makeTestUserCreateDto())).thenReturn(makeTestUser().withNoId());
        when(mockService.createUser(makeTestUser().withNoId())).thenReturn(makeTestUser());
        when(mockMapper.mapToDto(makeTestUser())).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.createUser(makeTestUserCreateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToUser(makeTestUserCreateDto());
        inOrder.verify(mockService).createUser(makeTestUser().withNoId());
        inOrder.verify(mockMapper).mapToDto(makeTestUser());
        assertThat(actual, equalTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void testGetUser() throws JSONException, IOException {
        when(mockService.getUser(USER_ID)).thenReturn(makeTestUser());
        when(mockMapper.mapToDto(makeTestUser())).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.getUser(USER_ID, mockHttpRequest);

        inOrder.verify(mockService).getUser(USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeTestUser());
        assertThat(actual, equalTo(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_user.json", getClass());
    }

    @Test
    void testGetUsers() throws JSONException, IOException {
        when(mockService.getAllUsers()).thenReturn(List.of(makeTestUser()));
        when(mockMapper.mapToDto(List.of(makeTestUser()))).thenReturn(List.of(makeTestUserRetrieveDto()));

        final List<UserRetrieveDto> actual = controller.getUsers(mockHttpRequest);

        inOrder.verify(mockService).getAllUsers();
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestUser()));
        assertThat(actual, contains(makeTestUserRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_users.json", getClass());
    }

    @Test
    void testPatchUser() throws JSONException, IOException {
        when(mockMapper.mapToPatch(USER_ID, makeTestUserUpdateDto())).thenReturn(makeTestUserPatch());
        when(mockService.patchUser(makeTestUserPatch())).thenReturn(makeTestUser());
        when(mockMapper.mapToDto(makeTestUser())).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.patchUser(USER_ID, makeTestUserUpdateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToPatch(USER_ID, makeTestUserUpdateDto());
        inOrder.verify(mockService).patchUser(makeTestUserPatch());
        inOrder.verify(mockMapper).mapToDto(makeTestUser());
        assertThat(actual, equalTo(makeTestUserRetrieveDto()));
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