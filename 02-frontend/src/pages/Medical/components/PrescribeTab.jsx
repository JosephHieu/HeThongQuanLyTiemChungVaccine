import React, { useState, useEffect } from "react";
import {
  FileSignature,
  CheckCircle,
  Syringe,
  Calendar,
  Info,
  Loader2,
} from "lucide-react";
import toast from "react-hot-toast";
import medicalApi from "../../../api/medicalApi";
import inventoryApi from "../../../api/inventoryApi";

const PrescribeTab = ({ data, onPrescribeSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [batches, setBatches] = useState([]); // Danh sách lô vắc-xin từ kho
  const [prescription, setPrescription] = useState({
    maLoVacXin: "", // Khớp với DTO Backend
    thoiGianCanTiem: "", // Khớp với DTO Backend
    ghiChu: "",
  });

  // 1. Lấy danh sách lô vắc-xin khi mở tab kê đơn
  useEffect(() => {
    const fetchBatches = async () => {
      try {
        // Giả sử bạn có API lấy danh sách lô còn hàng
        // Nếu dùng inventoryApi.getInventory, hãy lọc những lô có số lượng > 0
        const response = await inventoryApi.getInventory("", "", 0, 100);
        setBatches(response.content || []);
      } catch (error) {
        console.error("Lỗi lấy danh sách kho:", error);
      }
    };
    fetchBatches();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPrescription((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!prescription.maLoVacXin || !prescription.thoiGianCanTiem) {
      toast.error("Vui lòng chọn vắc-xin và ngày hẹn!");
      return;
    }

    setLoading(true);
    try {
      // 2. Gọi API kê đơn thực tế
      await medicalApi.prescribe(data.id, prescription);

      toast.success("Đã lưu chỉ định tiêm chủng mới thành công!");

      // Reset form
      setPrescription({ maLoVacXin: "", thoiGianCanTiem: "", ghiChu: "" });

      // Callback để trang chính cập nhật lại thông tin "Dự kiến mũi tiếp theo"
      if (onPrescribeSuccess) onPrescribeSuccess();
    } catch (error) {
      toast.error(error.message || "Lỗi khi lưu chỉ định!");
    } finally {
      setLoading(false);
    }
  };

  if (!data) return <EmptyState />;

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
          Bệnh nhân: <span className="text-purple-600">{data.hoTen}</span>
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Lựa chọn Vắc-xin từ Kho */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 group-focus-within:text-purple-600">
            Chọn Vắc-xin & Lô sản xuất
          </label>
          <div className="relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-purple-500">
              <Syringe size={20} />
            </div>
            <select
              name="maLoVacXin"
              value={prescription.maLoVacXin}
              onChange={handleChange}
              className="w-full pl-12 pr-10 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 outline-none transition-all font-bold text-slate-700 appearance-none"
            >
              <option value="">-- Chọn vắc-xin từ kho --</option>
              {batches.map((batch) => (
                <option
                  key={batch.maLo}
                  value={batch.maLo}
                  disabled={batch.soLuong <= 0}
                >
                  {/* Khớp hoàn toàn với JSON bạn gửi: tenVacXin, soLo, soLuong */}
                  {batch.tenVacXin} ({batch.tenLoaiVacXin}) - [Lô: {batch.soLo}]
                  - (Còn: {batch.soLuong} liều)
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Ngày hẹn tiêm */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 group-focus-within:text-purple-600">
            Ngày hẹn tiêm chủng
          </label>
          <div className="relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-purple-500">
              <Calendar size={20} />
            </div>
            <input
              type="date"
              name="thoiGianCanTiem"
              value={prescription.thoiGianCanTiem}
              onChange={handleChange}
              className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700"
            />
          </div>
        </div>

        {/* Ghi chú */}
        <div className="flex flex-col gap-2 group">
          <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 group-focus-within:text-purple-600">
            Ghi chú / Dặn dò
          </label>
          <textarea
            name="ghiChu"
            rows="3"
            value={prescription.ghiChu}
            onChange={handleChange}
            placeholder="Dặn dò bệnh nhân..."
            className="w-full px-6 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700 resize-none"
          />
        </div>

        <div className="p-4 bg-blue-50 rounded-2xl border border-blue-100 flex items-start gap-3">
          <Info className="text-blue-500 shrink-0 mt-0.5" size={18} />
          <p className="text-xs text-blue-600 leading-relaxed italic">
            <strong>Lưu ý:</strong> Việc kê đơn sẽ tạo một lịch hẹn mới. Số
            lượng vắc-xin sẽ chính thức được trừ khi thực hiện tiêm thực tế.
          </p>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-4 bg-purple-600 text-white font-black rounded-2xl hover:bg-purple-700 hover:shadow-xl transition-all flex items-center justify-center gap-2 uppercase tracking-widest disabled:opacity-70"
        >
          {loading ? (
            <Loader2 className="animate-spin" />
          ) : (
            <>
              <CheckCircle size={20} /> Xác nhận chỉ định
            </>
          )}
        </button>
      </form>
    </div>
  );
};

const EmptyState = () => (
  <div className="bg-white p-12 rounded-[2.5rem] border border-slate-100 text-center italic text-slate-400">
    Vui lòng tra cứu bệnh nhân trước khi thực hiện kê đơn.
  </div>
);

export default PrescribeTab;
