import React, { useState, useEffect } from "react";
import medicalApi from "../../api/medicalApi";
import toast from "react-hot-toast";
import {
  CheckCircle,
  Clock,
  AlertCircle,
  Trash2,
  Filter,
  Search,
  User,
  Phone,
} from "lucide-react";

const AdminFeedbackManagement = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState("all"); // all, 0, 1, 2

  useEffect(() => {
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = async () => {
    setLoading(true);
    try {
      const data = await medicalApi.adminGetAllHighLevelFeedbacks();
      setFeedbacks(data || []);
    } catch (error) {
      toast.error("Lỗi tải danh sách phản hồi: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await medicalApi.adminUpdateHighLevelStatus(id, newStatus);
      toast.success("Đã cập nhật trạng thái!");
      fetchFeedbacks(); // Reload dữ liệu
    } catch (error) {
      toast.error(error.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Xác nhận xóa vĩnh viễn phản hồi này?")) return;
    try {
      await medicalApi.adminDeleteHighLevelFeedback(id);
      toast.success("Đã xóa bản ghi.");
      fetchFeedbacks();
    } catch (error) {
      toast.error(error.message);
    }
  };

  // Lọc dữ liệu hiển thị
  const filteredData = feedbacks.filter((item) =>
    filterStatus === "all" ? true : item.trangThai.toString() === filterStatus,
  );

  const getStatusInfo = (status) => {
    switch (status) {
      case 0:
        return {
          label: "Mới",
          color: "bg-blue-100 text-blue-700",
          icon: <AlertCircle size={14} />,
        };
      case 1:
        return {
          label: "Đang xử lý",
          color: "bg-amber-100 text-amber-700",
          icon: <Clock size={14} />,
        };
      case 2:
        return {
          label: "Đã giải quyết",
          color: "bg-emerald-100 text-emerald-700",
          icon: <CheckCircle size={14} />,
        };
      default:
        return { label: "KĐ", color: "bg-slate-100", icon: null };
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Quản lý Phản hồi cấp cao
          </h1>
          <p className="text-slate-500 text-sm">
            Xem và xử lý các ý kiến đóng góp từ bệnh nhân
          </p>
        </div>

        <div className="flex items-center gap-2 bg-slate-100 p-1 rounded-xl border border-slate-200">
          <button
            onClick={() => setFilterStatus("all")}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${filterStatus === "all" ? "bg-white shadow-sm text-blue-600" : "text-slate-500"}`}
          >
            Tất cả
          </button>
          <button
            onClick={() => setFilterStatus("0")}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${filterStatus === "0" ? "bg-white shadow-sm text-blue-600" : "text-slate-500"}`}
          >
            Mới
          </button>
          <button
            onClick={() => setFilterStatus("1")}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${filterStatus === "1" ? "bg-white shadow-sm text-blue-600" : "text-slate-500"}`}
          >
            Đang xử lý
          </button>
        </div>
      </div>

      {/* Main Content Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left border-collapse">
            <thead className="bg-slate-50 text-slate-500 uppercase text-[10px] font-bold">
              <tr>
                <th className="p-4 border-b">Bệnh nhân</th>
                <th className="p-4 border-b">Loại & Nội dung</th>
                <th className="p-4 border-b">Thời gian gửi</th>
                <th className="p-4 border-b text-center">Trạng thái</th>
                <th className="p-4 border-b text-right">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td
                    colSpan="5"
                    className="p-12 text-center text-slate-400 italic"
                  >
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : filteredData.length === 0 ? (
                <tr>
                  <td colSpan="5" className="p-12 text-center text-slate-400">
                    Không có phản hồi nào cần xử lý.
                  </td>
                </tr>
              ) : (
                filteredData.map((item) => {
                  const status = getStatusInfo(item.trangThai);
                  return (
                    <tr
                      key={item.maPhanHoi}
                      className="hover:bg-slate-50 transition-colors"
                    >
                      <td className="p-4">
                        <div className="flex flex-col">
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
                          <span className="text-[10px] font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded-md mb-1 inline-block uppercase">
                            {item.tenLoaiPhanHoi}
                          </span>
                          <p className="text-slate-600 line-clamp-2">
                            {item.noiDung}
                          </p>
                        </div>
                      </td>
                      <td className="p-4 text-slate-500 whitespace-nowrap">
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
                          {/* Nút Tiếp nhận (Chỉ hiện khi trạng thái là Mới) */}
                          {item.trangThai === 0 && (
                            <button
                              onClick={() =>
                                handleUpdateStatus(item.maPhanHoi, 1)
                              }
                              className="px-3 py-1.5 bg-amber-500 text-white text-[10px] font-bold rounded-lg hover:bg-amber-600"
                            >
                              TIẾP NHẬN
                            </button>
                          )}
                          {/* Nút Hoàn tất (Chỉ hiện khi chưa giải quyết) */}
                          {item.trangThai !== 2 && (
                            <button
                              onClick={() =>
                                handleUpdateStatus(item.maPhanHoi, 2)
                              }
                              className="px-3 py-1.5 bg-emerald-500 text-white text-[10px] font-bold rounded-lg hover:bg-emerald-600"
                            >
                              HOÀN TẤT
                            </button>
                          )}
                          <button
                            onClick={() => handleDelete(item.maPhanHoi)}
                            className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
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
