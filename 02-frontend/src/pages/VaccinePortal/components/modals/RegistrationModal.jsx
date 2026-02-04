import React, { useState } from "react";
import userVaccineApi from "../../../../api/userVaccineApi";
import toast from "react-hot-toast";

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
    } finally {
      setLoading(false);
    }
  };

  if (!schedule) return null;

  return (
    // p-4: Tạo khoảng trống bao quanh modal trên mobile
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 animate-in fade-in duration-300">
      {/* w-full max-w-[850px]: Co giãn theo màn hình nhưng không quá 850px */}
      <div className="bg-white border-2 border-[#1e4e8c] w-full max-w-[850px] shadow-2xl overflow-hidden flex flex-col max-h-[95vh]">
        {/* Header: Chỉnh lại padding và text size cho mobile */}
        <div className="bg-[#1e4e8c] p-3 text-white text-center font-bold text-xs sm:text-sm uppercase">
          Xác nhận đăng ký tiêm chủng theo lịch trung tâm
        </div>

        <div className="p-4 sm:p-6 overflow-y-auto">
          <p className="mb-4 font-bold text-slate-800 text-xs sm:text-sm italic">
            Vui lòng kiểm tra kỹ thông tin đợt tiêm trước khi xác nhận:
          </p>

          {/* Bọc bảng trong div overflow-x-auto để mobile có thể vuốt ngang */}
          <div className="overflow-x-auto border border-slate-800 mb-8 shadow-sm">
            <table className="w-full text-[10px] sm:text-xs border-collapse bg-slate-100 min-w-[600px]">
              <thead>
                <tr className="bg-slate-300">
                  <th className="border border-slate-800 p-2 text-center w-10">
                    STT
                  </th>
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
                    {schedule.ngayTiem} <br className="hidden sm:block" /> (
                    {schedule.thoiGian})
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
                  <td className="border border-slate-800 p-2 uppercase">
                    {schedule.loaiVacXin}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Nút bấm: flex-col trên mobile (dọc), sm:flex-row trên PC (ngang) */}
          <div className="flex flex-col sm:flex-row justify-center gap-3 sm:gap-12">
            <button
              onClick={handleConfirm}
              disabled={loading}
              className="w-full sm:w-auto px-10 py-2 border border-slate-800 font-bold bg-white hover:bg-green-600 hover:text-white hover:border-green-700 transition-all shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5 disabled:opacity-50"
            >
              {loading ? "Đang xử lý..." : "Xác nhận đăng ký"}
            </button>
            <button
              onClick={onClose}
              disabled={loading}
              className="w-full sm:w-auto px-10 py-2 border border-slate-800 font-bold bg-white hover:bg-red-600 hover:text-white hover:border-red-700 transition-all shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5"
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
