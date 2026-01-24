import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// Tự động đính kèm Token vào Header trước khi gửi request
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosClient.interceptors.response.use(
  (response) => {
    // Nếu Backend trả về ApiResponse chuẩn, ta chỉ lấy phần 'result' để UI dễ dùng
    if (response.data && response.data.result !== undefined) {
      return response.data.result;
    }
    return response.data;
  },
  (error) => {
    // Tận dụng mã lỗi và message từ AppException/GlobalExceptionHandler

    const backendError = error.response?.data;

    // Nếu có cấu trúc ApiResponse (code, message), thì trả về nó
    if (backendError) {
      return Promise.reject(backendError);
    }

    return Promise.reject({ message: "Lỗi kết nối hệ thống" });
  },
);

export default axiosClient;
