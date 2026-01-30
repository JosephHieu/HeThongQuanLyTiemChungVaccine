import React, { useState, useEffect } from "react";
import {
  Save,
  Clock,
  MapPin,
  Users,
  UserCheck,
  Hash,
  XCircle,
  Syringe,
  Layers,
  AlertCircle,
} from "lucide-react";
import vaccinationApi from "../../../api/vaccinationApi";
import ErrorMessage from "./ErrorMessage";

const ScheduleForm = ({
  initialData,
  selectedDate,
  onSave,
  loading,
  onShiftChange,
}) => {
  const [formData, setFormData] = useState({
    thoiGian: initialData?.thoiGian || "",
    soLuong: initialData?.soLuong || "",
    doTuoi: initialData?.doTuoi || "",
    diaDiem: initialData?.diaDiem || "",
    maLo: initialData?.maLo || "",
    danhSachBacSiIds:
      initialData?.danhSachBacSi?.map((s) => s.maNhanVien) || [],
    ghiChu: initialData?.ghiChu || "",
  });

  // 1. Thêm State quản lý lỗi
  const [errors, setErrors] = useState({});
  const [medicalStaffs, setMedicalStaffs] = useState([]);
  const [batches, setBatches] = useState([]);
  const selectedBatch = batches.find((b) => b.maLo === formData.maLo);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [staffRes, batchRes] = await Promise.all([
          vaccinationApi.getDoctors(),
          vaccinationApi.getAvailableBatches(),
        ]);

        // KIỂM TRA TẠI ĐÂY: Thêm .data trước .result nếu bạn chưa có interceptor
        console.log("Kiểm tra batchRes:", batchRes);

        // Nếu bạn chưa cấu hình axios interceptor để bóc tách data:

        setMedicalStaffs(staffRes || []);
        setBatches(batchRes || []);
      } catch (error) {
        console.error("Lỗi tải dữ liệu:", error);
      }
    };
    loadData();
  }, []);

  // 2. Hàm kiểm tra tính hợp lệ của dữ liệu
  const validateForm = () => {
    let newErrors = {};

    // 1. Tìm thông tin lô đang chọn

    if (!formData.maLo) {
      newErrors.maLo = "Vui lòng chọn lô vắc-xin từ kho.";
    }

    if (!formData.thoiGian) {
      newErrors.thoiGian = "Vui lòng chọn ca trực.";
    }

    // 2. Kiểm tra số lượng liều tiêm
    const inputQty = parseInt(formData.soLuong);

    if (!formData.soLuong || inputQty <= 0) {
      newErrors.soLuong = "Số lượng phải lớn hơn 0.";
    } else if (selectedBatch && inputQty > selectedBatch.soLuongTon) {
      // CHẶN Ở ĐÂY: Nếu nhập quá số lượng tồn
      newErrors.soLuong = `Vượt quá tồn kho! Hiện tại chỉ còn ${selectedBatch.soLuongTon} liều.`;
    }

    if (!formData.diaDiem.trim())
      newErrors.diaDiem = "Địa điểm không được để trống.";
    if (!formData.doTuoi.trim())
      newErrors.doTuoi = "Vui lòng nhập đối tượng tiêm.";
    if (formData.danhSachBacSiIds.length === 0) {
      newErrors.danhSachBacSiIds = "Cần ít nhất một bác sĩ phụ trách.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    // Xóa lỗi
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }

    // MẮT XÍCH QUAN TRỌNG: Nếu đổi ca trực, báo cho trang cha để load lại dữ liệu ca đó
    if (name === "thoiGian" && onShiftChange) {
      onShiftChange(value);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 3. Chỉ gọi onSave nếu validate thành công
    if (validateForm()) {
      onSave(formData);
    }
  };

  const handleDoctorToggle = (id) => {
    setFormData((prev) => {
      const isSelected = prev.danhSachBacSiIds.includes(id);
      const newList = isSelected
        ? prev.danhSachBacSiIds.filter((item) => item !== id)
        : [...prev.danhSachBacSiIds, id];

      // Xóa thông báo lỗi khi đã chọn ít nhất 1 bác sĩ
      if (newList.length > 0) {
        setErrors((err) => ({ ...err, danhSachBacSiIds: null }));
      }

      return { ...prev, danhSachBacSiIds: newList };
    });
  };

  return (
    <div className="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden animate-in fade-in duration-500">
      {/* --- HEADER --- */}
      <div className="p-6 border-b border-slate-50 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-slate-50/30">
        <div>
          <h2 className="font-black text-slate-800 uppercase tracking-tight flex items-center gap-2">
            <span className="w-2 h-6 bg-blue-600 rounded-full"></span>
            Chi tiết lịch tiêm
          </h2>
          <p className="text-slate-400 text-[10px] font-bold uppercase tracking-widest mt-1">
            Ngày trực: {selectedDate.toLocaleDateString("vi-VN")}
          </p>
        </div>

        <div className="flex w-full sm:w-auto gap-2">
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="flex-1 sm:flex-none flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-2xl font-bold transition-all shadow-lg shadow-blue-100 disabled:opacity-50 active:scale-95"
          >
            {loading ? (
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <Save size={18} />
            )}
            {loading ? "Đang lưu..." : "Lưu lại"}
          </button>
        </div>
      </div>

      {/* --- FORM BODY --- */}
      <div className="p-6 md:p-8 grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="space-y-5">
          {/* Lô Vắc xin */}
          <div className="space-y-2">
            <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
              <Syringe size={14} className="text-blue-500" /> Vắc-xin & Số lô
            </label>
            <select
              name="maLo"
              value={formData.maLo}
              onChange={handleChange}
              className={`w-full p-4 bg-slate-50 border-2 rounded-2xl text-sm outline-none transition-all font-bold text-slate-700 ${errors.maLo ? "border-red-200 bg-red-50" : "border-transparent focus:border-blue-100 focus:bg-white"}`}
            >
              <option value="">-- Chọn vắc-xin từ kho --</option>
              {batches &&
                batches.length > 0 &&
                batches.map((b) => (
                  <option key={b.maLo} value={b.maLo}>
                    {/* Trong log bạn gửi là 'tenVacXin' (chữ X viết hoa) */}
                    {b.tenVacXin} - Lô: {b.soLo} (Còn {b.soLuongTon} liều)
                  </option>
                ))}
            </select>
            <ErrorMessage message={errors.maLo} />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Thời gian */}
            <div className="space-y-2">
              <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
                <Clock size={14} className="text-blue-500" /> Thời gian
              </label>
              <select
                name="thoiGian"
                value={formData.thoiGian}
                onChange={handleChange}
                className={`w-full p-4 bg-slate-50 border-2 rounded-2xl text-sm outline-none transition-all font-bold text-slate-700 ${errors.thoiGian ? "border-red-200 bg-red-50" : "border-transparent focus:border-blue-100"}`}
              >
                <option value="">Chọn ca trực</option>
                <option value="Sáng (07:30 - 11:30)">
                  Sáng (07:30 - 11:30)
                </option>
                <option value="Chiều (13:30 - 17:00)">
                  Chiều (13:30 - 17:00)
                </option>
              </select>
              <ErrorMessage message={errors.thoiGian} />
            </div>

            {/* Số lượng */}
            <div className="space-y-2">
              <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
                <Hash size={14} className="text-blue-500" /> Số lượng (Tối đa:{" "}
                {selectedBatch?.soLuongTon || 0})
              </label>
              <input
                name="soLuong"
                type="number"
                value={formData.soLuong}
                onChange={handleChange}
                placeholder={`Còn ${selectedBatch?.soLuongTon || 0} liều...`}
                className={`w-full p-4 bg-slate-50 border-2 rounded-2xl text-sm outline-none transition-all font-bold 
      ${errors.soLuong ? "border-red-200 bg-red-50 text-red-600" : "border-transparent focus:border-blue-100"}`}
              />
              <ErrorMessage message={errors.soLuong} />

              {/* Hiển thị thanh tiến độ nhỏ để cảnh báo mức độ tồn kho */}
              {selectedBatch && (
                <div className="mt-2 px-1">
                  <div className="flex justify-between text-[9px] font-bold uppercase mb-1">
                    <span className="text-slate-400">Tình trạng kho</span>
                    <span
                      className={
                        formData.soLuong > selectedBatch.soLuongTon
                          ? "text-red-500"
                          : "text-blue-500"
                      }
                    >
                      {formData.soLuong || 0} / {selectedBatch.soLuongTon}
                    </span>
                  </div>
                  <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className={`h-full transition-all duration-500 ${formData.soLuong > selectedBatch.soLuongTon ? "bg-red-500" : "bg-blue-500"}`}
                      style={{
                        width: `${Math.min((formData.soLuong / selectedBatch.soLuongTon) * 100, 100)}%`,
                      }}
                    ></div>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Địa điểm */}
          <div className="space-y-2">
            <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
              <MapPin size={14} className="text-blue-500" /> Địa điểm tổ chức
            </label>
            <input
              name="diaDiem"
              value={formData.diaDiem}
              onChange={handleChange}
              className={`w-full p-4 bg-slate-50 border-2 rounded-2xl text-sm outline-none transition-all font-bold ${errors.diaDiem ? "border-red-200 bg-red-50" : "border-transparent focus:border-blue-100"}`}
            />
            <ErrorMessage message={errors.diaDiem} />
          </div>
        </div>

        <div className="space-y-5">
          {/* Đối tượng */}
          <div className="space-y-2">
            <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
              <Users size={14} className="text-blue-500" /> Đối tượng (Độ tuổi)
            </label>
            <input
              name="doTuoi"
              value={formData.doTuoi}
              onChange={handleChange}
              className={`w-full p-4 bg-slate-50 border-2 rounded-2xl text-sm outline-none transition-all font-bold ${errors.doTuoi ? "border-red-200 bg-red-50" : "border-transparent focus:border-blue-100"}`}
            />
            <ErrorMessage message={errors.doTuoi} />
          </div>

          {/* Bác sĩ trực */}
          <div className="space-y-3">
            <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex justify-between items-center ml-1">
              <span className="flex items-center gap-2">
                <UserCheck size={14} className="text-blue-500" /> Bác sĩ trực
              </span>
            </label>
            <div
              className={`bg-slate-50 rounded-3xl p-3 h-[120px] overflow-y-auto border transition-all ${errors.danhSachBacSiIds ? "border-red-200 bg-red-50" : "border-slate-100"}`}
            >
              <div className="grid grid-cols-1 gap-2">
                {medicalStaffs.map((staff) => (
                  <button
                    key={staff.maNhanVien}
                    type="button"
                    onClick={() => handleDoctorToggle(staff.maNhanVien)}
                    className={`flex items-center justify-between p-3 rounded-xl transition-all text-left ${formData.danhSachBacSiIds.includes(staff.maNhanVien) ? "bg-blue-600 text-white shadow-md" : "bg-white text-slate-600 border border-slate-100 hover:border-blue-200"}`}
                  >
                    <span className="text-xs font-bold">
                      {staff.tenNhanVien}
                    </span>
                    {formData.danhSachBacSiIds.includes(staff.maNhanVien) && (
                      <UserCheck size={14} />
                    )}
                  </button>
                ))}
              </div>
            </div>
            <ErrorMessage message={errors.danhSachBacSiIds} />
          </div>

          <div className="space-y-2">
            <label className="text-[11px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-2 ml-1">
              <Layers size={14} className="text-blue-500" /> Ghi chú nội bộ
            </label>
            <textarea
              name="ghiChu"
              value={formData.ghiChu}
              onChange={handleChange}
              className="w-full p-4 bg-slate-50 border-2 border-transparent focus:border-blue-100 focus:bg-white rounded-2xl text-sm outline-none transition-all font-medium h-[80px] resize-none"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ScheduleForm;
