import axiosClient from "./axiosClient";

const epidemicApi = {
  /**
   * Lấy toàn bộ danh sách dịch bệnh khảo sát
   * @returns List<EpidemicResponse>
   */
  getAll: () => {
    return axiosClient.get("/v1/medical/epidemics");
  },

  /**
   * Tra cứu dịch bệnh theo vị trí/địa chỉ
   * @param {string} diaChi - Từ khóa khu vực cần tìm
   */
  searchByLocation: (diaChi) => {
    return axiosClient.get("/v1/medical/epidemics/search", {
      params: { diaChi }, // Axios sẽ tự động convert thành ?diaChi=...
    });
  },

  /**
   * Xem chi tiết một vụ dịch bệnh
   * @param {string} id - UUID của dịch bệnh
   */
  getById: (id) => {
    return axiosClient.get(`/v1/medical/epidemics/${id}`);
  },

  // ========================================================================
  // DÀNH CHO NHÂN VIÊN/ADMIN (STAFF & ADMIN PORTAL)
  // ========================================================================

  /**
   * Tạo mới bản ghi khảo sát dịch bệnh
   * @param {Object} data - Dữ liệu từ EpidemicRequest
   */
  create: (data) => {
    return axiosClient.post("/v1/medical/epidemics", data);
  },

  /**
   * Cập nhật thông tin dịch bệnh
   * @param {string} id - UUID của dịch bệnh
   * @param {Object} data - Dữ liệu mới
   */
  update: (id, data) => {
    return axiosClient.put(`/v1/medical/epidemics/${id}`, data);
  },

  /**
   * Xóa bản ghi dịch bệnh
   * @param {string} id - UUID
   */
  delete: (id) => {
    return axiosClient.delete(`/v1/medical/epidemics/${id}`);
  },
};

export default epidemicApi;
