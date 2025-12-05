package org.proj.lab5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetUserById_InvalidId() {
        assertThrows(InvalidUserDataException.class, () -> userService.getUserById(-1L));
        assertThrows(InvalidUserDataException.class, () -> userService.getUserById(null));
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testCreateUser_Success() {
        User user = new User();
        user.setName("Vika");
        user.setEmail("vika@example.com");
        user.setRole(Role.USER);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);
        assertEquals("Vika", created.getName());
    }

    @Test
    void testCreateUser_NullUser() {
        assertThrows(InvalidUserDataException.class, () -> userService.createUser(null));
    }

    @Test
    void testCreateUser_EmptyName() {
        User user = new User();
        user.setName(" ");
        user.setEmail("test@example.com");

        assertThrows(InvalidUserDataException.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_InvalidEmail() {
        User user = new User();
        user.setName("Vika");
        user.setEmail("invalid-email");

        assertThrows(InvalidUserDataException.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        User user = new User();
        user.setName("Vika");
        user.setEmail("vika@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@example.com");

        User update = new User();
        update.setName("New Name");
        update.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail(update.getEmail())).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);

        User updated = userService.updateUser(1L, update);
        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void testUpdateUser_EmailExists() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old");
        existing.setEmail("old@example.com");

        User update = new User();
        update.setName("New");
        update.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail(update.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, update));
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}