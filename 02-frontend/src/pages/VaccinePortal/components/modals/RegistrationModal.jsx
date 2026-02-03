import React, { useState } from "react";
import userVaccineApi from "../../../../api/userVaccineApi";
import toast from "react-hot-toast";

// Đổi tên prop từ 'vaccine' thành 'schedule' để đúng ngữ cảnh Lịch tiêm
const RegistrationModal = ({ schedule, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);

  const handleConfirm = async () => {
    if (!schedule) return;
    setLoading(true);

    try {
      const requestData = {
        maLichTiemChung: schedule.maLichTiemChung,
        maVacXin: schedule.maVacXin,
        ghiChu: "Đăng ký trực tuyến qua SchedulePortal",
      };

      await userVaccineApi.registerVaccination(requestData);

      toast.success("Đăng ký thành công!");
      if (onSuccess) onSuccess();
      onClose();
    } catch (error) {
      const errorMessage =
        error.message || "Đăng ký thất bại. Vui lòng thử lại sau!";

      toast.error(errorMessage);

      // Debug để bạn thấy sự khác biệt:
      console.log("Dữ liệu lỗi đã nhận:", error);
    } finally {
      setLoading(false);
    }
  };

  if (!schedule) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 animate-in fade-in duration-300">
      <div className="bg-white border-2 border-[#1e4e8c] w-[850px] shadow-2xl overflow-hidden">
        <div className="bg-[#1e4e8c] p-2 text-white text-center font-bold text-sm uppercase">
          Xác nhận đăng ký tiêm chủng theo lịch trung tâm
        </div>

        <div className="p-6">
          <p className="mb-4 font-bold text-slate-800 text-sm italic">
            Vui lòng kiểm tra kỹ thông tin đợt tiêm trước khi xác nhận:
          </p>

          <table className="w-full text-xs border-collapse border border-slate-800 mb-8 bg-slate-100">
            <thead>
              <tr className="bg-slate-300">
                <th className="border border-slate-800 p-2 text-center">STT</th>
                <th className="border border-slate-800 p-2 text-center">
                  Thời gian
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Địa điểm
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Tên Vắc xin
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Số lô
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Phòng trị bệnh
                </th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className="border border-slate-800 p-2 text-center">1</td>
                <td className="border border-slate-800 p-2 font-bold text-emerald-700">
                  {schedule.ngayTiem} ({schedule.thoiGian})
                </td>
                <td className="border border-slate-800 p-2">
                  {schedule.diaDiem}
                </td>
                <td className="border border-slate-800 p-2 font-bold">
                  {schedule.tenVacXin}
                </td>
                <td className="border border-slate-800 p-2 text-center text-blue-800 font-bold">
                  {schedule.soLo}
                </td>
                <td className="border border-slate-800 p-2">
                  {schedule.loaiVacXin}
                </td>
              </tr>
            </tbody>
          </table>

          <div className="flex justify-center gap-12">
            <button
              onClick={handleConfirm}
              disabled={loading}
              className="px-10 py-1 border border-slate-800 font-bold hover:bg-green-600 hover:text-white hover:border-green-700 transition-all shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5 disabled:opacity-50"
            >
              {loading ? "Đang xử lý..." : "Xác nhận đăng ký"}
            </button>
            <button
              onClick={onClose}
              disabled={loading}
              className="px-10 py-1 border border-slate-800 font-bold hover:bg-red-600 hover:text-white hover:border-red-700 transition-all shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5"
            >
              Hủy bỏ
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistrationModal;
