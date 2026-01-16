import React, { useState, useEffect } from "react";
import axiosClient from "../../api/axiosClient";
import {
  X,
  User,
  Lock,
  Shield,
  CreditCard,
  MapPin,
  AlignLeft,
} from "lucide-react";
import toast from "react-hot-toast";

const CreateUserModal = ({ isOpen, onClose }) => {
  const [rolesList, setRolesList] = useState([]);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    maQuyen: "",
    fullname: "",
    cmnd: "",
    address: "",
    description: "",
  });

  // Lấy danh sách quyền khi Modal được mở
  useEffect(() => {
    if (isOpen) {
      const fetchRoles = async () => {
        try {
          const response = await axiosClient.get("/roles");
          // Giả sử API trả về mảng trực tiếp hoặc nằm trong .result
          const data = response.data.result || response.data;
          setRolesList(data);

          // Gán giá trị mặc định là quyền đầu tiên nếu có
          if (data.length > 0) {
            setFormData((prev) => ({ ...prev, maQuyen: data[0].maQuyen }));
          }
        } catch (error) {
          toast.error("Không thể tải danh sách quyền hạn." + error.message);
        }
      };
      fetchRoles();
    }
  }, [isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Kiểm tra để trống bất kỳ ô nào
    if (Object.values(formData).some((val) => val === "")) {
      toast.error("Vui lòng nhập đầy đủ thông tin."); // Thông báo theo SRS
      return;
    }
    console.log("Dữ liệu gửi lên Backend:", formData);
    toast.success("Tạo tài khoản thành công!");
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
      <div className="bg-white w-full max-w-2xl rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-slate-100">
          <h2 className="text-xl font-bold text-slate-800">
            Tạo tài khoản mới
          </h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Nhóm 1: Thông tin đăng nhập */}
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">
                Tài khoản
              </h3>
              <InputGroup
                icon={<User size={18} />}
                label="Username"
                value={formData.username}
                onChange={(v) => setFormData({ ...formData, username: v })}
                placeholder="Tên đăng nhập"
              />
              <InputGroup
                icon={<Lock size={18} />}
                label="Password"
                type="password"
                value={formData.password}
                onChange={(v) => setFormData({ ...formData, password: v })}
                placeholder="••••••••"
              />

              <div className="space-y-1.5">
                <label className="text-sm font-medium text-slate-700">
                  Phân quyền
                </label>
                <div className="relative">
                  <Shield
                    className="absolute left-3 top-2.5 text-slate-400"
                    size={18}
                  />
                  <select
                    className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                    value={formData.maQuyen}
                    onChange={(e) =>
                      setFormData({ ...formData, maQuyen: e.target.value })
                    }
                  >
                    {/* Đổ dữ liệu động từ rolesList */}
                    {rolesList.map((role) => (
                      <option key={role.maQuyen} value={role.maQuyen}>
                        {role.tenQuyen}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            {/* Nhóm 2: Thông tin định danh */}
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">
                Cá nhân
              </h3>
              <InputGroup
                icon={<AlignLeft size={18} />}
                label="Họ tên"
                value={formData.fullname}
                onChange={(v) => setFormData({ ...formData, fullname: v })}
                placeholder="Nguyễn Văn A"
              />
              <InputGroup
                icon={<CreditCard size={18} />}
                label="Số CMND"
                value={formData.cmnd}
                onChange={(v) => setFormData({ ...formData, cmnd: v })}
                placeholder="123456789"
              />
              <InputGroup
                icon={<MapPin size={18} />}
                label="Nơi ở"
                value={formData.address}
                onChange={(v) => setFormData({ ...formData, address: v })}
                placeholder="Địa chỉ thường trú"
              />
            </div>
          </div>

          {/* Description */}
          <div className="space-y-1.5">
            <label className="text-sm font-medium text-slate-700">
              Mô tả (Description)
            </label>
            <textarea
              className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none min-h-[100px]"
              placeholder="Thông tin bổ sung về nhân viên..."
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
            />
          </div>

          {/* Buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={onClose}
              className="px-6 py-2.5 font-medium text-slate-600 hover:bg-slate-100 rounded-xl transition-colors"
            >
              Thoát
            </button>
            <button
              type="submit"
              className="px-8 py-2.5 font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-lg shadow-blue-200 transition-all"
            >
              Lưu
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// Component con để tái sử dụng giao diện Input
const InputGroup = ({
  icon,
  label,
  type = "text",
  value,
  onChange,
  placeholder,
}) => (
  <div className="space-y-1.5">
    <label className="text-sm font-medium text-slate-700">{label}</label>
    <div className="relative">
      <div className="absolute left-3 top-2.5 text-slate-400">{icon}</div>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all"
      />
    </div>
  </div>
);

export default CreateUserModal;
