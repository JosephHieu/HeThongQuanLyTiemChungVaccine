import React from "react";
import { Toaster } from "react-hot-toast";
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
import AccountManagement from "./pages/AccountManagement";
import ScheduleManagement from "./pages/Vaccination/ScheduleManagement";
import InventoryManagement from "./pages/Inventory/InventoryManagement";

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
      <Toaster position="top-right" />
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />

        {/* Cửa tổng /admin: Cho phép tất cả các loại nhân viên vào để thấy Layout */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute
              allowedRoles={[
                "Administrator",
                "Nhân viên y tế",
                "Quản lý kho",
                "Tài chính",
                "Hỗ trợ khách hàng",
              ]}
            >
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />

          {/* --- PHÂN QUYỀN CHI TIẾT TỪNG PHÂN HỆ --- */}

          {/* 1. Quản lý tài khoản: Chỉ duy nhất Admin */}
          <Route
            path="accounts"
            element={
              <ProtectedRoute allowedRoles={["Administrator"]}>
                <AccountManagement />
              </ProtectedRoute>
            }
          />

          {/* 2. Kho vắc-xin: Admin và nhân viên kho */}
          <Route
            path="warehouse"
            element={
              <ProtectedRoute allowedRoles={["Administrator", "Quản lý kho"]}>
                <InventoryManagement />
              </ProtectedRoute>
            }
          />

          {/* 3. Lịch tiêm: Admin, Hỗ trợ (tư vấn) và Y tế (xem lịch khám) */}
          <Route
            path="schedules"
            element={
              <ProtectedRoute
                allowedRoles={[
                  "Administrator",
                  "Hỗ trợ khách hàng",
                  "Nhân viên y tế",
                ]}
              >
                <ScheduleManagement />
              </ProtectedRoute>
            }
          />

          {/* 4. Báo cáo & Tài chính: Admin và nhân viên tài chính */}
          <Route
            path="reports"
            element={
              <ProtectedRoute allowedRoles={["Administrator", "Tài chính"]}>
                <Placeholder title="Báo cáo & Tài chính" />
              </ProtectedRoute>
            }
          />

          {/* 5. Điều phối (Khám bệnh): Admin và nhân viên y tế */}
          <Route
            path="moderator"
            element={
              <ProtectedRoute
                allowedRoles={["Administrator", "Nhân viên y tế"]}
              >
                <Placeholder title="Điều phối & Khám bệnh" />
              </ProtectedRoute>
            }
          />

          {/* 6. Hỗ trợ & Feedback: Admin và nhân viên hỗ trợ */}
          <Route
            path="support"
            element={
              <ProtectedRoute
                allowedRoles={["Administrator", "Hỗ trợ khách hàng"]}
              >
                <Placeholder title="Chăm sóc khách hàng" />
              </ProtectedRoute>
            }
          />

          {/* 7. Thông tin chung: Tất cả nhân viên đều xem được */}
          <Route
            path="about"
            element={<Placeholder title="Thông tin hệ thống" />}
          />
        </Route>

        {/* Trang báo lỗi quyền truy cập */}
        <Route
          path="/unauthorized"
          element={
            <div className="min-h-screen flex items-center justify-center bg-slate-100 font-bold text-red-500">
              Bạn không có quyền truy cập vào phân hệ này!
            </div>
          }
        />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
