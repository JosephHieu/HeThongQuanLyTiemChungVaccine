import React, { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/common/Sidebar";

const UserLayout = () => {
  // 1. Quản lý trạng thái co giãn của Sidebar
  const [isCollapsed, setIsCollapsed] = useState(false);

  const toggleCollapse = () => setIsCollapsed(!isCollapsed);

  return (
    <div className="flex min-h-screen bg-slate-100">
      {/* 2. Sidebar: Truyền trạng thái và hàm toggle xuống */}
      <div
        className={`transition-all duration-300 ease-in-out flex-shrink-0 ${
          isCollapsed ? "w-20" : "w-64"
        }`}
      >
        <Sidebar isCollapsed={isCollapsed} onToggle={toggleCollapse} />
      </div>

      {/* 3. Nội dung chính: Thêm transition để giãn ra mượt mà */}
      <main className="flex-1 min-w-0 overflow-auto transition-all duration-300 ease-in-out">
        {/* Bạn có thể thêm một thanh Header mỏng cho User ở đây nếu muốn */}
        <div className="p-4 md:p-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default UserLayout;
