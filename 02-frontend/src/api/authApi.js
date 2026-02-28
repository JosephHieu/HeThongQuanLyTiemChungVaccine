import axiosClient from "./axiosClient";

const authApi = {
  login: (data) => axiosClient.post("/auth/login", data),

  register: (data) => axiosClient.post("/auth/register", data),

  // API Quên mật khẩu mới
  forgotPassword: (email) => {
    // Dùng ForgotPasswordRequest gởi qua Body cho chuyên nghiệp
    return axiosClient.post("/auth/forgot-password", { email });
  },

  // API Reset mật khẩu mới
  resetPassword: (data) => {
    // data: { token: "...", newPassword: "..." }
    return axiosClient.post("/auth/reset-password", data);
  },
};

export default authApi;
