package com.josephhieu.vaccinebackend.modules.identity.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.josephhieu.vaccinebackend.modules.identity.dto.response.RoleResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.repository.PhanQuyenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private PhanQuyenRepository phanQuyenRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("getAllRoles: Phải ánh xạ đúng từ Entity sang DTO")
    void getAllRoles_Success() {
        // GIVEN
        UUID mockUuid = UUID.randomUUID();
        PhanQuyen role = PhanQuyen.builder()
                .maQuyen(mockUuid)
                .tenQuyen("Administrator")
                .build();

        when(phanQuyenRepository.findAll()).thenReturn(List.of(role));

        // WHEN
        List<RoleResponse> result = roleService.getAllRoles();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUuid, result.get(0).getMaQuyen());
        assertEquals("Administrator", result.get(0).getTenQuyen());
        verify(phanQuyenRepository, times(1)).findAll();
    }
}