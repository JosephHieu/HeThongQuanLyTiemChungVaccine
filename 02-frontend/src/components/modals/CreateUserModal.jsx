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
  Eye,
  EyeOff,
} from "lucide-react";
import toast from "react-hot-toast";

// BỔ SUNG: Thêm onSuccess vào props
const CreateUserModal = ({ isOpen, onClose, onSuccess }) => {
  const [rolesList, setRolesList] = useState([]);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // Tên các trường (key) phải khớp chính xác với UserCreationRequest ở Backend
  const [formData, setFormData] = useState({
    tenDangNhap: "", // Backend: tenDangNhap
    matKhau: "", // Backend: matKhau
    maQuyen: "", // Backend: maQuyen
    hoTen: "", // Backend: hoTen
    cmnd: "", // Backend: cmnd
    noiO: "", // Backend: noiO
    moTa: "", // Backend: moTa
  });

  // Hàm kiểm tra dữ liệu trước khi gửi (Client-side validation)
  const validate = () => {
    let tempErrors = {};
    // 1. Kiểm tra Username: Không dấu, không khoảng trắng
    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    if (!formData.tenDangNhap) {
      tempErrors.tenDangNhap = "Tên đăng nhập không được để trống";
    } else if (!usernameRegex.test(formData.tenDangNhap)) {
      tempErrors.tenDangNhap = "Username không được chứa khoảng trắng hoặc dấu";
    }

    // 2. Kiểm tra Mật khẩu: Tối thiểu 6 ký tự
    if (!formData.matKhau) {
      tempErrors.matKhau = "Mật khẩu không được để trống";
    } else if (formData.matKhau.length < 6) {
      tempErrors.matKhau = "Mật khẩu phải có ít nhất 6 ký tự";
    }

    // 3. Kiểm tra CMND: Phải là số và đúng độ dài
    const cmndRegex = /^[0-9]{9,12}$/;
    if (!formData.cmnd) {
      tempErrors.cmnd = "Vui lòng nhập số CMND";
    } else if (!cmndRegex.test(formData.cmnd)) {
      tempErrors.cmnd = "CMND phải là số và có từ 9 đến 12 chữ số";
    }

    // 4. Kiểm tra Họ tên: Không chứa số
    const nameRegex = /^[^0-9]*$/;
    if (!formData.hoTen) {
      tempErrors.hoTen = "Vui lòng nhập họ tên";
    } else if (!nameRegex.test(formData.hoTen)) {
      tempErrors.hoTen = "Họ tên không được chứa chữ số";
    }

    if (!formData.noiO) tempErrors.noiO = "Vui lòng nhập nơi ở";

    setErrors(tempErrors);
    return Object.keys(tempErrors).length === 0;
  };

  // Tự động reset form khi đóng/mở modal
  useEffect(() => {
    if (!isOpen) {
      setFormData({
        tenDangNhap: "",
        matKhau: "",
        maQuyen: "",
        hoTen: "",
        cmnd: "",
        noiO: "",
        moTa: "",
      });
    }
  }, [isOpen]);

  useEffect(() => {
    if (isOpen) {
      const fetchRoles = async () => {
        try {
          const response = await axiosClient.get("/roles");
          const data = response.data.result || response.data;
          setRolesList(data);
          if (data.length > 0) {
            setFormData((prev) => ({ ...prev, maQuyen: data[0].maQuyen }));
          }
        } catch (error) {
          toast.error("Không thể tải danh sách quyền hạn.");
        }
      };
      fetchRoles();
    }
  }, [isOpen]);

  const handleInputChange = (field, value) => {
    setFormData({ ...formData, [field]: value });
    if (errors[field]) {
      setErrors({ ...errors, [field]: "" });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Nếu validate sai thì dừng lại, không gọi API
    if (!validate()) {
      toast.error("Vui lòng kiểm tra lại các thông tin.");
      return;
    }

    try {
      setLoading(true);
      await axiosClient.post("/users/create", formData);
      toast.success("Tạo tài khoản thành công!");
      onSuccess();
      onClose();
    } catch (error) {
      const apiError = error.response?.data;

      console.log("Dữ liệu lỗi từ server: ", apiError);
      // Trường hợp 1: Trùng tên đăng nhập (Mã 1001 - USER_EXISTED)
      if (apiError && apiError.code === 1001) {
        setErrors((prev) => ({
          ...prev,
          tenDangNhap: "Tên đăng nhập này đã được sử dụng",
        }));
        toast.error("Tên đăng nhập đã tồn tại!");
      } else {
        toast.error(apiError?.message || "Đã có lỗi xảy ra khi lưu.");
      }
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
      {/* MODAL BOX: Khung chính của Modal */}
      <div className="bg-white w-full max-w-2xl max-h-[90vh] rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200 flex flex-col">
        {/* 1. HEADER: Cố định (flex-none) */}
        <div className="flex items-center justify-between p-6 border-b border-slate-100 flex-none">
          <h2 className="text-xl font-bold text-slate-800">
            Tạo tài khoản mới
          </h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors cursor-pointer"
          >
            <X size={20} />
          </button>
        </div>

        {/* 2. FORM: Container chính cho nội dung và nút bấm */}
        <form
          onSubmit={handleSubmit}
          className="flex flex-col flex-1 overflow-hidden"
        >
          {/* BODY: Vùng chứa input - Có thanh cuộn (flex-1) */}
          <div className="p-6 space-y-6 overflow-y-auto custom-scrollbar flex-1">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Nhóm 1: Tài khoản */}
              <div className="space-y-4">
                <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">
                  Tài khoản
                </h3>
                <InputGroup
                  icon={<User size={18} />}
                  label="Username"
                  value={formData.tenDangNhap}
                  error={errors.tenDangNhap}
                  onChange={(v) => handleInputChange("tenDangNhap", v)}
                  placeholder="Tên đăng nhập"
                />
                <InputGroup
                  icon={<Lock size={18} />}
                  label="Password"
                  type="password"
                  value={formData.matKhau}
                  error={errors.matKhau}
                  onChange={(v) => handleInputChange("matKhau", v)}
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
                      className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none appearance-none cursor-pointer"
                      value={formData.maQuyen}
                      onChange={(e) =>
                        handleInputChange("maQuyen", e.target.value)
                      }
                    >
                      {rolesList.map((role) => (
                        <option key={role.maQuyen} value={role.maQuyen}>
                          {role.tenQuyen}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>

              {/* Nhóm 2: Cá nhân */}
              <div className="space-y-4">
                <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">
                  Cá nhân
                </h3>
                <InputGroup
                  icon={<AlignLeft size={18} />}
                  label="Họ tên"
                  value={formData.hoTen}
                  error={errors.hoTen}
                  onChange={(v) => handleInputChange("hoTen", v)}
                  placeholder="Nguyễn Văn A"
                />
                <InputGroup
                  icon={<CreditCard size={18} />}
                  label="Số CMND"
                  value={formData.cmnd}
                  error={errors.cmnd}
                  onChange={(v) => handleInputChange("cmnd", v)}
                  placeholder="123456789"
                />
                <InputGroup
                  icon={<MapPin size={18} />}
                  label="Nơi ở"
                  value={formData.noiO}
                  error={errors.noiO}
                  onChange={(v) => handleInputChange("noiO", v)}
                  placeholder="Địa chỉ thường trú"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="text-sm font-medium text-slate-700">
                Mô tả (Description)
              </label>
              <textarea
                className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none min-h-[100px]"
                value={formData.moTa}
                onChange={(e) => handleInputChange("moTa", e.target.value)}
              />
            </div>
          </div>

          {/* 3. FOOTER: Luôn cố định ở đáy Modal (flex-none) */}
          <div className="flex items-center justify-end gap-3 p-6 border-t border-slate-100 flex-none bg-white">
            <button
              type="button"
              onClick={onClose}
              className="px-6 py-2.5 font-medium text-slate-600 hover:bg-slate-100 rounded-xl transition-colors cursor-pointer"
            >
              Thoát
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-8 py-2.5 font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-lg shadow-blue-200 transition-all disabled:opacity-50 cursor-pointer active:scale-95"
            >
              {loading ? "Đang lưu..." : "Lưu"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const InputGroup = ({
  icon,
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  error,
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const isPasswordField = type === "password";
  const inputType = isPasswordField
    ? showPassword
      ? "text"
      : "password"
    : type;

  return (
    <div className="space-y-1.5">
      <label className="text-sm font-medium text-slate-700">{label}</label>
      <div className="relative">
        <div className="absolute left-3 top-2.5 text-slate-400">{icon}</div>

        <input
          type={inputType}
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          // Thêm border đỏ nếu có lỗi
          className={`w-full pl-10 pr-10 py-2 bg-slate-50 border rounded-xl outline-none transition-all ${
            error
              ? "border-red-500 focus:ring-2 focus:ring-red-100"
              : "border-slate-200 focus:ring-2 focus:ring-blue-500"
          }`}
        />

        {isPasswordField && (
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            // Thêm cursor-pointer để hiện hình ngón tay
            className="absolute right-3 top-2.5 text-slate-400 hover:text-blue-600 cursor-pointer transition-colors"
          >
            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        )}
      </div>
      {/* Hiển thị câu thông báo lỗi nhỏ phía dưới */}
      {error && (
        <p className="text-[11px] text-red-500 font-medium ml-1 animate-in slide-in-from-top-1">
          {error}
        </p>
      )}
    </div>
  );
};

export default CreateUserModal;
