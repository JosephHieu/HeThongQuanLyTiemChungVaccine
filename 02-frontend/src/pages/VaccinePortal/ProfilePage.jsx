import React, { useState, useEffect } from "react";
import {
  User,
  Calendar,
  MapPin,
  ShieldCheck,
  Edit3,
  Save,
  X,
  History,
  UserCircle,
  BadgeCheck,
  Phone,
  Users,
} from "lucide-react";
import toast from "react-hot-toast";
import medicalApi from "../../api/medicalApi"; // Import API của bạn

const ProfilePage = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true); // Loading khi load trang
  const [history, setHistory] = useState([]);
  const [formData, setFormData] = useState({
    hoTen: "",
    ngaySinh: "",
    gioiTinh: "",
    diaChi: "",
    soDienThoai: "",
    nguoiGiamHo: "",
  });

  // 1. Fetch dữ liệu khi mount component
  const fetchProfileData = async () => {
    try {
      setFetching(true);
      const [profileData, historyData] = await Promise.all([
        medicalApi.getMyProfile(),
        medicalApi.getMyHistory(),
      ]);
      setFormData(profileData);
      setHistory(historyData);
    } catch (error) {
      toast.error(error.message || "Không thể tải dữ liệu hồ sơ");
    } finally {
      setFetching(false);
    }
  };

  useEffect(() => {
    fetchProfileData();
  }, []);

  // 2. Xử lý lưu thông tin
  const handleSave = async () => {
    setLoading(true);
    try {
      // Gửi đúng cấu trúc UpdateProfileRequest mà Backend yêu cầu
      const updateData = {
        hoTen: formData.hoTen,
        ngaySinh: formData.ngaySinh,
        gioiTinh: formData.gioiTinh,
        diaChi: formData.diaChi,
        soDienThoai: formData.soDienThoai,
        nguoiGiamHo: formData.nguoiGiamHo,
      };

      await medicalApi.updateMyProfile(updateData);
      toast.success("Cập nhật hồ sơ thành công!");
      setIsEditing(false);
    } catch (error) {
      toast.error(error.message || "Cập nhật thất bại!");
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="p-4 md:p-8 max-w-6xl mx-auto space-y-8 animate-in fade-in zoom-in-95 duration-500">
      {/* 1. Header Profile Card */}
      <div className="bg-indigo-900 rounded-[2.5rem] p-6 md:p-10 text-white flex flex-col md:flex-row items-center gap-8 shadow-2xl shadow-indigo-200 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -mr-32 -mt-32 blur-3xl"></div>

        <div className="relative">
          <div className="w-28 h-28 bg-gradient-to-br from-indigo-400 to-indigo-600 rounded-[2rem] flex items-center justify-center text-4xl font-black border-4 border-indigo-300 shadow-inner">
            {formData.hoTen?.charAt(0) || "?"}
          </div>
          <div className="absolute -bottom-2 -right-2 bg-emerald-500 p-1.5 rounded-full border-4 border-indigo-900">
            <BadgeCheck size={20} />
          </div>
        </div>

        <div className="text-center md:text-left flex-1 z-10">
          <h2 className="text-3xl font-black uppercase tracking-tight mb-2">
            {formData.hoTen}
          </h2>
          <div className="flex flex-wrap justify-center md:justify-start gap-4 text-indigo-200 text-sm font-bold">
            <span className="flex items-center gap-1">
              <Calendar size={16} /> {formData.ngaySinh}
            </span>
            <span className="flex items-center gap-1">
              <User size={16} /> {formData.gioiTinh}
            </span>
            <span className="flex items-center gap-1">
              <Phone size={16} /> {formData.soDienThoai}
            </span>
          </div>
        </div>

        <button
          onClick={() => setIsEditing(!isEditing)}
          className={`z-10 px-6 py-3 rounded-2xl font-black flex items-center gap-2 transition-all shadow-lg active:scale-95 
            ${isEditing ? "bg-rose-500 text-white" : "bg-white text-indigo-900 hover:bg-indigo-50"}`}
        >
          {isEditing ? <X size={20} /> : <Edit3 size={20} />}
          {isEditing ? "HỦY BỎ" : "CHỈNH SỬA HỒ SƠ"}
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* 2. Form Thông tin chi tiết */}
        <div className="lg:col-span-1 space-y-6">
          <div className="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-sm space-y-6">
            <h3 className="text-lg font-black text-slate-800 flex items-center gap-2 border-b border-slate-50 pb-4 uppercase tracking-tighter">
              <UserCircle size={22} className="text-indigo-600" /> Hồ sơ cá nhân
            </h3>

            <div className="space-y-4">
              {/* Họ tên */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Họ tên
                </label>
                <input
                  disabled={!isEditing}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-700 outline-none focus:border-indigo-500 disabled:opacity-50"
                  value={formData.hoTen}
                  onChange={(e) =>
                    setFormData({ ...formData, hoTen: e.target.value })
                  }
                />
              </div>

              {/* SĐT */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Số điện thoại
                </label>
                <input
                  disabled={!isEditing}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-700 outline-none focus:border-indigo-500 disabled:opacity-50"
                  value={formData.soDienThoai}
                  onChange={(e) =>
                    setFormData({ ...formData, soDienThoai: e.target.value })
                  }
                />
              </div>

              {/* Ngày sinh */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Ngày sinh
                </label>
                <input
                  type="date"
                  disabled={!isEditing}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-700 outline-none focus:border-indigo-500 disabled:opacity-50"
                  value={formData.ngaySinh}
                  onChange={(e) =>
                    setFormData({ ...formData, ngaySinh: e.target.value })
                  }
                />
              </div>

              {/* Giới tính */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Giới tính
                </label>
                <div className="flex gap-2">
                  {["Nam", "Nữ"].map((g) => (
                    <button
                      key={g}
                      disabled={!isEditing}
                      onClick={() => setFormData({ ...formData, gioiTinh: g })}
                      className={`flex-1 py-2 rounded-xl font-bold text-sm border-2 transition-all ${
                        formData.gioiTinh === g
                          ? "bg-indigo-50 border-indigo-600 text-indigo-600"
                          : "bg-white border-slate-100 text-slate-400"
                      }`}
                    >
                      {g}
                    </button>
                  ))}
                </div>
              </div>

              {/* Người giám hộ */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 text-wrap">
                  Người giám hộ (nếu có)
                </label>
                <input
                  disabled={!isEditing}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-700 outline-none focus:border-indigo-500 disabled:opacity-50"
                  value={formData.nguoiGiamHo || ""}
                  onChange={(e) =>
                    setFormData({ ...formData, nguoiGiamHo: e.target.value })
                  }
                />
              </div>

              {/* Địa chỉ */}
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 text-wrap">
                  Địa chỉ thường trú
                </label>
                <textarea
                  disabled={!isEditing}
                  rows="2"
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-bold text-slate-700 outline-none focus:border-indigo-500 disabled:opacity-50 resize-none"
                  value={formData.diaChi}
                  onChange={(e) =>
                    setFormData({ ...formData, diaChi: e.target.value })
                  }
                />
              </div>
            </div>

            {isEditing && (
              <button
                onClick={handleSave}
                disabled={loading}
                className="w-full py-4 bg-emerald-600 text-white font-black rounded-2xl hover:bg-emerald-700 shadow-xl shadow-emerald-100 flex items-center justify-center gap-2 uppercase tracking-widest transition-all"
              >
                {loading ? (
                  "Đang lưu..."
                ) : (
                  <>
                    <Save size={20} /> Lưu thay đổi
                  </>
                )}
              </button>
            )}
          </div>
        </div>

        {/* 3. Bảng lịch sử tiêm chủng */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white p-6 md:p-8 rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
            <div className="flex justify-between items-center mb-6 border-b border-slate-50 pb-4">
              <h3 className="text-lg font-black text-slate-800 flex items-center gap-2 uppercase tracking-tighter">
                <History size={22} className="text-indigo-600" /> Lịch sử tiêm
                chủng
              </h3>
              <span className="bg-indigo-50 text-indigo-600 px-4 py-1 rounded-full text-xs font-black">
                {history.length} MŨI TIÊM
              </span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse min-w-[700px]">
                <thead>
                  <tr className="bg-slate-50/50">
                    <th className="p-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                      Thời gian
                    </th>
                    <th className="p-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                      Vắc-xin
                    </th>
                    <th className="p-4 text-[10px] font-black text-slate-400 uppercase tracking-widest text-center">
                      Liều
                    </th>
                    <th className="p-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                      Người tiêm
                    </th>
                    <th className="p-4 text-[10px] font-black text-slate-400 uppercase tracking-widest text-center">
                      Kết quả
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-50">
                  {history.length > 0 ? (
                    history.map((row, idx) => (
                      <tr
                        key={idx}
                        className="hover:bg-slate-50/50 transition-colors group"
                      >
                        <td className="p-4">
                          <p className="text-xs font-black text-slate-700">
                            {row.thoiGian}
                          </p>
                          <p className="text-[10px] text-slate-400 font-bold flex items-center gap-1">
                            <MapPin size={10} /> {row.diaDiem}
                          </p>
                        </td>
                        <td className="p-4">
                          <p className="text-xs font-black text-indigo-600">
                            {row.tenVacXin}
                          </p>
                          <p className="text-[10px] text-slate-400 font-bold italic">
                            {row.loaiVacXin}
                          </p>
                        </td>
                        <td className="p-4 text-center">
                          <span className="text-[10px] font-black px-2 py-1 bg-slate-100 rounded-lg text-slate-500">
                            {row.lieuLuong}
                          </span>
                        </td>
                        <td className="p-4 text-xs font-bold text-slate-600">
                          Bs.{row.nguoiTiem}
                        </td>
                        <td className="p-4 text-center">
                          <span className="text-[10px] font-black px-3 py-1 bg-emerald-100 text-emerald-600 rounded-full">
                            {row.ketQua}
                          </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan="5"
                        className="p-10 text-center text-slate-400 font-bold italic text-sm"
                      >
                        Chưa có lịch sử tiêm chủng được ghi nhận.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
