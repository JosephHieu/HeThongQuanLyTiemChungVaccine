export const useAuth = () => {
  const role = localStorage.getItem("role")?.trim();
  const name = localStorage.getItem("userName");
  const token = localStorage.getItem("token");

  const isLoading = false;

  const hasAuthority = (allowedAuthorities) => {
    if (!role || !allowedAuthorities) return false;
    return allowedAuthorities.includes(role);
  };

  return {
    role,
    name,
    token,
    isLoading,
    isAuthenticated: !!token && !!role,
    hasAuthority,
    isAdmin: role === "Administrator",
    isMedical: role === "MEDICAL",
    isInventory: role === "WAREHOUSE",
    isFinance: role === "FINANCE",
    isSupport: role === "SUPPORT",
    isUser: role === "Normal User Account",
  };
};
