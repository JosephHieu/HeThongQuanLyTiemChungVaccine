import React, { useState } from "react";
import {
  X,
  Save,
  RotateCcw,
  PackagePlus,
  Globe,
  ShieldCheck,
  Thermometer,
} from "lucide-react";

const ImportVaccineModal = ({ isOpen, onClose }) => {
  // Khởi tạo state với đầy đủ các trường từ SRS
  const initialForm = {
    tenVacXin: "",
    loaiVacXin: "",
    ngayNhan: "",
    soGiayPhep: "",
    nuocSanXuat: "",
    hamLuong: "",
    soLo: "",
    hanSuDung: "",
    dieuKienBaoQuan: "2°C - 8°C",
    doTuoi: "",
    donGia: "",
  };

  const [formData, setFormData] = useState(initialForm);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => setFormData(initialForm); // Hành động "Hủy"

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div className="bg-white w-full max-w-3xl rounded-3xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300">
        {/* Header */}
        <div className="bg-slate-50 px-8 py-6 border-b border-slate-100 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-600 rounded-lg text-white">
              <PackagePlus size={24} />
            </div>
            <div>
              <h2 className="text-xl font-bold text-slate-800">
                Nhập vắc-xin mới
              </h2>
              <p className="text-xs text-slate-500 font-medium uppercase tracking-wider">
                Thông tin lưu kho hệ thống
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-slate-200 rounded-full transition-colors"
          >
            <X size={20} className="text-slate-500" />
          </button>
        </div>

        {/* Body Form - Chia làm 2 nhóm để đẹp hơn */}
        <div className="p-8 max-h-[70vh] overflow-y-auto custom-scrollbar">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Cột 1: Thông tin cơ bản */}
            <div className="space-y-5">
              <h3 className="text-sm font-bold text-blue-600 flex items-center gap-2 mb-4">
                <ShieldCheck size={16} /> Định danh vắc-xin
              </h3>
              <InputField
                label="Tên vắc-xin"
                name="tenVacXin"
                value={formData.tenVacXin}
                onChange={handleChange}
                placeholder="VD: AstraZeneca..."
              />
              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Loại vắc-xin"
                  name="loaiVacXin"
                  value={formData.loaiVacXin}
                  onChange={handleChange}
                />
                <InputField
                  label="Hàm lượng"
                  name="hamLuong"
                  value={formData.hamLuong}
                  onChange={handleChange}
                  placeholder="0.5ml..."
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Số lô"
                  name="soLo"
                  value={formData.soLo}
                  onChange={handleChange}
                />
                <InputField
                  label="Đơn giá"
                  name="donGia"
                  value={formData.donGia}
                  onChange={handleChange}
                  placeholder="VNĐ"
                />
              </div>
            </div>

            {/* Cột 2: Logistics & Bảo quản */}
            <div className="space-y-5">
              <h3 className="text-sm font-bold text-amber-600 flex items-center gap-2 mb-4">
                <Globe size={16} /> Xuất xứ & Bảo quản
              </h3>
              <InputField
                label="Nước sản xuất"
                name="nuocSanXuat"
                value={formData.nuocSanXuat}
                onChange={handleChange}
              />
              <InputField
                label="Số giấy phép"
                name="soGiayPhep"
                value={formData.soGiayPhep}
                onChange={handleChange}
              />
              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Ngày nhận"
                  type="date"
                  name="ngayNhan"
                  value={formData.ngayNhan}
                  onChange={handleChange}
                />
                <InputField
                  label="Hạn sử dụng"
                  name="hanSuDung"
                  value={formData.hanSuDung}
                  onChange={handleChange}
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Độ tuổi tiêm"
                  name="doTuoi"
                  value={formData.doTuoi}
                  onChange={handleChange}
                />
                <div className="space-y-1">
                  <label className="text-xs font-bold text-slate-500 uppercase tracking-tighter flex items-center gap-1">
                    <Thermometer size={12} /> Bảo quản
                  </label>
                  <select
                    name="dieuKienBaoQuan"
                    className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium focus:ring-2 focus:ring-blue-500 outline-none"
                  >
                    <option>2°C - 8°C</option>
                    <option>-20°C</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-6 bg-slate-50 border-t border-slate-100 flex justify-end gap-3">
          <button
            onClick={handleReset}
            className="flex items-center gap-2 px-6 py-2.5 text-slate-500 font-bold text-sm hover:bg-slate-200 rounded-xl transition-all"
          >
            <RotateCcw size={18} /> Hủy nhập
          </button>
          <button className="flex items-center gap-2 px-8 py-2.5 bg-blue-600 text-white font-bold text-sm rounded-xl shadow-lg shadow-blue-200 hover:bg-blue-700 transition-all active:scale-95">
            <Save size={18} /> Nhập kho ngay
          </button>
        </div>
      </div>
    </div>
  );
};

const InputField = ({ label, ...props }) => (
  <div className="space-y-1">
    <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
      {label}
    </label>
    <input
      {...props}
      className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium text-slate-700 focus:ring-2 focus:ring-blue-500 outline-none transition-all placeholder:text-slate-300"
    />
  </div>
);

export default ImportVaccineModal;
