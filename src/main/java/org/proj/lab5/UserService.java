package org.proj.lab5;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserDataException("Invalid user ID");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public User createUser(User user) {
        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new InvalidUserDataException("Failed to create user: " + e.getMessage());
        }
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        validateUser(userDetails);

        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new EmailAlreadyExistsException(userDetails.getEmail());
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new InvalidUserDataException("Failed to update user: " + e.getMessage());
        }
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);

        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new InvalidUserDataException("Failed to delete user: " + e.getMessage());
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User data cannot be null");
        }

        if (user.getName() != null && user.getName().trim().isEmpty()) {
            throw new InvalidUserDataException("Name cannot be empty");
        }

        if (user.getEmail() != null && !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidUserDataException("Invalid email format");
        }
    }

    public long countUsers() {
        return userRepository.count();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }
}
