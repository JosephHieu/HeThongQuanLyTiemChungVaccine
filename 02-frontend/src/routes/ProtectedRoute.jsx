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
  const hasPermission = () => {
    // Nếu không yêu cầu role nào thì cho qua
    if (!allowedRoles || allowedRoles.length === 0) return true;

    // Chuyển role về mảng để xử lý chung (phòng trường hợp role là string hoặc array)
    const userRoles = Array.isArray(role) ? role : [role];

    return userRoles.some((r) =>
      allowedRoles.map((a) => a?.trim()).includes(r?.trim()),
    );
  };

  if (!hasPermission()) {
    console.error("DEBUG PHÂN QUYỀN:", {
      "Role của bạn hiện tại": role,
      "Quyền cần có để vào trang": allowedRoles,
    });
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
