import axiosClient from "./axiosClient";

const supportApi = {
  // --- PHẦN 9.6.1: NHẮC LỊCH TIÊM CHỦNG ---
  searchReminderData: (email) => {
    return axiosClient.get("/v1/support/reminders/search", {
      params: { email },
    });
  },

  sendVaccinationReminder: (data) => {
    return axiosClient.post("/v1/support/reminders/send", data);
  },

  // --- PHẦN 9.5.6: QUẢN LÝ PHẢN HỒI CẤP CAO (ADMIN) ---
  // Chuyển từ medicalApi sang đây để gom nhóm chức năng Support
  adminGetAllHighLevelFeedbacks: () => {
    return axiosClient.get("/v1/medical/high-level-feedback/admin/all");
  },

  adminUpdateHighLevelStatus: (id, status) => {
    return axiosClient.put(
      `/v1/medical/high-level-feedback/admin/${id}/status?status=${status}`,
    );
  },

  adminDeleteHighLevelFeedback: (id) => {
    return axiosClient.delete(`/v1/medical/high-level-feedback/admin/${id}`);
  },
};

export default supportApi;
