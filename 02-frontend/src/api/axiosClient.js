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

    // 1. Xử lý lỗi Unauthenticated (Mã 1009 hoặc Status 401)
    if (status === 401 || errorCode === 1009) {
      console.warn("Phiên làm việc hết hạn!");
      localStorage.removeItem("token");
      localStorage.removeItem("user"); // Xóa cả thông tin user nếu có lưu

      // Chỉ redirect nếu không phải đang ở trang login
      if (!window.location.pathname.includes("/login")) {
        window.location.href = "/login?message=expired";
      }
    }

    // 2. Xử lý lỗi Access Denied (Mã 1010 hoặc Status 403)
    // Đây là khi @PreAuthorize chặn người dùng
    if (status === 403 || errorCode === 1010) {
      toast.error(
        backendError?.message || "Bạn không có quyền thực hiện hành động này!",
      );
    }

    // 3. Xử lý các lỗi nghiệp vụ khác (Ví dụ: 1301 - Hết hàng, 1001 - Trùng tên...)
    if (backendError && backendError.message) {
      // Bạn có thể chọn hiện toast tại đây hoặc để Page tự xử lý
      // toast.error(backendError.message);
    }

    return Promise.reject(
      backendError || { message: "Lỗi kết nối hệ thống", code: 9999 },
    );
  },
);

export default axiosClient;
