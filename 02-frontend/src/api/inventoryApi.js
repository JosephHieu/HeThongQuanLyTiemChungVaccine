import axiosClient from "./axiosClient";

const inventoryApi = {
  /**
   * Lấy danh sách tồn kho có phân trang và tìm kiếm
   * @param {string} criteria - Tiêu chí lọc (name, type, origin)
   * @param {string} search - Từ khóa tìm kiếm
   * @param {number} page - Trang hiện tại
   * @param {number} size - Số lượng bản ghi mỗi trang
   */
  getInventory: (criteria, search, page = 0, size = 10) => {
    return axiosClient.get("/v1/inventory", {
      params: { criteria, search, page, size },
    });
  },

  /**
   * Nhập lô vắc-xin mới
   * @param {Object} data - Dữ liệu từ VaccineImportRequest
   */
  importVaccine: (data) => {
    return axiosClient.post("/v1/inventory/import", data);
  },

  /**
   * Xuất vắc-xin điều phối
   * @param {Object} data - Dữ liệu từ VaccineExportRequest (maLo, soLuongXuat)
   */
  exportVaccine: (data) => {
    return axiosClient.post("/v1/inventory/export", data);
  },

  /**
   * Xem chi tiết một lô vắc-xin
   * @param {string} maLo - UUID của lô hàng
   */
  getBatchDetail: (maLo) => {
    return axiosClient.get(`/v1/inventory/${maLo}`);
  },
};

export default inventoryApi;
