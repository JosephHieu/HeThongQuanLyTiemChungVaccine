CREATE DATABASE IF NOT EXISTS vaccine_management
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE vaccine_management;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. NHÓM TÀI KHOẢN & PHÂN QUYỀN
CREATE TABLE PHANQUYEN (
    MaQuyen CHAR(36) PRIMARY KEY,
    TenQuyen VARCHAR(255)
);

CREATE TABLE TAIKHOAN (
    MaTaiKhoan CHAR(36) PRIMARY KEY,
    TenDangNhap VARCHAR(255),
    MatKhau VARCHAR(255),
    HoTen VARCHAR(255),
    CMND VARCHAR(255),
    NoiO VARCHAR(255),
    MoTa TEXT,
    Email VARCHAR(255)
);

CREATE TABLE CHITIETPHANQUYEN (
    MaQuyen CHAR(36),
    MaTaiKhoan CHAR(36),
    PRIMARY KEY (MaQuyen, MaTaiKhoan),
    FOREIGN KEY (MaQuyen) REFERENCES PHANQUYEN(MaQuyen),
    FOREIGN KEY (MaTaiKhoan) REFERENCES TAIKHOAN(MaTaiKhoan)
);

-- 2. NHÓM ĐỐI TƯỢNG (NHÂN VIÊN & BỆNH NHÂN)
CREATE TABLE NHANVIEN (
    MaNhanVien CHAR(36) PRIMARY KEY,
    MaTaiKhoan CHAR(36),
    TenNhanVien VARCHAR(255),
    NamSinh INT,
    SDT VARCHAR(255),
    FOREIGN KEY (MaTaiKhoan) REFERENCES TAIKHOAN(MaTaiKhoan)
);

CREATE TABLE BENHNHAN (
    MaBenhNhan CHAR(36) PRIMARY KEY,
    MaTaiKhoan CHAR(36),
    TenBenhNhan VARCHAR(255),
    NgaySinh DATE,
    DiaChi VARCHAR(255),
    NguoiGiamHo VARCHAR(255),
    SDT VARCHAR(255),
    CanNang DECIMAL(5,2),
    FOREIGN KEY (MaTaiKhoan) REFERENCES TAIKHOAN(MaTaiKhoan)
);

-- 3. NHÓM HÓA ĐƠN (Phải tạo trước để các bảng khác tham chiếu)
CREATE TABLE HOADON (
    MaHoaDon CHAR(36) PRIMARY KEY,
    TongTien DECIMAL(15,2)
);

-- 4. NHÓM VẮC XIN & KHO
CREATE TABLE LOAIVACXIN (
    MaLoaiVacXin CHAR(36) PRIMARY KEY,
    TenLoaiVacXin VARCHAR(255)
);

CREATE TABLE VACXIN (
    MaVacXin CHAR(36) PRIMARY KEY,
    MaLoaiVacXin CHAR(36),
    TenVacXin VARCHAR(255),
    HanSuDung DATE,
    HamLuong VARCHAR(255),
    PhongNguaBenh VARCHAR(255),
    DoTuoiTiemChung VARCHAR(255),
    DonGia DECIMAL(15,2),
    DieuKienBaoQuan VARCHAR(255),
    FOREIGN KEY (MaLoaiVacXin) REFERENCES LOAIVACXIN(MaLoaiVacXin)
);

CREATE TABLE NHACUNGCAP (
    MaNhaCungCap CHAR(36) PRIMARY KEY,
    TenNhaCungCap VARCHAR(255)
);

CREATE TABLE LOVACXIN (
    MaLo CHAR(36) PRIMARY KEY,
    MaVacXin CHAR(36),
    MaNhaCungCap CHAR(36),
    MaHoaDon CHAR(36), -- Khớp với Diagram (nối sang HOADON)
    SoLuong INT,
    TinhTrang VARCHAR(255),
    NgayNhan DATE,
    NuocSanXuat VARCHAR(255),
    GiayPhep VARCHAR(255),
    GhiChu TEXT,
    FOREIGN KEY (MaVacXin) REFERENCES VACXIN(MaVacXin),
    FOREIGN KEY (MaNhaCungCap) REFERENCES NHACUNGCAP(MaNhaCungCap),
    FOREIGN KEY (MaHoaDon) REFERENCES HOADON(MaHoaDon)
);

-- 5. NHÓM TIÊM CHỦNG
CREATE TABLE LICHTIEMCHUNG (
    MaLichTiem CHAR(36) PRIMARY KEY,
    DoiTuong VARCHAR(255),
    ThoiGianChung VARCHAR(255),
    GhiChu TEXT,
    SoLuongNguoiTiem INT,
    NgayTiem DATE,
    DiaDiem VARCHAR(255)
);

CREATE TABLE CHITIET_DK_TIEM (
    MaChiTietDKTiem CHAR(36) PRIMARY KEY,
    MaBenhNhan CHAR(36),
    MaLo CHAR(36),
    MaLichTiem CHAR(36),
    ThoiGianCanTiem DATE,
    FOREIGN KEY (MaBenhNhan) REFERENCES BENHNHAN(MaBenhNhan),
    FOREIGN KEY (MaLo) REFERENCES LOVACXIN(MaLo),
    FOREIGN KEY (MaLichTiem) REFERENCES LICHTIEMCHUNG(MaLichTiem)
);

CREATE TABLE HOSOBENHAN (
    MaHoSoBenhAn CHAR(36) PRIMARY KEY,
    MaChiTietDKTiem CHAR(36),
    MaHoaDon CHAR(36), -- Khớp với Diagram (nối sang HOADON)
    PhanUngSauTiem TEXT,
    ThoiGianTacDung VARCHAR(255),
    ThoiGianTiem DATETIME,
    FOREIGN KEY (MaChiTietDKTiem) REFERENCES CHITIET_DK_TIEM(MaChiTietDKTiem),
    FOREIGN KEY (MaHoaDon) REFERENCES HOADON(MaHoaDon)
);

CREATE TABLE CHITIET_NV_THAMGIA (
    MaNhanVien CHAR(36),
    MaLichTiem CHAR(36),
    PRIMARY KEY (MaNhanVien, MaLichTiem),
    FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(MaNhanVien),
    FOREIGN KEY (MaLichTiem) REFERENCES LICHTIEMCHUNG(MaLichTiem)
);

-- 6. NHÓM DỊCH BỆNH & HỖ TRỢ
CREATE TABLE DICHBENH (
    MaDichBenh CHAR(36) PRIMARY KEY,
    MaNhanVien CHAR(36),
    TenDichBenh VARCHAR(255),
    TacHaiSucKhoe TEXT,
    DuongLayNhiem TEXT,
    SoNguoiBiNhiem INT,
    DiaChi VARCHAR(255),
    GhiChu TEXT,
    ThoiDiemKhaoSat DATE,
    FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(MaNhanVien)
);

CREATE TABLE LUOTTUVAN (
    MaLuotTuVan CHAR(36) PRIMARY KEY,
    CauHoi TEXT,
    TraLoi TEXT,
    MaBenhNhan CHAR(36),
    MaNhanVien CHAR(36),
    CauHoiThuongGap BOOLEAN,
    FOREIGN KEY (MaBenhNhan) REFERENCES BENHNHAN(MaBenhNhan),
    FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(MaNhanVien)
);

CREATE TABLE LOAIPHANHOI (
    MaLoaiPhanHoi CHAR(36) PRIMARY KEY,
    TenLoaiPhanHoi VARCHAR(255)
);

CREATE TABLE PHANHOI (
    MaPhanHoi CHAR(36) PRIMARY KEY,
    MaLoaiPhanHoi CHAR(36),
    MaBenhNhan CHAR(36),
    TenNhanVienPhuTrach VARCHAR(255),
    TenVacXin VARCHAR(255),
    NoiDung TEXT,
    ThoiGianTiem DATE,
    DiaDiemTiem VARCHAR(255),
    FOREIGN KEY (MaLoaiPhanHoi) REFERENCES LOAIPHANHOI(MaLoaiPhanHoi),
    FOREIGN KEY (MaBenhNhan) REFERENCES BENHNHAN(MaBenhNhan)
);

SET FOREIGN_KEY_CHECKS = 1;