package com.josephhieu.vaccinebackend.modules.identity.service;

import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserService {

    UserResponse createNewUser(UserCreationRequest request);

    // Thêm tham số page và size
    PageResponse<UserResponse> getAllUsers(int page, int size, String search, String maQuyen);

    UserResponse updateUser(UUID id, UserCreationRequest request);

    void toggleLock(UUID id);
}
