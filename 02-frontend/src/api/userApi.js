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
};

export default userApi;
