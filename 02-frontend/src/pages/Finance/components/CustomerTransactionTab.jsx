import React, { useState, useEffect, useCallback } from "react";
import {
  ReceiptText,
  Printer,
  Eye,
  CheckCircle2,
  Clock,
  AlertCircle,
  CreditCard,
  Banknote,
  TrendingUp,
  Loader2,
  Check,
  ChevronLeft,
  ChevronRight,
  Inbox,
  X,
} from "lucide-react";
import financeApi from "../../../api/financeApi";
import toast from "react-hot-toast";

const CustomerTransactionTab = ({ searchTerm, dateRange }) => {
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState([]);
  const [summary, setSummary] = useState(null);

  // --- PHÂN TRANG ---
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize] = useState(8);

  // --- MODAL XÁC NHẬN THANH TOÁN (MỚI) ---
  const [confirmModal, setConfirmModal] = useState({
    isOpen: false,
    maHoaDon: null,
    tenKhachHang: "",
    soTien: 0,
    phuongThuc: "Tiền mặt",
  });

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [transRes, summaryRes] = await Promise.all([
        financeApi.getCustomerTransactions({
          page,
          size: pageSize,
          search: searchTerm,
          startDate: dateRange.start,
          endDate: dateRange.end,
        }),
        financeApi.getFinanceSummary(),
      ]);

      setTransactions(transRes.data || []);
      setTotalPages(transRes.totalPages || 1);
      setSummary(summaryRes);
    } catch (error) {
      toast.error(error.message || "Không thể tải dữ liệu");
    } finally {
      setLoading(false);
    }
  }, [page, searchTerm, dateRange, pageSize]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  useEffect(() => {
    setPage(1);
  }, [searchTerm, dateRange]);

  // --- LOGIC XỬ LÝ MODAL ---
  const openConfirmModal = (item) => {
    setConfirmModal({
      isOpen: true,
      maHoaDon: item.maHoaDon,
      tenKhachHang: item.tenKhachHang,
      soTien: item.gia,
      phuongThuc: "Tiền mặt",
    });
  };

  const executeConfirm = async () => {
    try {
      await financeApi.confirmPayment(
        confirmModal.maHoaDon,
        confirmModal.phuongThuc,
      );
      toast.success("Đã xác nhận thanh toán!");
      setConfirmModal({ ...confirmModal, isOpen: false });
      loadData();
    } catch (error) {
      toast.error(error.message || "Lỗi khi xác nhận giao dịch");
    }
  };

  // 3. HELPER: TRẠNG THÁI BADGE (Giữ nguyên logic của bạn)
  const getStatusBadge = (status) => {
    const baseClass =
      "flex items-center gap-1.5 px-3 py-1 rounded-full text-[10px] font-black uppercase border";
    if (status === "Đã thanh toán")
      return (
        <span
          className={`${baseClass} bg-emerald-50 text-emerald-600 border-emerald-100`}
        >
          <CheckCircle2 size={12} /> {status}
        </span>
      );
    if (status === "Chờ thanh toán")
      return (
        <span
          className={`${baseClass} bg-amber-50 text-amber-600 border-amber-100`}
        >
          <Clock size={12} /> {status}
        </span>
      );
    return (
      <span className={`${baseClass} bg-rose-50 text-rose-600 border-rose-100`}>
        <AlertCircle size={12} /> {status}
      </span>
    );
  };

  return (
    <div className="space-y-6 animate-in fade-in slide-in-from-bottom-2 duration-500">
      {/* 1. THẺ THỐNG KÊ (Responsive 2 cột) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <StatsCard
          label="Doanh thu hôm nay"
          value={`${summary?.totalRevenueToday?.toLocaleString() || 0} đ`}
          trend="+12.5%"
          color="emerald"
        />
        <StatsCard
          label="Hóa đơn chờ"
          value={`${summary?.pendingInvoiceCount || 0} đơn`}
          trend="Cần thu tiền"
          color="amber"
        />
      </div>

      {/* 2. BẢNG NHẬT KÝ */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden flex flex-col">
        <div className="p-6 border-b border-slate-50 flex items-center justify-between">
          <h3 className="font-black text-slate-800 flex items-center gap-2 uppercase text-xs tracking-wider">
            <ReceiptText size={18} className="text-emerald-500" /> Nhật ký giao
            dịch khách hàng
          </h3>
          {loading && (
            <Loader2 className="animate-spin text-emerald-500" size={20} />
          )}
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[900px]">
            <thead className="bg-slate-50/50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-4">Hóa đơn / Ngày</th>
                <th className="px-6 py-4">Khách hàng</th>
                <th className="px-6 py-4">Vắc-xin</th>
                <th className="px-6 py-4 text-center">Trạng thái</th>
                <th className="px-8 py-4 text-right">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm text-slate-600">
              {transactions.length > 0 ? (
                transactions.map((item) => (
                  <tr
                    key={item.maHoaDon}
                    className="hover:bg-slate-50/40 transition-colors group"
                  >
                    <td className="px-8 py-5">
                      <p className="text-slate-400 text-[10px] font-bold">
                        {new Date(item.ngay).toLocaleDateString("vi-VN")}
                      </p>
                      <p className="text-slate-800 font-black tracking-tight uppercase group-hover:text-emerald-600 transition-colors">
                        #{item.maHoaDon.substring(0, 8)}
                      </p>
                    </td>
                    <td className="px-6 py-5 font-bold text-slate-700">
                      {item.tenKhachHang}
                    </td>
                    <td className="px-6 py-5">
                      <p className="text-xs">{item.tenVacXin}</p>
                      <p className="text-emerald-600 font-black">
                        {item.gia?.toLocaleString()} đ
                      </p>
                    </td>
                    <td className="px-6 py-5 flex justify-center">
                      {getStatusBadge(item.trangThai)}
                    </td>
                    <td className="px-8 py-5 text-right">
                      <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        {item.trangThai === "Chờ thanh toán" && (
                          <button
                            onClick={() => openConfirmModal(item)}
                            className="p-2.5 text-amber-500 hover:bg-amber-50 rounded-xl transition-all border border-amber-100 shadow-sm"
                            title="Xác nhận thu tiền"
                          >
                            <Check size={18} strokeWidth={3} />
                          </button>
                        )}
                        <button className="p-2.5 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all border border-slate-100 shadow-sm">
                          <Printer size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="6"
                    className="py-24 text-center italic text-slate-400"
                  >
                    Không có giao dịch phù hợp
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* PHÂN TRANG */}
        <div className="p-6 bg-slate-50/50 border-t border-slate-50 flex items-center justify-between">
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
            Trang {page} / {totalPages}
          </p>
          <div className="flex gap-2">
            <button
              disabled={page === 1}
              onClick={() => setPage((p) => p - 1)}
              className="p-2 bg-white border border-slate-200 rounded-xl disabled:opacity-30"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              disabled={page === totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="p-2 bg-white border border-slate-200 rounded-xl disabled:opacity-30"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </div>

      {/* --- CUSTOM CONFIRM MODAL --- */}
      {confirmModal.isOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 animate-in fade-in duration-200">
          <div className="bg-white w-full max-w-md rounded-[2.5rem] shadow-2xl overflow-hidden animate-in zoom-in duration-300">
            <div className="p-8 text-center">
              <div className="w-16 h-16 bg-emerald-50 text-emerald-500 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-sm">
                <Banknote size={32} />
              </div>
              <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">
                Xác nhận thu tiền
              </h3>
              <p className="text-slate-500 text-sm mt-2 font-medium">
                Khách hàng:{" "}
                <span className="text-emerald-600 font-black italic">
                  {confirmModal.tenKhachHang}
                </span>
              </p>

              <div className="mt-6 p-4 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                  Số tiền cần thu
                </p>
                <p className="text-2xl font-black text-slate-800">
                  {confirmModal.soTien?.toLocaleString()} đ
                </p>
              </div>

              {/* Lựa chọn phương thức thanh toán nhanh */}
              <div className="mt-6 grid grid-cols-2 gap-2">
                {["Tiền mặt", "Chuyển khoản"].map((method) => (
                  <button
                    key={method}
                    onClick={() =>
                      setConfirmModal({ ...confirmModal, phuongThuc: method })
                    }
                    className={`py-3 rounded-xl text-[10px] font-black uppercase transition-all border ${confirmModal.phuongThuc === method ? "bg-slate-800 text-white border-slate-800" : "bg-white text-slate-400 border-slate-100 hover:bg-slate-50"}`}
                  >
                    {method}
                  </button>
                ))}
              </div>
            </div>

            <div className="flex border-t border-slate-50">
              <button
                onClick={() =>
                  setConfirmModal({ ...confirmModal, isOpen: false })
                }
                className="flex-1 py-5 text-xs font-black text-slate-400 hover:bg-slate-50 uppercase tracking-widest"
              >
                Hủy
              </button>
              <button
                onClick={executeConfirm}
                className="flex-1 py-5 bg-emerald-600 text-white text-xs font-black hover:bg-emerald-700 uppercase tracking-widest flex items-center justify-center gap-2"
              >
                <Check size={16} strokeWidth={3} /> Xác nhận
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// Component StatsCard giữ nguyên
const StatsCard = ({ label, value, trend, color }) => {
  const colors = {
    emerald: "text-emerald-600 bg-emerald-50",
    amber: "text-amber-600 bg-amber-50",
    blue: "text-blue-600 bg-blue-50",
  };
  return (
    <div className="bg-white p-6 rounded-[2.5rem] border border-slate-100 shadow-sm flex flex-col justify-between h-36 hover:shadow-lg transition-all group">
      <div className="flex justify-between items-start">
        <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none group-hover:text-slate-600">
          {label}
        </p>
        <div className={`p-2 rounded-xl ${colors[color]} opacity-80`}>
          <TrendingUp size={16} />
        </div>
      </div>
      <div>
        <p className="text-2xl font-black text-slate-800 tracking-tight mb-1 italic">
          {value}
        </p>
        <span
          className={`text-[9px] font-black px-2 py-0.5 rounded-md uppercase tracking-tighter ${colors[color]}`}
        >
          {trend}
        </span>
      </div>
    </div>
  );
};

export default CustomerTransactionTab;
