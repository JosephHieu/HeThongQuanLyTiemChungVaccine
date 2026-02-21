CREATE DATABASE  IF NOT EXISTS `vaccine_management` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `vaccine_management`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vaccine_management
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `BENHNHAN`
--

DROP TABLE IF EXISTS `BENHNHAN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BENHNHAN` (
  `MaBenhNhan` char(36) NOT NULL,
  `MaTaiKhoan` char(36) DEFAULT NULL,
  `TenBenhNhan` varchar(255) DEFAULT NULL,
  `NgaySinh` date DEFAULT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `NguoiGiamHo` varchar(255) DEFAULT NULL,
  `SDT` varchar(255) DEFAULT NULL,
  `GioiTinh` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`MaBenhNhan`),
  KEY `MaTaiKhoan` (`MaTaiKhoan`),
  CONSTRAINT `BENHNHAN_ibfk_1` FOREIGN KEY (`MaTaiKhoan`) REFERENCES `TAIKHOAN` (`MaTaiKhoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BENHNHAN`
--

LOCK TABLES `BENHNHAN` WRITE;
/*!40000 ALTER TABLE `BENHNHAN` DISABLE KEYS */;
INSERT INTO `BENHNHAN` VALUES ('31107089-91fa-4b8a-aceb-b3db0a510287','ea2e1328-9b00-4996-9498-83878153eb33','Nguyễn Văn A','2006-02-01','89/105, Phạm Văn Đồng, Phường Phú Nhuận, TPHCM','Nguyễn Giám Hộ A','0989382984','Nam'),('765e510a-7bc5-4552-814f-a93cbb1c8e7e','c2ec059e-dfde-46c4-8ad4-c4cf7cbbb445','Trần Mạnh Giàu','2002-02-05','83/12, Phan Đình Phùng, Phường Phú Nhuận, TPHCM','Trần Văn Liên','0923989212','Nữ'),('895877f3-df71-4ecc-83a9-e828e0e1b477','5635cf6f-23d6-44d2-82c2-91975db9b6ff','Phạm Gia Q','2025-12-24','89/7, đường số 1, phường Bến Nghé, Quận 1, TPHCM','','0932898913','Nam'),('c69b8568-9aae-46b1-8cf9-e665ff1468d8','cb9fe6a2-b98b-4a76-bcc0-28d49862c48c','Trần Thị Kiều Anh','2007-01-30','9/82, đường Phạm Văn Chiêu, Gò Vấp, TPHCM','Nguyễn Danh Quốc','0923188311','Nữ'),('e39b0743-774c-445a-9e64-b780a9e518aa','c4fc4bf1-9ac8-4e51-93e3-c47233d56738','Trần Thanh Sang','1997-02-26','135/10, An Dương Vương, Phường Phú Thọ, TPHCM','','0912984912','Nam');
/*!40000 ALTER TABLE `BENHNHAN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHITIETPHANQUYEN`
--

DROP TABLE IF EXISTS `CHITIETPHANQUYEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CHITIETPHANQUYEN` (
  `MaQuyen` char(36) NOT NULL,
  `MaTaiKhoan` char(36) NOT NULL,
  PRIMARY KEY (`MaQuyen`,`MaTaiKhoan`),
  KEY `MaTaiKhoan` (`MaTaiKhoan`),
  CONSTRAINT `CHITIETPHANQUYEN_ibfk_1` FOREIGN KEY (`MaQuyen`) REFERENCES `PHANQUYEN` (`MaQuyen`),
  CONSTRAINT `CHITIETPHANQUYEN_ibfk_2` FOREIGN KEY (`MaTaiKhoan`) REFERENCES `TAIKHOAN` (`MaTaiKhoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHITIETPHANQUYEN`
--

LOCK TABLES `CHITIETPHANQUYEN` WRITE;
/*!40000 ALTER TABLE `CHITIETPHANQUYEN` DISABLE KEYS */;
INSERT INTO `CHITIETPHANQUYEN` VALUES ('550e8400-e29b-41d4-a716-446655440001','12a7847a-4d75-4f75-8f50-be3f054df15d'),('550e8400-e29b-41d4-a716-446655440000','20f2e44e-db16-4a53-875a-15b222f923af'),('550e8400-e29b-41d4-a716-446655440005','5635cf6f-23d6-44d2-82c2-91975db9b6ff'),('550e8400-e29b-41d4-a716-446655440002','720f12c7-9c0e-4a36-8e8a-ad2f8f0ce89b'),('550e8400-e29b-41d4-a716-446655440004','7734a5c8-a2a5-4bba-95fd-001b520ee52e'),('550e8400-e29b-41d4-a716-446655440003','973b87c5-94a1-4d2d-b69c-df03de36a70d'),('550e8400-e29b-41d4-a716-446655440000','af56b30d-5988-4977-9a6e-fb6ac8e9aa83'),('550e8400-e29b-41d4-a716-446655440005','c2ec059e-dfde-46c4-8ad4-c4cf7cbbb445'),('550e8400-e29b-41d4-a716-446655440005','c4fc4bf1-9ac8-4e51-93e3-c47233d56738'),('550e8400-e29b-41d4-a716-446655440005','cb9fe6a2-b98b-4a76-bcc0-28d49862c48c'),('550e8400-e29b-41d4-a716-446655440002','d71c22ef-92e3-4661-9ee3-f903eabcada3'),('550e8400-e29b-41d4-a716-446655440005','ea2e1328-9b00-4996-9498-83878153eb33'),('550e8400-e29b-41d4-a716-446655440004','edcfad4c-0d79-445f-b922-44ce7cef3283'),('550e8400-e29b-41d4-a716-446655440004','fe1d6c87-d37f-4554-b544-cefdee4ddc3c');
/*!40000 ALTER TABLE `CHITIETPHANQUYEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHITIET_DK_TIEM`
--

DROP TABLE IF EXISTS `CHITIET_DK_TIEM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CHITIET_DK_TIEM` (
  `MaChiTietDKTiem` char(36) NOT NULL,
  `MaBenhNhan` char(36) DEFAULT NULL,
  `MaLo` char(36) DEFAULT NULL,
  `MaLichTiem` char(36) DEFAULT NULL,
  `ThoiGianCanTiem` date DEFAULT NULL,
  `GhiChu` text,
  `TrangThai` varchar(50) NOT NULL DEFAULT 'REGISTERED',
  `MaHoaDon` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`MaChiTietDKTiem`),
  KEY `MaBenhNhan` (`MaBenhNhan`),
  KEY `MaLo` (`MaLo`),
  KEY `MaLichTiem` (`MaLichTiem`),
  KEY `FK_CHITIET_HOADON` (`MaHoaDon`),
  CONSTRAINT `CHITIET_DK_TIEM_ibfk_1` FOREIGN KEY (`MaBenhNhan`) REFERENCES `BENHNHAN` (`MaBenhNhan`),
  CONSTRAINT `CHITIET_DK_TIEM_ibfk_2` FOREIGN KEY (`MaLo`) REFERENCES `LOVACXIN` (`MaLo`),
  CONSTRAINT `CHITIET_DK_TIEM_ibfk_3` FOREIGN KEY (`MaLichTiem`) REFERENCES `LICHTIEMCHUNG` (`MaLichTiem`),
  CONSTRAINT `FK_CHITIET_HOADON` FOREIGN KEY (`MaHoaDon`) REFERENCES `HOADON` (`MaHoaDon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHITIET_DK_TIEM`
--

LOCK TABLES `CHITIET_DK_TIEM` WRITE;
/*!40000 ALTER TABLE `CHITIET_DK_TIEM` DISABLE KEYS */;
INSERT INTO `CHITIET_DK_TIEM` VALUES ('0a1aa757-3538-4a1f-a03d-7998c9445a41','c69b8568-9aae-46b1-8cf9-e665ff1468d8','f57b7dfd-857f-4a86-b2b4-e50564ade3df','9a4ffa86-2664-42ab-a665-0c5fff5b4571','2026-09-30','Đăng ký trực tuyến qua SchedulePortal','COMPLETED',NULL),('2f6e5168-d6fb-4a4f-b268-18061760fab4','c69b8568-9aae-46b1-8cf9-e665ff1468d8','da293d93-e32f-43ba-b804-ba7c99200787',NULL,'2026-02-16','Đăng ký từ tra cứu vắc-xin trực tuyến','REGISTERED',NULL),('5887f0be-d87b-4afd-a033-6df53d018993','31107089-91fa-4b8a-aceb-b3db0a510287','2ffec5f8-15e5-44e9-ba9d-65114961c262',NULL,'2026-02-20','Đăng ký từ tra cứu vắc-xin trực tuyến','REGISTERED',NULL),('5cbb8b9f-304e-4c22-97b4-8896a6006252','c69b8568-9aae-46b1-8cf9-e665ff1468d8','67d1bdd6-531d-4e98-bbc4-e78565020d98',NULL,'2026-02-26','Đăng ký từ tra cứu vắc-xin trực tuyến','REGISTERED',NULL),('5f76b7e0-7016-427c-a94d-db2b40659c81','31107089-91fa-4b8a-aceb-b3db0a510287','67d1bdd6-531d-4e98-bbc4-e78565020d98','8f4ddebe-cf2d-4b3b-9bd1-b628ddc60947','2026-04-30','Đăng ký trực tuyến qua SchedulePortal','REGISTERED',NULL),('61d1a1ab-976a-4d4f-aa01-58134cfeeb05','c69b8568-9aae-46b1-8cf9-e665ff1468d8','f57b7dfd-857f-4a86-b2b4-e50564ade3df',NULL,'2026-02-11','Đăng ký từ tra cứu vắc-xin trực tuyến','REGISTERED',NULL),('84db9be0-1467-4f59-8353-1f42d72fa516','e39b0743-774c-445a-9e64-b780a9e518aa','e790e90c-5c58-4884-835a-b918a6eea361','c23b33c6-5c31-4492-8c7b-fc0e50b13ad3','2026-02-15','Đăng ký trực tuyến qua SchedulePortal','REGISTERED','9d4c4824-9c73-4007-89d5-9b73d2b60932'),('9145da36-84ce-4b92-b02c-ce643d2b9922','c69b8568-9aae-46b1-8cf9-e665ff1468d8','e790e90c-5c58-4884-835a-b918a6eea361',NULL,'2026-02-19','Đăng ký từ tra cứu vắc-xin trực tuyến','COMPLETED','f560d68d-956b-4559-a3ab-9600fcb62be7'),('a8ce7d35-8859-4df1-b386-03ac12098b45','31107089-91fa-4b8a-aceb-b3db0a510287','f57b7dfd-857f-4a86-b2b4-e50564ade3df','9a4ffa86-2664-42ab-a665-0c5fff5b4571','2026-09-30','Đăng ký trực tuyến qua SchedulePortal','REGISTERED',NULL),('be0fa013-f490-413f-9ff1-0622fa0df4cf','c69b8568-9aae-46b1-8cf9-e665ff1468d8','f57b7dfd-857f-4a86-b2b4-e50564ade3df','9a4ffa86-2664-42ab-a665-0c5fff5b4571','2026-09-30','Đăng ký trực tuyến qua SchedulePortal','CANCELED',NULL),('d354fa25-9671-4617-82da-635d7dac047a','e39b0743-774c-445a-9e64-b780a9e518aa','67d1bdd6-531d-4e98-bbc4-e78565020d98','8f4ddebe-cf2d-4b3b-9bd1-b628ddc60947','2026-04-30','Đăng ký trực tuyến qua SchedulePortal','COMPLETED','83bbd6ac-8ae9-4b3f-a1f7-2f9a78664625'),('d53c2b17-2770-46ce-b1ae-092c28579049','c69b8568-9aae-46b1-8cf9-e665ff1468d8','67d1bdd6-531d-4e98-bbc4-e78565020d98','8f4ddebe-cf2d-4b3b-9bd1-b628ddc60947','2026-04-30','Đăng ký trực tuyến qua SchedulePortal','COMPLETED',NULL),('e874b732-a128-42be-81e8-cedfc671cc0e','e39b0743-774c-445a-9e64-b780a9e518aa','f57b7dfd-857f-4a86-b2b4-e50564ade3df','9a4ffa86-2664-42ab-a665-0c5fff5b4571','2026-09-30','Đăng ký trực tuyến qua SchedulePortal','REGISTERED','c14a5a9d-4182-4303-9deb-e22f0456f3db'),('f9fae8b8-a65d-4274-8849-41a4739e9fd7','c69b8568-9aae-46b1-8cf9-e665ff1468d8','da293d93-e32f-43ba-b804-ba7c99200787',NULL,'2026-02-19','Đăng ký từ tra cứu vắc-xin trực tuyến','COMPLETED',NULL);
/*!40000 ALTER TABLE `CHITIET_DK_TIEM` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHITIET_NV_THAMGIA`
--

DROP TABLE IF EXISTS `CHITIET_NV_THAMGIA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CHITIET_NV_THAMGIA` (
  `MaNhanVien` char(36) NOT NULL,
  `MaLichTiem` char(36) NOT NULL,
  PRIMARY KEY (`MaNhanVien`,`MaLichTiem`),
  KEY `MaLichTiem` (`MaLichTiem`),
  CONSTRAINT `CHITIET_NV_THAMGIA_ibfk_1` FOREIGN KEY (`MaNhanVien`) REFERENCES `NHANVIEN` (`MaNhanVien`),
  CONSTRAINT `CHITIET_NV_THAMGIA_ibfk_2` FOREIGN KEY (`MaLichTiem`) REFERENCES `LICHTIEMCHUNG` (`MaLichTiem`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHITIET_NV_THAMGIA`
--

LOCK TABLES `CHITIET_NV_THAMGIA` WRITE;
/*!40000 ALTER TABLE `CHITIET_NV_THAMGIA` DISABLE KEYS */;
INSERT INTO `CHITIET_NV_THAMGIA` VALUES ('188cff0a-3cf2-4d45-b998-38db3d23f0d7','63f2df2b-8099-4633-8825-5948916dfeb0'),('08a123b8-023a-416d-a605-a79eab7c937a','842b9615-2190-413e-b4b6-4b47ee00de3d'),('188cff0a-3cf2-4d45-b998-38db3d23f0d7','8f4ddebe-cf2d-4b3b-9bd1-b628ddc60947'),('188cff0a-3cf2-4d45-b998-38db3d23f0d7','9a4ffa86-2664-42ab-a665-0c5fff5b4571'),('08a123b8-023a-416d-a605-a79eab7c937a','c23b33c6-5c31-4492-8c7b-fc0e50b13ad3');
/*!40000 ALTER TABLE `CHITIET_NV_THAMGIA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DICHBENH`
--

DROP TABLE IF EXISTS `DICHBENH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DICHBENH` (
  `MaDichBenh` char(36) NOT NULL,
  `MaNhanVien` char(36) DEFAULT NULL,
  `TenDichBenh` varchar(255) DEFAULT NULL,
  `TacHaiSucKhoe` text,
  `DuongLayNhiem` text,
  `SoNguoiBiNhiem` int DEFAULT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `GhiChu` text,
  `ThoiDiemKhaoSat` date DEFAULT NULL,
  PRIMARY KEY (`MaDichBenh`),
  KEY `MaNhanVien` (`MaNhanVien`),
  CONSTRAINT `DICHBENH_ibfk_1` FOREIGN KEY (`MaNhanVien`) REFERENCES `NHANVIEN` (`MaNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DICHBENH`
--

LOCK TABLES `DICHBENH` WRITE;
/*!40000 ALTER TABLE `DICHBENH` DISABLE KEYS */;
INSERT INTO `DICHBENH` VALUES ('1b0e2bbf-788c-4645-97e9-d202a818b884','188cff0a-3cf2-4d45-b998-38db3d23f0d7','Tay Chân Miệng','Suy nhược cơ thể, sốt cao','Hô hấp, tiếp xúc da trực tiếp, nước bọt',230,'Nguyễn Kiệm, Phường Phú Nhuận, TPHCM','Giữ gìn vệ sinh sạch sẽ nhà ở và hạn chế cho trẻ em tiếp xúc da hay nước bọt của người khác\n','2026-02-01'),('fe51761d-006a-4a3f-b048-a31162bd199e','188cff0a-3cf2-4d45-b998-38db3d23f0d7','Sởi','Suy giảm hệ miễn dịch, da mẩn đỏ và ngứa ngáy gây khó chịu.','Vệ sinh kém và uống ít nước\n',330,'Nguyễn Oanh, phường Gò Vấp, TPHCM','Uống đủ nước và mặc đồ thoáng mát, hạn chế tiếp xúc với nắng giấc trưa khi có nhiều tia UV gây hại cho da','2025-05-06');
/*!40000 ALTER TABLE `DICHBENH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HOADON`
--

DROP TABLE IF EXISTS `HOADON`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HOADON` (
  `MaHoaDon` char(36) NOT NULL,
  `TongTien` decimal(15,2) DEFAULT NULL,
  `NgayTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `TrangThai` int DEFAULT '0' COMMENT '0: Chờ, 1: Đã xong, 2: Hủy',
  `PhuongThucThanhToan` varchar(50) DEFAULT NULL,
  `LoaiHoaDon` varchar(20) DEFAULT NULL COMMENT 'XUAT hoặc NHAP',
  PRIMARY KEY (`MaHoaDon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HOADON`
--

LOCK TABLES `HOADON` WRITE;
/*!40000 ALTER TABLE `HOADON` DISABLE KEYS */;
INSERT INTO `HOADON` VALUES ('32370452-0958-11f1-b906-b24b75e02b11',74875000.00,'2026-01-25 10:00:00',1,'Chuyển khoản','NHAP'),('323ab5dc-0958-11f1-b906-b24b75e02b11',18687500000.00,'2026-01-25 11:00:00',1,'Chuyển khoản','NHAP'),('323cefaf-0958-11f1-b906-b24b75e02b11',112125000.00,'2026-01-25 12:00:00',1,'Tiền mặt','NHAP'),('323efe4b-0958-11f1-b906-b24b75e02b11',112500000.00,'2026-01-25 13:00:00',1,'Chuyển khoản','NHAP'),('32414f35-0958-11f1-b906-b24b75e02b11',62125000.00,'2026-01-25 14:00:00',1,'Tiền mặt','NHAP'),('324370c1-0958-11f1-b906-b24b75e02b11',149700000.00,'2026-02-09 09:00:00',1,'Chuyển khoản','NHAP'),('32456299-0958-11f1-b906-b24b75e02b11',112000000.00,'2026-01-25 15:00:00',1,'Tiền mặt','NHAP'),('83bbd6ac-8ae9-4b3f-a1f7-2f9a78664625',150000.00,'2026-02-11 16:06:51',1,'Chuyển khoản','XUAT'),('9d4c4824-9c73-4007-89d5-9b73d2b60932',200000.00,'2026-02-14 10:58:16',1,'Tiền mặt','XUAT'),('c14a5a9d-4182-4303-9deb-e22f0456f3db',250000.00,'2026-02-11 15:50:37',0,'Chưa xác định','XUAT'),('f560d68d-956b-4559-a3ab-9600fcb62be7',200000.00,'2026-02-11 15:27:50',1,'Tiền mặt','XUAT');
/*!40000 ALTER TABLE `HOADON` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HOSOBENHAN`
--

DROP TABLE IF EXISTS `HOSOBENHAN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HOSOBENHAN` (
  `MaHoSoBenhAn` char(36) NOT NULL,
  `MaChiTietDKTiem` char(36) DEFAULT NULL,
  `MaHoaDon` char(36) DEFAULT NULL,
  `PhanUngSauTiem` text,
  `ThoiGianTacDung` varchar(255) DEFAULT NULL,
  `ThoiGianTiem` datetime DEFAULT NULL,
  `MaNhanVien` char(36) DEFAULT NULL,
  PRIMARY KEY (`MaHoSoBenhAn`),
  KEY `MaChiTietDKTiem` (`MaChiTietDKTiem`),
  KEY `MaHoaDon` (`MaHoaDon`),
  KEY `fk_hosobenhan_nhanvien` (`MaNhanVien`),
  CONSTRAINT `fk_hosobenhan_nhanvien` FOREIGN KEY (`MaNhanVien`) REFERENCES `NHANVIEN` (`MaNhanVien`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `HOSOBENHAN_ibfk_1` FOREIGN KEY (`MaChiTietDKTiem`) REFERENCES `CHITIET_DK_TIEM` (`MaChiTietDKTiem`),
  CONSTRAINT `HOSOBENHAN_ibfk_2` FOREIGN KEY (`MaHoaDon`) REFERENCES `HOADON` (`MaHoaDon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HOSOBENHAN`
--

LOCK TABLES `HOSOBENHAN` WRITE;
/*!40000 ALTER TABLE `HOSOBENHAN` DISABLE KEYS */;
INSERT INTO `HOSOBENHAN` VALUES ('48f9a193-3bfc-4dcf-81e7-9ecde7aadb69','d53c2b17-2770-46ce-b1ae-092c28579049',NULL,'Không có gì bất thường','12 tháng','2026-02-04 11:41:45','e9bca248-be6a-45d1-b56f-20cae22a3c5e'),('6b6313be-1c69-4bd2-bb3c-a883d7cdc551','9145da36-84ce-4b92-b02c-ce643d2b9922',NULL,'Sốt nhẹ và chóng mặt\n','12 tháng','2026-02-11 14:08:13','08a123b8-023a-416d-a605-a79eab7c937a'),('6edf3fcf-ac52-405a-aa3c-58123a2ad9bb','d354fa25-9671-4617-82da-635d7dac047a','83bbd6ac-8ae9-4b3f-a1f7-2f9a78664625','Bình thường','12 tháng','2026-02-11 16:07:38','188cff0a-3cf2-4d45-b998-38db3d23f0d7'),('ec256150-1990-41c0-8d3b-c09d96be9ad7','f9fae8b8-a65d-4274-8849-41a4739e9fd7','f560d68d-956b-4559-a3ab-9600fcb62be7','Chóng mặt và sốt nhẹ','12 tháng','2026-02-05 16:35:32','e9bca248-be6a-45d1-b56f-20cae22a3c5e'),('ecc6b676-a020-437b-94a0-2ef0144eb88d','0a1aa757-3538-4a1f-a03d-7998c9445a41',NULL,'Bình thường','12 tháng','2026-02-05 17:11:26','08a123b8-023a-416d-a605-a79eab7c937a');
/*!40000 ALTER TABLE `HOSOBENHAN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LICHTIEMCHUNG`
--

DROP TABLE IF EXISTS `LICHTIEMCHUNG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LICHTIEMCHUNG` (
  `MaLichTiem` char(36) NOT NULL,
  `DoiTuong` varchar(255) DEFAULT NULL,
  `ThoiGianChung` varchar(255) DEFAULT NULL,
  `GhiChu` text,
  `SoLuongNguoiTiem` int DEFAULT NULL,
  `NgayTiem` date DEFAULT NULL,
  `DiaDiem` varchar(255) DEFAULT NULL,
  `MaLo` char(36) DEFAULT NULL,
  PRIMARY KEY (`MaLichTiem`),
  UNIQUE KEY `UC_Ngay_Ca` (`NgayTiem`,`ThoiGianChung`),
  KEY `FK_LichTiem_LoVacXin` (`MaLo`),
  CONSTRAINT `FK_LichTiem_LoVacXin` FOREIGN KEY (`MaLo`) REFERENCES `LOVACXIN` (`MaLo`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LICHTIEMCHUNG`
--

LOCK TABLES `LICHTIEMCHUNG` WRITE;
/*!40000 ALTER TABLE `LICHTIEMCHUNG` DISABLE KEYS */;
INSERT INTO `LICHTIEMCHUNG` VALUES ('63f2df2b-8099-4633-8825-5948916dfeb0','Mọi Lứa Tuổi','Sáng (07:30 - 11:30)','Không mất phí',97,'2026-01-30','Nhà thi đầu Rạch Mĩu, Phú Nhuận','2ffec5f8-15e5-44e9-ba9d-65114961c262'),('842b9615-2190-413e-b4b6-4b47ee00de3d','Trẻ em dưới 10 tuổi','Chiều (13:30 - 17:00)','Không mất phí',100,'2026-01-30','Bệnh viện nhi đồng 1','88a9b0a1-40fd-41ff-beeb-353f4cce9881'),('8f4ddebe-cf2d-4b3b-9bd1-b628ddc60947','Mọi Lứa Tuổi','Sáng (07:30 - 11:30)','',50,'2026-04-30','Nhà thi đầu Rạch Mĩu, Phú Nhuận','67d1bdd6-531d-4e98-bbc4-e78565020d98'),('9a4ffa86-2664-42ab-a665-0c5fff5b4571','Mọi Lứa Tuổi','Sáng (07:30 - 11:30)','Tiêm chủng vaccine ngừa thủy đậu',148,'2026-09-30','Trung tâm y tế Hồng Bàng, Quận 8','f57b7dfd-857f-4a86-b2b4-e50564ade3df'),('c23b33c6-5c31-4492-8c7b-fc0e50b13ad3','Trên 5 tuổi','Sáng (07:30 - 11:30)','',99,'2026-02-15','Nhà thi đầu Rạch Mĩu, Phú Nhuận','e790e90c-5c58-4884-835a-b918a6eea361');
/*!40000 ALTER TABLE `LICHTIEMCHUNG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LOAIPHANHOI`
--

DROP TABLE IF EXISTS `LOAIPHANHOI`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LOAIPHANHOI` (
  `MaLoaiPhanHoi` char(36) NOT NULL,
  `TenLoaiPhanHoi` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaLoaiPhanHoi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LOAIPHANHOI`
--

LOCK TABLES `LOAIPHANHOI` WRITE;
/*!40000 ALTER TABLE `LOAIPHANHOI` DISABLE KEYS */;
INSERT INTO `LOAIPHANHOI` VALUES ('1e309cfb-aa96-4e2f-9747-407f8b6c80f0','Phản hồi sau tiêm'),('dcf558cd-040e-11f1-ba94-222e410c1fe9','Phàn nàn'),('dcf6a6c1-040e-11f1-ba94-222e410c1fe9','Khen ngợi'),('dcf81c8c-040e-11f1-ba94-222e410c1fe9','Động viên'),('dcf8d57a-040e-11f1-ba94-222e410c1fe9','Khuyến khích'),('dcf9b2dd-040e-11f1-ba94-222e410c1fe9','Ủng hộ');
/*!40000 ALTER TABLE `LOAIPHANHOI` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LOAIVACXIN`
--

DROP TABLE IF EXISTS `LOAIVACXIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LOAIVACXIN` (
  `MaLoaiVacXin` char(36) NOT NULL,
  `TenLoaiVacXin` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaLoaiVacXin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LOAIVACXIN`
--

LOCK TABLES `LOAIVACXIN` WRITE;
/*!40000 ALTER TABLE `LOAIVACXIN` DISABLE KEYS */;
INSERT INTO `LOAIVACXIN` VALUES ('159702c1-f99b-11f0-b6d7-0a9b2774db02','BCG'),('15970b2b-f99b-11f0-b6d7-0a9b2774db02','ENGERIX B'),('15970de6-f99b-11f0-b6d7-0a9b2774db02','TETAVAX'),('15970e41-f99b-11f0-b6d7-0a9b2774db02','TRIMOVAX'),('15970e6b-f99b-11f0-b6d7-0a9b2774db02','MMR'),('15970e96-f99b-11f0-b6d7-0a9b2774db02','VARIVAX'),('15970eb8-f99b-11f0-b6d7-0a9b2774db02','INFANRIX'),('15970ede-f99b-11f0-b6d7-0a9b2774db02','GARDASIL'),('15970f03-f99b-11f0-b6d7-0a9b2774db02','COMIRNATY'),('15970f27-f99b-11f0-b6d7-0a9b2774db02','VAXIGRIP');
/*!40000 ALTER TABLE `LOAIVACXIN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LOVACXIN`
--

DROP TABLE IF EXISTS `LOVACXIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LOVACXIN` (
  `MaLo` char(36) NOT NULL,
  `SoLo` varchar(50) NOT NULL,
  `MaVacXin` char(36) DEFAULT NULL,
  `MaNhaCungCap` char(36) DEFAULT NULL,
  `MaHoaDon` char(36) DEFAULT NULL,
  `SoLuongNhap` int DEFAULT NULL,
  `SoLuong` int DEFAULT NULL,
  `TinhTrang` varchar(255) DEFAULT NULL,
  `NgayNhan` date DEFAULT NULL,
  `NuocSanXuat` varchar(255) DEFAULT NULL,
  `GiayPhep` varchar(255) DEFAULT NULL,
  `GhiChu` text,
  `GiaNhap` decimal(18,2) DEFAULT '0.00',
  PRIMARY KEY (`MaLo`),
  KEY `MaVacXin` (`MaVacXin`),
  KEY `MaNhaCungCap` (`MaNhaCungCap`),
  KEY `MaHoaDon` (`MaHoaDon`),
  CONSTRAINT `LOVACXIN_ibfk_1` FOREIGN KEY (`MaVacXin`) REFERENCES `VACXIN` (`MaVacXin`),
  CONSTRAINT `LOVACXIN_ibfk_2` FOREIGN KEY (`MaNhaCungCap`) REFERENCES `NHACUNGCAP` (`MaNhaCungCap`),
  CONSTRAINT `LOVACXIN_ibfk_3` FOREIGN KEY (`MaHoaDon`) REFERENCES `HOADON` (`MaHoaDon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LOVACXIN`
--

LOCK TABLES `LOVACXIN` WRITE;
/*!40000 ALTER TABLE `LOVACXIN` DISABLE KEYS */;
INSERT INTO `LOVACXIN` VALUES ('2ffec5f8-15e5-44e9-ba9d-65114961c262','BATCH-2026-2','c858cac6-7718-4473-a930-b7be65d6fa14','3bcc4eca-f99a-11f0-b6d7-0a9b2774db02','32370452-0958-11f1-b906-b24b75e02b11',700,599,'Còn','2026-01-25','Pháp','5','',125000.00),('4369f5f0-6129-429a-8c98-138bcf81cc57','BATCH-covid','315cb6bf-7d98-480e-b60f-dfd95e832bd3','3bcc4ea5-f99a-11f0-b6d7-0a9b2774db02','323ab5dc-0958-11f1-b906-b24b75e02b11',150000,149500,'Còn','2026-01-25','Anh','5','',125000.00),('67d1bdd6-531d-4e98-bbc4-e78565020d98','BATCH-2026','86cd0c98-0107-4766-97ce-c1baf71f04ee','3bcc3f26-f99a-11f0-b6d7-0a9b2774db02','323cefaf-0958-11f1-b906-b24b75e02b11',1000,897,'Còn','2026-01-25','Việt Nam','1','',125000.00),('88a9b0a1-40fd-41ff-beeb-353f4cce9881','BATCH-HANDFOOT','73644697-c1a0-417f-a969-8853e778589b','3bcc4e7e-f99a-11f0-b6d7-0a9b2774db02','323efe4b-0958-11f1-b906-b24b75e02b11',1000,900,'Còn','2026-01-25','Việt Nam','19','',125000.00),('da293d93-e32f-43ba-b804-ba7c99200787','BATCH-2026-1','c4e69e49-8eb2-4a58-b849-930b77f1883d','3bcc4927-f99a-11f0-b6d7-0a9b2774db02','32414f35-0958-11f1-b906-b24b75e02b11',1000,497,'Còn','2026-01-25','Bỉ','2','',125000.00),('e790e90c-5c58-4884-835a-b918a6eea361','BATCH-DAI','fa1a63b3-eeb5-4e11-ae40-78ba78498aac','3bcc4e5b-f99a-11f0-b6d7-0a9b2774db02','324370c1-0958-11f1-b906-b24b75e02b11',1000,998,'Còn','2026-02-09','Bồ Đào Nha','30','',150000.00),('f57b7dfd-857f-4a86-b2b4-e50564ade3df','BATCH-ThuyDau','abe7e377-3cdf-4b8e-8fd0-af436fc220d5','3bcc4d55-f99a-11f0-b6d7-0a9b2774db02','32456299-0958-11f1-b906-b24b75e02b11',1000,896,'Còn','2026-01-25','Việt Nam','4','',125000.00);
/*!40000 ALTER TABLE `LOVACXIN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LUOTTUVAN`
--

DROP TABLE IF EXISTS `LUOTTUVAN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LUOTTUVAN` (
  `MaLuotTuVan` char(36) NOT NULL,
  `CauHoi` text,
  `TraLoi` text,
  `MaBenhNhan` char(36) DEFAULT NULL,
  `MaNhanVien` char(36) DEFAULT NULL,
  `CauHoiThuongGap` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`MaLuotTuVan`),
  KEY `MaBenhNhan` (`MaBenhNhan`),
  KEY `MaNhanVien` (`MaNhanVien`),
  CONSTRAINT `LUOTTUVAN_ibfk_1` FOREIGN KEY (`MaBenhNhan`) REFERENCES `BENHNHAN` (`MaBenhNhan`),
  CONSTRAINT `LUOTTUVAN_ibfk_2` FOREIGN KEY (`MaNhanVien`) REFERENCES `NHANVIEN` (`MaNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LUOTTUVAN`
--

LOCK TABLES `LUOTTUVAN` WRITE;
/*!40000 ALTER TABLE `LUOTTUVAN` DISABLE KEYS */;
/*!40000 ALTER TABLE `LUOTTUVAN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NHACUNGCAP`
--

DROP TABLE IF EXISTS `NHACUNGCAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NHACUNGCAP` (
  `MaNhaCungCap` char(36) NOT NULL,
  `TenNhaCungCap` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaNhaCungCap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NHACUNGCAP`
--

LOCK TABLES `NHACUNGCAP` WRITE;
/*!40000 ALTER TABLE `NHACUNGCAP` DISABLE KEYS */;
INSERT INTO `NHACUNGCAP` VALUES ('3bcc3f26-f99a-11f0-b6d7-0a9b2774db02','VNVC - Hệ thống tiêm chủng Việt Nam'),('3bcc4927-f99a-11f0-b6d7-0a9b2774db02','Pfizer Vietnam Ltd'),('3bcc4d55-f99a-11f0-b6d7-0a9b2774db02','Moderna, Inc.'),('3bcc4ddf-f99a-11f0-b6d7-0a9b2774db02','AstraZeneca Vietnam'),('3bcc4e0c-f99a-11f0-b6d7-0a9b2774db02','GlaxoSmithKline (GSK) Vietnam'),('3bcc4e36-f99a-11f0-b6d7-0a9b2774db02','Sanofi Pasteur Vietnam'),('3bcc4e5b-f99a-11f0-b6d7-0a9b2774db02','Sinopharm (China National Biotec Group)'),('3bcc4e7e-f99a-11f0-b6d7-0a9b2774db02','Abbott Laboratories Vietnam'),('3bcc4ea5-f99a-11f0-b6d7-0a9b2774db02','Merck Sharp & Dohme (MSD) Vietnam'),('3bcc4eca-f99a-11f0-b6d7-0a9b2774db02','Takeda Pharmaceuticals Vietnam');
/*!40000 ALTER TABLE `NHACUNGCAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NHANVIEN`
--

DROP TABLE IF EXISTS `NHANVIEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NHANVIEN` (
  `MaNhanVien` char(36) NOT NULL,
  `MaTaiKhoan` char(36) DEFAULT NULL,
  `TenNhanVien` varchar(255) DEFAULT NULL,
  `NamSinh` int DEFAULT NULL,
  `SDT` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaNhanVien`),
  KEY `MaTaiKhoan` (`MaTaiKhoan`),
  CONSTRAINT `NHANVIEN_ibfk_1` FOREIGN KEY (`MaTaiKhoan`) REFERENCES `TAIKHOAN` (`MaTaiKhoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NHANVIEN`
--

LOCK TABLES `NHANVIEN` WRITE;
/*!40000 ALTER TABLE `NHANVIEN` DISABLE KEYS */;
INSERT INTO `NHANVIEN` VALUES ('033928f2-f596-4675-854b-0cb00f3ea7a2','973b87c5-94a1-4d2d-b69c-df03de36a70d','Lê Thị A',NULL,NULL),('08a123b8-023a-416d-a605-a79eab7c937a','fe1d6c87-d37f-4554-b544-cefdee4ddc3c','Nguyễn Trần B',NULL,NULL),('188cff0a-3cf2-4d45-b998-38db3d23f0d7','7734a5c8-a2a5-4bba-95fd-001b520ee52e','Nguyễn Nguyên Thảo',NULL,NULL),('698c5794-d6c4-49bc-b84c-4764ed14244f','12a7847a-4d75-4f75-8f50-be3f054df15d','Nguyễn Văn C',NULL,NULL),('94ece1f7-580c-4d8a-abc6-d2c32722fe15','720f12c7-9c0e-4a36-8e8a-ad2f8f0ce89b','Nguyễn Đình Bắc',NULL,NULL),('a71f29bf-4ef2-4c39-aa9d-be573ed7f6e9','d71c22ef-92e3-4661-9ee3-f903eabcada3','Nguyễn Thị Tài Chính',NULL,NULL),('dcdc7790-80af-463a-9a34-9854997b4485','af56b30d-5988-4977-9a6e-fb6ac8e9aa83','Trần Minh Hiếu',NULL,NULL),('e9bca248-be6a-45d1-b56f-20cae22a3c5e','edcfad4c-0d79-445f-b922-44ce7cef3283','Trần Nguyễn Công Hà',NULL,NULL);
/*!40000 ALTER TABLE `NHANVIEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PHANHOI`
--

DROP TABLE IF EXISTS `PHANHOI`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PHANHOI` (
  `MaPhanHoi` char(36) NOT NULL,
  `MaLoaiPhanHoi` char(36) DEFAULT NULL,
  `MaBenhNhan` char(36) DEFAULT NULL,
  `TenNhanVienPhuTrach` varchar(255) DEFAULT NULL,
  `TenVacXin` varchar(255) DEFAULT NULL,
  `NoiDung` text,
  `ThoiGianTiem` date DEFAULT NULL,
  `DiaDiemTiem` varchar(255) DEFAULT NULL,
  `TrangThai` int DEFAULT '0' COMMENT '0: Mới gửi, 1: Đang xử lý, 2: Đã giải quyết',
  `NgayTao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaPhanHoi`),
  KEY `MaLoaiPhanHoi` (`MaLoaiPhanHoi`),
  KEY `MaBenhNhan` (`MaBenhNhan`),
  CONSTRAINT `PHANHOI_ibfk_1` FOREIGN KEY (`MaLoaiPhanHoi`) REFERENCES `LOAIPHANHOI` (`MaLoaiPhanHoi`),
  CONSTRAINT `PHANHOI_ibfk_2` FOREIGN KEY (`MaBenhNhan`) REFERENCES `BENHNHAN` (`MaBenhNhan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PHANHOI`
--

LOCK TABLES `PHANHOI` WRITE;
/*!40000 ALTER TABLE `PHANHOI` DISABLE KEYS */;
INSERT INTO `PHANHOI` VALUES ('0faa0735-0802-4a40-9c44-8a5c983ed1e9','dcf6a6c1-040e-11f1-ba94-222e410c1fe9','e39b0743-774c-445a-9e64-b780a9e518aa','Administrator',NULL,'Nhân viên y tế nhiệt tình\n',NULL,NULL,2,'2026-02-08 14:46:59'),('324e9bf2-29c0-46c3-990d-06c8de766c53','dcf558cd-040e-11f1-ba94-222e410c1fe9','c69b8568-9aae-46b1-8cf9-e665ff1468d8','Administrator',NULL,'Xếp hàng chờ thiếu ổn định và quá lâu',NULL,NULL,2,'2026-02-08 08:13:00'),('f784084d-1482-47c4-a053-9881ad4d99de','dcf6a6c1-040e-11f1-ba94-222e410c1fe9','765e510a-7bc5-4552-814f-a93cbb1c8e7e','Administrator',NULL,'Bác sĩ tư vấn nhiệt tình và nồng nhiệt',NULL,NULL,2,'2026-02-08 14:47:51');
/*!40000 ALTER TABLE `PHANHOI` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PHANQUYEN`
--

DROP TABLE IF EXISTS `PHANQUYEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PHANQUYEN` (
  `MaQuyen` char(36) NOT NULL,
  `TenQuyen` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaQuyen`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PHANQUYEN`
--

LOCK TABLES `PHANQUYEN` WRITE;
/*!40000 ALTER TABLE `PHANQUYEN` DISABLE KEYS */;
INSERT INTO `PHANQUYEN` VALUES ('550e8400-e29b-41d4-a716-446655440000','Administrator'),('550e8400-e29b-41d4-a716-446655440001','Quản lý kho'),('550e8400-e29b-41d4-a716-446655440002','Tài chính'),('550e8400-e29b-41d4-a716-446655440003','Hỗ trợ khách hàng'),('550e8400-e29b-41d4-a716-446655440004','Nhân viên y tế'),('550e8400-e29b-41d4-a716-446655440005','Normal User Account');
/*!40000 ALTER TABLE `PHANQUYEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PHIEUXUAT`
--

DROP TABLE IF EXISTS `PHIEUXUAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PHIEUXUAT` (
  `MaPhieuXuat` char(36) NOT NULL,
  `SoPhieuXuat` varchar(255) NOT NULL,
  `MaLo` char(36) NOT NULL,
  `MaNhanVien` char(36) DEFAULT NULL,
  `NgayXuat` datetime DEFAULT CURRENT_TIMESTAMP,
  `SoLuongXuat` int NOT NULL,
  `NoiNhan` varchar(255) DEFAULT NULL,
  `GhiChu` text,
  PRIMARY KEY (`MaPhieuXuat`),
  UNIQUE KEY `UQ_SoPhieuXuat` (`SoPhieuXuat`),
  KEY `FK_PHIEUXUAT_LOVACXIN` (`MaLo`),
  KEY `FK_PHIEUXUAT_NHANVIEN` (`MaNhanVien`),
  CONSTRAINT `FK_PHIEUXUAT_LOVACXIN` FOREIGN KEY (`MaLo`) REFERENCES `LOVACXIN` (`MaLo`) ON DELETE CASCADE,
  CONSTRAINT `FK_PHIEUXUAT_NHANVIEN` FOREIGN KEY (`MaNhanVien`) REFERENCES `NHANVIEN` (`MaNhanVien`) ON DELETE SET NULL,
  CONSTRAINT `CHK_SoLuongXuat` CHECK ((`SoLuongXuat` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PHIEUXUAT`
--

LOCK TABLES `PHIEUXUAT` WRITE;
/*!40000 ALTER TABLE `PHIEUXUAT` DISABLE KEYS */;
INSERT INTO `PHIEUXUAT` VALUES ('bdf62de1-56cd-40fc-9644-e0bb8c83bfee','PX-1769568995','4369f5f0-6129-429a-8c98-138bcf81cc57',NULL,'2026-01-28 09:56:35',500,'Trạm y tế Hồng Bàng, Quận 8','Cấp vaccine cho ổ dịch mới bùng phát'),('f61e40de-d029-4695-9cab-11c99dbe6dfb','PX-1769402382','2ffec5f8-15e5-44e9-ba9d-65114961c262',NULL,'2026-01-26 11:39:42',98,'Trạm y tế phường 1','');
/*!40000 ALTER TABLE `PHIEUXUAT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TAIKHOAN`
--

DROP TABLE IF EXISTS `TAIKHOAN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TAIKHOAN` (
  `MaTaiKhoan` char(36) NOT NULL,
  `TenDangNhap` varchar(255) DEFAULT NULL,
  `MatKhau` varchar(255) DEFAULT NULL,
  `HoTen` varchar(255) DEFAULT NULL,
  `CMND` varchar(255) DEFAULT NULL,
  `NoiO` varchar(255) DEFAULT NULL,
  `MoTa` text,
  `TrangThai` tinyint(1) NOT NULL DEFAULT '1',
  `NgayTao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `NgayCapNhat` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaTaiKhoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TAIKHOAN`
--

LOCK TABLES `TAIKHOAN` WRITE;
/*!40000 ALTER TABLE `TAIKHOAN` DISABLE KEYS */;
INSERT INTO `TAIKHOAN` VALUES ('12a7847a-4d75-4f75-8f50-be3f054df15d','khobai01','$2a$10$07lwIqRp7doxBuodf/rtEe9Ad4HjjXTHSegMrO8jKFxxVWIYsLUKq','Nguyễn Văn C','123456789','49/90, đường Hồng Bàng, Quận 6, TPHCM','Quản lý kho bãi vaccine\n',1,'2026-01-24 15:21:33','2026-01-24 15:23:49',NULL),('20f2e44e-db16-4a53-875a-15b222f923af','hieu123','$2a$10$1Qnklbq.k7WPa3l3w3KLf..3cFVKPiBKYW.X01XL2q/X.ylaXpqie','Nguyễn Joseph Hiếu','123456789','123 Đường ABC, TP. Hồ Chí Minh','Tài khoản đăng ký mới qua Postman',1,'2026-01-18 13:09:37','2026-01-18 14:56:00','hieu@example.com'),('5635cf6f-23d6-44d2-82c2-91975db9b6ff','BenhNhan03','$2a$10$7D9un8cGjqF2s8quKtGweOYOi.4Y7ryDtVnXgPjwrMfH2CYP/0jAm','Phạm Gia Q','123456780','89/7, đường số 1, phường Bến Nghé, Quận 1, TPHCM','Bệnh nhân số 3',1,'2026-01-25 09:57:39','2026-01-25 09:57:39','bn03@gmail.com'),('720f12c7-9c0e-4a36-8e8a-ad2f8f0ce89b','TaiChinh02','$2a$10$WMEB76j0Gj7MaiFFWu2JS.6dKYcp/NcBtQc8im9U.LbyzzO.5s0Ia','Nguyễn Đình Bắc','128948992','80/89, ấp Hòa Hưng, Yên Bái','Nhân viên tài chính số 2',1,'2026-01-31 10:21:07','2026-01-31 10:21:07',''),('7734a5c8-a2a5-4bba-95fd-001b520ee52e','BacSi02','$2a$10$o.PAH.G7W4KnxxpzeeBh8.i2twhZD.m1dC4eO8lFNIisaUxrT.Vye','Nguyễn Nguyên Thảo','1924979702','90/812, Thành Thái, Quận 10, TPHCM','Bác sĩ tiêm phòng số 2',1,'2026-01-29 16:54:59','2026-02-06 15:13:32',''),('973b87c5-94a1-4d2d-b69c-df03de36a70d','HTKH01','$2a$10$JeNDADyL4WaV86zsCGmsKuGmgqTSZuaPsUDKpDUyjFF6W7N6drROe','Lê Thị A','123456789','90/90 đường An Dương Vương, Quận 5, TPHCM','Nhân viên chăm sóc khách hàng',1,'2026-01-24 15:22:43','2026-01-24 15:23:47',NULL),('af56b30d-5988-4977-9a6e-fb6ac8e9aa83','HieuThuHai','$2a$10$mxtstHpF3rIVk9uYXtTHjef8F70oYDsxX2B.b5Qz/BYvIl2CYdzRe','Trần Minh Hiếu','07920129792','Hoccmon, Quận 12, TPHCM','Là người con xa xứ',1,'2026-01-18 13:09:37','2026-01-18 14:03:18',NULL),('c2ec059e-dfde-46c4-8ad4-c4cf7cbbb445','BenhNhan04','$2a$10$H0kzwGivEu9lAIrpjibCzeDfor.PVPeBD0ySsFkjHDwoEPt/qb.pC','Trần Mạnh Giàu','1234567890','83/12, Phan Đình Phùng, Phường Phú Nhuận, TPHCM','',1,'2026-01-25 09:58:54','2026-02-08 15:19:13','chotohutieudi@gmail.com'),('c4fc4bf1-9ac8-4e51-93e3-c47233d56738','BenhNhan05','$2a$10$yxe5iAHerGEHUYl81PybJuH/S8.GQnkyCcWFlNEsddeOT7InYky3y','Trần Thanh Sang','123456789','135/10, An Dương Vương, Phường Phú Thọ, TPHCM','Bệnh nhân số 5\n',1,'2026-01-26 08:50:25','2026-01-26 08:50:25','bn05@gmail.com'),('cb9fe6a2-b98b-4a76-bcc0-28d49862c48c','BenhNhan02','$2a$10$L8yyPXXq2XYfLu1zrJF6hu5T3l9xQ5owZB4DxYlIFjw4VifrxckxO','Trần Thị Kiều Anh','123456789','9/82, đường Phạm Văn Chiêu, Gò Vấp, TPHCM','Bệnh nhân số 2',1,'2026-01-25 09:56:19','2026-02-08 14:58:49','nguyenhoanghieu15102004@gmail.com'),('d71c22ef-92e3-4661-9ee3-f903eabcada3','TaiChinh01','$2a$10$t/dshtJwdEUu8hnBw418hOW3eETeZrxCoY5C4lIfOX9E9kabhZUPy','Nguyễn Thị Tài Chính','0929894899','98/28 đường Hồng Bàng, Quận 5, TPHCM','Đam mê tài chính',1,'2026-01-18 13:09:37','2026-02-08 08:27:04','hieuhutieugo@gmail.com'),('ea2e1328-9b00-4996-9498-83878153eb33','BenhNhan01','$2a$10$YQO7IKkHKaFmDfHZnWO1.Oj2QGBQlSx.m4qRCSK/78qNgyFZu6JKy','Nguyễn Văn A','123456789','89, Phạm Văn Đồng, TPHCM','Bệnh nhân số 1',1,'2026-01-24 16:55:53','2026-01-24 17:05:44','bn01@gmail.com'),('edcfad4c-0d79-445f-b922-44ce7cef3283','BacSi03','$2a$10$Ks1GewAu11p2Y1KxUYZN3.p9un13rqxcrROJOgAyYLPCVuKAScd/a','Trần Nguyễn Công Hà','123456789','90/8, Phan Huy Ích, Quận 12, TPHCM','Nhân viên y tế số 3\n',1,'2026-02-05 16:52:46','2026-02-06 15:13:32',''),('fe1d6c87-d37f-4554-b544-cefdee4ddc3c','BacSi01','$2a$10$ojja9uiM3Od7bZOoPPHO..OUYGuM4VC.NKRPzjo6vHmjMNkSHUNdG','Nguyễn Trần B','123456789','589/9 phường , Quận 7, TPHCM','Bác sĩ Tiêm phòng',1,'2026-01-24 14:58:39','2026-02-06 15:13:32',NULL);
/*!40000 ALTER TABLE `TAIKHOAN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VACXIN`
--

DROP TABLE IF EXISTS `VACXIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `VACXIN` (
  `MaVacXin` char(36) NOT NULL,
  `MaLoaiVacXin` char(36) DEFAULT NULL,
  `TenVacXin` varchar(255) DEFAULT NULL,
  `HanSuDung` date DEFAULT NULL,
  `HamLuong` varchar(255) DEFAULT NULL,
  `PhongNguaBenh` varchar(255) DEFAULT NULL,
  `DoTuoiTiemChung` varchar(255) DEFAULT NULL,
  `DonGia` decimal(15,2) DEFAULT NULL,
  `DieuKienBaoQuan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaVacXin`),
  KEY `MaLoaiVacXin` (`MaLoaiVacXin`),
  CONSTRAINT `VACXIN_ibfk_1` FOREIGN KEY (`MaLoaiVacXin`) REFERENCES `LOAIVACXIN` (`MaLoaiVacXin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VACXIN`
--

LOCK TABLES `VACXIN` WRITE;
/*!40000 ALTER TABLE `VACXIN` DISABLE KEYS */;
INSERT INTO `VACXIN` VALUES ('315cb6bf-7d98-480e-b60f-dfd95e832bd3','15970ede-f99b-11f0-b6d7-0a9b2774db02','Covid-19','2026-05-25','1 ml','covid-19','Trẻ nhỏ trên 3 tuổi và người lớn',150000.00,'-20°C'),('73644697-c1a0-417f-a969-8853e778589b','15970f03-f99b-11f0-b6d7-0a9b2774db02','Phòng Tay Chân Miệng','2028-05-25','0.5 ml','Tay chân miệng','Trẻ em dưới 5 tuổi',150000.00,'2°C - 8°C'),('86cd0c98-0107-4766-97ce-c1baf71f04ee','159702c1-f99b-11f0-b6d7-0a9b2774db02','Phòng bệnh lao','2027-01-25','0.1 ml','Lao phổi','Trẻ em trên 3 tháng tuổi',150000.00,'2°C - 8°C'),('abe7e377-3cdf-4b8e-8fd0-af436fc220d5','15970e96-f99b-11f0-b6d7-0a9b2774db02','Phòng Thủy Đậu','2028-09-25','0.3 ml','Đậu Mùa','Mọi lứa tuổi',250000.00,'2°C - 8°C'),('c4e69e49-8eb2-4a58-b849-930b77f1883d','15970b2b-f99b-11f0-b6d7-0a9b2774db02','Phòng Viêm gan B','2028-05-25','1 ml','Viêm gan B','Mọi lứa tuổi',250000.00,'2°C - 8°C'),('c858cac6-7718-4473-a930-b7be65d6fa14','15970e41-f99b-11f0-b6d7-0a9b2774db02','Phòng bệnh sởi','2028-10-25','0.5 ml','Sởi, Mề đay','Trẻ em trên 3 tháng tuổi',200000.00,'-20°C'),('fa1a63b3-eeb5-4e11-ae40-78ba78498aac','15970f27-f99b-11f0-b6d7-0a9b2774db02','Phòng Dại','2028-02-09','0.1 ml','Bệnh Dại','Trên 5 tuổi',200000.00,'2°C - 8°C');
/*!40000 ALTER TABLE `VACXIN` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-21 20:46:16
