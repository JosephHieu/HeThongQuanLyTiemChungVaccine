import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom"; // Thêm useNavigate
import supportApi from "../../api/supportApi";
import toast from "react-hot-toast";
import {
  Search,
  Mail,
  User,
  Phone,
  XCircle,
  AlertCircle,
  ArrowLeft,
} from "lucide-react";

const VaccinationReminder = () => {
  const navigate = useNavigate(); // Khởi tạo navigate
  const [searchParams] = useSearchParams();

  const [email, setEmail] = useState("");
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [loiNhan, setLoiNhan] = useState("");

  const validateEmail = (email) => {
    return String(email)
      .toLowerCase()
      .match(/^[\w-\\.]+@([\w-]+\.)+[\w-]{2,4}$/);
  };

  // Tự động tra cứu nếu có email từ URL (khi nhảy từ trang Feedback sang)
  useEffect(() => {
    const emailFromUrl = searchParams.get("email");
    if (emailFromUrl) {
      setEmail(emailFromUrl);
      autoSearch(emailFromUrl);
    }
  }, [searchParams]);

  const autoSearch = async (targetEmail) => {
    setLoading(true);
    try {
      const result = await supportApi.searchReminderData(targetEmail);
      setData(result);
    } catch (error) {
      toast.error("Không thể tự động tìm kiếm bệnh nhân này");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!validateEmail(email))
      return toast.error("Định dạng Email không hợp lệ!");

    setLoading(true);
    try {
      const result = await supportApi.searchReminderData(email);
      setData(result);
      toast.success("Đã tìm thấy thông tin bệnh nhân");
    } catch (error) {
      toast.error(error.message || "Không tìm thấy dữ liệu bệnh nhân");
      setData(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSendReminder = async () => {
    if (data.lichDuKien.length === 0) {
      return toast.error("Không có lịch tiêm dự kiến để nhắc nhở!");
    }

    setSending(true);
    try {
      await supportApi.sendVaccinationReminder({
        email: data.email,
        loiNhan:
          loiNhan || "Vui lòng kiểm tra lịch tiêm chủng dự kiến của bạn.",
      });
      toast.success("Email nhắc lịch đã được gửi thành công!");
      setLoiNhan(""); // Reset lời nhắn sau khi gửi
    } catch (error) {
      toast.error(error.message || "Gửi email thất bại");
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 bg-slate-50 min-h-screen animate-in fade-in duration-500">
      <div className="max-w-5xl mx-auto space-y-4">
        {/* THANH ĐIỀU HƯỚNG PHỤ (BREADCRUMB) */}
        <div className="flex items-center justify-between px-1">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 text-slate-500 hover:text-emerald-700 font-bold text-xs transition-all group"
          >
            <div className="p-1.5 bg-white rounded-full shadow-sm group-hover:bg-emerald-50 transition-all">
              <ArrowLeft size={14} />
            </div>
            QUAY LẠI TRANG TRƯỚC
          </button>

          <span className="text-[10px] text-slate-400 font-medium uppercase tracking-widest">
            Module: Hỗ trợ khách hàng
          </span>
        </div>
        {/* Header Section */}
        <div className="bg-[#2d5a27] p-3 text-white font-bold text-center uppercase text-sm tracking-wider">
          Hỗ trợ khách hàng - Nhắc lịch tiêm chủng
        </div>

        <div className="p-6 space-y-8">
          {/* Search Area */}
          <div className="flex flex-col sm:flex-row gap-4 items-end bg-slate-50 p-4 border border-slate-200 rounded shadow-inner">
            <div className="flex-1 w-full">
              <label className="block text-[10px] font-bold text-slate-500 uppercase mb-1">
                Email bệnh nhân cần tra cứu:
              </label>
              <div className="relative">
                <input
                  type="email"
                  className="w-full p-2.5 pl-10 border border-slate-400 rounded-sm text-sm outline-none focus:border-emerald-600 focus:ring-1 focus:ring-emerald-600 transition-all"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                  placeholder="nhap-email@gmail.com..."
                />
                <Mail
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                  size={16}
                />
              </div>
            </div>
            <button
              onClick={handleSearch}
              disabled={loading}
              className="w-full sm:w-auto px-8 py-2.5 bg-slate-800 text-white font-bold text-xs rounded-sm flex items-center justify-center gap-2 hover:bg-black transition-all active:scale-95 disabled:bg-slate-400"
            >
              {loading ? (
                <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent" />
              ) : (
                <Search size={16} />
              )}
              {loading ? "ĐANG TÌM..." : "TRA CỨU"}
            </button>
          </div>

          {data && (
            <div className="animate-in slide-in-from-bottom-4 duration-500 space-y-8">
              {/* Patient Profile Header */}
              <div className="flex items-center justify-between border-b pb-4 border-dashed border-slate-300">
                <div className="flex gap-6 text-sm">
                  <span className="flex items-center gap-2 font-bold text-slate-700">
                    <User size={16} className="text-emerald-600" /> {data.hoTen}
                  </span>
                  <span className="flex items-center gap-2 text-slate-500">
                    <Phone size={16} /> {data.soDienThoai}
                  </span>
                </div>
                <div className="text-[10px] font-bold px-2 py-1 bg-blue-50 text-blue-600 rounded uppercase">
                  Hồ sơ hợp lệ
                </div>
              </div>

              {/* Bảng 1: Lịch sử */}
              <section>
                <h3 className="text-xs font-bold text-blue-800 mb-3 uppercase italic underline flex items-center gap-2">
                  1. Lịch sử tiêm chủng (Đã hoàn thành):
                </h3>
                <div className="border border-slate-400 rounded-sm overflow-hidden">
                  <table className="w-full text-[11px] text-left border-collapse">
                    <thead className="bg-slate-100 border-b border-slate-400 text-slate-600">
                      <tr>
                        <th className="p-2 border-r border-slate-300 w-12 text-center">
                          STT
                        </th>
                        <th className="p-2 border-r border-slate-300 w-32">
                          Ngày tiêm
                        </th>
                        <th className="p-2">Loại vắc xin</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {data.lichSuTiem.length > 0 ? (
                        data.lichSuTiem.map((item, index) => (
                          <tr
                            key={index}
                            className="hover:bg-slate-50 transition-colors"
                          >
                            <td className="p-2 border-r border-slate-300 text-center">
                              {index + 1}
                            </td>
                            <td className="p-2 border-r border-slate-300 font-medium">
                              {item.ngayTiem}
                            </td>
                            <td className="p-2 text-slate-600">
                              {item.tenVacXin}
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td
                            colSpan="3"
                            className="p-4 text-center text-slate-400 italic"
                          >
                            Không tìm thấy lịch sử tiêm chủng
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </section>

              {/* Bảng 2: Dự kiến */}
              <section>
                <h3 className="text-xs font-bold text-red-700 mb-3 uppercase italic underline">
                  2. Lịch tiêm chủng dự kiến sắp tới:
                </h3>
                {data.lichDuKien.length > 0 ? (
                  <div className="border border-slate-400 rounded-sm overflow-hidden shadow-sm">
                    <table className="w-full text-[11px] text-left">
                      <thead className="bg-red-50 border-b border-slate-400 text-red-900">
                        <tr>
                          <th className="p-2 border-r border-slate-300 w-12 text-center">
                            STT
                          </th>
                          <th className="p-2 border-r border-slate-300 w-32">
                            Ngày dự kiến
                          </th>
                          <th className="p-2 border-r border-slate-300">
                            Loại vắc xin
                          </th>
                          <th className="p-2 w-32">Giá tham khảo</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-200">
                        {data.lichDuKien.map((item, index) => (
                          <tr
                            key={index}
                            className="bg-white hover:bg-red-50/30 transition-colors"
                          >
                            <td className="p-2 border-r border-slate-300 text-center">
                              {index + 1}
                            </td>
                            <td className="p-2 border-r border-slate-300 font-bold text-red-600">
                              {item.ngayDuKien}
                            </td>
                            <td className="p-2 border-r border-slate-300 font-medium">
                              {item.tenVacXin}
                            </td>
                            <td className="p-2 font-bold text-emerald-700">
                              {item.giaTienDuKien.toLocaleString()} đ
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="p-6 border-2 border-dashed border-slate-200 text-center rounded-lg bg-slate-50">
                    <AlertCircle
                      className="mx-auto text-slate-300 mb-2"
                      size={32}
                    />
                    <p className="text-slate-500 text-xs">
                      Bệnh nhân đã hoàn thành tất cả các mũi tiêm trong lộ
                      trình.
                    </p>
                  </div>
                )}
              </section>

              {/* Action Form */}
              <div className="bg-emerald-50 p-5 border border-emerald-200 rounded-sm space-y-4 shadow-inner">
                <div className="flex items-center justify-between">
                  <label className="block text-[10px] font-bold text-emerald-800 uppercase">
                    Soạn lời nhắn kèm theo (Email HTML):
                  </label>
                  <span className="text-[10px] text-emerald-600 italic">
                    * Hệ thống sẽ tự động chèn bảng lịch dự kiến vào email
                  </span>
                </div>
                <textarea
                  className="w-full p-4 border border-emerald-300 rounded-sm text-sm outline-none focus:ring-2 focus:ring-emerald-500 min-h-[100px] shadow-sm"
                  placeholder="Chào anh/chị, trung tâm xin nhắc lịch tiêm mũi tiếp theo cho bé..."
                  value={loiNhan}
                  onChange={(e) => setLoiNhan(e.target.value)}
                />
                <div className="flex flex-col sm:flex-row justify-center gap-4 pt-2">
                  <button
                    onClick={handleSendReminder}
                    disabled={sending || data.lichDuKien.length === 0}
                    className="px-12 py-3 bg-emerald-600 text-white font-bold rounded-sm shadow-lg hover:bg-emerald-700 active:translate-y-0.5 flex items-center justify-center gap-2 disabled:bg-slate-300 disabled:shadow-none transition-all"
                  >
                    {sending ? (
                      <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent" />
                    ) : (
                      <Mail size={18} />
                    )}
                    {sending ? "ĐANG XỬ LÝ..." : "XÁC NHẬN GỬI EMAIL"}
                  </button>
                  <button
                    onClick={() => setData(null)}
                    className="px-12 py-3 bg-white text-slate-600 font-bold rounded-sm border border-slate-300 hover:bg-slate-100 transition-colors"
                  >
                    <XCircle size={18} /> THOÁT
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default VaccinationReminder;
