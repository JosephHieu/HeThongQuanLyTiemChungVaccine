import axiosClient from "./axiosClient";

const supportApi = {
  // =========================================================================
  // 1. NHẮC LỊCH TIÊM CHỦNG (REMINDERS)
  // =========================================================================

  /**
   * Tra cứu thông tin tiêm chủng của khách hàng qua email để chuẩn bị nhắc lịch
   */
  searchReminderData: (email) => {
    return axiosClient.get("/support/reminders/search", {
      params: { email: email?.trim() },
    });
  },

  /**
   * Thực hiện gửi email nhắc lịch
   * @param {Object} data - { email, customerName, vaccineName, nextDoseDate }
   */
  sendVaccinationReminder: (data) => {
    return axiosClient.post("/support/reminders/send", data);
  },

  // =========================================================================
  // 2. QUẢN LÝ PHẢN HỒI CẤP CAO (ADMIN ROLE)
  // =========================================================================

  /**
   * Admin lấy toàn bộ danh sách phản hồi từ tất cả người dùng
   */
  adminGetAllHighLevelFeedbacks: (params) => {
    // params có thể chứa page, size để phân trang
    return axiosClient.get("/medical/high-level-feedback/admin/all", {
      params,
    });
  },

  /**
   * Cập nhật trạng thái phản hồi (Ví dụ: 0: Chờ, 1: Đang xử lý, 2: Đã xong)
   */
  adminUpdateHighLevelStatus: (id, status) => {
    return axiosClient.put(
      `/medical/high-level-feedback/admin/${id}/status`,
      null, // Không có body
      { params: { status } }, // Truyền status qua Query Parameter ?status=...
    );
  },

  /**
   * Xóa vĩnh viễn phản hồi (Dành cho Admin)
   */
  adminDeleteHighLevelFeedback: (id) => {
    return axiosClient.delete(`/medical/high-level-feedback/admin/${id}`);
  },
};

export default supportApi;
