import React, { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/common/Sidebar";
import { Menu, X, User as UserIcon } from "lucide-react"; // Thêm icon User
import { useAuth } from "../hooks/useAuth"; // Import hook của bạn

const AdminLayout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const { name, role } = useAuth(); // Lấy name và role từ Hook

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  return (
    <div className="flex min-h-screen bg-slate-50 relative">
      {/* Overlay cho mobile */}
      {isSidebarOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={toggleSidebar}
        ></div>
      )}

      {/* Sidebar */}
      <div
        className={`fixed inset-y-0 left-0 z-30 w-64 transform transition-transform duration-300 ease-in-out md:relative md:translate-x-0 ${
          isSidebarOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <Sidebar />
      </div>

      {/* Nội dung chính */}
      <div className="flex-1 flex flex-col min-h-screen w-full md:w-[calc(100%-16rem)]">
        <header className="h-16 bg-white border-b border-slate-200 flex items-center px-4 md:px-8 sticky top-0 z-10">
          <button
            className="p-2 mr-4 text-slate-600 rounded-lg hover:bg-slate-100 md:hidden"
            onClick={toggleSidebar}
          >
            {isSidebarOpen ? <X size={24} /> : <Menu size={24} />}
          </button>

          <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider truncate">
            Hệ thống quản trị vắc-xin
          </h2>

          {/* HIỂN THỊ LỜI CHÀO & ROLE */}
          <div className="flex items-center gap-3 ml-auto">
            <div className="text-right hidden sm:block">
              {" "}
              {/* Ẩn bớt text trên mobile cực nhỏ */}
              <p className="text-slate-600 text-xs font-medium">Xin chào,</p>
              <p className="text-sm font-bold text-slate-800">
                {name || "Người dùng"}
                <span className="ml-2 px-2 py-0.5 bg-blue-100 text-blue-600 text-[10px] rounded-md uppercase">
                  {role}
                </span>
              </p>
            </div>

            {/* Avatar hoặc Icon mặc định */}
            <div className="w-10 h-10 bg-gradient-to-tr from-blue-600 to-indigo-600 text-white rounded-xl flex items-center justify-center font-bold shadow-lg shadow-blue-200">
              {name ? name.charAt(0).toUpperCase() : <UserIcon size={18} />}
            </div>
          </div>
        </header>

        <main className="p-4 md:p-8 flex-1 overflow-x-hidden">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;
