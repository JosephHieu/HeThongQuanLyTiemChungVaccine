import axiosClient from "./axiosClient";

/**
 * Phân hệ API Quản lý Tài chính & Vắc-xin
 * Mapping với Backend Controller: /api/finance
 */
const financeApi = {
  // =========================================================================
  // 1. TỔNG QUAN & THỐNG KÊ
  // =========================================================================

  /**
   * Lấy tổng quan Doanh thu (Khách hàng) & Kho
   */
  getFinanceSummary: () => {
    return axiosClient.get("/finance/summary");
  },

  /**
   * MỚI: Lấy tổng quan Chi phí & Công nợ (Nhà cung cấp)
   */
  getSupplierSummary: () => {
    return axiosClient.get("/finance/summary/suppliers");
  },

  // =========================================================================
  // 2. QUẢN LÝ DANH MỤC VẮC-XIN
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

  // =========================================================================
  // 3. GIAO DỊCH KHÁCH HÀNG (THU)
  // =========================================================================

  getCustomerTransactions: (params) => {
    return axiosClient.get("/finance/transactions/customers", { params });
  },

  confirmPayment: (maHoaDon, phuongThuc) => {
    return axiosClient.post(
      `/finance/transactions/customers/${maHoaDon}/confirm`,
      null, // No body
      { params: { phuongThuc } }, // Gửi query param ?phuongThuc=...
    );
  },

  // =========================================================================
  // 4. NHẬP HÀNG & NHÀ CUNG CẤP (CHI)
  // =========================================================================

  getSupplierTransactions: (page = 1, size = 10, search = "") => {
    return axiosClient.get("/finance/transactions/suppliers", {
      params: { page, size, search },
    });
  },

  /**
   * MỚI: Lấy chi tiết hóa đơn nhập hàng (để hiện Modal xem chi tiết lô hàng)
   */
  getSupplierDetail: (maHoaDon) => {
    return axiosClient.get(`/finance/transactions/suppliers/${maHoaDon}`);
  },

  /**
   * MỚI: Xác nhận đã chi tiền cho Nhà cung cấp
   */
  confirmSupplierPayment: (maHoaDon, phuongThuc) => {
    return axiosClient.post(
      `/finance/transactions/suppliers/${maHoaDon}/confirm`,
      null,
      { params: { phuongThuc } },
    );
  },

  // =========================================================================
  // 5. NGHIỆP VỤ CHUNG
  // =========================================================================

  /**
   * Hủy hóa đơn (Dùng chung cho cả Xuất và Nhập nếu chưa thanh toán)
   */
  cancelTransaction: (maHoaDon) => {
    return axiosClient.post(`/finance/transactions/${maHoaDon}/cancel`);
  },
};

export default financeApi;
