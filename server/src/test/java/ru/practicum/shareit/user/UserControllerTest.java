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
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.user.UserUtils.makeUserCreateDtoProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserPatchProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserRetrieveDtoProxy;
import static ru.practicum.shareit.user.UserUtils.makeUserUpdateDtoProxy;

class UserControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(UserController.class);

    private static final long USER_ID = 1L;

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
        when(mockMapper.mapToUser(makeUserCreateDtoProxy())).thenReturn(makeUserProxy());
        when(mockService.createUser(makeUserProxy())).thenReturn(makeUserProxy());
        when(mockMapper.mapToDto(makeUserProxy())).thenReturn(makeUserRetrieveDtoProxy());

        final UserRetrieveDto actual = controller.createUser(makeUserCreateDtoProxy(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToUser(makeUserCreateDtoProxy());
        inOrder.verify(mockService).createUser(makeUserProxy());
        inOrder.verify(mockMapper).mapToDto(makeUserProxy());
        assertThat(actual, equalTo(makeUserRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void testGetUser() throws JSONException, IOException {
        when(mockService.getUser(USER_ID)).thenReturn(makeUserProxy());
        when(mockMapper.mapToDto(makeUserProxy())).thenReturn(makeUserRetrieveDtoProxy());

        final UserRetrieveDto actual = controller.getUser(USER_ID, mockHttpRequest);

        inOrder.verify(mockService).getUser(USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeUserProxy());
        assertThat(actual, equalTo(makeUserRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_user.json", getClass());
    }

    @Test
    void testGetUsers() throws JSONException, IOException {
        when(mockService.getAllUsers()).thenReturn(List.of(makeUserProxy()));
        when(mockMapper.mapToDto(List.of(makeUserProxy()))).thenReturn(List.of(makeUserRetrieveDtoProxy()));

        final List<UserRetrieveDto> actual = controller.getUsers(mockHttpRequest);

        inOrder.verify(mockService).getAllUsers();
        inOrder.verify(mockMapper).mapToDto(List.of(makeUserProxy()));
        assertThat(actual, contains(makeUserRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_users.json", getClass());
    }

    @Test
    void testPatchUser() throws JSONException, IOException {
        when(mockMapper.mapToPatch(USER_ID, makeUserUpdateDtoProxy())).thenReturn(makeUserPatchProxy());
        when(mockService.patchUser(makeUserPatchProxy())).thenReturn(makeUserProxy());
        when(mockMapper.mapToDto(makeUserProxy())).thenReturn(makeUserRetrieveDtoProxy());

        final UserRetrieveDto actual = controller.patchUser(USER_ID, makeUserUpdateDtoProxy(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToPatch(USER_ID, makeUserUpdateDtoProxy());
        inOrder.verify(mockService).patchUser(makeUserPatchProxy());
        inOrder.verify(mockMapper).mapToDto(makeUserProxy());
        assertThat(actual, equalTo(makeUserRetrieveDtoProxy()));
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