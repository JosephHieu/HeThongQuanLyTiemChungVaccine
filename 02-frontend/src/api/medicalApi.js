import axiosClient from "./axiosClient";

const medicalApi = {
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
};

export default medicalApi;
