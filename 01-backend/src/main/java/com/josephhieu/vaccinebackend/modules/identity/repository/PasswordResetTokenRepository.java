package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.PasswordResetToken;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByTaiKhoan(TaiKhoan taiKhoan);
}
