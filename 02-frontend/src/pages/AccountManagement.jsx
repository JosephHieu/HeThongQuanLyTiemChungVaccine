import React, { useState, useEffect, useCallback } from "react";
import {
  UserPlus,
  Search,
  Trash2,
  ShieldCheck,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import CreateUserModal from "../components/modals/CreateUserModal";
import axiosClient from "../api/axiosClient";
import toast from "react-hot-toast";

const AccountManagement = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  // Trạng thái phân trang khớp với PageResponse ở Backend
  const [pagination, setPagination] = useState({
    currentPage: 1,
    totalPages: 0,
    pageSize: 10,
    totalElements: 0,
  });

  // Hàm lấy dữ liệu từ Backend
  const fetchUsers = useCallback(
    async (page = 1) => {
      setLoading(true);
      try {
        const response = await axiosClient.get(
          `/users?page=${page}&size=${pagination.pageSize}`
        );
        const { data, totalPages, currentPage, totalElements } =
          response.data.result;

        setUsers(data);
        setPagination((prev) => ({
          ...prev,
          currentPage,
          totalPages,
          totalElements,
        }));
      } catch (error) {
        toast.error("Không thể tải danh sách tài khoản." + error.message);
      } finally {
        setLoading(false);
      }
    },
    [pagination.pageSize]
  );

  // Load dữ liệu khi trang web vừa mở
  useEffect(() => {
    fetchUsers(1);
  }, [fetchUsers]);

  // Xử lý sau khi thêm User thành công (để danh sách tự cập nhật)
  const handleCreateSuccess = () => {
    setIsModalOpen(false);
    fetchUsers(1); // Tải lại trang đầu tiên để thấy người dùng mới nhất
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* HEADER SECTION - Giữ nguyên logic cũ nhưng thêm handle success */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Quản lý tài khoản
          </h1>
          <p className="text-slate-500 text-sm">
            Hiển thị {pagination.totalElements} tài khoản nhân viên
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 text-white font-semibold rounded-xl
             hover:bg-blue-700 transition-all cursor-pointer"
        >
          <UserPlus size={20} /> Thêm tài khoản
        </button>
      </div>

      {/* SEARCH BAR ... (giữ nguyên giao diện của bạn) */}

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
              {loading ? (
                <tr>
                  <td colSpan="4" className="text-center py-10 text-slate-400">
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr
                    key={user.maTaiKhoan}
                    className="hover:bg-blue-50/30 transition-colors group"
                  >
                    <td className="px-6 py-4 font-medium text-slate-700">
                      {user.tenDangNhap}
                    </td>
                    <td className="px-6 py-4 text-slate-600">{user.hoTen}</td>
                    <td className="px-6 py-4">
                      <div className="flex flex-wrap gap-1">
                        {user.roles.map((role, idx) => (
                          <span
                            key={idx}
                            className="px-3 py-1 rounded-full text-xs font-bold bg-blue-100 text-blue-600"
                          >
                            {role}
                          </span>
                        ))}
                      </div>
                    </td>
                    {/* Các nút Thao tác giữ nguyên */}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* BỔ SUNG: THANH ĐIỀU KHIỂN PHÂN TRANG */}
        <div className="flex items-center justify-between px-6 py-4 bg-slate-50 border-t border-slate-100">
          <p className="text-sm text-slate-500">
            Trang {pagination.currentPage} trên {pagination.totalPages}
          </p>
          <div className="flex gap-2">
            <button
              disabled={pagination.currentPage === 1}
              onClick={() => fetchUsers(pagination.currentPage - 1)}
              className="p-2 border rounded-lg hover:bg-white disabled:opacity-30 transition-all"
            >
              <ChevronLeft size={20} />
            </button>
            <button
              disabled={pagination.currentPage === pagination.totalPages}
              onClick={() => fetchUsers(pagination.currentPage + 1)}
              className="p-2 border rounded-lg hover:bg-white disabled:opacity-30 transition-all"
            >
              <ChevronRight size={20} />
            </button>
          </div>
        </div>
      </div>

      <CreateUserModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleCreateSuccess} // Truyền callback để refresh dữ liệu
      />
    </div>
  );
};

export default AccountManagement;
