import axiousClient from "./axiosClient";

const userApi = {
  // Lấy danh sách user có phân trang
  getAll: (page, size) => {
    return axiousClient.get(`/users?page=${page}&size=${size}`);
  },
  // Tạo user mới
  create: (data) => {
    return axiousClient.post("/users/create", data);
  },
  // Lấy danh sách quyền hạn cho ô Select
  getRoles: () => {
    return axiousClient.get("/roles");
  },

  // API Sửa: PUT /api/users/{id}
  update: (id, data) => axiousClient.put(`/users/${id}`, data),

  // API Khóa/Mở khóa: PATCH /api/users/{id}/toggle-status
  toggleStatus: (id) => axiousClient.patch(`/users/${id}/toggle-status`),
};

export default userApi;
