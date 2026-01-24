import axiosClient from "./axiosClient";

const userApi = {
  // Cập nhật: Nhận thêm search và maQuyen
  getAll: (page, size, search = "", maQuyen = "") => {
    return axiosClient.get("/users", {
      // Axios sẽ tự động biến đối tượng này thành query string: ?page=1&size=10&search=...
      params: {
        page,
        size,
        search: search || undefined, // Nếu rỗng thì không gửi lên để Backend dùng default
        maQuyen: maQuyen || undefined,
      },
    });
  },

  create: (data) => {
    return axiosClient.post("/users/create", data);
  },

  getRoles: () => {
    return axiosClient.get("/roles");
  },

  update: (id, data) => axiosClient.put(`/users/${id}`, data),

  toggleStatus: (id) => axiosClient.patch(`/users/${id}/toggle-status`),
};

export default userApi;
