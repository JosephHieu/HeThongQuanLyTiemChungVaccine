# 💉 VaxTrack Pro - Hệ thống Quản lý Tiêm chủng & Tài chính Tập trung
**VaxTrack Pro** là giải pháp quản trị tổng thể cho các trung tâm tiêm chủng, tích hợp từ quy trình tiếp đón bệnh nhân, tư vấn, tiêm chủng cho đến quản lý kho vắc-xin và dòng tiền tài chính. Hệ thống đảm bảo tính nhất quán tuyệt đối giữa dữ liệu vật tư và dữ liệu kế toán.

## 🌟 Tính năng cốt lõi
**1. Quản lý Kho & Chuỗi cung ứng**

- **Nhập kho tự động hóa:** Tích hợp quy trình tạo HOADON nhập hàng ngay khi thêm LOVACXIN mới.

- **Theo dõi Lô hàng (Batch Tracking):** Quản lý chi tiết số lượng nhập ban đầu (SoLuongNhap) và tồn kho thực tế (SoLuong) để phục vụ kiểm kê.

- **Hỗ trợ FEFO:** Ưu tiên xuất các lô hàng sắp hết hạn dựa trên dữ liệu HanSuDung.

**2. Phân hệ Tài chính & Giao dịch**

- **Đa dạng luồng tiền:** Phân tách rõ ràng giữa hóa đơn khách hàng (XUAT) và hóa đơn nhà cung cấp (NHAP).

- **Chi tiết Giao dịch:** Modal hiển thị thông tin chuyên sâu, cho phép đối soát đơn giá vốn, thành tiền và thông tin lô hàng ngay trên phiếu chi.

- **Dashboard Tài chính:** Tổng hợp doanh thu, vốn đầu tư và giá trị tài sản kho theo thời gian thực.

**3. Quy trình Tiêm chủng Khép kín (E2E)**

- **Quản lý Bệnh nhân:** Lưu trữ hồ sơ bệnh án, lịch sử tiêm chủng và phản hồi sau tiêm.

- **Hệ thống Tư vấn:** Quản lý lượt tư vấn và hỗ trợ bác sĩ đưa ra phác đồ tiêm phù hợp.

- **Quản trị Nhân sự:** Phân quyền chi tiết (PHANQUYEN) cho nhân viên y tế và nhân viên kho/kế toán.

## 📊 Thiết kế Cơ sở dữ liệu (Database Schema)
Hệ thống được xây dựng trên một sơ đồ quan hệ (Relational Schema) tối ưu, đảm bảo tính toàn vẹn dữ liệu cho hơn 15 thực thể chính.

- **Core Inventory:** VACXIN, LOAIVACXIN, LOVACXIN, NHACUNGCAP.

- **Finance:** HOADON (Kết nối trung tâm giữa Kho và Bệnh nhân).

- **Clinical:** BENHNHAN, HOSOBENHAN, LICHTIEMCHUNG, PHANHOI.

- **System:** NHANVIEN, TAIKHOAN, PHANQUYEN.

<img src="docs/DB_Vaccine_Management_SChema.svg" width="800" style="border:1px solid #ddd"/>

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

Dự án đã bao gồm file db-project-script.sql chứa đầy đủ cấu trúc bảng và dữ liệu mẫu để bạn có thể chạy thử Dashboard ngay lập tức.

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



  

