import React, { useState } from "react";
import userVaccineApi from "../../../../api/userVaccineApi";
import toast from "react-hot-toast";

const RegistrationModal = ({ vaccine, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);

  const handleConfirm = async () => {
    setLoading(true);
    try {
      const requestData = {
        maVacXin: vaccine.maVacXin, // Vẫn gửi ID lên Backend để xử lý logic
        maLichTiemChung: "79281546-8172-4d56-9123-1234567890ab", // ID thực tế từ DB
        ghiChu: "Đăng ký trực tuyến qua hệ thống Portal",
      };

      await userVaccineApi.registerVaccination(requestData);

      toast.success(
        "Đăng ký thành công! Hệ thống sẽ gửi thông báo xác nhận qua Email/SMS.",
      );

      if (onSuccess) onSuccess();
      onClose();
    } catch (error) {
      toast.error(error.message || "Đăng ký thất bại. Vui lòng thử lại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 animate-in fade-in duration-300">
      <div className="bg-white border-2 border-[#1e4e8c] w-[700px] shadow-2xl overflow-hidden">
        <div className="bg-[#1e4e8c] p-2 text-white text-center font-bold text-sm uppercase">
          Đăng ký tiêm phòng vắc xin
        </div>

        <div className="p-6">
          <p className="mb-4 font-bold text-slate-800 text-sm italic">
            Bạn có chắc chắn đồng ý đăng ký tiêm phòng loại vắc xin dưới đây:
          </p>

          <table className="w-full text-xs border-collapse border border-slate-800 mb-8 bg-slate-100">
            <thead>
              <tr className="bg-slate-300">
                <th className="border border-slate-800 p-2 text-center">STT</th>
                {/* SỬA TẠI ĐÂY: Đổi Mã Vắc xin thành Số lô */}
                <th className="border border-slate-800 p-2 text-center">
                  Số lô
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Tên Vắc xin
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Phòng trị bệnh
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Số lượng
                </th>
                <th className="border border-slate-800 p-2 text-center">
                  Độ tuổi
                </th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className="border border-slate-800 p-2 text-center">1</td>
                {/* SỬA TẠI ĐÂY: Hiển thị item.soLo thay vì substring của maVacXin */}
                <td className="border border-slate-800 p-2 text-center font-bold text-blue-800">
                  {vaccine.soLo || "N/A"}
                </td>
                <td className="border border-slate-800 p-2 font-bold">
                  {vaccine.tenVacXin}
                </td>
                <td className="border border-slate-800 p-2">
                  {vaccine.phongNguaBenh}
                </td>
                <td className="border border-slate-800 p-2 text-center">
                  1 liều
                </td>
                <td className="border border-slate-800 p-2 text-center">
                  {vaccine.doTuoi}
                </td>
              </tr>
            </tbody>
          </table>

          <div className="flex justify-center gap-12">
            <button
              onClick={handleConfirm}
              disabled={loading}
              className="px-10 py-1 border border-slate-800 font-bold hover:bg-green-600 hover:text-white hover:border-green-700 transition-all shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5"
            >
              {loading ? "Đang xử lý..." : "Đồng ý"}
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
