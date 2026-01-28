import React, { useState, useEffect } from "react";
import {
  FileText,
  MapPin,
  Clock,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  Filter,
  Trash2,
} from "lucide-react";
import { toast } from "react-hot-toast";
import inventoryApi from "../../../api/inventoryApi";

const ExportHistory = () => {
  const [history, setHistory] = useState([]);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchHistory = async (currentPage) => {
    setLoading(true);
    try {
      // 1. Chuẩn bị tham số ngày (Gửi kèm giờ để lấy trọn vẹn ngày đó)
      const start = startDate ? `${startDate}T00:00:00` : null;
      const end = endDate ? `${endDate}T23:59:59` : null;

      const res = await inventoryApi.getExportHistory(
        currentPage,
        10,
        start,
        end,
      );

      setHistory(res.content || []);
      setTotalPages(res.totalPages || 0);
      setTotalElements(res.totalElements || 0);
    } catch (error) {
      toast.error("Lỗi tải lịch sử: " + (error.message || "Lỗi hệ thống"));
    } finally {
      setLoading(false);
    }
  };

  // Gọi lại khi trang thay đổi
  useEffect(() => {
    fetchHistory(page);
  }, [page]);

  const handleApplyFilter = (e) => {
    e.preventDefault();
    setPage(0); // Quay về trang đầu khi lọc
    fetchHistory(0);
  };

  const handleClearFilter = () => {
    setStartDate("");
    setEndDate("");
    setPage(0);
    // Lưu ý: Sau khi set state, useEffect sẽ tự gọi fetchHistory do page thay đổi hoặc bạn gọi thủ công
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="space-y-4 animate-in fade-in duration-500">
      {/* 1. FILTER BAR */}
      <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-100">
        <form
          onSubmit={handleApplyFilter}
          className="flex flex-wrap items-end gap-4"
        >
          <div className="flex-1 min-w-[200px] space-y-1">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
              Từ ngày
            </label>
            <input
              type="date"
              className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium focus:ring-2 focus:ring-blue-500 outline-none"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>
          <div className="flex-1 min-w-[200px] space-y-1">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
              Đến ngày
            </label>
            <input
              type="date"
              className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium focus:ring-2 focus:ring-blue-500 outline-none"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>
          <div className="flex gap-2">
            <button
              type="submit"
              className="flex items-center gap-2 bg-slate-800 text-white px-6 py-2.5 rounded-xl font-bold text-sm hover:bg-slate-900 transition-all active:scale-95"
            >
              <Filter size={16} /> Lọc dữ liệu
            </button>
            {(startDate || endDate) && (
              <button
                type="button"
                onClick={handleClearFilter}
                className="flex items-center gap-2 bg-slate-100 text-slate-500 px-4 py-2.5 rounded-xl font-bold text-sm hover:bg-slate-200 transition-all"
              >
                <Trash2 size={16} />
              </button>
            )}
          </div>
        </form>
      </div>

      {/* 2. TABLE HEADER */}
      <div className="flex justify-between items-center px-1">
        <h3 className="text-lg font-bold text-slate-700 flex items-center gap-2">
          <FileText size={20} className="text-blue-600" /> Nhật ký phiếu xuất
        </h3>
        <button
          onClick={() => fetchHistory(page)}
          disabled={loading}
          className="p-2 hover:bg-slate-100 rounded-full transition-all text-slate-500"
        >
          <RefreshCw size={18} className={loading ? "animate-spin" : ""} />
        </button>
      </div>

      {/* 3. DATA TABLE */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 border-b border-slate-100 font-bold text-slate-500 uppercase">
              <tr>
                <th className="p-4">Mã phiếu</th>
                <th className="p-4">Vắc-xin & Số lô</th>
                <th className="p-4">Số lượng</th>
                <th className="p-4">Đơn vị nhận</th>
                <th className="p-4">Ngày xuất</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {loading ? (
                <tr>
                  <td colSpan="5" className="p-10 text-center text-slate-400">
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : history.length > 0 ? (
                history.map((item) => (
                  <tr
                    key={item.maPhieuXuat}
                    className="hover:bg-slate-50/50 transition-colors"
                  >
                    <td className="p-4 font-mono font-bold text-blue-600 italic">
                      #{item.soPhieuXuat}
                    </td>
                    <td className="p-4">
                      <p className="font-bold text-slate-800">
                        {item.tenVacXin}
                      </p>
                      <p className="text-[10px] text-slate-400 font-mono">
                        Lô: {item.soLoThucTe}
                      </p>
                    </td>
                    <td className="p-4 font-bold text-slate-700">
                      {item.soLuongDaXuat}{" "}
                      <span className="text-[10px] text-slate-400 font-normal ml-0.5">
                        liều
                      </span>
                    </td>
                    <td className="p-4 text-slate-600 font-medium">
                      <div className="flex items-center gap-1.5">
                        <MapPin size={14} className="text-slate-300" />
                        {item.noiNhan}
                      </div>
                    </td>
                    <td className="p-4 text-slate-500">
                      <div className="flex flex-col">
                        <span className="font-medium">
                          {new Date(item.ngayXuat).toLocaleDateString("vi-VN")}
                        </span>
                        <span className="text-[10px] text-slate-300">
                          {new Date(item.ngayXuat).toLocaleTimeString("vi-VN")}
                        </span>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="5"
                    className="p-10 text-center text-slate-400 italic"
                  >
                    Không có giao dịch nào trong khoảng thời gian này.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* 4. PAGINATION */}
        {totalPages > 0 && (
          <div className="p-4 bg-slate-50/50 border-t border-slate-100 flex flex-col md:flex-row justify-between items-center gap-4">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-widest">
              Trang {page + 1} / {totalPages} — {totalElements} phiếu
            </span>
            <div className="flex items-center gap-1">
              <button
                disabled={page === 0}
                onClick={() => handlePageChange(page - 1)}
                className="p-2 hover:bg-white rounded-lg transition-all text-slate-400 disabled:opacity-30"
              >
                <ChevronLeft size={16} />
              </button>

              {[...Array(totalPages)]
                .map((_, i) => (
                  <button
                    key={i}
                    onClick={() => handlePageChange(i)}
                    className={`w-8 h-8 rounded-lg text-xs font-bold transition-all ${
                      page === i
                        ? "bg-blue-600 text-white shadow-md shadow-blue-100"
                        : "hover:bg-white text-slate-500"
                    }`}
                  >
                    {i + 1}
                  </button>
                ))
                .slice(Math.max(0, page - 2), Math.min(totalPages, page + 3))}

              <button
                disabled={page >= totalPages - 1}
                onClick={() => handlePageChange(page + 1)}
                className="p-2 hover:bg-white rounded-lg transition-all text-slate-400 disabled:opacity-30"
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ExportHistory;
