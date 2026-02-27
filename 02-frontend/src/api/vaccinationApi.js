import axiosClient from "./axiosClient";

const vaccinationApi = {
  // =========================================================================
  // 1. QUẢN LÝ LỊCH TIÊM (SCHEDULES)
  // =========================================================================

  /**
   * Lấy chi tiết lịch tiêm theo ngày và ca (Sáng/Chiều)
   * @param {Object} params - { date: 'YYYY-MM-DD', shift: 'MORNING'|'AFTERNOON' }
   */
  getScheduleByDate: (params) => {
    return axiosClient.get("/vaccination/schedules/by-date", { params });
  },

  /**
   * Lấy danh sách ngày có lịch để hiển thị dấu chấm trên Calendar
   */
  getActiveDates: (start, end) => {
    return axiosClient.get("/vaccination/schedules/active-dates", {
      params: { start, end },
    });
  },

  getAllSchedules: (params = { page: 0, size: 10 }) => {
    return axiosClient.get("/vaccination/schedules", {
      params: {
        ...params,
        search: params.search || undefined,
        start: params.start || undefined,
        end: params.end || undefined,
      },
    });
  },

  createSchedule: (data) => axiosClient.post("/vaccination/schedules", data),

  updateSchedule: (id, data) =>
    axiosClient.put(`/vaccination/schedules/${id}`, data),

  deleteSchedule: (id) => axiosClient.delete(`/vaccination/schedules/${id}`),

  // =========================================================================
  // 2. NGHIỆP VỤ ĐĂNG KÝ & THỰC THI (OPERATIONS)
  // =========================================================================

  /**
   * Lấy danh sách bệnh nhân dựa trên ngày tiêm (dùng cho màn hình Check-in tại quầy)
   */
  getRegistrations: (params = { page: 0, size: 10 }) => {
    return axiosClient.get("/vaccination/schedules/registrations-by-date", {
      params: {
        ...params,
        date: params.date || undefined,
      },
    });
  },

  // =========================================================================
  // 3. DỮ LIỆU BỔ TRỢ (DROPDOWNS / MODALS)
  // =========================================================================

  /**
   * Lấy danh sách bác sĩ/nhân viên y tế để gán vào lịch tiêm
   */
  getDoctors: () => axiosClient.get("/users/medical-staffs"),

  /**
   * Lấy danh sách các lô vắc xin còn hạn và còn hàng để xếp lịch
   */
  getAvailableBatches: () =>
    axiosClient.get("/vaccination/schedules/available-batches"),
};

export default vaccinationApi;
