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
import medicalApi from "../../../api/medicalApi"; // Import API của bạn

const UpdateTab = ({ data, onUpdateSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    tenBenhNhan: "",
    gioiTinh: "Nam",
    ngaySinh: "", // Đổi từ tuoi -> ngaySinh để khớp với Backend
    nguoiGiamHo: "",
    sdt: "", // Đổi từ dienThoai -> sdt
    diaChi: "",
  });

  // Đồng bộ dữ liệu từ props vào state
  useEffect(() => {
    if (data) {
      // Hàm helper để đảm bảo ngày tháng đúng định dạng yyyy-MM-dd
      const formatToInputDate = (dateStr) => {
        if (!dateStr) return "";
        // Nếu Backend trả về yyyy-MM-dd (mặc định của LocalDate.toString())
        if (dateStr.includes("-")) return dateStr.split("T")[0];

        // Nếu Backend trả về dd/MM/yyyy, ta cần đảo ngược lại
        if (dateStr.includes("/")) {
          const [day, month, year] = dateStr.split("/");
          return `${year}-${month}-${day}`;
        }
        return dateStr;
      };

      setFormData({
        tenBenhNhan: data.hoTen || "",
        gioiTinh: data.gioiTinh || "Nam",
        ngaySinh: formatToInputDate(data.ngaySinh), // Cập nhật quan trọng ở đây
        nguoiGiamHo: data.nguoiGiamHo || "",
        sdt: data.dienThoai || "",
        diaChi: data.diaChi || "",
      });
    }
  }, [data]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Gọi API thực tế từ Backend
      await medicalApi.updateInfo(data.id, formData);

      toast.success("Cập nhật hồ sơ bệnh nhân thành công!");

      // Callback để load lại dữ liệu mới ở trang chủ (MedicalRecord.jsx)
      if (onUpdateSuccess) onUpdateSuccess();
    } catch (error) {
      toast.error(error.message || "Có lỗi xảy ra khi cập nhật!");
    } finally {
      setLoading(false);
    }
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
      </div>

      <form onSubmit={handleSubmit} className="space-y-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 md:gap-x-10 md:gap-y-8">
          <InputGroup
            label="Họ và tên bệnh nhân"
            name="tenBenhNhan"
            icon={<User size={18} />}
            value={formData.tenBenhNhan}
            onChange={handleChange}
            required
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
            label="Ngày sinh"
            name="ngaySinh"
            type="date" // Chuyển sang chọn ngày để lưu vào DB chính xác hơn
            icon={<Calendar size={18} />}
            value={formData.ngaySinh}
            onChange={handleChange}
            required
          />

          <InputGroup
            label="Người giám hộ (Nếu có)"
            name="nguoiGiamHo"
            icon={<Baby size={18} />}
            value={formData.nguoiGiamHo}
            onChange={handleChange}
          />

          <InputGroup
            label="Số điện thoại liên lạc"
            name="sdt"
            icon={<Phone size={18} />}
            value={formData.sdt}
            onChange={handleChange}
            required
          />

          <div className="md:col-span-2">
            <InputGroup
              label="Địa chỉ cư trú hiện tại"
              name="diaChi"
              icon={<MapPin size={18} />}
              value={formData.diaChi}
              onChange={handleChange}
            />
          </div>
        </div>

        <div className="pt-6 border-t border-slate-50 flex flex-col sm:flex-row gap-4">
          <button
            type="submit"
            disabled={loading}
            className="flex-1 sm:flex-none px-10 py-4 bg-purple-600 text-white font-black rounded-2xl hover:bg-purple-700 hover:shadow-xl transition-all flex items-center justify-center gap-3 uppercase tracking-wider disabled:opacity-50"
          >
            {loading ? (
              "Đang xử lý..."
            ) : (
              <>
                <CheckCircle size={22} /> Lưu thay đổi
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

const InputGroup = ({ label, icon, ...props }) => (
  <div className="flex flex-col gap-2 group">
    <label className="text-xs font-black text-slate-400 uppercase tracking-[0.2em] ml-1 group-focus-within:text-purple-600">
      {label}
    </label>
    <div className="relative">
      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-purple-500">
        {icon}
      </div>
      <input
        {...props}
        className="w-full pl-12 pr-6 py-4 bg-slate-50 border border-slate-100 rounded-[1.25rem] focus:ring-4 focus:ring-purple-500/10 focus:border-purple-500 focus:bg-white outline-none transition-all font-bold text-slate-700 placeholder:text-slate-300"
      />
    </div>
  </div>
);

export default UpdateTab;
