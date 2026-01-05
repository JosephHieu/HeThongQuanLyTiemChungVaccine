package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.PhanQuyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhanQuyenRepository extends JpaRepository<PhanQuyen, UUID> {

    Optional<PhanQuyen> findByTenQuyen(String tenQuyen);
}
