import React, { useState } from "react";
import {
  FileSignature,
  CheckCircle,
  XCircle,
  Syringe,
  Calendar,
  Info,
  Loader2,
} from "lucide-react";
import toast from "react-hot-toast";

const PrescribeTab = ({ data }) => {
  const [loading, setLoading] = useState(false);
  const [prescription, setPrescription] = useState({
    tenVacxin: "",
    ngayHen: "",
    ghiChu: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPrescription((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!prescription.tenVacxin || !prescription.ngayHen) {
      toast.error("Vui lòng nhập đầy đủ tên vắc-xin và ngày hẹn!");
      return;
    }

    setLoading(true);
    // Giả lập gọi API lưu đơn thuốc
    setTimeout(() => {
      setLoading(false);
      toast.success("Đã lưu chỉ định tiêm chủng mới!");
      // Reset form sau khi thành công
      setPrescription({ tenVacxin: "", ngayHen: "", ghiChu: "" });
    }, 1500);
  };

  if (!data) {
    return (
      <div className="bg-white p-12 rounded-[2.5rem] border border-slate-100 text-center italic text-slate-400">
        Vui lòng tra cứu bệnh nhân trước khi thực hiện kê đơn.
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto bg-white p-6 md:p-12 rounded-[2.5rem] shadow-sm border border-slate-100 animate-in zoom-in-95 duration-500">
      {/* Header */}
      <div className="mb-10 text-center">
        <div className="inline-flex p-4 bg-purple-100 text-purple-600 rounded-3xl mb-4">
          <FileSignature size={32} />
        </div>
        <h3 className="text-2xl font-black text-slate-800 uppercase tracking-tight">
          Chỉ định tiêm chủng
        </h3>
        <p className="text-slate-400 text-sm font-bold mt-2">
          Bệnh nhân: <span className="text-purple-600">{data.hoTen}</span> | ID:{" "}
          <span className="text-purple-600">{data.id}</span>
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Ô nhập Tên Vắc-xin */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 transition-colors group-focus-within:text-purple-600">
            Loại vắc-xin chỉ định
          </label>
          <div className="relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-purple-500">
              <Syringe size={20} />
            </div>
            <input
              type="text"
              name="tenVacxin"
              value={prescription.tenVacxin}
              onChange={handleChange}
              placeholder="Ví dụ: TETAVAX, 6 TRONG 1..."
              className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700"
            />
          </div>
        </div>

        {/* Ô nhập Ngày hẹn */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 transition-colors group-focus-within:text-purple-600">
            Ngày hẹn tiêm chủng
          </label>
          <div className="relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-purple-500">
              <Calendar size={20} />
            </div>
            <input
              type="date"
              name="ngayHen"
              value={prescription.ngayHen}
              onChange={handleChange}
              className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700"
            />
          </div>
        </div>

        {/* Ghi chú thêm */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 transition-colors group-focus-within:text-purple-600">
            Ghi chú / Dặn dò
          </label>
          <textarea
            name="ghiChu"
            rows="3"
            value={prescription.ghiChu}
            onChange={handleChange}
            placeholder="Dặn dò bệnh nhân trước khi tiêm..."
            className="w-full px-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700 resize-none"
          />
        </div>

        {/* Cảnh báo an toàn */}
        <div className="p-4 bg-blue-50 rounded-2xl border border-blue-100 flex items-start gap-3">
          <Info className="text-blue-500 shrink-0 mt-0.5" size={18} />
          <p className="text-xs text-blue-600 leading-relaxed italic">
            <strong>Lưu ý:</strong> Vui lòng kiểm tra kỹ tiền sử dị ứng và phản
            ứng của mũi tiêm trước đó trước khi xác nhận chỉ định mới.
          </p>
        </div>

        {/* Nút bấm */}
        <div className="pt-4 flex flex-col sm:flex-row gap-4">
          <button
            type="submit"
            disabled={loading}
            className="flex-1 py-4 bg-purple-600 text-white font-black rounded-2xl hover:bg-purple-700 hover:shadow-xl hover:shadow-purple-200 transition-all flex items-center justify-center gap-2 uppercase tracking-widest disabled:opacity-70"
          >
            {loading ? (
              <Loader2 className="animate-spin" />
            ) : (
              <>
                <CheckCircle size={20} /> Xác nhận chỉ định
              </>
            )}
          </button>
          <button
            type="button"
            className="px-10 py-4 bg-slate-100 text-slate-500 font-black rounded-2xl hover:bg-slate-200 transition-all uppercase tracking-widest"
          >
            Hủy
          </button>
        </div>
      </form>
    </div>
  );
};

export default PrescribeTab;
