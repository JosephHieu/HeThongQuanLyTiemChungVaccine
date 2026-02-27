import axiosClient from "./axiosClient";

/**
 * API Service dành cho phân hệ Bệnh nhân (Normal User)
 */
const userVaccineApi = {
  /**
   * Lấy danh sách vắc-xin hiển thị cho người dùng
   * @param {Object} params - { keyword, page, size }
   */
  getAvailableVaccines: (params = { page: 0, size: 10 }) => {
    return axiosClient.get("/vaccinations", {
      params: {
        // Sử dụng giá trị mặc định trực tiếp từ params để code ngắn gọn hơn
        ...params,
        keyword: params.keyword?.trim() || undefined,
      },
    });
  },

  /**
   * Gửi đơn đăng ký tiêm chủng trực tuyến
   */
  registerVaccination: (data) => {
    return axiosClient.post("/vaccinations/register", data);
  },

  /**
   * Lấy danh sách lịch tiêm chủng đang mở
   */
  getOpeningSchedules: () => {
    return axiosClient.get("/vaccination/schedules/opening");
  },

  /**
   * Xem lịch sử đăng ký của cá nhân (có hỗ trợ phân trang)
   */
  getMyRegistrations: (params = { page: 0, size: 5 }) => {
    return axiosClient.get("/vaccinations/my-registrations", { params });
  },

  /**
   * Hủy đơn đăng ký (nếu trạng thái chưa được xác nhận)
   */
  cancelRegistration: (id) => {
    return axiosClient.post(`/vaccinations/cancel/${id}`);
  },
};

export default userVaccineApi;
