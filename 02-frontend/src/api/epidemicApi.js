import axiosClient from "./axiosClient";

const epidemicApi = {
  /**
   * Lấy toàn bộ danh sách dịch bệnh khảo sát
   */
  getAll: () => {
    // Đã bỏ /v1 vì baseURL đã có /v1 rồi
    return axiosClient.get("/medical/epidemics");
  },

  /**
   * Tra cứu dịch bệnh theo vị trí/địa chỉ
   */
  searchByLocation: (diaChi) => {
    return axiosClient.get("/medical/epidemics/search", {
      params: {
        // Dùng trim() để tránh gửi khoảng trắng thừa lên server
        diaChi: diaChi?.trim(),
      },
    });
  },

  /**
   * Xem chi tiết một vụ dịch bệnh
   */
  getById: (id) => {
    // Kiểm tra nhanh id trước khi gọi để tránh lỗi 404 không đáng có
    if (!id) return Promise.reject("ID không hợp lệ");
    return axiosClient.get(`/medical/epidemics/${id}`);
  },

  // ========================================================================
  // DÀNH CHO NHÂN VIÊN/ADMIN
  // ========================================================================

  create: (data) => {
    return axiosClient.post("/medical/epidemics", data);
  },

  update: (id, data) => {
    return axiosClient.put(`/medical/epidemics/${id}`, data);
  },

  delete: (id) => {
    return axiosClient.delete(`/medical/epidemics/${id}`);
  },
};

export default epidemicApi;
