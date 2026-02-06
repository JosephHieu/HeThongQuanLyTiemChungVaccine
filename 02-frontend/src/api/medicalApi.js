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
};

export default medicalApi;
