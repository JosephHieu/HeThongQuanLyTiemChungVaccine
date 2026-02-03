import React from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/common/Sidebar";

const UserLayout = () => (
  <div className="flex min-h-screen bg-slate-100">
    <Sidebar /> {/* Sidebar sẽ tự động đổi màu và menu cho User */}
    <main className="flex-1 overflow-auto">
      <Outlet /> {/* Nơi các trang con như VaccinePortal sẽ hiện ra */}
    </main>
  </div>
);

export default UserLayout;
