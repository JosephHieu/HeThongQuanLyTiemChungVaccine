# 💉 VaxTrack Pro - Hệ thống Quản lý Tiêm chủng & Tài chính Tập trung
**VaxTrack Pro** là giải pháp quản trị tổng thể cho các trung tâm y tế dự phòng, được thiết kế theo chuẩn SRS v3.0. Hệ thống giải quyết bài toán cấp thiết trong việc bảo vệ cộng đồng khỏi dịch bệnh thông qua quản lý tiêm chủng, cơ sở vật chất và tài chính minh bạch.

## 👥 Hệ thống Phân quyền (Role-Based Access Control)

Dựa trên yêu cầu nghiệp vụ, hệ thống chia người dùng thành 3 nhóm chính với 6 vai trò cụ thể:

- **Administrator:** Toàn quyền quản trị hệ thống, quản lý tài khoản và phân quyền.
- **Moderator (Nhóm điều hành):**
  - **Quản lý kho:** Theo dõi tình hình vắc-xin, thực hiện nhập/xuất kho.
  - **Nhân viên tài chính:** Quản lý giá, thu chi, đối soát giao dịch khách hàng và nhà cung cấp.
  - **Nhân viên y tế:** Trực tiếp khám, cập nhật hồ sơ bệnh án và kê đơn.
  - **Hỗ trợ khách hàng:** Tư vấn, giải đáp thắc mắc và nhắc lịch tiêm chủng qua Email/SMS.
  - **Normal User (Khách hàng):** Tra cứu thông tin vắc-xin, đăng ký tiêm phòng trực tuyến và theo dõi hồ sơ cá nhân

## 🌟 Tính năng cốt lõi theo quy trình SRS

### 1. Quản lý Kho & Logistics

- **Xem tình hình kho:** Tra cứu đa năng theo tên, loại vắc-xin, nơi sản xuất hoặc độ tuổi.
- **Nhập kho tự động:** Tự động tạo hóa đơn tài chính ngay khi thêm lô mới.
- **Xuất kho:** Kiểm soát số lượng xuất thực tế, đảm bảo không xuất quá số lượng tồn.

### 2. Quy trình Y tế Khép kín (E2E)

- **Hồ sơ bệnh án điện tử:** Lưu vết toàn bộ lịch sử tiêm, phản ứng sau tiêm và thời gian tác dụng của vắc-xin.
- **Kê đơn & Hẹn tiêm:** Hỗ trợ bác sĩ kê đơn và lập lịch tiêm nhắc lại cho bệnh nhân.
- **Tư vấn khách hàng:** Hệ thống giải đáp thắc mắc và FAQ tự động cho người dùng.

### 3. Quản trị Tài chính & Giao dịch 

- **Quản lý thu chi:** Thống kê định kỳ doanh thu từ khách hàng và công nợ nhà cung cấp.
- **Biên lai điện tử:** Xuất biên lai giao dịch ngay sau khi hoàn tất quy trình tiêm.

## 📊 Thiết kế Cơ sở dữ liệu (Database Schema)
Hệ thống được xây dựng trên một sơ đồ quan hệ (Relational Schema) tối ưu, đảm bảo tính toàn vẹn dữ liệu cho hơn 15 thực thể chính.

- **Core Inventory:** VACXIN, LOAIVACXIN, LOVACXIN, NHACUNGCAP.

- **Finance:** HOADON (Kết nối trung tâm giữa Kho và Bệnh nhân).

- **Clinical:** BENHNHAN, HOSOBENHAN, LICHTIEMCHUNG, PHANHOI.

- **System:** NHANVIEN, TAIKHOAN, PHANQUYEN.

<img width="1277" height="895" alt="image" src="https://github.com/user-attachments/assets/c779745f-bf47-4921-b5ab-608b45f09e91" />


## 🛠 Công nghệ sử dụng
`Backend`

- **Spring Boot 3.x:** Framework chính cho REST API.

- **Spring Security & JWT:** Bảo mật hệ thống và phân quyền dựa trên vai trò.

- **Spring Data JPA:** Quản lý tương tác cơ sở dữ liệu và Transaction.

- **Hibernate:** Xử lý nạp dữ liệu Lazy/Eager và Proxy optimization.

`Frontend`

- **React.js & Vite:** Thư viện giao diện người dùng hiện đại và tốc độ build nhanh.

- **Tailwind CSS:** Framework CSS tối ưu cho giao diện Responsive.

- **Lucide Icons:** Bộ icon vector chuyên nghiệp cho ngành y tế.

## 🚀 Hướng dẫn cài đặt & Thiết lập Database

Dự án cung cấp file db-project-script.sql chứa đầy đủ cấu trúc và dữ liệu mẫu.

**1. Thiết lập Database**

1. Mở MySQL Workbench hoặc Terminal.
2. Tạo database: CREATE DATABASE vaccine_management;
3. Import dữ liệu:

```bash
mysql -u username -p vaccine_management < db-project-script.sql
```

**2. Cấu hình Backend**
Sửa file application.properties:

```Properties
spring.datasource.url=jdbc:mysql://localhost:3306/vaccine_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```



  




