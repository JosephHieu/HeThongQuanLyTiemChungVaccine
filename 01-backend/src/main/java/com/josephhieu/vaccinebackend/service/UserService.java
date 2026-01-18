package com.josephhieu.vaccinebackend.service;

import com.josephhieu.vaccinebackend.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserService {

    UserResponse createNewUser(UserCreationRequest request);

    // Thêm tham số page và size
    PageResponse<UserResponse> getAllUsers(int page, int size);

    UserResponse updateUser(UUID id, UserCreationRequest request);

    void toggleLock(UUID id);
}
