import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Wallet,
  Warehouse,
  Stethoscope,
  Users,
  Calendar,
  MessageSquare,
} from "lucide-react";

const Dashboard = () => {
  const navigate = useNavigate();

  // Dữ liệu hiển thị dựa trên Screen Content trong tài liệu
  const adminFunctions = [
    {
      title: "Tài chính",
      icon: <Wallet />,
      color: "bg-blue-500",
      desc: "Quản lý doanh thu và chi phí tiêm chủng.",
    },
    {
      title: "Kho bãi",
      icon: <Warehouse />,
      color: "bg-green-500",
      desc: "Quản lý vắc-xin, lô hàng và tồn kho.",
      path: "/admin/warehouse",
    },
    {
      title: "Nhân viên y tế",
      icon: <Stethoscope />,
      color: "bg-purple-500",
      desc: "Quản lý hồ sơ bác sĩ và điều dưỡng.",
    },
    {
      title: "Chăm sóc khách hàng",
      icon: <Users />,
      color: "bg-orange-500",
      desc: "Hỗ trợ và giải đáp thắc mắc người dân.",
    },
    {
      title: "Lịch tiêm chủng",
      icon: <Calendar />,
      color: "bg-rose-500",
      desc: "Theo dõi và điều chỉnh lịch tiêm trung tâm.",
      path: "/admin/schedules",
    },
    {
      title: "Feedback",
      icon: <MessageSquare />,
      color: "bg-cyan-500",
      desc: "Xem phản hồi và góp ý từ khách hàng.",
    },
  ];

  return (
    <div className="space-y-6">
      <header>
        <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">
          Bảng điều khiển quản trị
        </h1>
        <p className="text-slate-500 text-sm">
          Chào mừng bạn trở lại hệ thống quản lý tiêm chủng
        </p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {adminFunctions.map((item, index) => (
          <div
            key={index}
            onClick={() => item.path && navigate(item.path)}
            className="group bg-white p-6 rounded-2xl shadow-sm border border-slate-100 hover:shadow-xl hover:-translate-y-1 transition-all cursor-pointer"
          >
            <div
              className={`${item.color} w-14 h-14 rounded-xl flex items-center justify-center text-white mb-4 shadow-lg group-hover:scale-110 transition-transform`}
            >
              {React.cloneElement(item.icon, { size: 28 })}
            </div>
            <h3 className="text-xl font-bold text-slate-800 mb-2">
              {item.title}
            </h3>
            <p className="text-slate-500 text-sm leading-relaxed">
              {item.desc}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;
