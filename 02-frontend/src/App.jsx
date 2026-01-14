import React from "react";
// Import đầy đủ các thành phần từ react-router-dom
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Login from "./pages/Login";
import AdminLayout from "./layouts/AdminLayout";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./routes/ProtectedRoute";

// Các trang tạm thời cho các chức năng khác
const Placeholder = ({ title }) => (
  <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200">
    <h2 className="text-2xl font-bold text-slate-800">Phân hệ: {title}</h2>
    <p className="text-slate-500 mt-2">
      Chức năng này đang được phát triển theo thiết kế trong SRS.
    </p>
  </div>
);

function App() {
  return (
    <Router>
      <Routes>
        {/* 1. Mặc định khi vào trang web sẽ đẩy về Login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* 2. Trang Login công khai */}
        <Route path="/login" element={<Login />} />

        {/* 3. Phân hệ Admin: Sử dụng Layout chung có Sidebar */}
        {/* Route này được bảo vệ, chỉ cho phép Administrator vào */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRoles={["Administrator"]}>
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          {/* Mặc định khi vào /admin sẽ chuyển tới dashboard */}
          <Route index element={<Navigate to="dashboard" replace />} />

          {/* Dashboard chính với các thẻ chức năng */}
          <Route path="dashboard" element={<Dashboard />} />

          {/* Các trang con khớp với mục lục Screen Content */}
          <Route
            path="accounts"
            element={<Placeholder title="Quản lý tài khoản (Account)" />}
          />
          <Route
            path="warehouse"
            element={<Placeholder title="Quản lý kho bãi (Kho bãi)" />}
          />
          <Route path="reports" element={<Placeholder title="Báo cáo" />} />
          <Route
            path="moderator"
            element={<Placeholder title="Điều phối (Moderator)" />}
          />
          <Route
            path="about"
            element={<Placeholder title="Thông tin (About)" />}
          />
        </Route>

        {/* 4. Trang xử lý khi không có quyền truy cập */}
        <Route
          path="/unauthorized"
          element={
            <div className="min-h-screen flex items-center justify-center bg-slate-100 font-bold text-red-500">
              Bạn không có quyền truy cập vào khu vực này!
            </div>
          }
        />

        {/* 5. Trang 404 - Không tìm thấy đường dẫn */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
