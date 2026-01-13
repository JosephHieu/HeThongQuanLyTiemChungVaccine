package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.BenhNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BenhNhanRepository extends JpaRepository<BenhNhan, UUID> {
}
