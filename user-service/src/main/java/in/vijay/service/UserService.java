package in.vijay.service;


import in.vijay.dto.UserRequest;
import in.vijay.dto.UserResponse;

import java.util.List;


public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    /*User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);*/
}
