import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Wallet,
  Warehouse,
  Stethoscope,
  Users,
  Calendar,
  MessageSquare,
  UserCog, // Import thêm icon này
} from "lucide-react";
import { useAuth } from "../hooks/useAuth";

const Dashboard = () => {
  const navigate = useNavigate();
  const { role } = useAuth();

  const adminFunctions = [
    // 1. CHỨC NĂNG QUẢN TRỊ (Chỉ Admin)
    {
      title: "Quản trị hệ thống",
      icon: <UserCog />,
      color: "bg-slate-700",
      desc: "Quản lý tài khoản, phân quyền và trạng thái nhân viên.",
      path: "/admin/accounts",
      roles: ["Administrator"],
    },
    // 2. KHO VẮC-XIN (Admin + Kho)
    {
      title: "Kho bãi",
      icon: <Warehouse />,
      color: "bg-green-600",
      desc: "Quản lý vắc-xin, lô hàng, hạn sử dụng và tồn kho.",
      path: "/admin/warehouse",
      roles: ["Administrator", "Quản lý kho"],
    },
    // 3. ĐIỀU PHỐI & KHÁM BỆNH (Admin + Y tế) - Khớp với path 'moderator' trong App.jsx
    {
      title: "Nhân viên y tế",
      icon: <Stethoscope />,
      color: "bg-purple-500",
      desc: "Xem danh sách chờ khám và cập nhật hồ sơ bệnh án.",
      path: "/admin/moderator",
      roles: ["Administrator", "Nhân viên y tế"],
    },
    // 4. BÁO CÁO & TÀI CHÍNH (Admin + Tài chính)
    {
      title: "Báo cáo tài chính",
      icon: <Wallet />,
      color: "bg-blue-500",
      desc: "Theo dõi doanh thu, hóa đơn và chi tiết thu chi.",
      path: "/admin/reports",
      roles: ["Administrator", "Tài chính"],
    },
    // 5. LỊCH TIÊM CHỦNG (Admin + Hỗ trợ + Y tế)
    {
      title: "Lịch tiêm chủng",
      icon: <Calendar />,
      color: "bg-rose-500",
      desc: "Theo dõi lịch tiêm định kỳ và điều chỉnh lịch tiêm trung tâm.",
      path: "/admin/schedules",
      roles: ["Administrator"],
    },
    // 6. CHĂM SÓC KHÁCH HÀNG (Admin + Hỗ trợ)
    {
      title: "Hỗ trợ & Feedback",
      icon: <MessageSquare />,
      color: "bg-cyan-500",
      desc: "Xem phản hồi và giải đáp thắc mắc từ người dân.",
      path: "/admin/support",
      roles: ["Administrator", "Hỗ trợ khách hàng"],
    },
  ];

  const filteredFunctions = adminFunctions.filter((item) =>
    item.roles.includes(role),
  );

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <header>
        <h1 className="text-2xl font-bold text-slate-800 uppercase tracking-tight">
          Bảng điều khiển
        </h1>
        <p className="text-slate-500 text-sm">
          Chào mừng bạn trở lại. Vai trò hiện tại:{" "}
          <span className="font-extrabold text-blue-600 px-2 py-0.5 bg-blue-50 rounded-lg">
            {role}
          </span>
        </p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredFunctions.length > 0 ? (
          filteredFunctions.map((item, index) => (
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
          ))
        ) : (
          <div className="col-span-full py-20 text-center">
            <div className="bg-slate-50 inline-block p-10 rounded-3xl border-2 border-dashed border-slate-200">
              <p className="text-slate-400 font-medium">
                Bạn không có quyền thực hiện chức năng quản trị nào.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
