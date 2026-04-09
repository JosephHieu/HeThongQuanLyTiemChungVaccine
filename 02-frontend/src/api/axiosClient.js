import axios from "axios";
import { toast } from "react-hot-toast";

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: { "Content-Type": "application/json" },
});

// axiosClient.js
axiosClient.interceptors.response.use(
  (response) => {
    return response.data?.result ?? response.data;
  },
  (error) => {
    const backendError = error.response?.data;
    const status = error.response?.status;
    const errorCode = backendError?.code;

    // --- NHÓM 1: LỖI BUỘC PHẢI ĐĂNG XUẤT (401 / INVALID TOKEN / NOT LOGGED IN) ---
    // Chỉ xóa token khi mã lỗi là 1016 (Token hỏng), 1017 (Chưa login) hoặc 1013 (Token hết hạn)
    const logoutErrorCodes = [1013, 1016, 1017];

    if (status === 401 && logoutErrorCodes.includes(errorCode)) {
      console.warn("Hệ thống yêu cầu đăng xuất. Mã lỗi:", errorCode);

      localStorage.removeItem("token");
      localStorage.removeItem("role");
      localStorage.removeItem("userName");

      if (!window.location.pathname.includes("/login")) {
        toast.error("Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại!");
        window.location.href = "/login?message=expired";
      }
      return Promise.reject(backendError);
    }

    // --- NHÓM 2: LỖI SAI THÔNG TIN ĐĂNG NHẬP (1009) ---
    if (errorCode === 1009) {
      toast.error("Tên đăng nhập hoặc mật khẩu không đúng!");
      return Promise.reject(backendError);
    }

    // --- NHÓM 3: LỖI CẤM TRUY CẬP (403 / 1010) ---
    if (status === 403 || errorCode === 1010) {
      toast.error("Bạn không có quyền thực hiện chức năng này!");
      return Promise.reject(backendError);
    }

    // --- NHÓM 4: CÁC LỖI NGHIỆP VỤ KHÁC ---
    if (backendError?.message) {
      toast.error(backendError.message);
    }

    return Promise.reject(
      backendError || { message: "Lỗi kết nối hệ thống", code: 9999 },
    );
  },
);

export default axiosClient;
