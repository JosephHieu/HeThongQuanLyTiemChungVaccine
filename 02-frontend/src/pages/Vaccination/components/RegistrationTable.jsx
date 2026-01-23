import React, { useState } from "react";
import { Search, UserCheck, MoreVertical, Phone, Calendar } from "lucide-react";

const RegistrationTable = ({ data = [] }) => {
  const [searchTerm, setSearchTerm] = useState("");

  // Logic lọc dữ liệu theo tên hoặc số điện thoại
  const filteredData = data.filter(
    (item) =>
      item.hoTen.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.soDienThoai.includes(searchTerm),
  );

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
      {/* Header của bảng */}
      <div className="p-6 border-b border-slate-50 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h3 className="text-lg font-bold text-slate-800">
            Danh sách đăng ký
          </h3>
          <p className="text-sm text-slate-500">
            Hiển thị những người đã đăng ký cho đợt tiêm này
          </p>
        </div>

        {/* Thanh tìm kiếm nhanh */}
        <div className="relative">
          <Search
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm theo tên, SĐT..."
            className="pl-10 pr-4 py-2 bg-slate-50 border-none rounded-xl text-sm focus:ring-2 focus:ring-blue-500 w-full md:w-64 transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* Nội dung bảng */}
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/50">
              <th className="py-4 px-6 text-xs font-bold text-slate-500 uppercase tracking-wider">
                Bệnh nhân
              </th>
              <th className="py-4 px-6 text-xs font-bold text-slate-500 uppercase tracking-wider">
                Liên hệ
              </th>
              <th className="py-4 px-6 text-xs font-bold text-slate-500 uppercase tracking-wider">
                Trạng thái
              </th>
              <th className="py-4 px-6 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {filteredData.length > 0 ? (
              filteredData.map((item, index) => (
                <tr
                  key={index}
                  className="hover:bg-blue-50/30 transition-colors group"
                >
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold text-sm">
                        {item.hoTen.charAt(0)}
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-800">
                          {item.hoTen}
                        </p>
                        <p className="text-xs text-slate-400">
                          ID: {item.maBenhNhan || "BN-001"}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center gap-2 text-sm text-slate-600">
                        <Phone size={14} className="text-slate-400" />
                        {item.soDienThoai}
                      </div>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <span
                      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium ${
                        item.trangThai === "Đã tiêm"
                          ? "bg-green-100 text-green-700"
                          : "bg-amber-100 text-amber-700"
                      }`}
                    >
                      <div
                        className={`w-1.5 h-1.5 rounded-full ${
                          item.trangThai === "Đã tiêm"
                            ? "bg-green-500"
                            : "bg-amber-500"
                        }`}
                      />
                      {item.trangThai || "Chờ tiêm"}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-right">
                    <button className="p-2 hover:bg-white rounded-lg text-slate-400 hover:text-blue-600 transition-all shadow-sm">
                      <MoreVertical size={18} />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4" className="py-16 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center text-slate-300">
                      <UserCheck size={32} />
                    </div>
                    <p className="text-slate-400 text-sm">
                      Không tìm thấy dữ liệu phù hợp
                    </p>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Footer bảng (Phân trang giả) */}
      <div className="p-4 bg-slate-50/30 border-t border-slate-50 flex justify-between items-center text-xs text-slate-500 font-medium">
        <span>Hiển thị {filteredData.length} kết quả</span>
        <div className="flex gap-2">
          <button
            className="px-3 py-1 bg-white border border-slate-200 rounded-md hover:bg-slate-50 disabled:opacity-50"
            disabled
          >
            Trước
          </button>
          <button
            className="px-3 py-1 bg-white border border-slate-200 rounded-md hover:bg-slate-50 disabled:opacity-50"
            disabled
          >
            Sau
          </button>
        </div>
      </div>
    </div>
  );
};

export default RegistrationTable;
