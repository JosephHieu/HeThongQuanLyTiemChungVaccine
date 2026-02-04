package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HoSoBenhAnRepository extends JpaRepository<HoSoBenhAn, UUID> {

    /**
     * Tìm hồ sơ bệnh án mới nhất của một bệnh nhân dựa trên thời gian tiêm.
     */
    @Query("SELECT hs FROM HoSoBenhAn hs " +
            "JOIN FETCH hs.chiTietDangKyTiem ct " +
            "JOIN FETCH ct.loVacXin l " +
            "JOIN FETCH l.vacXin v " +
            "WHERE ct.benhNhan.maBenhNhan = :maBN " +
            "ORDER BY hs.thoiGianTiem DESC")
    List<HoSoBenhAn> findHistoryByPatient(@Param("maBN") UUID maBN);
}
