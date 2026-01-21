package com.josephhieu.vaccinebackend.modules.identity.service.impl;

import com.josephhieu.vaccinebackend.modules.identity.repository.PhanQuyenRepository;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.RoleResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final PhanQuyenRepository phanQuyenRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return phanQuyenRepository.findAll().stream()
                .map(role -> RoleResponse.builder()
                        .maQuyen(role.getMaQuyen())
                        .tenQuyen(role.getTenQuyen())
                        .build())
                .collect(Collectors.toList());
    }
}
