import React, { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import { Menu, X } from "lucide-react";

const AdminLayout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  const userName = localStorage.getItem("userName") || "Quản trị viên";

  return (
    <div className="flex min-h-screen bg-slate-50 relative">
      {/* Overlay cho mobile khi sidebar mở */}
      {isSidebarOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={toggleSidebar}
        ></div>
      )}

      {/* Sidebar: Ẩn trên mobile (trừ khi mở), hiện trên desktop (md trở lên) */}
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
          {/* Nút Hamburger cho mobile */}
          <button
            className="p-2 mr-4 text-slate-600 rounded-lg hover:bg-slate-100 md:hidden"
            onClick={toggleSidebar}
          >
            {isSidebarOpen ? <X size={24} /> : <Menu size={24} />}
          </button>

          <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider truncate">
            Hệ thống quản trị vắc-xin
          </h2>

          {/* HIỂN THỊ LỜI CHÀO TẠI ĐÂY */}
          <div className="flex items-center gap-3 ml-auto">
            <span className="text-slate-600 text-sm">
              Xin chào,{" "}
              <span className="font-bold text-blue-600">{userName}</span>
            </span>
            <div className="w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold text-xs">
              {userName.charAt(0)}
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
