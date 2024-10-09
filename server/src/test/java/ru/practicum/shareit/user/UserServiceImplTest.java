package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.common.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserRepository mockRepository;
    private UserService service;
    private ArgumentCaptor<User> userCaptured;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(UserRepository.class);
        service = new UserServiceImpl(mockRepository);
        userCaptured = ArgumentCaptor.forClass(User.class);
    }

    @AfterEach()
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockRepository);
    }

    @Test
    void testCreateUser() {
        when(mockRepository.save(any(User.class))).thenReturn(makeTestSavedUser());

        final User actual = service.createUser(makeTestNewUser());

        verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestNewUser(), userCaptured.getValue());
        assertUserEqual(makeTestSavedUser(), actual);
    }

    @Test
    void testCreateUserWhenDuplicateEmail() {
        final Exception expected = new DataIntegrityViolationException("duplicate email");
        when(mockRepository.save(any(User.class))).thenThrow(expected);

        final DataIntegrityViolationException actual = assertThrows(DataIntegrityViolationException.class,
                () -> service.createUser(makeTestNewUser()));

        verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestNewUser(), userCaptured.getValue());
        assertEquals(expected, actual);
    }

    @Test
    void testGetUser() {
        when(mockRepository.findById(1L)).thenReturn(Optional.of(makeTestSavedUser()));

        final User actual = service.getUser(1L);

        verify(mockRepository).findById(1L);
        assertUserEqual(makeTestSavedUser(), actual);
    }

    @Test
    void testGetUserWhenNotFound() {
        when(mockRepository.findById(1L)).thenReturn(Optional.empty());

        final NotFoundException actual = assertThrows(NotFoundException.class, () -> service.getUser(1L));

        verify(mockRepository).findById(1L);
        assertEquals("user", actual.getModelName());
        assertEquals(1L, actual.getModelId());
    }

    @Test
    void testGetAllUsers() {
        when(mockRepository.findAll()).thenReturn(List.of(makeTestSavedUser()));

        final List<User> actual = service.getAllUsers();

        verify(mockRepository).findAll();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertUserEqual(makeTestSavedUser(), actual.getFirst());
    }

    @Test
    void testGetAllUsersWhenEmpty() {
        when(mockRepository.findAll()).thenReturn(List.of());

        final List<User> actual = service.getAllUsers();

        verify(mockRepository).findAll();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testExistsByIdWhenTrue() {
        when(mockRepository.existsById(1L)).thenReturn(true);

        final boolean actual = service.existsById(1L);

        verify(mockRepository).existsById(1L);
        assertTrue(actual);
    }

    @Test
    void testExistsByIdWhenFalse() {
        when(mockRepository.existsById(1L)).thenReturn(false);

        final boolean actual = service.existsById(1L);

        verify(mockRepository).existsById(1L);
        assertFalse(actual);
    }

    @Test
    void testPatchUser() {
        final User existingUser = makeTestNewUser();
        existingUser.setId(42L);
        when(mockRepository.findById(42L)).thenReturn(Optional.of(existingUser));
        when(mockRepository.save(any(User.class))).thenReturn(makeTestSavedUser());

        final User actual = service.patchUser(makeTestPatch());

        final InOrder inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findById(42L);
        inOrder.verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertEquals(makeTestSavedUser(), actual);
    }

    @Test
    void testPatchUserWhenOnlyName() {
        final User existingUser = makeTestSavedUser();
        existingUser.setName(makeTestNewUser().getName());
        final UserPatch patch = makeTestPatch();
        patch.setEmail(null);
        when(mockRepository.findById(42L)).thenReturn(Optional.of(existingUser));
        when(mockRepository.save(any(User.class))).thenReturn(makeTestSavedUser());

        final User actual = service.patchUser(makeTestPatch());

        final InOrder inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findById(42L);
        inOrder.verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertEquals(makeTestSavedUser(), actual);
    }

    @Test
    void testPatchUserWhenOnlyEmail() {
        final User existingUser = makeTestSavedUser();
        existingUser.setEmail(makeTestNewUser().getEmail());
        final UserPatch patch = makeTestPatch();
        patch.setName(null);
        when(mockRepository.findById(42L)).thenReturn(Optional.of(existingUser));
        when(mockRepository.save(any(User.class))).thenReturn(makeTestSavedUser());

        final User actual = service.patchUser(makeTestPatch());

        final InOrder inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findById(42L);
        inOrder.verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertEquals(makeTestSavedUser(), actual);
    }

    @Test
    void testPatchUserWhenNothingToPatch() {
        final UserPatch patch = makeTestPatch();
        patch.setName(null);
        patch.setEmail(null);
        when(mockRepository.findById(42L)).thenReturn(Optional.of(makeTestSavedUser()));
        when(mockRepository.save(any(User.class))).thenReturn(makeTestSavedUser());

        final User actual = service.patchUser(makeTestPatch());

        final InOrder inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findById(42L);
        inOrder.verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertEquals(makeTestSavedUser(), actual);
    }

    @Test
    void testPatchUserWhenPatchIsNull() {
        final NullPointerException actual = assertThrows(NullPointerException.class, () -> service.patchUser(null));

        assertEquals("Cannot patch user: is null", actual.getMessage());
    }

    @Test
    void testPatchUserWhenUserNotFound() {
        when(mockRepository.findById(42L)).thenReturn(Optional.empty());

        final NotFoundException actual = assertThrows(NotFoundException.class,
                () -> service.patchUser(makeTestPatch()));

        verify(mockRepository).findById(42L);
        assertEquals("user", actual.getModelName());
        assertEquals(42L, actual.getModelId());
    }

    @Test
    void testPatchUserWhenDuplicateEmail() {
        final User existingUser = makeTestNewUser();
        existingUser.setId(42L);
        final Exception expected = new DataIntegrityViolationException("duplicate email");
        when(mockRepository.findById(42L)).thenReturn(Optional.of(existingUser));
        when(mockRepository.save(any(User.class))).thenThrow(expected);

        final DataIntegrityViolationException actual = assertThrows(DataIntegrityViolationException.class,
                () -> service.patchUser(makeTestPatch()));

        final InOrder inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findById(42L);
        inOrder.verify(mockRepository).save(userCaptured.capture());
        assertUserEqual(makeTestSavedUser(), userCaptured.getValue());
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteUser() {
        when(mockRepository.delete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.deleteUser(1L));

        verify(mockRepository).delete(1L);
    }

    @Test
    void testDeleteUserWhenUserNotFound() {
        when(mockRepository.delete(1L)).thenReturn(0);

        assertDoesNotThrow(() -> service.deleteUser(1L));

        verify(mockRepository).delete(1L);
    }

    private User makeTestNewUser() {
        final User user = new User();
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    private UserPatch makeTestPatch() {
        final UserPatch patch = new UserPatch();
        patch.setUserId(42L);
        patch.setName("Mr Nobody");
        patch.setEmail("nobody@nowhere.com");
        return patch;
    }

    private User makeTestSavedUser() {
        final User user = new User();
        user.setId(42L);
        user.setName("Mr Nobody");
        user.setEmail("nobody@nowhere.com");
        return user;
    }

    private void assertUserEqual(final User expected, final User actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }
}