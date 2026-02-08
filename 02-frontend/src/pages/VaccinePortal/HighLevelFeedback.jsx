import React, { useState, useEffect } from "react";
import medicalApi from "../../api/medicalApi";
import toast from "react-hot-toast";
import { Send, Clock, Trash2, Edit3, MessageSquare } from "lucide-react"; // Dùng lucide-react cho icon đẹp hơn

const HighLevelFeedback = () => {
  // --- State quản lý dữ liệu ---
  const [types, setTypes] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // --- State cho Form ---
  const [formData, setFormData] = useState({
    maLoaiPhanHoi: "",
    noiDung: "",
  });

  // --- Khởi tạo dữ liệu ---
  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    try {
      const [typesRes, historyRes] = await Promise.all([
        medicalApi.getHighLevelFeedbackTypes(),
        medicalApi.getHighLevelFeedbackHistory(),
      ]);
      setTypes(typesRes || []);
      setHistory(historyRes || []);
    } catch (error) {
      toast.error("Không thể tải dữ liệu: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Xử lý gửi phản hồi ---
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.maLoaiPhanHoi || !formData.noiDung.trim()) {
      toast.error("Vui lòng nhập đầy đủ thông tin");
      return;
    }

    setSubmitting(true);
    try {
      await medicalApi.sendHighLevelFeedback(formData);
      toast.success("Đã gửi phản hồi thành công!");
      setFormData({ maLoaiPhanHoi: "", noiDung: "" }); // Reset form
      fetchInitialData(); // Tải lại lịch sử
    } catch (error) {
      toast.error(error.message || "Gửi thất bại");
    } finally {
      setSubmitting(false);
    }
  };

  // --- Xử lý Xóa phản hồi ---
  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn thu hồi phản hồi này?")) return;
    try {
      await medicalApi.deleteHighLevelFeedback(id);
      toast.success("Đã xóa phản hồi");
      fetchInitialData();
    } catch (error) {
      toast.error(error.message);
    }
  };

  // --- Helper: Định dạng nhãn trạng thái ---
  const getStatusBadge = (status) => {
    const styles = {
      0: "bg-blue-100 text-blue-700 border-blue-200", // Mới gửi
      1: "bg-amber-100 text-amber-700 border-amber-200", // Đang xử lý
      2: "bg-emerald-100 text-emerald-700 border-emerald-200", // Đã giải quyết
    };
    const labels = { 0: "Mới gửi", 1: "Đang xử lý", 2: "Đã giải quyết" };
    return (
      <span
        className={`px-2 py-1 rounded-full text-[10px] font-bold border ${styles[status]}`}
      >
        {labels[status]}
      </span>
    );
  };

  return (
    <div className="p-4 sm:p-6 bg-slate-50 min-h-screen space-y-6">
      <div className="max-w-5xl mx-auto">
        {/* Header Section */}
        <div className="flex items-center gap-3 mb-6">
          <div className="p-3 bg-[#1e4e8c] text-white rounded-lg shadow-lg">
            <MessageSquare size={24} />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">
              Phản hồi cấp cao
            </h1>
            <p className="text-sm text-slate-500 italic">
              Góp ý trực tiếp tới Ban quản trị trung tâm
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* LEFT: FORM GỬI PHẢN HỒI */}
          <div className="lg:col-span-1">
            <form
              onSubmit={handleSubmit}
              className="bg-white border border-slate-200 shadow-sm p-5 space-y-4 sticky top-6"
            >
              <h2 className="font-bold text-slate-700 border-b pb-2 flex items-center gap-2">
                <Send size={16} /> Gửi ý kiến mới
              </h2>

              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1">
                  Loại phản hồi:
                </label>
                <select
                  className="w-full p-2 border border-slate-300 rounded text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                  value={formData.maLoaiPhanHoi}
                  onChange={(e) =>
                    setFormData({ ...formData, maLoaiPhanHoi: e.target.value })
                  }
                >
                  <option value="">-- Chọn loại phản hồi --</option>
                  {types.map((t) => (
                    <option key={t.maLoaiPhanHoi} value={t.maLoaiPhanHoi}>
                      {t.tenLoaiPhanHoi}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1">
                  Nội dung:
                </label>
                <textarea
                  rows="5"
                  placeholder="Nhập ý kiến hoặc phàn nàn của bạn tại đây..."
                  className="w-full p-2 border border-slate-300 rounded text-sm focus:ring-2 focus:ring-blue-500 outline-none resize-none"
                  value={formData.noiDung}
                  onChange={(e) =>
                    setFormData({ ...formData, noiDung: e.target.value })
                  }
                />
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-[#1e4e8c] text-white font-bold rounded shadow-md hover:bg-blue-800 transition-all flex items-center justify-center gap-2 disabled:bg-slate-400"
              >
                {submitting ? "Đang gửi..." : "GỬI PHẢN HỒI"}
              </button>
            </form>
          </div>

          {/* RIGHT: LỊCH SỬ PHẢN HỒI */}
          <div className="lg:col-span-2 space-y-4">
            <div className="bg-white border border-slate-200 shadow-sm overflow-hidden">
              <div className="bg-slate-100 p-3 border-b border-slate-200 flex justify-between items-center">
                <h2 className="font-bold text-slate-700 flex items-center gap-2">
                  <Clock size={16} /> Lịch sử phản hồi của bạn
                </h2>
                <span className="text-[10px] bg-slate-200 px-2 py-0.5 rounded font-bold">
                  Tổng: {history.length}
                </span>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-xs text-left border-collapse">
                  <thead>
                    <tr className="bg-slate-50 text-slate-500 border-b">
                      <th className="p-3 font-bold">Thời gian</th>
                      <th className="p-3 font-bold">Loại</th>
                      <th className="p-3 font-bold w-1/2">Nội dung</th>
                      <th className="p-3 font-bold text-center">Trạng thái</th>
                      <th className="p-3 font-bold text-center">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {loading ? (
                      <tr>
                        <td
                          colSpan="5"
                          className="p-8 text-center text-slate-400 italic"
                        >
                          Đang tải lịch sử...
                        </td>
                      </tr>
                    ) : history.length === 0 ? (
                      <tr>
                        <td
                          colSpan="5"
                          className="p-8 text-center text-slate-400"
                        >
                          Bạn chưa gửi phản hồi nào.
                        </td>
                      </tr>
                    ) : (
                      history.map((item) => (
                        <tr
                          key={item.maPhanHoi}
                          className="hover:bg-slate-50 transition-colors"
                        >
                          <td className="p-3 whitespace-nowrap text-slate-600">
                            {item.thoiGianGui}
                          </td>
                          <td className="p-3 whitespace-nowrap">
                            <span className="font-bold text-blue-900">
                              {item.tenLoaiPhanHoi}
                            </span>
                          </td>
                          <td className="p-3 text-slate-700 leading-relaxed">
                            {item.noiDung}
                          </td>
                          <td className="p-3 text-center">
                            {getStatusBadge(item.trangThai)}
                          </td>
                          <td className="p-3">
                            <div className="flex justify-center gap-2">
                              {/* Chỉ cho sửa/xóa nếu trạng thái là 0 (Mới) */}
                              {item.trangThai === 0 ? (
                                <>
                                  <button
                                    className="p-1.5 text-blue-600 hover:bg-blue-50 rounded"
                                    title="Chỉnh sửa"
                                  >
                                    <Edit3 size={14} />
                                  </button>
                                  <button
                                    onClick={() => handleDelete(item.maPhanHoi)}
                                    className="p-1.5 text-red-600 hover:bg-red-50 rounded"
                                    title="Thu hồi"
                                  >
                                    <Trash2 size={14} />
                                  </button>
                                </>
                              ) : (
                                <span className="text-[10px] italic text-slate-400">
                                  Không thể sửa
                                </span>
                              )}
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HighLevelFeedback;
