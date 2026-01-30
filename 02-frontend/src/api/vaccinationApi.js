import axiosClient from "./axiosClient";

const vaccinationApi = {
  /**
   * Lấy chi tiết lịch tiêm theo một ngày cụ thể
   * @param {string} date - Định dạng YYYY-MM-DD
   */
  getScheduleByDate: (date, shift) => {
    return axiosClient.get("/v1/vaccination/schedules/by-date", {
      params: { date: date, shift: shift },
    });
  },

  /**
   * Lấy danh sách các ngày có lịch tiêm trong tháng (để hiện dấu chấm xanh)
   * @param {string} start - Ngày bắt đầu tháng (YYYY-MM-DD)
   * @param {string} end - Ngày kết thúc tháng (YYYY-MM-DD)
   */
  getActiveDates: (start, end) => {
    return axiosClient.get("/v1/vaccination/schedules/active-dates", {
      params: { start, end },
    });
  },

  /**
   * Tạo mới một lịch tiêm chủng
   * @param {Object} data - Dữ liệu từ ScheduleCreationRequest
   */
  createSchedule: (data) => {
    return axiosClient.post("/v1/vaccination/schedules", data);
  },

  /**
   * Cập nhật lịch tiêm chủng hiện có
   * @param {string} id - UUID của lịch tiêm
   * @param {Object} data - Dữ liệu cập nhật
   */
  updateSchedule: (id, data) => {
    return axiosClient.put(`/v1/vaccination/schedules/${id}`, data);
  },

  /**
   * Xóa một lịch tiêm chủng
   * @param {string} id - UUID của lịch tiêm
   */
  deleteSchedule: (id) => {
    return axiosClient.delete(`/v1/vaccination/schedules/${id}`);
  },

  /**
   * Lấy toàn bộ danh sách lịch tiêm (phân trang & tìm kiếm)
   */
  getAllSchedules: (params) => {
    return axiosClient.get("/v1/vaccination/schedules", {
      params: {
        page: params.page || 1,
        size: params.size || 10,
        search: params.search || "",
        start: params.start || "",
        end: params.end || "",
      },
    });
  },

  /**
   * Lấy danh sách bác sĩ (Module Identity)
   * Backend: @RequestMapping("/api/users") -> @GetMapping("/medical-staffs")
   */
  getDoctors: () => {
    return axiosClient.get("/users/medical-staffs");
  },

  /**
   * Lấy danh sách bệnh nhân đăng ký cho một lịch tiêm
   * Backend: @RequestMapping("api/v1/vaccination/schedules") -> @GetMapping("/{id}/registrations")
   */
  getRegistrations: (date, page = 1, size = 10) => {
    // Gọi tới endpoint mới dùng query param thay vì path variable
    return axiosClient.get("/v1/vaccination/schedules/registrations-by-date", {
      params: { date, page, size },
    });
  },

  // Lấy danh sách lô vắc xin để chọn
  getAvailableBatches: () => {
    return axiosClient.get("/v1/vaccination/schedules/available-batches");
  },
};

export default vaccinationApi;
