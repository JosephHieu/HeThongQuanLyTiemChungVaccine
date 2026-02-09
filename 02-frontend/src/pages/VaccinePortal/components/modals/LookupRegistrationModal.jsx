import React, { useState } from "react";
import {
  Calendar,
  X,
  CheckCircle,
  Info,
  ShieldCheck,
  Banknote,
} from "lucide-react";
import toast from "react-hot-toast";

const LookupRegistrationModal = ({ vaccine, onClose, onConfirm, loading }) => {
  const [bookingDate, setBookingDate] = useState("");

  if (!vaccine) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!bookingDate) {
      toast.error("Vui lòng chọn ngày dự định tiêm!");
      return;
    }
    onConfirm(bookingDate);
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-in fade-in duration-300">
      <div className="bg-white border-2 border-[#1e4e8c] w-full max-w-[550px] shadow-[8px_8px_0px_0px_rgba(30,78,140,0.2)] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="bg-[#1e4e8c] p-4 text-white flex justify-between items-center">
          <div className="flex items-center gap-2">
            <ShieldCheck size={20} />
            <span className="font-bold text-sm uppercase tracking-wider">
              Xác nhận đăng ký tiêm chủng
            </span>
          </div>
          <button
            onClick={onClose}
            className="hover:rotate-90 transition-transform"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          <p className="text-slate-600 text-sm italic font-medium">
            Thông tin chi tiết vắc-xin và chi phí đăng ký:
          </p>

          {/* Thẻ hiển thị thông tin vắc-xin & GIÁ TIỀN */}
          <div className="bg-slate-50 border-l-4 border-[#1e4e8c] p-4 space-y-3 shadow-sm">
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Tên vắc-xin
                </span>
                <h4 className="text-xl font-black text-[#1e4e8c] leading-tight">
                  {vaccine.tenVacXin}
                </h4>
              </div>

              {/* PHẦN HIỂN THỊ GIÁ TIỀN MỚI */}
              <div className="text-right ml-4">
                <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Đơn giá (Dự kiến)
                </span>
                <p className="text-xl font-black text-rose-600">
                  {vaccine.donGia
                    ? `${vaccine.donGia.toLocaleString()} đ`
                    : "Liên hệ"}
                </p>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4 pt-3 border-t border-slate-200">
              <div>
                <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Số lô
                </span>
                <p className="text-xs font-bold text-blue-700 uppercase">
                  {vaccine.soLo || "N/A"}
                </p>
              </div>
              <div>
                <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Phòng bệnh
                </span>
                <p className="text-[11px] font-bold text-slate-700 uppercase truncate">
                  {vaccine.phongNguaBenh}
                </p>
              </div>
              <div className="text-right">
                <span className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Độ tuổi
                </span>
                <p className="text-xs font-bold text-slate-700">
                  {vaccine.doTuoi || "Mọi lứa tuổi"}
                </p>
              </div>
            </div>
          </div>

          {/* Phần chọn ngày */}
          <div className="space-y-2">
            <label className="text-xs font-black text-slate-500 uppercase tracking-widest flex items-center gap-2">
              <Calendar size={14} className="text-[#1e4e8c]" />
              Chọn ngày dự định đến tiêm <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              required
              min={new Date().toISOString().split("T")[0]}
              value={bookingDate}
              onChange={(e) => setBookingDate(e.target.value)}
              className="w-full p-3 bg-white border-2 border-slate-200 focus:border-[#1e4e8c] outline-none font-bold text-slate-700 transition-all shadow-inner"
            />
          </div>

          {/* THÔNG BÁO TÀI CHÍNH MỚI */}
          <div className="bg-blue-50 p-3 border border-blue-100 rounded-lg flex gap-3">
            <Banknote size={18} className="text-blue-600 shrink-0 mt-0.5" />
            <p className="text-[11px] text-blue-800 leading-relaxed italic">
              * Sau khi xác nhận, hệ thống sẽ tự động tạo{" "}
              <b>Hóa đơn chờ thanh toán</b> dựa trên đơn giá niêm yết. Vui lòng
              hoàn tất thanh toán khi đến tiêm.
            </p>
          </div>

          {/* Nút bấm */}
          <div className="flex gap-4 pt-2">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-[#1e4e8c] text-white font-bold py-3 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5 active:translate-y-0.5 transition-all disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {loading ? (
                "Đang xử lý..."
              ) : (
                <>
                  <CheckCircle size={18} /> ĐỒNG Ý ĐĂNG KÝ
                </>
              )}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="flex-1 bg-white border-2 border-slate-800 text-slate-800 font-bold py-3 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-0.5 active:translate-y-0.5 transition-all"
            >
              HỦY BỎ
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LookupRegistrationModal;
