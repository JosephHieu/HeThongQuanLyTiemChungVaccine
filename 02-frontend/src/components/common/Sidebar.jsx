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
} from "lucide-react";

const Sidebar = () => {
  // Danh sách các mục từ Menustrip trong SRS
  const menuItems = [
    {
      path: "/admin/dashboard",
      icon: <LayoutDashboard size={20} />,
      label: "Dashboard",
    },
    { path: "/admin/accounts", icon: <UserCog size={20} />, label: "Account" },
    {
      path: "/admin/moderator",
      icon: <ShieldCheck size={20} />,
      label: "Moderator",
    },
    {
      path: "/admin/reports",
      icon: <FileBarChart size={20} />,
      label: "Báo cáo",
    },
    { path: "/admin/about", icon: <Info size={20} />, label: "About" },
  ];

  const handleLogout = () => {
    // Hiển thị Popup xác nhận
    Swal.fire({
      title: "Bạn có chắc chắn muốn đăng xuất?",
      text: "Mọi phiên làm việc hiện tại sẽ kết thúc!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Đăng xuất",
      cancelButtonText: "Hủy",
      background: "#ffffff",
      customClass: {
        popup: "rounded-2xl",
        confirmButton: "rounded-lg px-4 py-2",
        cancelButton: "rounded-lg px-4 py-2",
      },
    }).then((result) => {
      if (result.isConfirmed) {
        localStorage.clear();
        window.location.href = "/login";
      }
    });
  };

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col h-screen sticky top-0">
      {/* Logo / Brand */}
      <div className="p-6 text-xl font-bold text-white border-b border-slate-800 flex items-center gap-3">
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center text-sm">
          V
        </div>
        Vaccine System
      </div>

      {/* Menu List */}
      <nav className="flex-1 p-4 space-y-1">
        {menuItems.map((item) => (
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
      <div className="p-4 border-t border-slate-800">
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
