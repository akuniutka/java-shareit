package ru.practicum.shareit.user;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.user.UserUtils.deepEqualTo;
import static ru.practicum.shareit.user.UserUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserUpdateDto;

public class UserControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(UserController.class);

    private static final long USER_ID = 1L;

    @Mock
    private UserClient client;

    @Captor
    private ArgumentCaptor<UserCreateDto> userCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<UserUpdateDto> userUpdateDtoCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private UserController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new UserController(client);
        logListener.startListen();
        logListener.reset();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(client);
        super.tearDown();
    }

    @Test
    void testCreateUser() throws JSONException, IOException {
        when(client.createUser(any(UserCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createUser(makeTestUserCreateDto(), mockHttpRequest);

        verify(client).createUser(userCreateDtoCaptor.capture());
        assertThat(userCreateDtoCaptor.getValue(), deepEqualTo(makeTestUserCreateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void testGetUser() throws JSONException, IOException {
        when(client.getUser(USER_ID)).thenReturn(testResponse);

        final Object actual = controller.getUser(USER_ID, mockHttpRequest);

        verify(client).getUser(USER_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_user.json", getClass());
    }

    @Test
    void testGetUsers() throws JSONException, IOException {
        when(client.getUsers()).thenReturn(testResponse);

        final Object actual = controller.getUsers(mockHttpRequest);

        verify(client).getUsers();
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_users.json", getClass());
    }

    @Test
    void testUpdateUser() throws JSONException, IOException {
        when(client.updateUser(any(long.class), any(UserUpdateDto.class))).thenReturn(testResponse);

        final Object actual = controller.updateUser(USER_ID, makeTestUserUpdateDto(), mockHttpRequest);

        verify(client).updateUser(userIdCaptor.capture(), userUpdateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(userUpdateDtoCaptor.getValue(), deepEqualTo(makeTestUserUpdateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "update_user.json", getClass());
    }

    @Test
    void testDeleteUser() throws JSONException, IOException {
        doNothing().when(client).deleteUser(USER_ID);

        controller.deleteUser(USER_ID, mockHttpRequest);

        verify(client).deleteUser(USER_ID);
        assertLogs(logListener.getEvents(), "delete_user.json", getClass());
    }
}
