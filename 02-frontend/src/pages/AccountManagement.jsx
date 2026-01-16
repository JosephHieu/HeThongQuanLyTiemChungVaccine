import React, { useState, useEffect } from "react";
import {
  UserPlus,
  Search,
  Trash2,
  ShieldCheck,
  MoreVertical,
} from "lucide-react";
import CreateUserModal from "../components/modals/CreateUserModal"; // Component đã làm ở bước trước
import toast from "react-hot-toast";

const AccountManagement = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [users, setUsers] = useState([
    // Dữ liệu mẫu để bạn thấy giao diện, sau này sẽ gọi từ API Backend
    {
      maTaiKhoan: "1",
      tenDangNhap: "admin_hieu",
      hoTen: "Nguyễn Joseph Hiếu",
      email: "hieu@example.com",
      roles: ["Administrator"],
    },
    {
      maTaiKhoan: "2",
      tenDangNhap: "mod_lan",
      hoTen: "Lê Thị Lan",
      email: "lan@example.com",
      roles: ["Moderator"],
    },
  ]);

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* HEADER SECTION */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Quản lý tài khoản
          </h1>
          <p className="text-slate-500 text-sm">
            Thực hiện tạo mới, xóa và phân quyền cho tài khoản nhân viên
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-xl font-bold shadow-lg shadow-blue-200 transition-all active:scale-95"
        >
          <UserPlus size={20} />
          Thêm tài khoản
        </button>
      </div>

      {/* SEARCH & FILTER TOOLBAR */}
      <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-100 flex items-center gap-4">
        <div className="relative flex-1">
          <Search
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm kiếm theo tên hoặc username..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* DATA TABLE SECTION */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-100">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase">
                  Username
                </th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase">
                  Họ tên
                </th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase">
                  Quyền hạn
                </th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase text-right">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((user) => (
                <tr
                  key={user.maTaiKhoan}
                  className="hover:bg-blue-50/30 transition-colors group"
                >
                  <td className="px-6 py-4 font-medium text-slate-700">
                    {user.tenDangNhap}
                  </td>
                  <td className="px-6 py-4 text-slate-600">{user.hoTen}</td>
                  <td className="px-6 py-4">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-bold ${
                        user.roles.includes("Administrator")
                          ? "bg-purple-100 text-purple-600"
                          : "bg-blue-100 text-blue-600"
                      }`}
                    >
                      {user.roles[0]}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button
                        className="p-2 hover:bg-blue-100 text-blue-600 rounded-lg transition-colors"
                        title="Phân quyền"
                      >
                        <ShieldCheck size={18} />
                      </button>
                      <button
                        className="p-2 hover:bg-rose-100 text-rose-600 rounded-lg transition-colors"
                        title="Xóa tài khoản"
                      >
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* MODAL TẠO USER */}
      <CreateUserModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      />
    </div>
  );
};

export default AccountManagement;
