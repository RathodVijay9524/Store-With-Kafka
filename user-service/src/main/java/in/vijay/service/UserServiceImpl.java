package in.vijay.service;

import in.vijay.dto.user.Role;
import in.vijay.dto.user.UserRequest;
import in.vijay.dto.user.UserResponse;
import in.vijay.event.user.UserCreatedEvent;
import in.vijay.event.user.UserDeletedEvent;
import in.vijay.event.user.UserUpdatedEvent;
import in.vijay.beans.User;
import in.vijay.event.*;
import in.vijay.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserEventPublisher publisher;
    private final IdGeneratorService idGeneratorService;


    /**
     * Creates a new user and publishes a UserCreatedEvent to Kafka.
     */
    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("🔧 Creating user: {}", request.getUsername());

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setRole(user.getRole() != null ? user.getRole() : Role.USER);
        //user.setId(idGeneratorService.generateId("USER","USR",6));
        user.setId(idGeneratorService.generateDateBasedId("USER","USR"));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("✅ User saved with ID: {}", savedUser.getId());

        // Publish user created event
        UserCreatedEvent event = new UserCreatedEvent();
        BeanUtils.copyProperties(savedUser, event);
        event.setCreatedAt(savedUser.getCreatedAt());
        publisher.publishUserCreatedEvent(event);
        log.info("📤 UserCreatedEvent published for userId: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }


    /**
     * Updates an existing user and publishes a UserUpdatedEvent to Kafka.
     */
    @Override
    public UserResponse updateUser(String id, UserRequest request) {
        log.info("🔧 Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        // Copy updated properties, excluding id and createdAt
        BeanUtils.copyProperties(request, user, "id", "createdAt");
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("✅ User updated: {}", updatedUser.getId());

        // Publish user updated event
        UserUpdatedEvent event = new UserUpdatedEvent();
        BeanUtils.copyProperties(updatedUser, event);
        event.setUpdatedAt(updatedUser.getUpdatedAt());
        publisher.publishUserUpdatedEvent(event);
        log.info("📤 UserUpdatedEvent published for userId: {}", updatedUser.getId());

        return mapToResponse(updatedUser);
    }


    /**
     * Deletes a user and publishes a UserDeletedEvent to Kafka.
     */
    @Override
    public void deleteUser(String id) {
        log.info("🗑️ Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        userRepository.delete(user);
        log.info("✅ User deleted: {}", id);

        // Publish user deleted event
        UserDeletedEvent event = new UserDeletedEvent();
        BeanUtils.copyProperties(user, event);
        publisher.publishUserDeletedEvent(event);
        log.info("📤 UserDeletedEvent published for userId: {}", id);
    }

    /**
     * Returns user details by ID.
     */
    @Override
    public UserResponse getUserById(String id) {
        log.info("🔍 Getting user by ID: {}", id);

        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    log.error("❌ User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
    }

    /**
     * Returns a list of all users.
     */
    @Override
    public List<UserResponse> getAllUsers() {
        log.info("📦 Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert User entity to UserResponse DTO.
     */
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        BeanUtils.copyProperties(user, response);
        return response;
    }



  /*  @Override
    public User createUser(User user) {
        user.setRole(user.getRole() != null ? user.getRole() : Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setRole(updatedUser.getRole());
            user.setActive(updatedUser.isActive());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }*/
}
