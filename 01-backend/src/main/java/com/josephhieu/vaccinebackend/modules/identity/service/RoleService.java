package com.josephhieu.vaccinebackend.modules.identity.service;

import com.josephhieu.vaccinebackend.modules.identity.dto.response.RoleResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleService {

    List<RoleResponse> getAllRoles();
}
