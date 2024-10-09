package ru.practicum.shareit.user;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.TestUtils.assertCreateDtoEqual;
import static ru.practicum.shareit.user.TestUtils.assertRetrieveDtoEqual;
import static ru.practicum.shareit.user.TestUtils.assertUpdateDtoEqual;
import static ru.practicum.shareit.user.TestUtils.assertUserEqual;
import static ru.practicum.shareit.user.TestUtils.assertUserPatchEqual;
import static ru.practicum.shareit.user.TestUtils.makeTestNewUser;
import static ru.practicum.shareit.user.TestUtils.makeTestSavedUser;
import static ru.practicum.shareit.user.TestUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.TestUtils.makeTestUserPatch;
import static ru.practicum.shareit.user.TestUtils.makeTestUserRetrieveDto;
import static ru.practicum.shareit.user.TestUtils.makeTestUserUpdateDto;

class UserControllerTest {

    private AutoCloseable openMocks;

    @Mock
    private UserService mockService;

    @Mock
    private UserMapper mockMapper;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Captor
    private ArgumentCaptor<UserCreateDto> userCreateDtoCaptured;

    @Captor
    private ArgumentCaptor<UserUpdateDto> userUpdateDtoCaptured;

    @Captor
    private ArgumentCaptor<User> userCaptured;

    @Captor
    private ArgumentCaptor<List<User>> usersCaptured;

    @Captor
    private ArgumentCaptor<UserPatch> userPatchCaptured;

    @Captor
    private ArgumentCaptor<Long> idCaptured;

    private UserController controller;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        controller = new UserController(mockService, mockMapper);
        when(mockHttpRequest.getMethod()).thenReturn(null);
        when(mockHttpRequest.getRequestURI()).thenReturn(null);
        when(mockHttpRequest.getQueryString()).thenReturn(null);
        when(mockHttpRequest.getHeader("X-Sharer-User-Id")).thenReturn(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verify(mockHttpRequest).getHeader("X-Sharer-User-Id");
        Mockito.verifyNoMoreInteractions(mockService, mockMapper, mockHttpRequest);
        openMocks.close();
    }

    @Test
    void testCreateUser() {
        when(mockMapper.mapToUser(any(UserCreateDto.class))).thenReturn(makeTestNewUser());
        when(mockService.createUser(any(User.class))).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.createUser(makeTestUserCreateDto(), mockHttpRequest);

        InOrder inOrder = inOrder(mockMapper, mockService);
        inOrder.verify(mockMapper).mapToUser(userCreateDtoCaptured.capture());
        assertCreateDtoEqual(makeTestUserCreateDto(), userCreateDtoCaptured.getValue());
        inOrder.verify(mockService).createUser(userCaptured.capture());
        assertUserEqual(makeTestNewUser(), userCaptured.getValue());
        inOrder.verify(mockMapper).mapToDto(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertRetrieveDtoEqual(makeTestUserRetrieveDto(), actual);
    }

    @Test
    void testGetUser() {
        when(mockService.getUser(1L)).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.getUser(1L, mockHttpRequest);

        InOrder inOrder = inOrder(mockService, mockMapper);
        inOrder.verify(mockService).getUser(1L);
        inOrder.verify(mockMapper).mapToDto(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertRetrieveDtoEqual(makeTestUserRetrieveDto(), actual);
    }

    @Test
    void testGetUsers() {
        when(mockService.getAllUsers()).thenReturn(List.of(makeTestSavedUser()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestUserRetrieveDto()));

        final List<UserRetrieveDto> actual = controller.getUsers(mockHttpRequest);

        InOrder inOrder = inOrder(mockService, mockMapper);
        inOrder.verify(mockService).getAllUsers();
        inOrder.verify(mockMapper).mapToDto(usersCaptured.capture());
        assertNotNull(usersCaptured.getValue());
        assertEquals(1, usersCaptured.getValue().size());
        assertUserEqual(makeTestSavedUser(), usersCaptured.getValue().getFirst());
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertRetrieveDtoEqual(makeTestUserRetrieveDto(), actual.getFirst());
    }

    @Test
    void testPatchUser() {
        when(mockMapper.mapToPatch(eq(42L), any(UserUpdateDto.class))).thenReturn(makeTestUserPatch());
        when(mockService.patchUser(any(UserPatch.class))).thenReturn(makeTestSavedUser());
        when(mockMapper.mapToDto(any(User.class))).thenReturn(makeTestUserRetrieveDto());

        final UserRetrieveDto actual = controller.patchUser(42L, makeTestUserUpdateDto(), mockHttpRequest);

        InOrder inOrder = inOrder(mockMapper, mockService);
        inOrder.verify(mockMapper).mapToPatch(idCaptured.capture(), userUpdateDtoCaptured.capture());
        assertEquals(42L, idCaptured.getValue());
        assertUpdateDtoEqual(makeTestUserUpdateDto(), userUpdateDtoCaptured.getValue());
        inOrder.verify(mockService).patchUser(userPatchCaptured.capture());
        assertUserPatchEqual(makeTestUserPatch(), userPatchCaptured.getValue());
        inOrder.verify(mockMapper).mapToDto(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertRetrieveDtoEqual(makeTestUserRetrieveDto(), actual);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(mockService).deleteUser(1L);

        controller.deleteUser(1L, mockHttpRequest);

        verify(mockService).deleteUser(1L);
    }
}