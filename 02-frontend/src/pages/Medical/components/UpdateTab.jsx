import React, { useState, useEffect } from "react";
import {
  UserPlus,
  CheckCircle,
  XCircle,
  User,
  Phone,
  MapPin,
  Baby,
  Calendar,
  Info,
} from "lucide-react";
import toast from "react-hot-toast";

const UpdateTab = ({ data }) => {
  const [formData, setFormData] = useState({
    hoTen: "",
    gioiTinh: "Nam",
    tuoi: "",
    nguoiGiamHo: "",
    dienThoai: "",
    diaChi: "",
  });

  // Đồng bộ dữ liệu từ props vào state khi có dữ liệu mới
  useEffect(() => {
    if (data) {
      setFormData({
        hoTen: data.hoTen || "",
        gioiTinh: data.gioiTinh || "Nam",
        tuoi: data.tuoi || "",
        nguoiGiamHo: data.nguoiGiamHo || "",
        dienThoai: data.dienThoai || "",
        diaChi: data.diaChi || "",
      });
    }
  }, [data]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Giả lập xử lý lưu dữ liệu
    toast.promise(new Promise((resolve) => setTimeout(resolve, 1500)), {
      loading: "Đang lưu thay đổi...",
      success: "Cập nhật hồ sơ thành công!",
      error: "Có lỗi xảy ra, vui lòng thử lại.",
    });
  };

  if (!data) {
    return (
      <div className="bg-white p-12 rounded-[2.5rem] border border-slate-100 text-center italic text-slate-400">
        Vui lòng tra cứu ID trước khi cập nhật hồ sơ.
      </div>
    );
  }

  return (
    <div className="bg-white p-6 md:p-12 rounded-[2.5rem] shadow-sm border border-slate-100 animate-in fade-in slide-in-from-right-4 duration-500">
      {/* Header của Form */}
      <div className="mb-10 border-b border-slate-50 pb-6 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-amber-100 text-amber-600 rounded-2xl">
            <UserPlus size={28} />
          </div>
          <div>
            <h3 className="text-xl font-black text-slate-800 uppercase">
              Cập nhật hồ sơ hành chính
            </h3>
            <p className="text-slate-400 text-xs font-bold uppercase tracking-widest mt-1">
              ID: {data.id}
            </p>
          </div>
        </div>
        <div className="hidden md:flex items-center gap-2 px-4 py-2 bg-blue-50 text-blue-600 rounded-xl text-xs font-bold italic">
          <Info size={16} /> Thông tin được bảo mật
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Lưới các ô nhập liệu - Responsive: 1 cột trên Mobile, 2 cột trên Desktop */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 md:gap-x-10 md:gap-y-8">
          <InputGroup
            label="Họ và tên bệnh nhân"
            name="hoTen"
            icon={<User size={18} />}
            value={formData.hoTen}
            onChange={handleChange}
            placeholder="Ví dụ: Nguyễn Văn A"
          />

          <div className="flex flex-col gap-3">
            <label className="text-xs font-black text-slate-400 uppercase tracking-[0.2em] ml-1">
              Giới tính
            </label>
            <div className="flex gap-4 p-1 bg-slate-50 rounded-2xl w-fit border border-slate-100">
              {["Nam", "Nữ"].map((gender) => (
                <label
                  key={gender}
                  className={`flex items-center gap-2 px-6 py-2.5 rounded-xl cursor-pointer transition-all font-bold text-sm ${
                    formData.gioiTinh === gender
                      ? "bg-white text-purple-600 shadow-sm ring-1 ring-slate-200"
                      : "text-slate-400 hover:text-slate-600"
                  }`}
                >
                  <input
                    type="radio"
                    name="gioiTinh"
                    value={gender}
                    checked={formData.gioiTinh === gender}
                    onChange={handleChange}
                    className="hidden"
                  />
                  {gender}
                </label>
              ))}
            </div>
          </div>

          <InputGroup
            label="Tuổi"
            name="tuoi"
            type="number"
            icon={<Calendar size={18} />}
            value={formData.tuoi}
            onChange={handleChange}
            placeholder="Nhập số tuổi"
          />

          <InputGroup
            label="Người giám hộ (Nếu có)"
            name="nguoiGiamHo"
            icon={<Baby size={18} />}
            value={formData.nguoiGiamHo}
            onChange={handleChange}
            placeholder="Tên cha/mẹ hoặc người thân"
          />

          <InputGroup
            label="Số điện thoại liên lạc"
            name="dienThoai"
            icon={<Phone size={18} />}
            value={formData.dienThoai}
            onChange={handleChange}
            placeholder="090..."
          />

          <div className="md:col-span-2">
            <InputGroup
              label="Địa chỉ cư trú hiện tại"
              name="diaChi"
              icon={<MapPin size={18} />}
              value={formData.diaChi}
              onChange={handleChange}
              placeholder="Số nhà, tên đường, phường/xã..."
            />
          </div>
        </div>

        {/* Nút hành động */}
        <div className="pt-6 border-t border-slate-50 flex flex-col sm:flex-row gap-4">
          <button
            type="submit"
            className="flex-1 sm:flex-none px-10 py-4 bg-purple-600 text-white font-black rounded-2xl hover:bg-purple-700 hover:shadow-xl hover:shadow-purple-200 transition-all flex items-center justify-center gap-3 uppercase tracking-wider"
          >
            <CheckCircle size={22} /> Lưu thay đổi
          </button>
          <button
            type="button"
            className="flex-1 sm:flex-none px-10 py-4 bg-slate-100 text-slate-500 font-black rounded-2xl hover:bg-slate-200 transition-all flex items-center justify-center gap-3 uppercase tracking-wider"
          >
            <XCircle size={22} /> Hủy bỏ
          </button>
        </div>
      </form>
    </div>
  );
};

// --- HELPER COMPONENT CHO Ô NHẬP LIỆU ---
const InputGroup = ({ label, icon, ...props }) => (
  <div className="flex flex-col gap-2 group">
    <label className="text-xs font-black text-slate-400 uppercase tracking-[0.2em] ml-1 transition-colors group-focus-within:text-purple-600">
      {label}
    </label>
    <div className="relative">
      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 transition-colors group-focus-within:text-purple-500">
        {icon}
      </div>
      <input
        {...props}
        className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-[1.25rem] focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700 placeholder:text-slate-300 placeholder:font-medium"
      />
    </div>
  </div>
);

export default UpdateTab;
