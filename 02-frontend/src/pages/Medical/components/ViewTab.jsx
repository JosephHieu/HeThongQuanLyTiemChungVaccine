import React from "react";
import {
  User,
  Phone,
  MapPin,
  Syringe,
  Calendar,
  AlertCircle,
  Tag,
  Baby,
  Activity,
  Info,
} from "lucide-react";

const ViewTab = ({ data }) => {
  // Nếu chưa có dữ liệu (chưa tìm kiếm), hiển thị trạng thái trống
  if (!data) {
    return (
      <div className="bg-white p-16 rounded-[2.5rem] border-2 border-dashed border-slate-200 flex flex-col items-center text-center animate-in fade-in zoom-in-95 duration-500">
        <div className="w-20 h-20 bg-slate-50 text-slate-300 rounded-full flex items-center justify-center mb-6">
          <Activity size={40} />
        </div>
        <h3 className="text-xl font-bold text-slate-400 italic">
          Vui lòng nhập ID và nhấn "Truy xuất" để xem hồ sơ...
        </h3>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 animate-in slide-in-from-bottom-4 duration-500">
      {/* --- CỘT TRÁI: THÔNG TIN HÀNH CHÍNH (4/12) --- */}
      <div className="lg:col-span-4 space-y-6">
        <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 flex flex-col items-center text-center sticky top-24">
          {/* Avatar & Tên */}
          <div className="w-24 h-24 bg-gradient-to-br from-purple-500 to-indigo-600 text-white rounded-full flex items-center justify-center mb-4 shadow-xl ring-4 ring-purple-50">
            <User size={48} />
          </div>
          <h2 className="text-2xl font-black text-slate-800 uppercase tracking-tight">
            {data.hoTen}
          </h2>
          <div className="mt-1 px-4 py-1 bg-purple-50 text-purple-600 rounded-full text-xs font-black tracking-widest uppercase">
            ID: {data.id}
          </div>

          {/* List thông tin chi tiết */}
          <div className="w-full mt-8 space-y-5 text-left border-t border-slate-50 pt-6">
            <InfoItem
              icon={<Calendar size={18} />}
              label="Tuổi bệnh nhân"
              value={`${data.tuoi} tuổi`}
            />
            <InfoItem
              icon={<Activity size={18} />}
              label="Giới tính"
              value={data.gioiTinh}
            />
            <InfoItem
              icon={<Phone size={18} />}
              label="Số điện thoại"
              value={data.dienThoai}
            />
            <div className="flex gap-3">
              <div className="text-purple-400 mt-1">
                <MapPin size={18} />
              </div>
              <div>
                <p className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">
                  Địa chỉ thường trú
                </p>
                <p className="text-sm font-bold text-slate-700 leading-tight">
                  {data.diaChi}
                </p>
              </div>
            </div>
          </div>

          {/* Badge Người giám hộ (Chỉ hiện nếu có) */}
          {data.nguoiGiamHo && (
            <div className="w-full mt-6 p-4 bg-amber-50 rounded-2xl border border-amber-100 flex items-center gap-3">
              <Baby className="text-amber-500" size={20} />
              <div className="text-left">
                <p className="text-[9px] font-black text-amber-500 uppercase">
                  Người giám hộ
                </p>
                <p className="text-xs font-bold text-amber-900">
                  {data.nguoiGiamHo}
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* --- CỘT PHẢI: THÔNG TIN Y TẾ (8/12) --- */}
      <div className="lg:col-span-8 space-y-6">
        {/* Khối 1: Lịch sử tiêm chủng */}
        <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 relative overflow-hidden">
          <div className="flex items-center gap-3 mb-8 border-b border-slate-50 pb-4">
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <Syringe size={20} />
            </div>
            <h3 className="font-black text-slate-800 uppercase tracking-wider">
              Mũi tiêm gần nhất
            </h3>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-6">
              <div>
                <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                  Loại vắc-xin
                </p>
                <p className="text-2xl font-black text-blue-600 uppercase leading-none">
                  {data.vacxinDaTiem}
                </p>
              </div>
              <div className="flex gap-10">
                <div className="space-y-1">
                  <p className="text-[10px] font-black text-slate-400 uppercase">
                    Mã lô
                  </p>
                  <p className="font-bold text-slate-700">{data.maLo}</p>
                </div>
                <div className="space-y-1">
                  <p className="text-[10px] font-black text-slate-400 uppercase">
                    Ngày tiêm
                  </p>
                  <p className="font-bold text-slate-700">
                    {data.thoiGianTiemTruoc}
                  </p>
                </div>
              </div>
            </div>

            {/* Trạng thái phản ứng */}
            <div
              className={`p-6 rounded-[2rem] flex flex-col justify-center border ${
                data.phanUng === "Bình thường"
                  ? "bg-emerald-50 border-emerald-100 text-emerald-700"
                  : "bg-rose-50 border-rose-100 text-rose-700"
              }`}
            >
              <div className="flex items-center gap-2 mb-2">
                <AlertCircle size={20} />
                <span className="text-[10px] font-black uppercase tracking-widest">
                  Phản ứng sau tiêm
                </span>
              </div>
              <p className="text-xl font-black">
                {data.phanUng || "Chưa ghi nhận"}
              </p>
            </div>
          </div>

          {/* Hình trang trí chìm */}
          <Syringe
            className="absolute -right-6 -bottom-6 text-slate-50 rotate-12"
            size={150}
          />
        </div>

        {/* Khối 2: Chỉ định tiếp theo */}
        <div className="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
          <div className="flex items-center gap-3 mb-8 border-b border-slate-50 pb-4">
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <Calendar size={20} />
            </div>
            <h3 className="font-black text-slate-800 uppercase tracking-wider">
              Dự kiến mũi tiếp theo
            </h3>
          </div>

          <div className="flex flex-col md:flex-row gap-6 items-start md:items-center">
            <div className="flex-1">
              <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                Vắc-xin chỉ định
              </p>
              <p className="text-2xl font-black text-purple-600 uppercase">
                {data.vacxinCanTiem}
              </p>
            </div>
            <div className="bg-slate-50 px-8 py-4 rounded-2xl border border-slate-100 text-center min-w-[180px]">
              <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                Thời gian dự kiến
              </p>
              <p className="text-lg font-black text-slate-800 uppercase">
                {data.thoiGianTiemTiepTheo}
              </p>
            </div>
          </div>

          <div className="mt-8 flex items-start gap-2 text-slate-400 italic text-xs">
            <Info size={14} className="shrink-0 mt-0.5" />
            <p>
              Lưu ý: Nhân viên y tế cần thực hiện khám sàng lọc và kiểm tra phản
              ứng của bệnh nhân trước khi tiến hành tiêm mũi tiếp theo.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

// Component con hỗ trợ hiển thị dòng thông tin
const InfoItem = ({ icon, label, value }) => (
  <div className="flex items-center gap-3 group">
    <div className="text-purple-300 group-hover:text-purple-500 transition-colors">
      {icon}
    </div>
    <div className="flex-1">
      <p className="text-[10px] uppercase font-bold text-slate-400 tracking-wider mb-0.5">
        {label}
      </p>
      <p className="font-bold text-slate-700 text-sm">{value}</p>
    </div>
  </div>
);

export default ViewTab;
