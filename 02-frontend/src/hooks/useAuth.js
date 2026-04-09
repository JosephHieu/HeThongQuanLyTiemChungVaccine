export const useAuth = () => {
  const role = localStorage.getItem("role");
  const name = localStorage.getItem("userName");
  const token = localStorage.getItem("token");

  const hasAuthority = (allowedAuthorities) => {
    return allowedAuthorities.includes(role);
  };

  return {
    role,
    name,
    token,
    isAuthenticated: !!token,
    hasAuthority,
    // Đầy đủ 6 quyền tương ứng với các phân hệ trong trung tâm
    isAdmin: role === "Administrator",
    isMedical: role === "Nhân viên y tế",
    isInventory: role === "Quản lý kho",
    isFinance: role === "Tài chính",
    isSupport: role === "Hỗ trợ khách hàng",
    isUser: role === "Normal User Account",
  };
};
