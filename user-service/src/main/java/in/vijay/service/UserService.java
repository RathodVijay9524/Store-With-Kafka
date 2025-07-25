package in.vijay.service;


import in.vijay.dto.user.UserRequest;
import in.vijay.dto.user.UserResponse;

import java.util.List;


public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse updateUser(String id, UserRequest request);
    void deleteUser(String id);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();



    /*User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);*/
}
