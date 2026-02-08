import React, { useState, useEffect } from "react";
import supportApi from "../../api/supportApi";
import { useNavigate } from "react-router-dom";
import { BellRing, SendHorizontal } from "lucide-react";
import toast from "react-hot-toast";
import {
  CheckCircle,
  Clock,
  AlertCircle,
  Trash2,
  Search,
  User,
  Phone,
} from "lucide-react";

const AdminFeedbackManagement = () => {
  const navigate = useNavigate();
  const [feedbacks, setFeedbacks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = async () => {
    setLoading(true);
    try {
      const data = await supportApi.adminGetAllHighLevelFeedbacks();
      setFeedbacks(data || []);
    } catch (error) {
      toast.error(
        "Lỗi tải danh sách phản hồi: " + (error.message || "Lỗi kết nối"),
      );
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await supportApi.adminUpdateHighLevelStatus(id, newStatus);
      toast.success("Đã cập nhật trạng thái!");
      fetchFeedbacks();
    } catch (error) {
      toast.error(error.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Xác nhận xóa vĩnh viễn phản hồi này?")) return;
    try {
      await supportApi.adminDeleteHighLevelFeedback(id);
      toast.success("Đã xóa bản ghi.");
      fetchFeedbacks();
    } catch (error) {
      toast.error(error.message);
    }
  };

  const filteredData = feedbacks.filter((item) => {
    const matchesStatus =
      filterStatus === "all"
        ? true
        : item.trangThai.toString() === filterStatus;
    const matchesSearch =
      item.tenBenhNhan.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.sdtBenhNhan.includes(searchTerm);
    return matchesStatus && matchesSearch;
  });

  const getStatusInfo = (status) => {
    const statusMap = {
      0: {
        label: "Mới",
        color: "bg-blue-100 text-blue-700",
        icon: <AlertCircle size={14} />,
      },
      1: {
        label: "Đang xử lý",
        color: "bg-amber-100 text-amber-700",
        icon: <Clock size={14} />,
      },
      2: {
        label: "Đã giải quyết",
        color: "bg-emerald-100 text-emerald-700",
        icon: <CheckCircle size={14} />,
      },
    };
    return (
      statusMap[status] || { label: "KĐ", color: "bg-slate-100", icon: null }
    );
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* PAGE HEADER: Đã dọn dẹp, chỉ giữ lại một cái duy nhất */}
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 tracking-tight">
            Quản lý Phản hồi cấp cao
          </h1>
          <p className="text-slate-500 text-sm">
            Xử lý các ý kiến đóng góp và khiếu nại từ khách hàng
          </p>
        </div>

        <div className="flex flex-col sm:flex-row items-center gap-3 w-full lg:w-auto">
          {/* NÚT NHẢY NHANH QUA NHẮC LỊCH TRÊN HEADER */}
          <button
            onClick={() => navigate("/admin/reminders")}
            className="flex items-center gap-2 px-4 py-2 bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-xl text-xs font-bold hover:bg-emerald-100 transition-all shadow-sm"
          >
            <BellRing size={14} /> ĐI TỚI NHẮC LỊCH
          </button>

          {/* Search bar */}
          <div className="relative w-full sm:w-64">
            <Search
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
              size={16}
            />
            <input
              type="text"
              placeholder="Tìm tên hoặc SĐT..."
              className="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500 transition-all"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          {/* Filter buttons */}
          <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl border border-slate-200">
            {[
              { v: "all", l: "Tất cả" },
              { v: "0", l: "Mới" },
              { v: "1", l: "Đang làm" },
              { v: "2", l: "Đã xong" },
            ].map((s) => (
              <button
                key={s.v}
                onClick={() => setFilterStatus(s.v)}
                className={`px-3 py-1.5 rounded-lg text-[11px] font-bold transition-all ${
                  filterStatus === s.v
                    ? "bg-white shadow-sm text-blue-600"
                    : "text-slate-500 hover:text-slate-700"
                }`}
              >
                {s.l}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* TABLE CONTENT */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left border-collapse">
            <thead className="bg-slate-50 text-slate-500 uppercase text-[10px] font-bold">
              <tr>
                <th className="p-4 border-b">Bệnh nhân</th>
                <th className="p-4 border-b">Nội dung phản hồi</th>
                <th className="p-4 border-b">Thời gian</th>
                <th className="p-4 border-b text-center">Trạng thái</th>
                <th className="p-4 border-b text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td
                    colSpan="5"
                    className="p-12 text-center text-slate-400 italic animate-pulse"
                  >
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : filteredData.length === 0 ? (
                <tr>
                  <td
                    colSpan="5"
                    className="p-12 text-center text-slate-400 font-medium"
                  >
                    {searchTerm
                      ? `Không tìm thấy kết quả cho "${searchTerm}"`
                      : "Không có phản hồi nào."}
                  </td>
                </tr>
              ) : (
                filteredData.map((item) => {
                  const status = getStatusInfo(item.trangThai);
                  // console.log("Dữ liệu phản hồi:", item);
                  return (
                    <tr
                      key={item.maPhanHoi}
                      className="hover:bg-slate-50/50 transition-colors"
                    >
                      <td className="p-4">
                        <div className="flex justify-end gap-2">
                          {/* NÚT NHẮC LỊCH RIÊNG CHO BỆNH NHÂN NÀY */}
                          <button
                            onClick={() =>
                              navigate(
                                `/admin/reminders?email=${item.emailBenhNhan}`,
                              )
                            } // Truyền email qua URL
                            className="p-1.5 text-blue-500 hover:bg-blue-50 rounded-lg transition-colors"
                            title="Phản hồi cho bệnh nhân này"
                          >
                            <SendHorizontal size={18} />
                          </button>
                          <span className="font-bold text-slate-700 flex items-center gap-1">
                            <User size={14} className="text-slate-400" />{" "}
                            {item.tenBenhNhan}
                          </span>
                          <span className="text-[11px] text-slate-500 flex items-center gap-1">
                            <Phone size={12} className="text-slate-400" />{" "}
                            {item.sdtBenhNhan}
                          </span>
                        </div>
                      </td>
                      <td className="p-4">
                        <div className="max-w-xs sm:max-w-md">
                          <span className="text-[9px] font-extrabold text-blue-600 bg-blue-50 px-2 py-0.5 rounded border border-blue-100 mb-1 inline-block">
                            {item.tenLoaiPhanHoi}
                          </span>
                          <p className="text-slate-600 line-clamp-2 text-xs">
                            {item.noiDung}
                          </p>
                        </div>
                      </td>
                      <td className="p-4 text-slate-500 text-xs whitespace-nowrap">
                        {item.thoiGianGui}
                      </td>
                      <td className="p-4 text-center">
                        <div
                          className={`inline-flex items-center gap-1 px-3 py-1 rounded-full font-bold text-[10px] border ${status.color}`}
                        >
                          {status.icon} {status.label}
                        </div>
                      </td>
                      <td className="p-4">
                        <div className="flex justify-end gap-2">
                          {item.trangThai === 0 && (
                            <button
                              onClick={() =>
                                handleUpdateStatus(item.maPhanHoi, 1)
                              }
                              className="px-3 py-1.5 bg-amber-500 text-white text-[10px] font-bold rounded-lg hover:bg-amber-600 shadow-sm transition-all"
                            >
                              TIẾP NHẬN
                            </button>
                          )}
                          {item.trangThai !== 2 && (
                            <button
                              onClick={() =>
                                handleUpdateStatus(item.maPhanHoi, 2)
                              }
                              className="px-3 py-1.5 bg-emerald-500 text-white text-[10px] font-bold rounded-lg hover:bg-emerald-600 shadow-sm transition-all"
                            >
                              HOÀN TẤT
                            </button>
                          )}
                          <button
                            onClick={() => handleDelete(item.maPhanHoi)}
                            className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                            title="Xóa phản hồi"
                          >
                            <Trash2 size={18} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminFeedbackManagement;
