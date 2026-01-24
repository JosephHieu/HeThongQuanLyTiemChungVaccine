package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenhNhanRepository extends JpaRepository<BenhNhan, UUID> {

    Optional<BenhNhan> findByTaiKhoan(TaiKhoan taiKhoan);

    boolean existsByTaiKhoan(TaiKhoan taiKhoan);
}
