import axiosClient from "./axiosClient";

const medicalApi = {
  // ========================================================================
  // PHÂN HỆ NHÂN VIÊN (STAFF PORTAL) - Cần truyền ID bệnh nhân
  // ========================================================================
  /**
   * Truy xuất hồ sơ bệnh án tổng hợp theo ID bệnh nhân
   * @param {string} id - UUID của bệnh nhân
   * @returns MedicalRecordResponse (Đã được interceptor bóc tách lấy .result)
   */
  getRecord: (id) => {
    return axiosClient.get(`/v1/medical/records/${id}`);
  },

  /**
   * Cập nhật thông tin hành chính bệnh nhân
   * @param {string} id - UUID của bệnh nhân
   * @param {Object} data - Dữ liệu từ UpdatePatientRequest
   */
  updateInfo: (id, data) => {
    return axiosClient.put(`/v1/medical/records/${id}`, data);
  },

  /**
   * Kê đơn/Chỉ định tiêm chủng mới
   * @param {string} id - UUID của bệnh nhân
   * @param {Object} data - Dữ liệu từ PrescribeRequest (maLoVacXin, thoiGianCanTiem)
   */
  prescribe: (id, data) => {
    return axiosClient.post(`/v1/medical/records/${id}/prescribe`, data);
  },

  confirmInjection: (maDangKy, data) => {
    const url = `/v1/medical/records/confirm-injection/${maDangKy}`;
    return axiosClient.post(url, data);
  },

  // ========================================================================
  // PHÂN HỆ BỆNH NHÂN (PATIENT PORTAL) - Lấy danh tính từ Token
  // ========================================================================

  /**
   * Lấy hồ sơ cá nhân của chính người dùng đang đăng nhập
   * @returns PatientProfileResponse
   */
  getMyProfile: () => {
    return axiosClient.get(`/v1/medical/my-profile`);
  },

  /**
   * Cập nhật thông tin cá nhân của bệnh nhân
   * @param {Object} data - Dữ liệu từ UpdateProfileRequest
   * @returns PatientProfileResponse (Thông tin mới sau cập nhật)
   */
  updateMyProfile: (data) => {
    return axiosClient.put(`/v1/medical/my-profile`, data);
  },

  /**
   * Lấy lịch sử tiêm chủng cá nhân cho bảng lịch sử
   * @returns List<VaccinationHistoryResponse>
   */
  getMyHistory: () => {
    return axiosClient.get(`/v1/medical/my-history`);
  },

  // --- PHẦN PHẢN HỒI SAU TIÊM (FEEDBACK THÔNG THƯỜNG - 9.5.4) ---

  /**
   * Gửi phản hồi tình trạng sức khỏe sau khi tiêm
   */
  sendFeedback: (data) => {
    return axiosClient.post("/v1/medical/feedback", data);
  },

  /**
   * Lấy danh sách lịch sử các phản hồi đã gửi
   */
  getMyFeedbackHistory: () => {
    return axiosClient.get("/v1/medical/feedback/my-history");
  },

  // --- PHẦN PHẢN HỒI CẤP CAO (HIGH-LEVEL FEEDBACK - 9.5.6) ---

  /**
   * Lấy danh mục các loại phản hồi (Khen ngợi, Phàn nàn...) từ DB
   */
  getHighLevelFeedbackTypes: () => {
    return axiosClient.get("/v1/medical/high-level-feedback/types");
  },

  /**
   * Gửi phản hồi trực tiếp tới Administrator
   */
  sendHighLevelFeedback: (data) => {
    return axiosClient.post("/v1/medical/high-level-feedback", data);
  },

  /**
   * Xem lịch sử phản hồi gửi Admin của cá nhân
   */
  getHighLevelFeedbackHistory: () => {
    return axiosClient.get("/v1/medical/high-level-feedback/my-history");
  },

  /**
   * Cập nhật phản hồi (Khi trạng thái còn là 0)
   */
  updateHighLevelFeedback: (id, data) => {
    return axiosClient.put(`/v1/medical/high-level-feedback/${id}`, data);
  },

  /**
   * Xóa phản hồi (Khi trạng thái còn là 0)
   */
  deleteHighLevelFeedback: (id) => {
    return axiosClient.delete(`/v1/medical/high-level-feedback/${id}`);
  },

  // ========================================================================
  // PHÂN HỆ QUẢN TRỊ (ADMINISTRATOR ONLY)
  // ========================================================================

  /**
   * Admin lấy tất cả phản hồi cấp cao của toàn hệ thống
   */
  adminGetAllHighLevelFeedbacks: () => {
    return axiosClient.get("/v1/medical/high-level-feedback/admin/all");
  },

  /**
   * Admin cập nhật trạng thái xử lý (Tiếp nhận/Đã giải quyết)
   * Sử dụng Query Parameter ?status=...
   */
  adminUpdateHighLevelStatus: (id, status) => {
    return axiosClient.put(
      `/v1/medical/high-level-feedback/admin/${id}/status?status=${status}`,
    );
  },

  /**
   * Admin xóa phản hồi không phù hợp
   */
  adminDeleteHighLevelFeedback: (id) => {
    return axiosClient.delete(`/v1/medical/high-level-feedback/admin/${id}`);
  },

  // ========================================================================
  // PHẦN 9.5.2: LỊCH TIÊM TRUNG TÂM & ĐĂNG KÝ
  // ========================================================================
  getCenterSchedules: () => {
    return axiosClient.get("/v1/medical/schedules/center");
  },

  registerVaccination: (data) => {
    return axiosClient.post("/v1/medical/registrations", data);
  },
};

export default medicalApi;
