import axiosClient from "./axiosClient";

const inventoryApi = {
  /**
   * Lấy danh sách tồn kho có phân trang và tìm kiếm
   * @param {Object} params - { criteria, search, page, size }
   */
  getInventory: (params = { page: 0, size: 10 }) => {
    // Đã bỏ /v1
    return axiosClient.get("/inventory", { params });
  },

  /**
   * Nhập lô vắc-xin mới
   */
  importVaccine: (data) => {
    return axiosClient.post("/inventory/import", data);
  },

  /**
   * Xuất vắc-xin điều phối
   */
  exportVaccine: (data) => {
    return axiosClient.post("/inventory/export", data);
  },

  /**
   * Xem chi tiết một lô vắc-xin
   */
  getBatchDetail: (maLo) => {
    return axiosClient.get(`/inventory/${maLo}`);
  },

  /**
   * Lấy danh sách danh mục (Dropdowns)
   */
  getAllSuppliers: () => axiosClient.get("/inventory/suppliers"),
  getAllVaccineTypes: () => axiosClient.get("/inventory/vaccine-types"),

  /**
   * Thống kê
   */
  getTotalDoses: () => axiosClient.get("/inventory/stats/total-doses"),

  /**
   * Lấy lịch sử phiếu xuất kho
   * @param {Object} params - { page, size, startDate, endDate }
   */
  getExportHistory: (params = { page: 0, size: 10 }) => {
    return axiosClient.get("/inventory/export-history", { params });
  },
};

export default inventoryApi;
