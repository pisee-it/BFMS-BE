package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.UserRequest;
import com.bfms.bfms_backend.dtos.res.UserResponse;
import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.mapper.UserMapper;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.service.UserService;
import com.bfms.bfms_backend.util.EntityLookupHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityLookupHelper lookupHelper;
    private final AuditService auditService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Integer id) {
        AppUser user = lookupHelper.getUser(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        AppUser user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        AppUser savedUser = userRepository.save(user);

        auditService.log("CREATE_USER", "Tạo người dùng mới: " + savedUser.getUsername());
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer id, UserRequest request) {
        AppUser user = lookupHelper.getUser(id);

        if (!user.getUsername().equals(request.username())) {
            if (userRepository.findByUsername(request.username()).isPresent()) {
                throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
            }
        }

        userMapper.updateEntity(request, user);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        AppUser updatedUser = userRepository.save(user);

        auditService.log("UPDATE_USER", "Cập nhật người dùng ID: " + id);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        AppUser user = lookupHelper.getUser(id);
        userRepository.delete(user);
        auditService.log("DELETE_USER", "Xóa người dùng ID: " + id + ", username: " + user.getUsername());
    }

    @Override
    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
}
