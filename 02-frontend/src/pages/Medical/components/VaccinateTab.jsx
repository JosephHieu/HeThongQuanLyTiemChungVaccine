import React, { useState } from "react";
import {
  Syringe,
  CheckCircle,
  AlertCircle,
  Loader2,
  Info,
  Clock,
} from "lucide-react"; // Thêm icon Clock
import toast from "react-hot-toast";
import medicalApi from "../../../api/medicalApi";

const VaccinateTab = ({ data, onVaccinateSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [selectedRegId, setSelectedRegId] = useState("");
  const [reaction, setReaction] = useState("Bình thường");
  const [effectDuration, setEffectDuration] = useState("12 tháng"); // State mới cho thời gian tác dụng

  // 1. Lấy trực tiếp từ pendingRegistrations (Khớp với DTO Backend)
  const pendingRegistrations = data?.pendingRegistrations || [];

  const handleConfirm = async (e) => {
    e.preventDefault();
    if (!selectedRegId) {
      toast.error("Vui lòng chọn một mũi tiêm để xác nhận!");
      return;
    }

    setLoading(true);
    try {
      // 2. Gửi thêm trường thoiGianTacDung lên Backend
      await medicalApi.confirmInjection(selectedRegId, {
        phanUngSauTiem: reaction,
        thoiGianTacDung: effectDuration,
      });

      toast.success("Xác nhận hoàn thành tiêm chủng!");
      setSelectedRegId("");
      setReaction("Bình thường");

      if (onVaccinateSuccess) onVaccinateSuccess();
    } catch (error) {
      toast.error(error.message || "Lỗi khi xác nhận tiêm!");
    } finally {
      setLoading(false);
    }
  };

  if (!data) return <EmptyState />;

  return (
    <div className="max-w-3xl mx-auto bg-white p-6 md:p-12 rounded-[2.5rem] shadow-sm border border-slate-100 animate-in fade-in zoom-in-95 duration-500">
      {/* Header */}
      <div className="mb-10 text-center">
        <div className="inline-flex p-4 bg-emerald-100 text-emerald-600 rounded-3xl mb-4">
          <Syringe size={32} />
        </div>
        <h3 className="text-2xl font-black text-slate-800 uppercase tracking-tight">
          Thực hiện tiêm chủng
        </h3>
        <p className="text-slate-400 text-sm font-bold mt-2">
          Bệnh nhân: <span className="text-emerald-600">{data.hoTen}</span>
        </p>
      </div>

      <form onSubmit={handleConfirm} className="space-y-6">
        {/* Bước 1: Chọn mũi tiêm */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1">
            Mũi tiêm đang chờ ({pendingRegistrations.length})
          </label>
          <select
            value={selectedRegId}
            onChange={(e) => setSelectedRegId(e.target.value)}
            className="w-full px-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 outline-none transition-all font-bold text-slate-700 appearance-none"
          >
            <option value="">-- Chọn mũi tiêm thực hiện --</option>
            {pendingRegistrations.map((reg) => (
              <option key={reg.id} value={reg.id}>
                {reg.tenVacXin} - Lô: {reg.soLo} (Hẹn: {reg.ngayHen})
              </option>
            ))}
          </select>
        </div>

        {/* MỚI: Bước 2: Thời gian tác dụng dự kiến */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 group-focus-within:text-emerald-600">
            Thời gian hiệu lực vắc-xin
          </label>
          <div className="relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300">
              <Clock size={20} />
            </div>
            <input
              type="text"
              value={effectDuration}
              onChange={(e) => setEffectDuration(e.target.value)}
              placeholder="Ví dụ: 12 tháng, 10 năm, Trọn đời..."
              className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 outline-none transition-all font-bold text-slate-700"
            />
          </div>
        </div>

        {/* Bước 3: Phản ứng sau tiêm */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 group-focus-within:text-emerald-600">
            Phản ứng sau 30 phút theo dõi
          </label>
          <textarea
            value={reaction}
            onChange={(e) => setReaction(e.target.value)}
            rows="3"
            className="w-full px-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 outline-none transition-all font-bold text-slate-700 resize-none"
          />
        </div>

        <div className="p-4 bg-blue-50 rounded-2xl border border-blue-100 flex items-start gap-3">
          <Info className="text-blue-500 shrink-0 mt-0.5" size={18} />
          <p className="text-xs text-blue-600 leading-relaxed italic">
            <strong>Lưu ý:</strong> Việc xác nhận sẽ trừ 1 liều vắc-xin trong
            kho và chuyển mũi tiêm này vào lịch sử bệnh án chính thức.
          </p>
        </div>

        <button
          type="submit"
          disabled={loading || pendingRegistrations.length === 0}
          className="w-full py-4 bg-emerald-600 text-white font-black rounded-2xl hover:bg-emerald-700 hover:shadow-xl transition-all flex items-center justify-center gap-2 uppercase tracking-widest disabled:opacity-50 shadow-lg shadow-emerald-100"
        >
          {loading ? (
            <Loader2 className="animate-spin" />
          ) : (
            <>
              <CheckCircle size={20} /> Xác nhận hoàn thành
            </>
          )}
        </button>
      </form>
    </div>
  );
};

const EmptyState = () => (
  <div className="text-center p-16 bg-white rounded-[2.5rem] border-2 border-dashed border-slate-100 text-slate-400 italic font-medium">
    Vui lòng tra cứu bệnh nhân để thực hiện tiêm chủng.
  </div>
);

export default VaccinateTab;
