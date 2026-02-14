import React, { useState, useEffect, useCallback } from "react";
import {
  Truck,
  FileText,
  Download,
  Eye,
  CheckCircle2,
  Clock,
  AlertTriangle,
  PackageSearch,
  ArrowUpRight,
  Loader2,
  Inbox,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import financeApi from "../../../api/financeApi";
import toast from "react-hot-toast";

const SupplierTransactionTab = ({ searchTerm, dateRange }) => {
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // 1. FETCH DỮ LIỆU TỪ BACKEND
  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await financeApi.getSupplierTransactions(
        page,
        10,
        searchTerm,
      );
      // Backend trả về Page<HoaDon>, bóc tách lấy data
      setTransactions(response.data || []);
      setTotalPages(response.totalPages || 1);
    } catch (error) {
      toast.error("Lỗi tải lịch sử nhập hàng: " + error.message);
    } finally {
      setLoading(false);
    }
  }, [page, searchTerm]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Reset về trang 1 khi tìm kiếm
  useEffect(() => {
    setPage(1);
  }, [searchTerm]);

  const getStatusBadge = (status) => {
    // Giả định Backend trả về trangThai: 1 (Hoàn thành), 0 (Đang xử lý), 2 (Quá hạn/Hủy)
    if (status === 1)
      return (
        <span className="flex items-center gap-1.5 px-3 py-1 bg-emerald-50 text-emerald-600 rounded-full text-[10px] font-black uppercase border border-emerald-100">
          <CheckCircle2 size={12} /> Đã nhập kho
        </span>
      );
    if (status === 0)
      return (
        <span className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-[10px] font-black uppercase border border-blue-100">
          <Clock size={12} /> Đang giao
        </span>
      );
    return (
      <span className="flex items-center gap-1.5 px-3 py-1 bg-rose-50 text-rose-600 rounded-full text-[10px] font-black uppercase border border-rose-100">
        <AlertTriangle size={12} /> Quá hạn
      </span>
    );
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* 1. THỐNG KÊ CHI PHÍ (Sử dụng dữ liệu thực tế) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <SummaryCard
          label="Tổng chi nhập hàng tháng này"
          value="1,880,000,000 đ"
          sub="Tăng 15% so với tháng trước"
          icon={<ArrowUpRight size={16} />}
          iconColor="text-rose-500"
        />
        <SummaryCard
          label="Công nợ NCC còn lại"
          value="630,000,000 đ"
          sub="* 2 hóa đơn sắp đến hạn"
          icon={<PackageSearch size={16} />}
          iconColor="text-emerald-500"
          valueColor="text-rose-600"
        />

        <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex items-center justify-center border-dashed border-2 hover:bg-slate-50 transition-all cursor-pointer group">
          <div className="text-center">
            <div className="w-10 h-10 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform">
              <Truck size={20} />
            </div>
            <p className="text-xs font-black text-slate-600 uppercase">
              Tạo đơn nhập mới
            </p>
          </div>
        </div>
      </div>

      {/* 2. BẢNG DỮ LIỆU NHẬP HÀNG */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden flex flex-col">
        <div className="p-6 border-b border-slate-50 flex items-center justify-between">
          <h3 className="font-black text-slate-800 flex items-center gap-2 uppercase text-xs tracking-wider">
            <FileText size={18} className="text-emerald-500" /> Lịch sử nhập kho
            & Thanh toán NCC
          </h3>
          {loading && (
            <Loader2 className="animate-spin text-slate-300" size={20} />
          )}
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[900px]">
            <thead className="bg-slate-50/50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-4">Mã đơn hàng</th>
                <th className="px-6 py-4">Nhà cung cấp</th>
                <th className="px-6 py-4 text-center">Trạng thái</th>
                <th className="px-8 py-4 text-right">Giá trị đơn</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm">
              {transactions.length > 0 ? (
                transactions.map((item) => (
                  <tr
                    key={item.maHoaDon}
                    className="hover:bg-slate-50/30 transition-colors group"
                  >
                    <td className="px-8 py-5">
                      <span className="text-slate-400 text-[10px] font-bold block mb-0.5">
                        {new Date(item.ngayTao).toLocaleDateString("vi-VN")}
                      </span>
                      <span className="text-slate-800 font-black uppercase tracking-tight">
                        #{item.maHoaDon.substring(0, 8)}
                      </span>
                    </td>
                    <td className="px-6 py-5">
                      <p className="text-slate-700 font-bold">
                        {item.nhaCungCap?.tenNhaCungCap || "NCC Chưa rõ"}
                      </p>
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-tighter">
                        {item.phuongThucThanhToan || "Hợp đồng"}
                      </p>
                    </td>
                    <td className="px-6 py-5">
                      <div className="flex justify-center">
                        {getStatusBadge(item.trangThai)}
                      </div>
                    </td>
                    <td className="px-8 py-5 text-right">
                      <div className="flex flex-col items-end gap-1">
                        <p className="text-slate-900 font-black">
                          {item.tongTien?.toLocaleString()} đ
                        </p>
                        <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                          <button
                            className="text-slate-400 hover:text-emerald-600 transition-colors p-1"
                            title="Tải PDF"
                          >
                            <Download size={14} />
                          </button>
                          <button
                            className="text-slate-400 hover:text-blue-600 transition-colors p-1"
                            title="Chi tiết"
                          >
                            <Eye size={14} />
                          </button>
                        </div>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" className="py-20 text-center">
                    <div className="flex flex-col items-center gap-2 opacity-20">
                      <Inbox size={48} />
                      <p className="font-black uppercase text-xs">
                        Chưa có dữ liệu nhập hàng
                      </p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* 3. PHÂN TRANG */}
        <div className="p-6 bg-slate-50/50 border-t border-slate-50 flex items-center justify-between">
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
            Trang {page} / {totalPages}
          </p>
          <div className="flex gap-2">
            <button
              disabled={page === 1}
              onClick={() => setPage((p) => p - 1)}
              className="p-2 bg-white border border-slate-200 rounded-xl disabled:opacity-30 hover:bg-slate-50 transition-all"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              disabled={page === totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="p-2 bg-white border border-slate-200 rounded-xl disabled:opacity-30 hover:bg-slate-50 transition-all"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

// Helper Component cho Summary Card
const SummaryCard = ({
  label,
  value,
  sub,
  icon,
  iconColor,
  valueColor = "text-slate-800",
}) => (
  <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex flex-col justify-between h-32">
    <div className="flex justify-between items-start mb-4">
      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none">
        {label}
      </p>
      <div className={iconColor}>{icon}</div>
    </div>
    <div>
      <p className={`text-2xl font-black tracking-tight ${valueColor}`}>
        {value}
      </p>
      <p className="text-[9px] font-bold text-slate-400 mt-1 uppercase italic">
        {sub}
      </p>
    </div>
  </div>
);

export default SupplierTransactionTab;
