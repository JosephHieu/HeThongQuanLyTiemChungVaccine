import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axiosClient from "../api/axiosClient";
import toast, { Toaster } from "react-hot-toast";
import { Lock, User, Eye, EyeOff, LogIn } from "lucide-react";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Lấy trang mà người dùng định vào trước khi bị đá ra Login (nếu có)
  const from = location.state?.from?.pathname || "/admin/dashboard";

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Gọi API qua client để tự động lấy phần 'result'
      const result = await axiosClient.post("/auth/login", {
        tenDangNhap: username,
        matKhau: password,
      });

      const { token, roles, hoTen } = result;

      // Lưu thông tin
      localStorage.setItem("token", token);
      localStorage.setItem("role", roles[0]);
      localStorage.setItem("userName", hoTen);

      toast.success(`Chào mừng ${hoTen} trở lại!`);

      // Logic điều hướng dựa trên Role
      const staffRoles = [
        "Administrator",
        "Quản lý kho",
        "Nhân viên y tế",
        "Tài chính",
        "Hỗ trợ khách hàng",
      ];
      const isStaffMember = roles.some((role) => staffRoles.includes(role));

      if (isStaffMember) {
        // Nhân viên vào trang quản trị
        navigate(from, { replace: true });
      } else {
        // Bệnh nhân vào trang tra cứu vắc-xin (Khớp với App.jsx của bạn)
        navigate("/user", { replace: true });
      }
    } catch (error) {
      // error lúc này đã là object {code, message} nhờ interceptor của axiosClient
      toast.error(error.message || "Đăng nhập thất bại");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="p-8 md:p-12">
          <div className="text-center mb-8">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 text-blue-600 rounded-full mb-4">
              <LogIn size={32} />
            </div>
            <h1 className="text-3xl font-extrabold text-gray-900">Đăng nhập</h1>
            <p className="text-gray-500 mt-2">Hệ thống quản lý tiêm chủng</p>
          </div>

          <form onSubmit={handleLogin} className="space-y-6">
            {/* Username Input */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tên đăng nhập
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                  <User size={20} />
                </span>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-gray-50 focus:bg-white"
                  placeholder="Nhập tên đăng nhập"
                />
              </div>
            </div>

            {/* Password Input */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mật khẩu
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                  <Lock size={20} />
                </span>
                <input
                  type={showPassword ? "text" : "password"}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-gray-50 focus:bg-white"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 focus:outline-none"
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between text-sm">
              <a
                href="#"
                className="text-blue-600 hover:text-blue-700 font-medium transition-colors"
              >
                Quên mật khẩu?
              </a>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl shadow-lg shadow-blue-500/30 focus:ring-4 focus:ring-blue-300 transition-all flex items-center justify-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {loading ? (
                <svg
                  className="animate-spin h-5 w-5 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  ></circle>
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
              ) : (
                "Đăng nhập"
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
