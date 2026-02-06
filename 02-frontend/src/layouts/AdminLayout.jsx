import React, { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/common/Sidebar";
import { Menu, X, User as UserIcon } from "lucide-react";
import { useAuth } from "../hooks/useAuth";

const AdminLayout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false); // Mobile toggle
  const [isCollapsed, setIsCollapsed] = useState(false); // Desktop collapse (THÊM MỚI)
  const { name, role } = useAuth();

  const toggleSidebarMobile = () => setIsSidebarOpen(!isSidebarOpen);
  const toggleCollapseDesktop = () => setIsCollapsed(!isCollapsed); // Hàm toggle (THÊM MỚI)

  return (
    <div className="flex min-h-screen bg-slate-50 relative">
      {/* Overlay cho mobile */}
      {isSidebarOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={toggleSidebarMobile}
        ></div>
      )}

      {/* Sidebar - Cập nhật chiều rộng động dựa trên isCollapsed */}
      <div
        className={`fixed inset-y-0 left-0 z-30 transform transition-all duration-300 ease-in-out md:relative md:translate-x-0 
        ${isCollapsed ? "w-20" : "w-64"} 
        ${isSidebarOpen ? "translate-x-0" : "-translate-x-full"}`}
      >
        {/* Truyền state và hàm xuống Sidebar */}
        <Sidebar isCollapsed={isCollapsed} onToggle={toggleCollapseDesktop} />
      </div>

      {/* Nội dung chính - Xóa bỏ calc() cố định */}
      <div className="flex-1 flex flex-col min-h-screen w-full transition-all duration-300">
        <header className="h-16 bg-white border-b border-slate-200 flex items-center px-4 md:px-8 sticky top-0 z-10">
          <button
            className="p-2 mr-4 text-slate-600 rounded-lg hover:bg-slate-100 md:hidden"
            onClick={toggleSidebarMobile}
          >
            {isSidebarOpen ? <X size={24} /> : <Menu size={24} />}
          </button>

          <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider truncate">
            Hệ thống quản trị vắc-xin
          </h2>

          <div className="flex items-center gap-3 ml-auto">
            <div className="text-right hidden sm:block">
              <p className="text-slate-600 text-xs font-medium">Xin chào,</p>
              <p className="text-sm font-bold text-slate-800">
                {name || "Người dùng"}
                <span className="ml-2 px-2 py-0.5 bg-blue-100 text-blue-600 text-[10px] rounded-md uppercase">
                  {role}
                </span>
              </p>
            </div>

            <div className="w-10 h-10 bg-gradient-to-tr from-blue-600 to-indigo-600 text-white rounded-xl flex items-center justify-center font-bold shadow-lg shadow-blue-200">
              {name ? name.charAt(0).toUpperCase() : <UserIcon size={18} />}
            </div>
          </div>
        </header>

        <main className="p-4 md:p-8 flex-1 overflow-x-hidden bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;
