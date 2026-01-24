import React, { useState, useEffect } from "react";
import toast from "react-hot-toast";
import inventoryApi from "../../../api/inventoryApi";
import {
  X,
  Save,
  RotateCcw,
  PackagePlus,
  Globe,
  ShieldCheck,
  Thermometer,
} from "lucide-react";

const ImportVaccineModal = ({ isOpen, onClose, onSuccess }) => {
  const [categories, setCategories] = useState([]); // Danh mục Loại vắc-xin
  const [suppliers, setSuppliers] = useState([]); // Danh mục Nhà cung cấp
  const [isSubmitting, setIsSubmitting] = useState(false);

  const initialForm = {
    tenVacXin: "",
    maLoaiVacXin: "", // UUID
    ngayNhan: new Date().toISOString().split("T")[0],
    giayPhep: "",
    nuocSanXuat: "",
    hamLuong: "",
    maLo: "", // Số lô
    hanSuDung: "",
    dieuKienBaoQuan: "2°C - 8°C",
    doTuoiTiemChung: "",
    donGia: "",
    soLuong: "",
    maNhaCungCap: "", // UUID
    phongNguaBenh: "",
    ghiChu: "",
  };

  const [formData, setFormData] = useState(initialForm);

  // Lấy dữ liệu danh mục khi mở Modal
  useEffect(() => {
    if (isOpen) {
      const fetchMetadata = async () => {
        try {
          const [resCats, resSups] = await Promise.all([
            inventoryApi.getVaccineTypes(), // Bạn cần thêm hàm này vào inventoryApi.js
            inventoryApi.getSuppliers(), // Bạn cần thêm hàm này vào inventoryApi.js
          ]);
          setCategories(resCats || []);
          setSuppliers(resSups || []);
        } catch (error) {
          toast.error("Không thể tải danh sách danh mục");
        }
      };
      fetchMetadata();
    }
  }, [isOpen]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await inventoryApi.importVaccine(formData);
      toast.success("Nhập kho vắc-xin thành công!");
      onSuccess(); // Reload bảng ở trang chính
      onClose(); // Đóng modal
      setFormData(initialForm);
    } catch (error) {
      toast.error(error || "Có lỗi xảy ra khi nhập kho");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <form
        onSubmit={handleSubmit}
        className="bg-white w-full max-w-3xl rounded-3xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300"
      >
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
            type="button"
            onClick={onClose}
            className="p-2 hover:bg-slate-200 rounded-full transition-colors"
          >
            <X size={20} className="text-slate-500" />
          </button>
        </div>

        {/* Body Form */}
        <div className="p-8 max-h-[70vh] overflow-y-auto custom-scrollbar">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Cột 1 */}
            <div className="space-y-5">
              <h3 className="text-sm font-bold text-blue-600 flex items-center gap-2 mb-4">
                <ShieldCheck size={16} /> Định danh vắc-xin
              </h3>
              <InputField
                label="Tên vắc-xin"
                name="tenVacXin"
                required
                value={formData.tenVacXin}
                onChange={handleChange}
              />

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                    Loại vắc-xin
                  </label>
                  <select
                    name="maLoaiVacXin"
                    required
                    value={formData.maLoaiVacXin}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium focus:ring-2 focus:ring-blue-500 outline-none"
                  >
                    <option value="">-- Chọn --</option>
                    {categories.map((cat) => (
                      <option key={cat.maLoaiVacXin} value={cat.maLoaiVacXin}>
                        {cat.tenLoaiVacXin}
                      </option>
                    ))}
                  </select>
                </div>
                <InputField
                  label="Hàm lượng"
                  name="hamLuong"
                  value={formData.hamLuong}
                  onChange={handleChange}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Số lô"
                  name="maLo"
                  required
                  value={formData.maLo}
                  onChange={handleChange}
                />
                <InputField
                  label="Đơn giá"
                  name="donGia"
                  type="number"
                  required
                  value={formData.donGia}
                  onChange={handleChange}
                />
              </div>
              <InputField
                label="Phòng ngừa bệnh"
                name="phongNguaBenh"
                value={formData.phongNguaBenh}
                onChange={handleChange}
              />
            </div>

            {/* Cột 2 */}
            <div className="space-y-5">
              <h3 className="text-sm font-bold text-amber-600 flex items-center gap-2 mb-4">
                <Globe size={16} /> Xuất xứ & Bảo quản
              </h3>
              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Nước sản xuất"
                  name="nuocSanXuat"
                  value={formData.nuocSanXuat}
                  onChange={handleChange}
                />
                <InputField
                  label="Số lượng nhập"
                  name="soLuong"
                  type="number"
                  required
                  value={formData.soLuong}
                  onChange={handleChange}
                />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Nhà cung cấp
                </label>
                <select
                  name="maNhaCungCap"
                  required
                  value={formData.maNhaCungCap}
                  onChange={handleChange}
                  className="w-full bg-slate-50 border-none rounded-xl p-2.5 text-sm font-medium focus:ring-2 focus:ring-blue-500 outline-none"
                >
                  <option value="">-- Chọn nhà cung cấp --</option>
                  {suppliers.map((sup) => (
                    <option key={sup.maNhaCungCap} value={sup.maNhaCungCap}>
                      {sup.tenNhaCungCap}
                    </option>
                  ))}
                </select>
              </div>

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
                  type="date"
                  name="hanSuDung"
                  required
                  value={formData.hanSuDung}
                  onChange={handleChange}
                />
              </div>
              <InputField
                label="Số giấy phép"
                name="giayPhep"
                value={formData.giayPhep}
                onChange={handleChange}
              />
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-6 bg-slate-50 border-t border-slate-100 flex justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            className="flex items-center gap-2 px-6 py-2.5 text-slate-500 font-bold text-sm hover:bg-slate-200 rounded-xl transition-all"
          >
            Hủy nhập
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex items-center gap-2 px-8 py-2.5 bg-blue-600 text-white font-bold text-sm rounded-xl shadow-lg shadow-blue-200 hover:bg-blue-700 transition-all active:scale-95 disabled:opacity-50"
          >
            {isSubmitting ? (
              "Đang lưu..."
            ) : (
              <>
                <Save size={18} /> Nhập kho ngay
              </>
            )}
          </button>
        </div>
      </form>
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
