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
  (response) => response.data?.result ?? response.data,
  (error) => {
    const backendError = error.response?.data;
    const status = error.response?.status;
    const errorCode = backendError?.code;

    // --- NHÓM 1: LỖI BUỘC PHẢI LOGOUT (1013, 1016, 1017) ---
    // Chúng ta sử dụng mã lỗi mà mình đã hướng dẫn bạn thêm vào Backend
    const forceLogoutCodes = [1013, 1016, 1017];

    if (status === 401) {
      // CHỈ xóa khi mã lỗi khẳng định Token hỏng/hết hạn
      if (forceLogoutCodes.includes(errorCode)) {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("userName");

        if (!window.location.pathname.includes("/login")) {
          toast.error("Phiên làm việc hết hạn. Vui lòng đăng nhập lại!");
          window.location.href = "/login?message=expired";
        }
      } else if (errorCode === 1009) {
        // 1009 là sai Pass lúc Login, hiện Toast thôi, đừng xóa token cũ (nếu có)
        toast.error("Tên đăng nhập hoặc mật khẩu không đúng!");
      }
      return Promise.reject(backendError);
    }

    // --- NHÓM 2: LỖI SAI QUYỀN (403 / 1010) ---
    // Khi nhấn "Kê đơn" bị 403, nó sẽ nhảy vào đây -> Hiện Toast -> KHÔNG BỊ VĂNG
    if (status === 403 || errorCode === 1010) {
      toast.error(
        backendError?.message || "Bạn không có quyền thực hiện chức năng này!",
      );
      return Promise.reject(backendError);
    }

    // --- NHÓM 3: LỖI NGHIỆP VỤ KHÁC ---
    if (backendError?.message && errorCode !== 1009) {
      toast.error(backendError.message);
    }

    return Promise.reject(
      backendError || { message: "Lỗi kết nối hệ thống", code: 9999 },
    );
  },
);

export default axiosClient;
