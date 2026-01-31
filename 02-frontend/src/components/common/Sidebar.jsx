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
} from "lucide-react";
import { useAuth } from "../../hooks/useAuth"; // Import hook phân quyền

const Sidebar = () => {
  const { role } = useAuth(); // Lấy role hiện tại

  // 1. Định nghĩa danh sách menu kèm theo quyền truy cập (roles)
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
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col h-screen sticky top-0 shadow-xl">
      {/* Logo / Brand */}
      <div className="p-6 text-xl font-bold text-white border-b border-slate-800 flex items-center gap-3">
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center text-sm shadow-lg shadow-blue-500/20">
          V
        </div>
        Vaccine System
      </div>

      {/* 3. Hiển thị Menu đã được lọc */}
      <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
        {filteredMenu.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 p-3 rounded-lg transition-all duration-200 ${
                isActive
                  ? "bg-blue-600 text-white shadow-lg shadow-blue-900/20"
                  : "hover:bg-slate-800 hover:text-white"
              }`
            }
          >
            {item.icon}
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Bottom Action: Logout */}
      <div className="p-4 border-t border-slate-800 bg-slate-900/50">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 p-3 w-full rounded-lg text-slate-400 hover:bg-rose-500/10 hover:text-rose-500 transition-colors"
        >
          <LogOut size={20} />
          <span className="font-medium">Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
