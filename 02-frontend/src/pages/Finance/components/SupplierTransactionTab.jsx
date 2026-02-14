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
  Banknote,
  Check,
  X,
  Calendar,
  Zap,
  Hash,
  Layers,
} from "lucide-react";
import financeApi from "../../../api/financeApi";
import toast from "react-hot-toast";

const SupplierTransactionTab = ({ searchTerm }) => {
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState([]);
  const [summary, setSummary] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // --- STATE CHI TIẾT HÓA ĐƠN ---
  const [detailModal, setDetailModal] = useState({
    isOpen: false,
    data: null,
    loading: false,
  });

  // --- STATE XÁC NHẬN THANH TOÁN ---
  const [confirmModal, setConfirmModal] = useState({
    isOpen: false,
    maHoaDon: null,
    tenNCC: "",
    soTien: 0,
    phuongThuc: "Chuyển khoản",
  });

  // --- STATE TẠO ĐƠN NHẬP MỚI ---
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [transRes, summaryRes] = await Promise.all([
        financeApi.getSupplierTransactions(page, 10, searchTerm),
        financeApi.getSupplierSummary(),
      ]);
      setTransactions(transRes.data || []);
      setTotalPages(transRes.totalPages || 1);
      setSummary(summaryRes);
    } catch (error) {
      toast.error("Lỗi tải dữ liệu: " + error.message);
    } finally {
      setLoading(false);
    }
  }, [page, searchTerm]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleViewDetail = async (maHoaDon) => {
    setDetailModal({ isOpen: true, data: null, loading: true });
    try {
      const data = await financeApi.getSupplierDetail(maHoaDon);
      setDetailModal({ isOpen: true, data, loading: false });
    } catch (error) {
      toast.error("Không thể lấy thông tin chi tiết:  " + error.message);
      setDetailModal({ ...detailModal, isOpen: false });
    }
  };

  const executeConfirm = async () => {
    try {
      await financeApi.confirmSupplierPayment(
        confirmModal.maHoaDon,
        confirmModal.phuongThuc,
      );
      toast.success("Đã xác nhận thanh toán!");
      setConfirmModal({ ...confirmModal, isOpen: false });
      loadData();
    } catch (error) {
      toast.error(error.message || "Lỗi thanh toán");
    }
  };

  const getStatusBadge = (statusLabel, rawStatus) => {
    const baseClass =
      "flex items-center gap-1.5 px-3 py-1 rounded-full text-[10px] font-black uppercase border";
    if (rawStatus === 1)
      return (
        <span
          className={`${baseClass} bg-emerald-50 text-emerald-600 border-emerald-100`}
        >
          <CheckCircle2 size={12} /> {statusLabel}
        </span>
      );
    if (rawStatus === 0)
      return (
        <span
          className={`${baseClass} bg-blue-50 text-blue-600 border-blue-100`}
        >
          <Clock size={12} /> {statusLabel}
        </span>
      );
    return (
      <span className={`${baseClass} bg-rose-50 text-rose-600 border-rose-100`}>
        <AlertTriangle size={12} /> {statusLabel}
      </span>
    );
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* 1. THỐNG KÊ CHI PHÍ */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <SummaryCard
          label="Tổng chi nhập hàng tháng này"
          value={`${summary?.totalSpendingThisMonth?.toLocaleString() || 0} đ`}
          sub={summary?.spendingTrend || "Dữ liệu thực tế"}
          icon={<ArrowUpRight size={16} />}
          iconColor="text-rose-500"
        />
        <SummaryCard
          label="Hóa đơn chưa thanh toán"
          value={`${summary?.overdueInvoices || 0} đơn`}
          sub="Cần đối soát công nợ"
          icon={<PackageSearch size={16} />}
          iconColor="text-emerald-500"
          valueColor="text-rose-600"
        />

        <div
          onClick={() => setIsImportModalOpen(true)}
          className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex items-center justify-center border-dashed border-2 hover:bg-emerald-50 hover:border-emerald-200 transition-all cursor-pointer group"
        >
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

      {/* 2. BẢNG DỮ LIỆU */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden flex flex-col">
        <div className="p-6 border-b border-slate-50 flex items-center justify-between font-black text-slate-800 uppercase text-xs tracking-wider">
          <div className="flex items-center gap-2">
            <FileText size={18} className="text-emerald-500" /> Lịch sử nhập kho
            & NCC
          </div>
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
                <th className="px-8 py-4 text-right">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm">
              {transactions.map((item) => (
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
                      {item.tenNhaCungCap}
                    </p>
                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-tighter">
                      {item.phuongThucThanhToan || "Chờ đối soát"}
                    </p>
                  </td>
                  <td className="px-6 py-5 flex justify-center">
                    {getStatusBadge(item.trangThai, item.rawTrangThai)}
                  </td>
                  <td className="px-8 py-5 text-right">
                    <div className="flex flex-col items-end gap-1">
                      <p className="text-slate-900 font-black">
                        {item.tongTien?.toLocaleString()} đ
                      </p>
                      <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        {item.rawTrangThai === 0 && (
                          <button
                            onClick={() =>
                              setConfirmModal({
                                isOpen: true,
                                maHoaDon: item.maHoaDon,
                                tenNCC: item.tenNhaCungCap,
                                soTien: item.tongTien,
                                phuongThuc: "Chuyển khoản",
                              })
                            }
                            className="text-amber-500 hover:text-amber-600 p-1"
                          >
                            <Check size={16} strokeWidth={3} />
                          </button>
                        )}
                        <button
                          onClick={() => handleViewDetail(item.maHoaDon)}
                          className="text-slate-400 hover:text-blue-600 p-1"
                        >
                          <Eye size={16} />
                        </button>
                      </div>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* Phân trang... */}
      </div>

      {/* --- MODAL CHI TIẾT HÓA ĐƠN --- */}
      {detailModal.isOpen && (
        <div className="fixed inset-0 z-[110] flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4 animate-in fade-in duration-300">
          <div className="bg-white w-full max-w-4xl rounded-[3rem] shadow-2xl overflow-hidden animate-in zoom-in duration-300">
            {/* Modal Header */}
            <div className="p-8 border-b border-slate-50 flex justify-between items-center bg-slate-50/50">
              <div>
                <h3 className="text-2xl font-black text-slate-800 uppercase tracking-tight flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-600 text-white rounded-2xl flex items-center justify-center shadow-lg shadow-blue-200">
                    <FileText size={20} />
                  </div>
                  Chi tiết phiếu nhập kho
                </h3>
                <p className="text-[10px] text-slate-400 font-black mt-1 uppercase tracking-widest">
                  Mã giao dịch: {detailModal.data?.maHoaDon}
                </p>
              </div>
              <button
                onClick={() =>
                  setDetailModal({ ...detailModal, isOpen: false })
                }
                className="p-3 hover:bg-white hover:shadow-md rounded-2xl transition-all text-slate-400 hover:text-rose-500"
              >
                <X size={24} />
              </button>
            </div>

            <div className="p-8 max-h-[70vh] overflow-y-auto custom-scrollbar">
              {detailModal.loading ? (
                <div className="py-20 flex flex-col items-center gap-4">
                  <Loader2 className="animate-spin text-blue-600" size={40} />
                  <p className="text-xs font-black text-slate-400 uppercase">
                    Đang trích xuất dữ liệu kho...
                  </p>
                </div>
              ) : (
                <div className="space-y-8">
                  <div className="grid grid-cols-3 gap-6">
                    <InfoBox
                      label="Nhà cung cấp"
                      value={
                        transactions.find(
                          (t) => t.maHoaDon === detailModal.data?.maHoaDon,
                        )?.tenNhaCungCap
                      }
                      icon={<Truck size={14} />}
                    />
                    <InfoBox
                      label="Ngày nhập kho"
                      value={new Date(detailModal.data?.ngayTao).toLocaleString(
                        "vi-VN",
                      )}
                      icon={<Calendar size={14} />}
                    />
                    <div className="bg-blue-600 p-5 rounded-[2rem] text-white shadow-xl shadow-blue-100">
                      <p className="text-[10px] font-black uppercase opacity-60 mb-1 tracking-widest">
                        Tổng tiền thanh toán
                      </p>
                      <p className="text-2xl font-black">
                        {detailModal.data?.tongTien?.toLocaleString()} đ
                      </p>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <h4 className="text-[11px] font-black text-slate-400 uppercase tracking-[0.2em] flex items-center gap-2">
                      <Layers size={16} className="text-blue-500" /> Danh sách
                      lô hàng chi tiết
                    </h4>
                    <div className="border border-slate-100 rounded-[2rem] overflow-hidden shadow-sm">
                      <table className="w-full text-left border-collapse">
                        <thead className="bg-slate-50/80 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                          <tr>
                            <th className="px-6 py-4">Thông tin vắc-xin</th>
                            <th className="px-6 py-4 text-center">Số lô</th>
                            <th className="px-6 py-4 text-center">Số lượng</th>
                            <th className="px-8 py-4 text-right">
                              Đơn giá vốn
                            </th>
                            <th className="px-8 py-4 text-right">Thành tiền</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50 font-medium">
                          {detailModal.data?.danhSachLo?.map((lo, index) => (
                            <tr
                              key={index}
                              className="hover:bg-slate-50/50 transition-colors"
                            >
                              <td className="px-6 py-5">
                                <p className="font-black text-slate-800 text-sm uppercase">
                                  {lo.vacXin?.tenVacXin}
                                </p>
                                <div className="flex gap-2 mt-1">
                                  <span className="text-[9px] font-black text-blue-500 bg-blue-50 px-2 py-0.5 rounded uppercase">
                                    HSD:{" "}
                                    {new Date(
                                      lo.vacXin?.hanSuDung,
                                    ).toLocaleDateString("vi-VN")}
                                  </span>
                                  <span className="text-[9px] font-black text-slate-400 border border-slate-100 px-2 py-0.5 rounded uppercase">
                                    Xuất xứ: {lo.nuocSanXuat || "N/A"}
                                  </span>
                                </div>
                              </td>
                              <td className="px-6 py-5 text-center">
                                <span className="font-mono font-black text-slate-600 bg-slate-100 px-3 py-1 rounded-lg text-xs">
                                  {lo.soLo}
                                </span>
                              </td>
                              <td className="px-6 py-5 text-center font-black text-slate-700">
                                {lo.soLuong?.toLocaleString()}{" "}
                                <span className="text-[10px] text-slate-400 uppercase">
                                  liều
                                </span>
                              </td>
                              <td className="px-8 py-5 text-right font-bold text-slate-600">
                                {lo.giaNhap?.toLocaleString()} đ
                              </td>
                              <td className="px-8 py-5 text-right font-black text-slate-900">
                                {(lo.soLuong * lo.giaNhap).toLocaleString()} đ
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                  {/* Note & Signature Section... */}
                </div>
              )}
            </div>
            <div className="p-6 bg-slate-50/50 border-t border-slate-50 flex justify-between items-center px-10">
              <div className="flex items-center gap-2 text-emerald-600">
                <CheckCircle2 size={18} />
                <span className="text-[10px] font-black uppercase tracking-widest">
                  Chứng từ hợp lệ trên hệ thống
                </span>
              </div>
              <div className="flex gap-3">
                <button className="px-8 py-4 bg-slate-900 text-white rounded-2xl text-[10px] font-black uppercase tracking-widest hover:bg-black transition-all flex items-center gap-2 shadow-xl shadow-slate-200">
                  <FileText size={16} /> In hóa đơn PDF
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* MODAL XÁC NHẬN THANH TOÁN (Giữ nguyên cũ của bạn) */}
      {confirmModal.isOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 animate-in fade-in duration-200">
          <div className="bg-white w-full max-w-md rounded-[2.5rem] shadow-2xl overflow-hidden animate-in zoom-in duration-300">
            <div className="p-8 text-center">
              <div className="w-16 h-16 bg-rose-50 text-rose-500 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <Banknote size={32} />
              </div>
              <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">
                Xác nhận chi tiền
              </h3>
              <p className="text-slate-500 text-sm mt-2 font-medium">
                Nhà cung cấp:{" "}
                <span className="text-rose-600 font-black">
                  {confirmModal.tenNCC}
                </span>
              </p>
              <div className="mt-6 p-4 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                <p className="text-2xl font-black text-slate-800">
                  {confirmModal.soTien?.toLocaleString()} đ
                </p>
              </div>
              <div className="mt-6 grid grid-cols-2 gap-2">
                {["Tiền mặt", "Chuyển khoản"].map((m) => (
                  <button
                    key={m}
                    onClick={() =>
                      setConfirmModal({ ...confirmModal, phuongThuc: m })
                    }
                    className={`py-3 rounded-xl text-[10px] font-black uppercase border ${confirmModal.phuongThuc === m ? "bg-slate-800 text-white border-slate-800 shadow-md" : "bg-white text-slate-400 hover:bg-slate-50"}`}
                  >
                    {m}
                  </button>
                ))}
              </div>
            </div>
            <div className="flex border-t border-slate-50">
              <button
                onClick={() => setConfirmModal({ isOpen: false })}
                className="flex-1 py-5 text-xs font-black text-slate-400 uppercase hover:bg-slate-50"
              >
                Để sau
              </button>
              <button
                onClick={executeConfirm}
                className="flex-1 py-5 bg-rose-600 text-white text-xs font-black uppercase hover:bg-rose-700 flex items-center justify-center gap-2 shadow-inner"
              >
                <Check size={16} strokeWidth={3} /> Xác nhận chi
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- HELPER COMPONENTS ---

const InfoBox = ({ label, value, icon }) => (
  <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm">
    <p className="text-[10px] font-black text-slate-400 uppercase mb-2 flex items-center gap-2 tracking-widest">
      {icon} {label}
    </p>
    <p className="text-sm font-black text-slate-800 uppercase leading-none">
      {value || "---"}
    </p>
  </div>
);

const SummaryCard = ({
  label,
  value,
  sub,
  icon,
  iconColor,
  valueColor = "text-slate-800",
}) => (
  <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex flex-col justify-between h-32 hover:shadow-lg transition-all group">
    <div className="flex justify-between items-start mb-4">
      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none group-hover:text-slate-600">
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
