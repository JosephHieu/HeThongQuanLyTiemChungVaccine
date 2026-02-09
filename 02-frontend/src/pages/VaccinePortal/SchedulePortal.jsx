import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // 1. Thêm useNavigate để tránh lỗi quay lại trang Login
import userVaccineApi from "../../api/userVaccineApi";
import toast from "react-hot-toast";
import RegistrationModal from "./components/modals/RegistrationModal";

const SchedulePortal = () => {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(false);

  const [selectedSchedule, setSelectedSchedule] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const navigate = useNavigate();

  const fetchSchedules = async () => {
    setLoading(true);
    try {
      const response = await userVaccineApi.getOpeningSchedules();
      // Backend của bạn bọc kết quả trong .result nếu dùng axiosClient chung
      setSchedules(response || []);
    } catch (error) {
      toast.error(error.message || "Không thể tải lịch tiêm");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchedules();
  }, []);

  return (
    <div className="p-6 bg-slate-100 min-h-screen">
      <div className="max-w-7xl mx-auto bg-white shadow-xl border border-slate-300 overflow-hidden">
        <div className="bg-[#1e4e8c] p-2 text-white text-center font-bold text-lg uppercase border-b border-slate-400">
          Lịch tiêm phòng vắc xin của trung tâm
        </div>

        <div className="p-6 space-y-6">
          <div className="overflow-x-auto border border-slate-400 shadow-sm">
            <table className="w-full text-sm border-collapse bg-white">
              <thead>
                <tr className="bg-slate-200 text-slate-800">
                  <th className="border border-slate-400 p-2 font-bold w-12">
                    STT
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Thời gian
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Địa điểm
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Tên Vắc xin
                  </th>
                  <th className="border border-slate-400 p-2 font-bold text-rose-700">
                    Giá tiêm
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Loại vắc xin
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Số lượng
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Đối tượng
                  </th>
                  <th className="border border-slate-400 p-2 font-bold">
                    Ghi chú
                  </th>
                  <th className="border border-slate-400 p-2 font-bold w-24">
                    Đăng ký
                  </th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td
                      colSpan="9"
                      className="p-10 text-center italic text-slate-500"
                    >
                      Đang tải lịch tiêm...
                    </td>
                  </tr>
                ) : schedules.length > 0 ? (
                  schedules.map((item, index) => (
                    <tr
                      key={item.maLichTiemChung}
                      className="hover:bg-blue-50 transition-colors"
                    >
                      <td className="border border-slate-400 p-2 text-center">
                        {index + 1}
                      </td>
                      <td className="border border-slate-400 p-2">
                        {/* Hiển thị Ngày + Ca tiêm */}
                        {item.ngayTiem} ({item.thoiGian})
                      </td>
                      <td className="border border-slate-400 p-2">
                        {item.diaDiem}
                      </td>
                      <td className="border border-slate-400 p-2 font-bold text-blue-900">
                        {item.tenVacXin}
                      </td>
                      {/* HIỂN THỊ ĐƠN GIÁ */}
                      <td className="border border-slate-400 p-2 text-right font-black text-rose-600">
                        {item.donGia
                          ? `${item.donGia.toLocaleString()} đ`
                          : "Liên hệ"}
                      </td>
                      <td className="border border-slate-400 p-2">
                        {item.loaiVacXin}
                      </td>
                      <td className="border border-slate-400 p-2 text-center text-green-700 font-medium">
                        {/* 3. HIỂN THỊ SỐ LƯỢNG CÒN LẠI: Tổng - Đã đăng ký */}
                        {item.soLuong - item.daDangKy} liều
                      </td>
                      <td className="border border-slate-400 p-2 italic text-slate-600">
                        {item.doTuoi}
                      </td>
                      <td className="border border-slate-400 p-2 text-xs">
                        {item.ghiChu}
                      </td>
                      <td className="border border-slate-400 p-2 text-center">
                        <button
                          className="px-3 py-0.5 bg-white border border-slate-800 font-bold text-xs shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] hover:bg-slate-800 hover:text-white active:shadow-none active:translate-x-[1px] active:translate-y-[1px] transition-all"
                          onClick={() => {
                            setSelectedSchedule(item); // Lưu dữ liệu hàng hiện tại
                            setIsModalOpen(true); // Mở Modal
                          }}
                        >
                          Đăng ký
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="9" className="p-10 text-center text-red-500">
                      Hiện tại chưa có lịch tiêm nào được mở.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* LƯU Ý TÀI CHÍNH DƯỚI BẢNG */}
          <p className="text-[11px] text-slate-500 italic">
            * Giá tiêm trên đã bao gồm vắc-xin, phí khám sàng lọc và phí tiêm
            chủng. Hệ thống sẽ tạo hóa đơn chờ thanh toán sau khi bạn xác nhận
            đăng ký.
          </p>

          <div className="flex justify-center mt-4">
            <button
              // 4. FIX ĐIỀU HƯỚNG: Chuyển về /user thay vì back()
              onClick={() => navigate("/user")}
              className="px-12 py-1 bg-slate-200 border border-slate-400 hover:bg-slate-300 font-bold text-slate-700 shadow-[1px_1px_0px_0px_rgba(0,0,0,1)] active:shadow-none"
            >
              OK
            </button>
          </div>
        </div>
      </div>
      {isModalOpen && (
        <RegistrationModal
          schedule={selectedSchedule}
          onClose={() => setIsModalOpen(false)}
          onSuccess={fetchSchedules} // Tải lại bảng sau khi đăng ký thành công
        />
      )}
    </div>
  );
};

export default SchedulePortal;
