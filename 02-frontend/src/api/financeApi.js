import axiosClient from "./axiosClient";

/**
 * Phân hệ API Quản lý Tài chính & Vắc-xin
 * Mapping với Backend Controller: /api/finance
 */
const financeApi = {
  // =========================================================================
  // 1. TỔNG QUAN & THỐNG KÊ (Dành cho Header Dashboard)
  // =========================================================================

  /**
   * Lấy dữ liệu tổng quan (Doanh thu hôm nay, Hóa đơn chờ, Giá trị kho)
   */
  getFinanceSummary: () => {
    return axiosClient.get("/finance/summary");
  },

  // =========================================================================
  // 2. QUẢN LÝ DANH MỤC VẮC-XIN (Tab Bảng giá/Danh mục)
  // =========================================================================

  getVaccines: (page = 1, size = 7, search = "") => {
    return axiosClient.get("/finance/vaccines", {
      params: { page, size, search },
    });
  },

  createVaccine: (data) => {
    return axiosClient.post("/finance/vaccines", data);
  },

  updateVaccine: (id, data) => {
    return axiosClient.put(`/finance/vaccines/${id}`, data);
  },

  deleteVaccine: (id) => {
    return axiosClient.delete(`/finance/vaccines/${id}`);
  },

  getTotalInventoryValue: () => {
    return axiosClient.get("/finance/vaccines");
  },

  // =========================================================================
  // 3. GIAO DỊCH KHÁCH HÀNG (Tab Giao dịch khách hàng)
  // =========================================================================

  /**
   * Truy xuất danh sách giao dịch khách hàng
   * @param {Object} params - { page, size, search, startDate, endDate }
   */
  getCustomerTransactions: (params) => {
    return axiosClient.get("/finance/transactions/customers", { params });
  },

  /**
   * Xác nhận thu tiền khách hàng
   */
  confirmPayment: (maHoaDon, phuongThuc) => {
    return axiosClient.post(
      `/finance/transactions/customers/${maHoaDon}/confirm`,
      null,
      {
        params: { phuongThuc },
      },
    );
  },

  /**
   * Hủy hóa đơn giao dịch
   */
  cancelTransaction: (maHoaDon) => {
    return axiosClient.post(
      `/finance/transactions/customers/${maHoaDon}/cancel`,
    );
  },

  // =========================================================================
  // 4. NHẬP HÀNG NCC (Tab Nhập hàng)
  // =========================================================================

  getSupplierTransactions: (page = 1, size = 10, search = "") => {
    return axiosClient.get("/finance/transactions/suppliers", {
      params: { page, size, search },
    });
  },
};

export default financeApi;
