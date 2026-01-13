SELECT * FROM vaccine_management.PHANQUYEN;

use vaccine_management;
INSERT INTO PHANQUYEN (MaQuyen, TenQuyen) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'Administrator'), -- Toàn quyền hệ thống 
('550e8400-e29b-41d4-a716-446655440001', 'Quản lý kho'),     -- Moderator: Quản lý kho 
('550e8400-e29b-41d4-a716-446655440002', 'Tài chính'),       -- Moderator: Tài chính 
('550e8400-e29b-41d4-a716-446655440003', 'Hỗ trợ khách hàng'),-- Moderator: Hỗ trợ khách hàng 
('550e8400-e29b-41d4-a716-446655440004', 'Nhân viên y tế'),   -- Moderator: Nhân viên y tế 
('550e8400-e29b-41d4-a716-446655440005', 'Normal User Account'); -- Người dùng đăng ký tiêm