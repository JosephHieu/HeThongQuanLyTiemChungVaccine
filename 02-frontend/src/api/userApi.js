import axiosClient from "./axiosClient";

const userApi = {
  // Lấy danh sách user có phân trang
  getAll: (page, size) => {
    return axiosClient.get(`/users?page=${page}&size=${size}`);
  },
  // Tạo user mới
  create: (data) => {
    return axiosClient.post("/users/create", data);
  },
  // Lấy danh sách quyền hạn cho ô Select
  getRoles: () => {
    return axiosClient.get("/roles");
  },

  // API Sửa: PUT /api/users/{id}
  update: (id, data) => axiosClient.put(`/users/${id}`, data),

  // API Khóa/Mở khóa: PATCH /api/users/{id}/toggle-status
  toggleStatus: (id) => axiosClient.patch(`/users/${id}/toggle-status`),
};

export default userApi;
