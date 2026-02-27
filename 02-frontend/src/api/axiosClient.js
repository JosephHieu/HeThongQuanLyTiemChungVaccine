import axios from "axios";

const axiosClient = axios.create({
  // Sử dụng biến môi trường để linh hoạt giữa dev và production
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: { "Content-Type": "application/json" },
});

// [Request Interceptor]
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

// [Response Interceptor]
axiosClient.interceptors.response.use(
  (response) => {
    // Chỉ trả về phần 'result' nếu có
    return response.data?.result ?? response.data;
  },
  (error) => {
    const backendError = error.response?.data;
    const status = error.response?.status;

    // 1. Xử lý lỗi Unauthorized (Token hết hạn hoặc không hợp lệ)
    if (status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login"; // Hoặc dùng navigate nếu có context
    }

    // 2. Trả về cấu trúc lỗi từ Backend hoặc lỗi mặc định
    return Promise.reject(
      backendError || { message: "Lỗi kết nối hệ thống", code: 9999 },
    );
  },
);

export default axiosClient;
