import axios from "axios";
import { toast } from "react-hot-toast"; // Khuyên dùng để hiện thông báo đẹp hơn alert

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: { "Content-Type": "application/json" },
});

// [Request Interceptor] - ĐÃ TỐT
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// [Response Interceptor] - CẬP NHẬT THÊM LOGIC
axiosClient.interceptors.response.use(
  (response) => {
    // Trả về phần result để code ở Page ngắn gọn hơn
    return response.data?.result ?? response.data;
  },
  (error) => {
    const backendError = error.response?.data; // Đây là ApiResponse của bạn
    const status = error.response?.status;
    const errorCode = backendError?.code;

    // 1. Xử lý lỗi Unauthenticated (Mã 1009) hoặc Token hết hạn (401)
    if (status === 401 || errorCode === 1009) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      if (!window.location.pathname.includes("/login")) {
        window.location.href = "/login?message=expired";
      }
    }

    // 2. Xử lý lỗi Reset Password đặc thù
    if (errorCode === 1013) {
      toast.error("Mã xác thực đã hết hạn. Vui lòng yêu cầu lại mã mới!");
    } else if (errorCode === 1012) {
      toast.error("Mã xác thực không hợp lệ!");
    }

    // 3. Xử lý Access Denied (Mã 1010)
    if (status === 403 || errorCode === 1010) {
      toast.error(backendError?.message || "Bạn không có quyền truy cập!");
    }

    // 4. Các lỗi nghiệp vụ khác
    if (
      backendError &&
      backendError.message &&
      ![1012, 1013, 1009].includes(errorCode)
    ) {
      toast.error(backendError.message);
    }

    return Promise.reject(
      backendError || { message: "Lỗi kết nối hệ thống", code: 9999 },
    );
  },
);

export default axiosClient;
