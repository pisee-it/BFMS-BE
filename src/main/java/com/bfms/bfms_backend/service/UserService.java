package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.UserRequest;
import com.bfms.bfms_backend.dtos.res.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Integer id, UserRequest request);

    void deleteUser(Integer id);

    UserResponse getCurrentUser();
}
