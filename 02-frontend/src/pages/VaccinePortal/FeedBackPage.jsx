import React, { useState, useEffect } from "react";
import {
  MessageSquare,
  Send,
  Clock,
  User,
  MapPin,
  Syringe,
  ClipboardCheck,
  AlertCircle,
  History,
  CheckCircle2,
  RefreshCcw,
} from "lucide-react";
import toast from "react-hot-toast";
import medicalApi from "../../api/medicalApi";

const FeedbackPage = () => {
  const [loading, setLoading] = useState(false);
  const [completedInjections, setCompletedInjections] = useState([]);
  const [feedbackHistory, setFeedbackHistory] = useState([]); // THÊM MỚI
  const [selectedIdx, setSelectedIdx] = useState(null);

  const [formData, setFormData] = useState({
    tenVacXin: "",
    thoiGianTiem: "",
    diaDiemTiem: "",
    nhanVienPhuTrach: "",
    noiDung: "",
  });

  // Load cả lịch sử tiêm và lịch sử phản hồi
  const loadData = async () => {
    try {
      const [history, feedbacks] = await Promise.all([
        medicalApi.getMyHistory(),
        medicalApi.getMyFeedbackHistory(), // Gọi API mới thêm
      ]);
      setCompletedInjections(history);
      setFeedbackHistory(feedbacks);
    } catch (error) {
      console.error("Lỗi tải dữ liệu:", error);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSelectInjection = (injection, index) => {
    setSelectedIdx(index);
    setFormData({
      ...formData,
      tenVacXin: injection.tenVacXin,
      thoiGianTiem: injection.thoiGian,
      diaDiemTiem: injection.diaDiem,
      nhanVienPhuTrach: injection.nguoiTiem,
    });
    toast.success(`Đã chọn: ${injection.tenVacXin}`);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.noiDung.trim()) return toast.error("Vui lòng nhập nội dung!");

    setLoading(true);
    try {
      await medicalApi.sendFeedback(formData);
      toast.success("Gửi phản hồi thành công!");
      setFormData({ ...formData, noiDung: "" });
      setSelectedIdx(null);
      loadData(); // Tải lại danh sách phản hồi để thấy cái mới nhất
    } catch (error) {
      toast.error(error.message || "Gửi phản hồi thất bại");
    } finally {
      setLoading(false); // Sửa từ true thành false
    }
  };

  // Hàm helper hiển thị Badge trạng thái
  const getStatusBadge = (status) => {
    const config = {
      0: {
        text: "Mới gửi",
        color: "bg-blue-100 text-blue-600 border-blue-200",
      },
      1: {
        text: "Đang xử lý",
        color: "bg-amber-100 text-amber-600 border-amber-200",
      },
      2: {
        text: "Đã giải quyết",
        color: "bg-emerald-100 text-emerald-600 border-emerald-200",
      },
    };
    const item = config[status] || config["0"];
    return (
      <span
        className={`px-2 py-0.5 rounded-md text-[10px] font-bold border ${item.color}`}
      >
        {item.text}
      </span>
    );
  };

  return (
    <div className="p-4 md:p-8 max-w-6xl mx-auto space-y-10 animate-in fade-in slide-in-from-bottom-4 duration-500">
      {/* Header Section */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-slate-200 pb-6">
        <div className="space-y-2">
          <h2 className="text-3xl font-black text-indigo-900 flex items-center gap-3 uppercase tracking-tighter">
            <MessageSquare size={32} className="text-emerald-500" /> Phản hồi
            sau tiêm
          </h2>
          <p className="text-slate-500 font-medium">
            Theo dõi sức khỏe và phản hồi dịch vụ y tế.
          </p>
        </div>
        <button
          onClick={loadData}
          className="flex items-center gap-2 text-xs font-bold text-indigo-600 hover:text-indigo-700 bg-indigo-50 px-4 py-2 rounded-xl transition-all"
        >
          <RefreshCcw size={14} /> Làm mới dữ liệu
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8">
        {/* Bước 1: Chọn mũi tiêm */}
        <div className="lg:col-span-2 space-y-4">
          <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
            <ClipboardCheck size={16} /> Chọn mũi tiêm vừa thực hiện
          </h3>
          <div className="space-y-3 max-h-[450px] overflow-y-auto pr-2 scrollbar-hide">
            {completedInjections.map((inj, index) => (
              <div
                key={index}
                onClick={() => handleSelectInjection(inj, index)}
                className={`p-4 rounded-2xl border-2 transition-all cursor-pointer group ${
                  selectedIdx === index
                    ? "border-indigo-600 bg-indigo-50 shadow-md"
                    : "border-slate-100 bg-white hover:border-indigo-200"
                }`}
              >
                <div className="flex items-center gap-3">
                  <div
                    className={`p-2 rounded-xl transition-colors ${selectedIdx === index ? "bg-indigo-600 text-white" : "bg-slate-100 text-slate-500"}`}
                  >
                    <Syringe size={20} />
                  </div>
                  <div className="flex-1">
                    <p
                      className={`text-sm font-black ${selectedIdx === index ? "text-indigo-900" : "text-slate-700"}`}
                    >
                      {inj.tenVacXin}
                    </p>
                    <p className="text-[10px] font-bold text-slate-400">
                      {inj.thoiGian}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Bước 2: Nhập nội dung */}
        <form
          onSubmit={handleSubmit}
          className="lg:col-span-3 space-y-6 bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-sm relative overflow-hidden"
        >
          <div className="absolute top-0 right-0 p-4 opacity-5 pointer-events-none">
            <MessageSquare size={120} />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 relative z-10">
            <div className="space-y-1">
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                Vắc-xin
              </label>
              <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-600 flex items-center gap-2">
                <Syringe size={16} className="text-indigo-500" />{" "}
                {formData.tenVacXin || "Chưa chọn"}
              </div>
            </div>
            <div className="space-y-1">
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                Người tiêm
              </label>
              <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-600 flex items-center gap-2">
                <User size={16} className="text-indigo-500" />{" "}
                {formData.nhanVienPhuTrach || "N/A"}
              </div>
            </div>
          </div>

          <div className="space-y-2 relative z-10">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 flex items-center gap-1">
              Nội dung phản hồi{" "}
              <AlertCircle size={12} className="text-rose-400" />
            </label>
            <textarea
              rows="4"
              className="w-full px-5 py-4 bg-slate-50 border border-slate-100 rounded-2xl font-bold text-slate-700 focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all resize-none"
              placeholder="Nhập triệu chứng hoặc ý kiến của bạn..."
              value={formData.noiDung}
              onChange={(e) =>
                setFormData({ ...formData, noiDung: e.target.value })
              }
            />
          </div>

          <div className="flex justify-between items-center pt-2 relative z-10">
            <p className="text-[10px] text-slate-400 font-bold max-w-[200px]">
              Thông tin được bảo mật và gửi tới bác sĩ phụ trách.
            </p>
            <button
              type="submit"
              disabled={loading || !formData.tenVacXin}
              className="px-8 py-3.5 bg-indigo-600 text-white font-black rounded-xl hover:bg-indigo-700 shadow-lg shadow-indigo-100 flex items-center gap-2 uppercase tracking-widest transition-all disabled:opacity-50"
            >
              {loading ? (
                "Đang gửi..."
              ) : (
                <>
                  <Send size={18} /> Gửi ngay
                </>
              )}
            </button>
          </div>
        </form>
      </div>

      {/* PHẦN MỚI: Lịch sử phản hồi */}
      <div className="space-y-4">
        <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
          <History size={18} /> Lịch sử phản hồi của bạn
        </h3>

        {feedbackHistory.length === 0 ? (
          <div className="bg-slate-50 border-2 border-dashed border-slate-200 rounded-3xl p-10 text-center">
            <p className="text-slate-400 font-bold text-sm">
              Bạn chưa có phản hồi nào trước đây.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {feedbackHistory.map((fb, idx) => (
              <div
                key={idx}
                className="bg-white border border-slate-100 p-5 rounded-3xl shadow-sm hover:shadow-md transition-all space-y-3"
              >
                <div className="flex justify-between items-start">
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
                      <Syringe size={16} />
                    </div>
                    <p className="text-xs font-black text-slate-700">
                      {fb.tenVacXin}
                    </p>
                  </div>
                  {getStatusBadge(fb.trangThai)}
                </div>
                <p className="text-xs text-slate-500 font-medium line-clamp-3 bg-slate-50 p-3 rounded-xl">
                  "{fb.noiDung}"
                </p>
                <div className="flex items-center gap-2 text-[10px] font-bold text-slate-400 pt-1">
                  <Clock size={12} /> {fb.thoiGianTiem}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default FeedbackPage;
