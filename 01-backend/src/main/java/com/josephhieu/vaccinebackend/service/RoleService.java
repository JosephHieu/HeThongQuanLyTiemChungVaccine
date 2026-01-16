package com.josephhieu.vaccinebackend.service;

import com.josephhieu.vaccinebackend.dto.response.RoleResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleService {

    List<RoleResponse> getAllRoles();
}
