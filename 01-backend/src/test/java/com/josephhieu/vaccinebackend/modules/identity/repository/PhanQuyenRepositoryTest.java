package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PhanQuyenRepositoryTest {

    @Autowired
    private PhanQuyenRepository phanQuyenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByTenQuyen: Tìm thấy quyền hạn khi tên tồn tại trong database")
    void findByTenQuyen_Success() {
        // GIVEN: Lưu một quyền mẫu vào H2
        PhanQuyen adminRole = PhanQuyen.builder()
                .tenQuyen("Administrator")
                .build();
        entityManager.persist(adminRole);
        entityManager.flush();

        // WHEN: Gọi hàm tìm kiếm
        Optional<PhanQuyen> result = phanQuyenRepository.findByTenQuyen("Administrator");

        // THEN: Kiểm tra kết quả
        assertTrue(result.isPresent());
        assertEquals("Administrator", result.get().getTenQuyen());
        assertNotNull(result.get().getMaQuyen());
    }

    @Test
    @DisplayName("findByTenQuyen: Trả về Optional rỗng khi tên quyền không tồn tại")
    void findByTenQuyen_NotFound() {
        // WHEN
        Optional<PhanQuyen> result = phanQuyenRepository.findByTenQuyen("Non-Existent Role");

        // THEN
        assertTrue(result.isEmpty());
    }
}