import React from "react";
import {
  X,
  ShieldCheck,
  Baby,
  Syringe,
  ThermometerSnowflake,
  Globe,
  FileBadge,
  CalendarDays,
  Package,
  Barcode,
  StickyNote,
  CircleDollarSign, // Cho giá nhập
  TrendingUp, // Cho giá bán
  Wallet, // Cho tổng giá trị
} from "lucide-react";

const VaccineDetailModal = ({ isOpen, data, onClose }) => {
  // Nếu modal chưa mở hoặc chưa có dữ liệu thì không render gì cả
  if (!isOpen || !data) return null;

  // Helper function để format ngày tháng
  const formatDate = (dateString) => {
    if (!dateString) return "---";
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const formatCurrency = (amount) => {
    if (amount === undefined || amount === null) return "---";
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  // Kiểm tra hạn sử dụng (dưới 6 tháng là cảnh báo vàng, hết hạn là đỏ)
  const expiryDate = new Date(data.hanSuDung);
  const today = new Date();
  const sixMonthsFromNow = new Date(new Date().setMonth(today.getMonth() + 6));

  let expiryColorClass = "text-slate-700";
  let expiryStatus = "";

  if (expiryDate < today) {
    expiryColorClass = "text-red-600 font-black";
    expiryStatus = "(Đã hết hạn)";
  } else if (expiryDate < sixMonthsFromNow) {
    expiryColorClass = "text-amber-600 font-bold";
    expiryStatus = "(Sắp hết hạn)";
  }

  // Cấu hình các trường thông tin để render bằng vòng lặp (giúp code gọn hơn)
  const medicalInfo = [
    {
      label: "Bệnh phòng ngừa",
      value: data.phongNguaBenh,
      icon: <ShieldCheck size={18} className="text-blue-500" />,
    },
    {
      label: "Độ tuổi chỉ định",
      value: data.doTuoiTiemChung,
      icon: <Baby size={18} className="text-pink-500" />,
    },
    {
      label: "Hàm lượng/Liều",
      value: data.hamLuong,
      icon: <Syringe size={18} className="text-cyan-500" />,
    },
  ];

  const logisticsInfo = [
    {
      label: "Điều kiện bảo quản",
      value: data.dieuKienBaoQuan,
      icon: <ThermometerSnowflake size={18} className="text-sky-600" />,
    },
    {
      label: "Nước sản xuất",
      value: data.nuocSanXuat,
      icon: <Globe size={18} className="text-indigo-500" />,
    },
    {
      label: "Số giấy phép",
      value: data.giayPhep,
      icon: <FileBadge size={18} className="text-purple-500" />,
    },
  ];

  // 1. Tính toán an toàn trước khi render
  // Ưu tiên lấy giaBan, nếu không có thì thử lấy donGia, cuối cùng là 0
  const unitPrice = data.giaBan || data.donGia || 0;
  const entryPrice = data.giaNhap || 0;
  const totalValue = (data.soLuong || 0) * unitPrice;

  return (
    // Backdrop nền tối
    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-in fade-in duration-200">
      {/* Modal Container */}
      <div className="bg-white w-full max-w-2xl rounded-3xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300">
        {/* === HEADER === */}
        <div className="bg-slate-50 px-6 py-5 border-b border-slate-100 flex justify-between items-start">
          <div>
            <h2 className="text-xl font-black text-slate-800 leading-tight">
              {data.tenVacXin}
            </h2>
            <p className="text-sm font-bold text-blue-600 uppercase tracking-wider mt-1">
              {data.tenLoaiVacXin}
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 -mt-2 -mr-2 hover:bg-slate-200 rounded-full transition-colors"
          >
            <X size={22} className="text-slate-500" />
          </button>
        </div>

        {/* === BODY: Scrollable Area === */}
        <div className="p-6 max-h-[70vh] overflow-y-auto custom-scrollbar space-y-8">
          {/* Section 1: Thông tin Lô hàng & Tài chính */}
          <section className="bg-blue-50/50 p-5 rounded-2xl border border-blue-100">
            <h3 className="text-xs font-black text-blue-800 uppercase tracking-widest mb-4 flex justify-between">
              <span>Thông tin lô hàng & Tài chính</span>
              {/* Hiển thị tổng giá trị tồn kho của lô này */}
              <div className="flex items-center gap-2 bg-white px-3 py-1 rounded-lg border border-blue-100 shadow-sm">
                <Wallet size={14} className="text-blue-500" />
                <span className="text-blue-600 font-bold">
                  Tổng trị giá tồn: {formatCurrency(totalValue)}
                </span>
              </div>
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {/* ID Lô */}
              <DetailItem
                icon={<Barcode size={20} className="text-slate-400" />}
                label="Mã định danh (ID)"
                value={
                  <span className="font-mono text-[11px] break-all leading-relaxed text-slate-600">
                    {data.maLo}
                  </span>
                }
              />

              {/* Tồn kho */}
              <DetailItem
                icon={<Package size={20} className="text-blue-500" />}
                label="Tồn kho hiện tại"
                value={
                  <span className="text-xl font-black text-blue-700">
                    {data.soLuong?.toLocaleString()}{" "}
                    <span className="text-sm font-medium text-slate-500">
                      liều
                    </span>
                  </span>
                }
              />

              {/* GIÁ NHẬP - MỚI */}
              <DetailItem
                icon={
                  <CircleDollarSign size={20} className="text-emerald-500" />
                }
                label="Giá nhập vào"
                value={
                  <span className="font-bold text-emerald-600">
                    {formatCurrency(entryPrice)}
                  </span>
                }
              />

              {/* GIÁ BÁN - MỚI */}
              <DetailItem
                icon={<TrendingUp size={20} className="text-rose-500" />}
                label="Giá bán ra"
                value={
                  <span className="font-black text-rose-600">
                    {formatCurrency(unitPrice)}
                  </span>
                }
              />
              <p className="text-[10px] text-emerald-600 font-bold mt-1">
                Lợi nhuận: +{formatCurrency(unitPrice - entryPrice)} / liều
              </p>
            </div>

            {/* Hạn sử dụng - Chuyển xuống hàng dưới hoặc giữ nguyên tùy layout */}
            <div className="mt-6 pt-4 border-t border-blue-100/50">
              <DetailItem
                icon={<CalendarDays size={20} className={expiryColorClass} />}
                label="Hạn sử dụng"
                value={
                  <div className="flex items-center gap-2">
                    <span className={`font-bold ${expiryColorClass}`}>
                      {formatDate(data.hanSuDung)}
                    </span>
                    {expiryStatus && (
                      <span className="text-[10px] font-bold text-red-500 uppercase">
                        {expiryStatus}
                      </span>
                    )}
                  </div>
                }
              />
            </div>
          </section>

          {/* Section 2: Thông tin Y tế & Chỉ định */}
          <section>
            <h3 className="text-xs font-black text-slate-400 uppercase tracking-widest mb-4 border-b border-slate-100 pb-2">
              Chỉ định & Y tế
            </h3>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-y-6 gap-x-4">
              {medicalInfo.map((item, idx) => (
                <DetailItem key={idx} {...item} />
              ))}
            </div>
          </section>

          {/* Section 3: Hậu cần & Pháp lý */}
          <section>
            <h3 className="text-xs font-black text-slate-400 uppercase tracking-widest mb-4 border-b border-slate-100 pb-2">
              Hậu cần & Pháp lý
            </h3>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-y-6 gap-x-4">
              {logisticsInfo.map((item, idx) => (
                <DetailItem key={idx} {...item} />
              ))}
            </div>
          </section>

          {/* Section 4: Ghi chú */}
          {data.ghiChu && (
            <section className="bg-amber-50/50 p-4 rounded-2xl border border-amber-100 flex gap-3 items-start">
              <StickyNote
                size={20}
                className="text-amber-500 mt-0.5 shrink-0"
              />
              <div>
                <h4 className="text-xs font-bold text-amber-700 uppercase mb-1">
                  Ghi chú bổ sung
                </h4>
                <p className="text-sm text-amber-900 italic leading-relaxed">
                  "{data.ghiChu}"
                </p>
              </div>
            </section>
          )}
        </div>

        {/* === FOOTER === */}
        <div className="bg-slate-50 p-4 border-t border-slate-100 flex justify-end">
          <button
            onClick={onClose}
            className="px-6 py-2.5 bg-white border border-slate-200 text-slate-700 font-bold text-sm rounded-xl hover:bg-slate-100 transition-all"
          >
            Đóng lại
          </button>
        </div>
      </div>
    </div>
  );
};

// Component con để hiển thị từng mục thông tin (giúp code sạch hơn)
const DetailItem = ({ icon, label, value }) => (
  <div className="flex items-start gap-3">
    <div className="mt-1 shrink-0">{icon}</div>
    <div>
      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-0.5">
        {label}
      </p>
      <div className="text-sm font-bold text-slate-700">
        {value || <span className="text-slate-300 italic">---</span>}
      </div>
    </div>
  </div>
);

export default VaccineDetailModal;
