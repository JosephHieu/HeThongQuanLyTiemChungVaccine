import React from "react";
import { NavLink } from "react-router-dom";
import Swal from "sweetalert2";
import {
  UserCog,
  ShieldCheck,
  FileBarChart,
  Info,
  LogOut,
  LayoutDashboard,
  Warehouse,
  Calendar,
  Syringe,
  CalendarDays,
  ClipboardList,
  UserCircle,
} from "lucide-react";
import { useAuth } from "../../hooks/useAuth"; // Import hook phân quyền

const Sidebar = () => {
  const { role } = useAuth(); // Lấy role hiện tại
  const isUser = role === "Normal User Account";

  // --- NHÓM QUẢN TRỊ (ADMIN/STAFF) ---
  const menuItems = [
    {
      path: "/admin/dashboard",
      icon: <LayoutDashboard size={20} />,
      label: "Dashboard",
      roles: [
        "Administrator",
        "Nhân viên y tế",
        "Quản lý kho",
        "Tài chính",
        "Hỗ trợ khách hàng",
      ],
    },
    {
      path: "/admin/accounts",
      icon: <UserCog size={20} />,
      label: "Tài khoản",
      roles: ["Administrator"],
    },
    {
      path: "/admin/warehouse",
      icon: <Warehouse size={20} />,
      label: "Kho vắc-xin",
      roles: ["Administrator", "Quản lý kho"],
    },
    // BỔ SUNG: Lịch tiêm (Cho Y tế và Hỗ trợ)
    {
      path: "/admin/schedules",
      icon: <Calendar size={20} />, // Nhớ import Calendar từ lucide-react
      label: "Lịch tiêm",
      roles: ["Administrator"],
    },
    {
      path: "/admin/moderator",
      icon: <ShieldCheck size={20} />,
      label: "Điều phối",
      roles: ["Administrator", "Nhân viên y tế"],
    },
    {
      path: "/admin/reports",
      icon: <FileBarChart size={20} />,
      label: "Báo cáo",
      roles: ["Administrator", "Tài chính"],
    },
    // BỔ SUNG: Hỗ trợ (Khớp với path 'support' trong App.jsx)
    // {
    //   path: "/admin/support",
    //   icon: <Users size={20} />,
    //   label: "Hỗ trợ",
    //   roles: ["Administrator", "Hỗ trợ khách hàng"],
    // },
    {
      path: "/admin/about",
      icon: <Info size={20} />,
      label: "Thông tin",
      roles: [
        "Administrator",
        "Nhân viên y tế",
        "Quản lý kho",
        "Tài chính",
        "Hỗ trợ khách hàng",
      ],
    },

    // --- NHÓM NGƯỜI DÙNG (NORMAL USER) ---
    {
      path: "/user/vaccines", // Chức năng 9.5.1
      icon: <Syringe size={20} />,
      label: "Tra cứu vắc-xin",
      roles: ["Normal User Account"],
    },
    {
      path: "/user/schedules", // Chức năng 9.5.2
      icon: <CalendarDays size={20} />,
      label: "Lịch tiêm trung tâm",
      roles: ["Normal User Account"],
    },
    {
      path: "/user/my-registrations",
      icon: <ClipboardList size={20} />,
      label: "Đăng ký của tôi",
      roles: ["Normal User Account"],
    },
    {
      path: "/user/profile",
      icon: <UserCircle size={20} />,
      label: "Hồ sơ cá nhân",
      roles: ["Normal User Account"],
    },
  ];

  // 2. Lọc danh sách: Chỉ giữ lại những mục mà User có quyền truy cập
  const filteredMenu = menuItems.filter((item) => item.roles.includes(role));

  const handleLogout = () => {
    Swal.fire({
      title: "Bạn có chắc chắn muốn đăng xuất?",
      text: "Mọi phiên làm việc hiện tại sẽ kết thúc!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Đăng xuất",
      cancelButtonText: "Hủy",
      customClass: { popup: "rounded-2xl" },
    }).then((result) => {
      if (result.isConfirmed) {
        localStorage.clear();
        window.location.href = "/login";
      }
    });
  };

  return (
    <aside
      className={`w-64 flex flex-col h-screen sticky top-0 shadow-xl transition-all duration-300 
      ${isUser ? "bg-indigo-900 text-white" : "bg-white text-slate-600 border-r border-slate-200"}`}
    >
      {/* 1. Header Logo - Điều chỉnh màu text và border theo role */}
      <div
        className={`p-6 text-xl font-bold flex items-center gap-3 border-b 
        ${isUser ? "text-white border-indigo-800" : "text-slate-800 border-slate-100"}`}
      >
        <div
          className={`w-8 h-8 rounded-lg flex items-center justify-center text-sm shadow-lg text-white
          ${isUser ? "bg-emerald-500" : "bg-blue-600"}`}
        >
          V
        </div>
        {isUser ? "Portal Bệnh Nhân" : "Vaccine Admin"}
      </div>

      {/* 2. Menu - Tùy biến màu sắc Active và Hover cho từng Role */}
      <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
        {filteredMenu.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 p-3 rounded-xl transition-all duration-200 font-medium ${
                isActive
                  ? isUser
                    ? "bg-blue-600 text-white shadow-lg shadow-blue-900/40" // Active User
                    : "bg-blue-50 text-blue-600 shadow-sm" // Active Admin (Sáng)
                  : isUser
                    ? "text-indigo-200 hover:bg-white/10 hover:text-white" // Hover User
                    : "text-slate-500 hover:bg-slate-50 hover:text-blue-600" // Hover Admin
              }`
            }
          >
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* 3. Bottom Action - Điều chỉnh border và bg */}
      <div
        className={`p-4 border-t ${isUser ? "border-indigo-800 bg-indigo-950/30" : "border-slate-100 bg-slate-50/50"}`}
      >
        <button
          onClick={handleLogout}
          className={`flex items-center gap-3 p-3 w-full rounded-xl transition-colors font-medium
            ${isUser ? "text-indigo-300 hover:bg-rose-500/20 hover:text-rose-400" : "text-slate-500 hover:bg-rose-50 hover:text-rose-600"}`}
        >
          <LogOut size={20} />
          <span>Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
