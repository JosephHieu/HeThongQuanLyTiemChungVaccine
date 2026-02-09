import axiosClient from "./axiosClient";

/**
 * Phân hệ API Quản lý Danh mục Vắc-xin & Tài chính
 * Mapping với Backend Controller: /api/finance/vaccines
 */
const financeApi = {
  /**
   * Lấy danh sách vắc-xin đầy đủ thông tin (Phân trang)
   * @param {number} page - Trang hiện tại (mặc định 1)
   * @param {number} size - Số lượng bản ghi mỗi trang
   */
  getVaccines: (page = 1, size = 10) => {
    return axiosClient.get("/finance/vaccines", {
      params: { page, size },
    });
  },

  /**
   * Thêm mới một loại vắc-xin vào danh mục
   * @param {Object} data - VaccineFullRequest
   */
  createVaccine: (data) => {
    return axiosClient.post("/finance/vaccines", data);
  },

  /**
   * Cập nhật toàn diện thông tin vắc-xin
   * @param {string} id - UUID của vắc-xin
   * @param {Object} data - VaccineFullRequest
   */
  updateVaccine: (id, data) => {
    return axiosClient.put(`/finance/vaccines/${id}`, data);
  },

  /**
   * Xóa vắc-xin khỏi hệ thống
   * @param {string} id - UUID của vắc-xin
   */
  deleteVaccine: (id) => {
    return axiosClient.delete(`/finance/vaccines/${id}`);
  },

  /**
   * Lấy tổng giá trị hàng tồn kho (Dữ liệu Dashboard)
   */
  getTotalInventoryValue: () => {
    return axiosClient.get("/finance/vaccines/total-inventory-value");
  },
};

export default financeApi;
