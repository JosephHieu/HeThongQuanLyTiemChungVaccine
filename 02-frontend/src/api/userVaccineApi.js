import axiosClient from "./axiosClient";

/**
 * API Service dành cho phân hệ Bệnh nhân (Normal User)
 * Tập trung vào việc xem thông tin vắc-xin và thực hiện đăng ký tiêm chủng.
 */
const userVaccineApi = {
  /**
   * Lấy danh sách vắc-xin hiển thị cho người dùng (có phân trang và tìm kiếm)
   * Phục vụ màn hình tra cứu vắc-xin (9.5.1 trong SRS)
   * @param {Object} params - { keyword, page, size }
   */
  getAvailableVaccines: (params) => {
    return axiosClient.get("/v1/vaccinations", {
      params: {
        keyword: params?.keyword || "",
        page: params?.page || 0,
        size: params?.size || 10,
      },
    });
  },

  /**
   * Gửi đơn đăng ký tiêm chủng trực tuyến
   * Phục vụ nút "Đồng ý" trong Modal xác nhận đăng ký
   * @param {Object} data - VaccinationRegistrationRequest (maVacXin, maLichTiemChung, ghiChu)
   */
  registerVaccination: (data) => {
    return axiosClient.post("/v1/vaccinations/register", data);
  },

  /**
   * Lấy danh sách lịch tiêm chủng đang mở để người dùng chọn đợt tiêm
   * (Nếu bạn có một màn hình chọn ngày tiêm cụ thể cho User)
   */
  getOpeningSchedules: () => {
    return axiosClient.get("/v1/vaccination/schedules/opening");
  },

  /**
   * Xem lịch sử đăng ký của chính tôi
   * (Dùng cho Tab "Lịch sử của tôi" trong giao diện User)
   */
  getMyRegistrations: () => {
    return axiosClient.get("/v1/vaccinations/my-registrations");
  },

  cancelRegistration: (id) => axiosClient.post(`/v1/vaccinations/cancel/${id}`),
};

export default userVaccineApi;
