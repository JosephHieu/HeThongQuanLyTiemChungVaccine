import axiosClient from "./axiosClient";

const medicalApi = {
  // ========================================================================
  // PHÂN HỆ NHÂN VIÊN (STAFF PORTAL)
  // ========================================================================

  getRecord: (id) => axiosClient.get(`/medical/records/${id}`),

  updateInfo: (id, data) => axiosClient.put(`/medical/records/${id}`, data),

  /**
   * Kê đơn/Chỉ định tiêm chủng mới
   */
  prescribe: (id, data) =>
    axiosClient.post(`/medical/records/${id}/prescribe`, data),

  /**
   * Xác nhận đã tiêm thực tế (Staff xác nhận cho bệnh nhân)
   */
  confirmInjection: (maDangKy, data) => {
    return axiosClient.post(
      `/medical/records/confirm-injection/${maDangKy}`,
      data,
    );
  },

  // ========================================================================
  // PHÂN HỆ BỆNH NHÂN (PATIENT PORTAL)
  // ========================================================================

  getMyProfile: () => axiosClient.get("/medical/my-profile"),

  updateMyProfile: (data) => axiosClient.put("/medical/my-profile", data),

  /**
   * Lấy lịch sử tiêm chủng cá nhân
   */
  getMyHistory: (params) => axiosClient.get("/medical/my-history", { params }),

  // --- PHẢN HỒI SAU TIÊM (FEEDBACK THÔNG THƯỜNG) ---

  sendFeedback: (data) => axiosClient.post("/medical/feedback", data),

  getMyFeedbackHistory: (params) =>
    axiosClient.get("/medical/feedback/my-history", { params }),

  // --- PHẢN HỒI CẤP CAO (HIGH-LEVEL FEEDBACK) ---

  getHighLevelFeedbackTypes: () =>
    axiosClient.get("/medical/high-level-feedback/types"),

  sendHighLevelFeedback: (data) =>
    axiosClient.post("/medical/high-level-feedback", data),

  getHighLevelFeedbackHistory: (params) => {
    return axiosClient.get("/medical/high-level-feedback/my-history", {
      params,
    });
  },

  updateHighLevelFeedback: (id, data) => {
    return axiosClient.put(`/medical/high-level-feedback/${id}`, data);
  },

  deleteHighLevelFeedback: (id) => {
    return axiosClient.delete(`/medical/high-level-feedback/${id}`);
  },

  // ========================================================================
  // LỊCH TIÊM TRUNG TÂM & ĐĂNG KÝ
  // ========================================================================

  getCenterSchedules: (params) =>
    axiosClient.get("/medical/schedules/center", { params }),

  registerVaccination: (data) =>
    axiosClient.post("/medical/registrations", data),
};

export default medicalApi;
