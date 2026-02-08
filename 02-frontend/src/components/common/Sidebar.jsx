import React from "react";
import { NavLink } from "react-router-dom";
import Swal from "sweetalert2";
import {
  UserCog,
  ShieldCheck,
  FileBarChart,
  Info,
  ChevronLeft,
  ChevronRight,
  Menu,
  LogOut,
  LayoutDashboard,
  Warehouse,
  Calendar,
  Syringe,
  CalendarDays,
  ClipboardList,
  UserCircle,
  MessageSquare,
  ShieldAlert,
} from "lucide-react";
import { useAuth } from "../../hooks/useAuth"; // Import hook phân quyền

const Sidebar = ({ isCollapsed, onToggle }) => {
  const { role } = useAuth(); // Lấy role hiện tại
  const isUser = role === "Normal User Account";

  // Sử dụng hàm toggle từ props
  const toggleSidebar = onToggle;
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
      path: "/admin/feedbacks",
      icon: <MessageSquare size={20} />, // Icon tin nhắn
      label: "Quản lý phản hồi",
      roles: ["Administrator", "Hỗ trợ khách hàng"],
    },
    {
      path: "/admin/moderator",
      icon: <ShieldCheck size={20} />,
      label: "Điều phối",
      roles: ["Administrator", "Nhân viên y tế"],
    },
    {
      path: "/admin/epidemics",
      icon: <ShieldAlert size={20} />,
      label: "Dịch bệnh",
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
      path: "/user/epidemics",
      icon: <ShieldAlert size={20} />,
      label: "Tình hình dịch bệnh",
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
    {
      path: "/user/feedback",
      icon: <MessageSquare size={20} />,
      label: "Phản hồi sau tiêm",
      roles: ["Normal User Account"],
    },
    {
      path: "/user/high-level-feedback",
      icon: <MessageSquare size={20} />,
      label: "Phản hồi cấp cao",
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
      className={`flex flex-col h-screen sticky top-0 shadow-xl transition-all duration-300 ease-in-out overflow-x-hidden
      ${isCollapsed ? "w-20" : "w-64"} 
      ${isUser ? "bg-indigo-900 text-white" : "bg-white text-slate-600 border-r border-slate-200"}`}
    >
      {/* 1. Header Logo & Toggle Button */}
      <div
        className={`p-6 flex items-center justify-between border-b transition-all
        ${isUser ? "text-white border-indigo-800" : "text-slate-800 border-slate-100"}`}
      >
        <div className="flex items-center gap-3 overflow-hidden">
          <div
            className={`w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-sm shadow-lg text-white
            ${isUser ? "bg-emerald-500" : "bg-blue-600"}`}
          >
            V
          </div>
          {!isCollapsed && (
            <span className="font-bold whitespace-nowrap opacity-100 transition-opacity duration-300">
              {isUser ? "Portal BN" : "Admin"}
            </span>
          )}
        </div>

        <button
          onClick={toggleSidebar} // Gọi hàm từ Layout
          className={`p-1.5 rounded-lg hover:bg-black/10 transition-colors ${isCollapsed ? "mx-auto" : ""}`}
        >
          {isCollapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
        </button>
      </div>

      {/* 2. Menu Navigation (Sử dụng isCollapsed từ props) */}
      <nav className="flex-1 p-3 space-y-2 overflow-y-auto">
        {filteredMenu.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            title={isCollapsed ? item.label : ""}
            className={({ isActive }) =>
              `flex items-center gap-3 p-3 rounded-xl transition-all duration-200 font-medium group ${
                isActive
                  ? isUser
                    ? "bg-blue-600 text-white shadow-lg"
                    : "bg-blue-50 text-blue-600"
                  : isUser
                    ? "text-indigo-200 hover:bg-white/10 hover:text-white"
                    : "text-slate-500 hover:bg-slate-50"
              } ${isCollapsed ? "justify-center" : ""}`
            }
          >
            <div className="flex-shrink-0">{item.icon}</div>
            {!isCollapsed && (
              <span className="whitespace-nowrap">{item.label}</span>
            )}
          </NavLink>
        ))}
      </nav>

      {/* 3. Bottom Action */}
      <div
        className={`p-4 border-t ${isUser ? "border-indigo-800 bg-indigo-950/30" : "border-slate-100"}`}
      >
        <button
          onClick={handleLogout}
          className={`flex items-center gap-3 p-3 w-full rounded-xl transition-colors font-medium
            ${isCollapsed ? "justify-center" : ""}`}
        >
          <LogOut size={20} className="flex-shrink-0" />
          {!isCollapsed && <span className="whitespace-nowrap">Đăng xuất</span>}
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
