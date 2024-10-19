package ru.practicum.shareit.user;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.common.LogListener;
import ru.practicum.shareit.common.exception.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.user.UserUtils.deepEqualTo;
import static ru.practicum.shareit.user.UserUtils.makeTestUser;
import static ru.practicum.shareit.user.UserUtils.makeTestUserPatch;

class UserServiceImplTest {

    private static final LogListener logListener = new LogListener(UserServiceImpl.class);

    private AutoCloseable openMocks;

    @Mock
    private UserRepository mockRepository;

    private InOrder inOrder;

    private UserService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new UserServiceImpl(mockRepository);
        logListener.startListen();
        logListener.reset();
        inOrder = Mockito.inOrder(mockRepository);
    }

    @AfterEach()
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository);
        openMocks.close();
    }

    @Test
    void testCreateUser() throws JSONException, IOException {
        when(mockRepository.save(makeTestUser().withNoId())).thenReturn(makeTestUser());

        final User actual = service.createUser(makeTestUser().withNoId());

        verify(mockRepository).save(makeTestUser().withNoId());
        assertThat(actual, deepEqualTo(makeTestUser()));
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void testCreateUserWhenUserIsNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createUser(null));

        assertThat(exception.getMessage(), equalTo("Cannot create user: is null"));
    }

    @Test
    void testGetUser() {
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.of(makeTestUser()));

        final User actual = service.getUser(USER_ID);

        verify(mockRepository).findById(USER_ID);
        assertThat(actual, deepEqualTo(makeTestUser()));
    }

    @Test
    void testGetUserWhenNotFound() {
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getUser(USER_ID));

        verify(mockRepository).findById(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testGetAllUsers() {
        when(mockRepository.findAll()).thenReturn(List.of(makeTestUser()));

        final List<User> actual = service.getAllUsers();

        verify(mockRepository).findAll();
        assertThat(actual, contains(deepEqualTo(makeTestUser())));
    }

    @Test
    void testGetAllUsersWhenEmpty() {
        when(mockRepository.findAll()).thenReturn(List.of());

        final List<User> actual = service.getAllUsers();

        verify(mockRepository).findAll();
        assertThat(actual, empty());
    }

    @Test
    void testExistsByIdWhenTrue() {
        when(mockRepository.existsById(USER_ID)).thenReturn(true);

        final boolean actual = service.existsById(USER_ID);

        verify(mockRepository).existsById(USER_ID);
        assertThat(actual, equalTo(true));
    }

    @Test
    void testExistsByIdWhenFalse() {
        when(mockRepository.existsById(USER_ID)).thenReturn(false);

        final boolean actual = service.existsById(USER_ID);

        verify(mockRepository).existsById(USER_ID);
        assertThat(actual, equalTo(false));
    }

    @Test
    void testPatchUser() throws JSONException, IOException {
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.of(makeTestUser().withNoName().withNoEmail()));
        when(mockRepository.save(makeTestUser())).thenReturn(makeTestUser());

        final User actual = service.patchUser(makeTestUserPatch());

        inOrder.verify(mockRepository).findById(USER_ID);
        inOrder.verify(mockRepository).save(makeTestUser());
        assertThat(actual, deepEqualTo(makeTestUser()));
        assertLogs(logListener.getEvents(), "patch_user.json", getClass());
    }

    @Test
    void testPatchUserWhenOnlyName() throws JSONException, IOException {
        final UserPatch patch = makeTestUserPatch();
        patch.setEmail(null);
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.of(makeTestUser().withNoName()));
        when(mockRepository.save(makeTestUser())).thenReturn(makeTestUser());

        final User actual = service.patchUser(patch);

        inOrder.verify(mockRepository).findById(USER_ID);
        inOrder.verify(mockRepository).save(makeTestUser());
        assertThat(actual, deepEqualTo(makeTestUser()));
        assertLogs(logListener.getEvents(), "patch_user_name.json", getClass());
    }

    @Test
    void testPatchUserWhenOnlyEmail() throws JSONException, IOException {
        final UserPatch patch = makeTestUserPatch();
        patch.setName(null);
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.of(makeTestUser().withNoEmail()));
        when(mockRepository.save(makeTestUser())).thenReturn(makeTestUser());

        final User actual = service.patchUser(patch);

        inOrder.verify(mockRepository).findById(USER_ID);
        inOrder.verify(mockRepository).save(makeTestUser());
        assertThat(actual, deepEqualTo(makeTestUser()));
        assertLogs(logListener.getEvents(), "patch_user_email.json", getClass());
    }

    @Test
    void testPatchUserWhenNothingToPatch() throws JSONException, IOException {
        final UserPatch patch = new UserPatch();
        patch.setUserId(USER_ID);
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.of(makeTestUser()));
        when(mockRepository.save(makeTestUser())).thenReturn(makeTestUser());

        final User actual = service.patchUser(patch);

        inOrder.verify(mockRepository).findById(USER_ID);
        inOrder.verify(mockRepository).save(makeTestUser());
        assertThat(actual, deepEqualTo(makeTestUser()));
        assertLogs(logListener.getEvents(), "patch_user_nothing.json", getClass());
    }

    @Test
    void testPatchUserWhenPatchIsNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class, () -> service.patchUser(null));

        assertThat(exception.getMessage(), equalTo("Cannot patch user: is null"));
    }

    @Test
    void testPatchUserWhenUserNotFound() {
        when(mockRepository.findById(USER_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.patchUser(makeTestUserPatch()));

        verify(mockRepository).findById(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testDeleteUser() throws JSONException, IOException {
        when(mockRepository.delete(USER_ID)).thenReturn(1);

        service.deleteUser(USER_ID);

        verify(mockRepository).delete(USER_ID);
        assertLogs(logListener.getEvents(), "delete_user.json", getClass());
    }

    @Test
    void testDeleteUserWhenUserNotFound() throws JSONException, IOException {
        when(mockRepository.delete(USER_ID)).thenReturn(0);

        service.deleteUser(USER_ID);

        verify(mockRepository).delete(USER_ID);
        assertLogs(logListener.getEvents(), "delete_user_not_found.json", getClass());
    }
}