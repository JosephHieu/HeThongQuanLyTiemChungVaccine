import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
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
  MessageCircle, // Thêm icon cho phần tư vấn
} from "lucide-react";

const VaccinationReminder = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [email, setEmail] = useState("");
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [loiNhan, setLoiNhan] = useState("");

  // BỘ MẪU TƯ VẤN NHANH
  const CONSULT_TEMPLATES = [
    {
      title: "Cám ơn khách hàng",
      content: (name) =>
        `Chào anh/chị ${name}, Trung tâm tiêm chủng xin chân thành cảm ơn anh/chị đã tin tưởng và sử dụng dịch vụ của chúng tôi trong thời gian qua. Chúc anh/chị và gia đình luôn mạnh khỏe và hạnh phúc!`,
    },
    {
      title: "Hướng dẫn sau tiêm",
      content: (name) =>
        `Chào anh/chị ${name}, sau khi tiêm xong, anh/chị vui lòng theo dõi sức khỏe tại nhà. Nếu có biểu hiện sốt nhẹ, hãy chườm mát hoặc dùng thuốc theo chỉ dẫn của bác sĩ. Trong trường hợp có dấu hiệu bất thường, hãy liên hệ ngay với hotline của trung tâm để được hỗ trợ kịp thời.`,
    },
    {
      title: "Nhắc mũi 6in1",
      content: (name) =>
        `Chào anh/chị ${name}, bé đã đến kỳ tiêm mũi 6 trong 1 tiếp theo. Đây là mũi tiêm quan trọng phòng 6 bệnh nguy hiểm. Anh/chị nên sắp xếp cho bé đi tiêm đúng lịch nhé.`,
    },
    {
      title: "Tư vấn vắc xin mới",
      content: (name) =>
        `Chào anh/chị ${name}, hiện trung tâm vừa nhập về lô vắc xin mới phù hợp với độ tuổi của mình/bé. Anh/chị có thể đăng ký giữ vắc xin ngay qua email này hoặc liên hệ tổng đài để được ưu tiên lịch tiêm sớm nhất.`,
    },
    {
      title: "Xin lỗi Feedback",
      content: (name) =>
        `Chào anh/chị ${name}, trung tâm đã ghi nhận phản hồi và rất xin lỗi về trải nghiệm chưa tốt của mình. Chúng tôi đã điều chỉnh quy trình và chuẩn bị sẵn lộ trình ưu tiên cho lần tiêm tới của anh/chị. Rất mong tiếp tục được phục vụ gia đình.`,
    },
    {
      title: "Giải đáp về giá",
      content: (name) =>
        `Chào anh/chị ${name}, hiện tại giá vắc xin đang có sẵn tại kho với giá niêm yết ổn định. Anh/chị có thể đến trực tiếp hoặc đặt giữ vắc xin trước để đảm bảo không bị ảnh hưởng nếu có sự thay đổi về giá nhập kho sau này.`,
    },
  ];

  const validateEmail = (email) => {
    return String(email)
      .toLowerCase()
      .match(/^[\w-\\.]+@([\w-]+\.)+[\w-]{2,4}$/);
  };

  useEffect(() => {
    const emailFromUrl = searchParams.get("email");
    if (emailFromUrl && emailFromUrl !== "undefined") {
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
      toast.error("Không thể tự động tìm kiếm bệnh nhân này: " + error.message);
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
    if (!data) return;
    setSending(true);
    try {
      await supportApi.sendVaccinationReminder({
        email: data.email,
        loiNhan:
          loiNhan || "Vui lòng kiểm tra lịch tiêm chủng dự kiến của bạn.",
      });
      toast.success("Đã gửi email tư vấn & nhắc lịch thành công!");
      setLoiNhan("");
    } catch (error) {
      toast.error(error.message || "Gửi email thất bại");
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 bg-slate-50 min-h-screen animate-in fade-in duration-500">
      <div className="max-w-5xl mx-auto space-y-4">
        {/* THANH ĐIỀU HƯỚNG */}
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
        </div>

        <div className="bg-white border-2 border-slate-300 shadow-2xl rounded-sm overflow-hidden">
          <div className="bg-[#2d5a27] p-3 text-white font-bold text-center uppercase text-sm tracking-wider">
            Tư vấn & Nhắc lịch tiêm chủng
          </div>

          <div className="p-6 space-y-8">
            {/* Search Area */}
            <div className="flex flex-col sm:flex-row gap-4 items-end bg-slate-50 p-4 border border-slate-200 rounded shadow-inner">
              <div className="flex-1 w-full">
                <label className="block text-[10px] font-bold text-slate-500 uppercase mb-1">
                  Tra cứu Email:
                </label>
                <div className="relative">
                  <input
                    type="email"
                    className="w-full p-2.5 pl-10 border border-slate-400 rounded-sm text-sm outline-none focus:border-emerald-600 transition-all"
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
                className="w-full sm:w-auto px-8 py-2.5 bg-slate-800 text-white font-bold text-xs rounded-sm flex items-center justify-center gap-2"
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
                {/* Thông tin bệnh nhân */}
                <div className="flex items-center justify-between border-b pb-4 border-dashed border-slate-300">
                  <div className="flex gap-6 text-sm">
                    <span className="flex items-center gap-2 font-bold text-slate-700">
                      <User size={16} className="text-emerald-600" />{" "}
                      {data.hoTen}
                    </span>
                    <span className="flex items-center gap-2 text-slate-500">
                      <Phone size={16} /> {data.soDienThoai}
                    </span>
                  </div>
                </div>

                {/* Bảng 1: Lịch sử */}
                <section>
                  <h3 className="text-xs font-bold text-blue-800 mb-3 uppercase italic underline flex items-center gap-2">
                    1. Lịch sử tiêm chủng:
                  </h3>
                  <div className="border border-slate-400 rounded-sm overflow-hidden">
                    <table className="w-full text-[11px] text-left">
                      <thead className="bg-slate-100 border-b border-slate-400">
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
                            <tr key={index}>
                              <td className="p-2 border-r border-slate-300 text-center">
                                {index + 1}
                              </td>
                              <td className="p-2 border-r border-slate-300">
                                {item.ngayTiem}
                              </td>
                              <td className="p-2">{item.tenVacXin}</td>
                            </tr>
                          ))
                        ) : (
                          <tr>
                            <td
                              colSpan="3"
                              className="p-4 text-center text-slate-400 italic"
                            >
                              Chưa có lịch sử tiêm
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
                    2. Lịch tiêm dự kiến:
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
                            <tr key={index} className="bg-white">
                              <td className="p-2 border-r border-slate-300 text-center">
                                {index + 1}
                              </td>
                              <td className="p-2 border-r border-slate-300 font-bold text-red-600">
                                {item.ngayDuKien}
                              </td>
                              <td className="p-2 border-r border-slate-300">
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
                        Đã hoàn thành tất cả các mũi tiêm.
                      </p>
                    </div>
                  )}
                </section>

                {/* PHẦN TƯ VẤN VÀ GỬI EMAIL */}
                <div className="bg-emerald-50 p-5 border border-emerald-200 rounded-sm space-y-4">
                  {/* Bộ nút Template */}
                  <div className="space-y-2">
                    <label className="flex items-center gap-2 text-[10px] font-bold text-emerald-800 uppercase italic">
                      <MessageCircle size={14} /> Chọn nội dung tư vấn nhanh:
                    </label>
                    <div className="flex flex-wrap gap-2">
                      {CONSULT_TEMPLATES.map((tmpl, idx) => (
                        <button
                          key={idx}
                          onClick={() => setLoiNhan(tmpl.content(data.hoTen))}
                          className="px-3 py-1.5 bg-white border border-emerald-300 text-emerald-700 text-[10px] font-bold rounded-md hover:bg-emerald-600 hover:text-white transition-all shadow-sm active:scale-95"
                        >
                          + {tmpl.title}
                        </button>
                      ))}
                    </div>
                  </div>

                  <textarea
                    className="w-full p-4 border border-emerald-300 rounded-sm text-sm outline-none focus:ring-2 focus:ring-emerald-500 min-h-[120px] shadow-sm"
                    placeholder="Nội dung tư vấn hoặc lời nhắn gửi đến khách hàng..."
                    value={loiNhan}
                    onChange={(e) => setLoiNhan(e.target.value)}
                  />

                  <div className="flex flex-col sm:flex-row justify-center gap-4 pt-2">
                    <button
                      onClick={handleSendReminder}
                      disabled={sending}
                      className="px-12 py-3 bg-emerald-600 text-white font-bold rounded-sm shadow-lg hover:bg-emerald-700 flex items-center justify-center gap-2 disabled:bg-slate-300 transition-all"
                    >
                      {sending ? (
                        <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent" />
                      ) : (
                        <Mail size={18} />
                      )}
                      {sending ? "ĐANG GỬI..." : "XÁC NHẬN GỬI TƯ VẤN"}
                    </button>
                    <button
                      onClick={() => setData(null)}
                      className="px-12 py-3 bg-white text-slate-600 font-bold rounded-sm border border-slate-300 hover:bg-slate-100 transition-colors"
                    >
                      <XCircle size={18} /> HỦY
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default VaccinationReminder;
