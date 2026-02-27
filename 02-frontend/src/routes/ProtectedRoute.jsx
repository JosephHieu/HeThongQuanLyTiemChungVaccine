import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { isAuthenticated, role, isLoading } = useAuth(); // Thêm isLoading từ hook của bạn
  const location = useLocation();

  // 1. Chờ cho đến khi kiểm tra xong trạng thái đăng nhập (đọc xong localStorage/Token)
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  // 2. Nếu chưa đăng nhập -> Sang trang Login
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 3. Kiểm tra quyền hạn (Dùng logic giao thoa mảng để linh hoạt hơn)
  const hasPermission =
    !allowedRoles ||
    (Array.isArray(role)
      ? role.some((r) => allowedRoles.includes(r))
      : allowedRoles.includes(role));

  if (!hasPermission) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
