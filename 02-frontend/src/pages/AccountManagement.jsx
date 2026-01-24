import React, { useState, useEffect, useCallback } from "react";
import {
  UserPlus,
  Search,
  Trash2,
  ShieldCheck,
  ChevronLeft,
  ChevronRight,
  Lock,
  Unlock,
} from "lucide-react";
import CreateUserModal from "../components/modals/CreateUserModal";
import ConfirmModal from "../components/modals/ConfirmModal";
import axiosClient from "../api/axiosClient";
import toast from "react-hot-toast";

const AccountManagement = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null); // Để lưu user khi nhấn Sửa

  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [userToToggle, setUserToToggle] = useState(null);
  const [confirmLoading, setConfirmLoading] = useState(false);

  // Trạng thái phân trang khớp với PageResponse ở Backend
  const [pagination, setPagination] = useState({
    currentPage: 1,
    totalPages: 0,
    pageSize: 10,
    totalElements: 0,
  });

  // Khi nhấn vào nút Khóa/Mở khóa trên bảng
  const handleToggleClick = (user) => {
    setUserToToggle(user);
    setIsConfirmOpen(true);
  };

  // Khi nhấn xác nhận trong Modal đẹp
  const confirmToggleStatus = async () => {
    if (!userToToggle) return;

    try {
      setConfirmLoading(true);
      await axiosClient.patch(
        `/users/${userToToggle.maTaiKhoan}/toggle-status`,
      );

      // THÔNG BÁO ĐẸP SAU KHI XONG
      const action = userToToggle.trangThai ? "khóa" : "mở khóa";
      toast.success(
        `Tài khoản ${userToToggle.tenDangNhap} đã được ${action}!`,
        {
          icon: userToToggle.trangThai ? "🔒" : "🔓",
          style: { borderRadius: "12px", background: "#333", color: "#fff" },
        },
      );

      setIsConfirmOpen(false);
      fetchUsers(pagination.currentPage); // Tải lại danh sách
    } catch (error) {
      toast.error("Không thể cập nhật trạng thái: " + error.message);
    } finally {
      setConfirmLoading(false);
    }
  };

  // Hàm lấy dữ liệu từ Backend
  const fetchUsers = useCallback(
    async (page = 1) => {
      setLoading(true);
      try {
        // axiosClient đã trả về phần 'result' (tức là PageResponse)
        const result = await axiosClient.get(
          `/users?page=${page}&size=${pagination.pageSize}`,
        );

        // 1. Bóc tách các trường từ PageResponse.java
        // Lưu ý: Backend dùng 'data', không phải 'content'
        const { data, totalPages, currentPage, totalElements } = result;

        // 2. Cập nhật State
        setUsers(data || []);
        setPagination((prev) => ({
          ...prev,
          currentPage: currentPage, // Backend đã trả về số trang (1-indexed)
          totalPages: totalPages,
          totalElements: totalElements,
        }));
      } catch (error) {
        toast.error("Không thể tải danh sách tài khoản: " + error);
      } finally {
        setLoading(false);
      }
    },
    [pagination.pageSize],
  );

  const handleEditClick = (user) => {
    setSelectedUser(user);
    setIsModalOpen(true);
  };

  // Load dữ liệu khi trang web vừa mở
  useEffect(() => {
    fetchUsers(1);
  }, [fetchUsers]);

  // Xử lý sau khi thêm User thành công (để danh sách tự cập nhật)
  const handleCreateSuccess = () => {
    setIsModalOpen(false);
    setSelectedUser(null); // BỔ SUNG: Reset lại selectedUser sau khi xong
    fetchUsers(pagination.currentPage);
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
          onClick={() => {
            setSelectedUser(null); // BỔ SUNG: Reset lại selectedUser khi tạo mới
            setIsModalOpen(true);
          }}
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
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase">
                  Trạng thái
                </th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase text-right">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="5" className="text-center py-10 text-slate-400">
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr
                    key={user.maTaiKhoan}
                    className={
                      !user.trangThai
                        ? "opacity-60 bg-slate-50/50"
                        : "hover:bg-slate-50/50"
                    }
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

                    <td className="px-6 py-4">
                      <span
                        className={`px-2.5 py-1 rounded-full text-[10px] font-bold ${user.trangThai ? "bg-green-100 text-green-600" : "bg-rose-100 text-rose-600"}`}
                      >
                        {user.trangThai ? "Hoạt động" : "Bị khóa"}
                      </span>
                    </td>

                    <td className="px-6 py-4 text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => handleEditClick(user)}
                          className="p-2 hover:bg-blue-100 text-blue-600 rounded-lg cursor-pointer"
                          title="Sửa"
                        >
                          <ShieldCheck size={18} />
                        </button>
                        <button
                          onClick={() => handleToggleClick(user)}
                          className={`p-2 rounded-lg cursor-pointer ${user.trangThai ? "text-amber-500 hover:bg-amber-50" : "text-green-500 hover:bg-green-50"}`}
                          title={user.trangThai ? "Khóa" : "Mở khóa"}
                        >
                          {user.trangThai ? (
                            <Lock size={18} />
                          ) : (
                            <Unlock size={18} />
                          )}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* PHÂN TRANG */}
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
        onClose={() => {
          setIsModalOpen(false);
          setSelectedUser(null); // BỔ SUNG: Reset lại selectedUser khi đóng modal
        }}
        onSuccess={handleCreateSuccess} // Truyền callback để refresh dữ liệu
        selectedUser={selectedUser} // BỔ SUNG: Truyền selectedUser vào đây
      />

      {/* Modal Xác nhận Khóa (Chỉ đặt 1 cái duy nhất ở ngoài vòng lặp) */}
      <ConfirmModal
        isOpen={isConfirmOpen}
        onClose={() => setIsConfirmOpen(false)}
        onConfirm={confirmToggleStatus}
        loading={confirmLoading}
        type={userToToggle?.trangThai ? "warning" : "success"}
        title={
          userToToggle
            ? userToToggle.trangThai
              ? "Khóa tài khoản"
              : "Mở khóa tài khoản"
            : ""
        }
        message={
          userToToggle
            ? userToToggle.trangThai
              ? `Bạn có chắc chắn muốn khóa tài khoản "${userToToggle.tenDangNhap}"?`
              : `Bạn muốn mở khóa cho tài khoản "${userToToggle.tenDangNhap}"?`
            : ""
        }
      />
    </div>
  );
};

export default AccountManagement;
