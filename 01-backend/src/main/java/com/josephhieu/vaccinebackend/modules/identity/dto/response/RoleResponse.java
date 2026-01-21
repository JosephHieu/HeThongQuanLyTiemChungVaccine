package com.josephhieu.vaccinebackend.modules.identity.dto.response;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private UUID maQuyen;
    private String tenQuyen; // Ví dụ: Administrator, Quản lý kho...
}