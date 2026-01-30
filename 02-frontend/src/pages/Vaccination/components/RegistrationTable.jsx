import React, { useState } from "react";
import {
  Search,
  UserCheck,
  MoreVertical,
  Phone,
  Syringe,
  SearchX,
  Loader2,
} from "lucide-react";

const RegistrationTable = ({ data = [], loading }) => {
  const [searchTerm, setSearchTerm] = useState("");

  // Logic lọc dữ liệu dựa trên tên bệnh nhân hoặc số điện thoại
  const filteredData = data.filter(
    (item) =>
      item.tenBenhNhan?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.soDienThoai?.includes(searchTerm),
  );

  return (
    <div className="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden transition-all duration-300">
      {/* --- HEADER & SEARCH (Responsive) --- */}
      <div className="p-6 border-b border-slate-50 flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white">
        <div>
          <h3 className="text-lg font-black text-slate-800 uppercase tracking-tight flex items-center gap-2">
            <span className="w-2 h-6 bg-emerald-500 rounded-full"></span>
            Danh sách đăng ký
          </h3>
          <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider mt-1">
            Tổng số: {filteredData.length} bệnh nhân
          </p>
        </div>

        <div className="relative w-full md:w-72 group">
          <Search
            className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-blue-500 transition-colors"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm theo tên, SĐT..."
            className="w-full pl-11 pr-4 py-3 bg-slate-50 border-2 border-transparent focus:border-blue-100 focus:bg-white rounded-2xl text-sm outline-none transition-all font-medium"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* --- TABLE CONTENT (Horizontal Scroll on Mobile) --- */}
      <div className="overflow-x-auto custom-scrollbar">
        <table className="w-full text-left border-collapse min-w-[700px]">
          <thead>
            <tr className="bg-slate-50/50">
              <th className="py-4 px-6 text-[11px] font-black text-slate-400 uppercase tracking-widest">
                Bệnh nhân
              </th>
              <th className="py-4 px-6 text-[11px] font-black text-slate-400 uppercase tracking-widest">
                Liên hệ
              </th>
              <th className="py-4 px-6 text-[11px] font-black text-slate-400 uppercase tracking-widest">
                Loại vắc-xin
              </th>
              <th className="py-4 px-6 text-[11px] font-black text-slate-400 uppercase tracking-widest text-right">
                Thao tác
              </th>
            </tr>
          </thead>

          <tbody className="divide-y divide-slate-50">
            {loading ? (
              /* Loading State */
              <tr>
                <td colSpan="4" className="py-20 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <Loader2 className="w-10 h-10 text-blue-500 animate-spin" />
                    <p className="text-slate-400 text-xs font-bold uppercase tracking-widest">
                      Đang tải dữ liệu...
                    </p>
                  </div>
                </td>
              </tr>
            ) : filteredData.length > 0 ? (
              /* Data Rows */
              filteredData.map((item, index) => (
                <tr
                  key={item.maDangKy || index}
                  className="hover:bg-blue-50/30 transition-all group"
                >
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 bg-slate-100 text-slate-500 group-hover:bg-blue-600 group-hover:text-white rounded-2xl flex items-center justify-center font-bold text-sm transition-all shadow-sm">
                        {item.tenBenhNhan?.charAt(0)}
                      </div>
                      <div>
                        <p className="text-sm font-bold text-slate-800">
                          {item.tenBenhNhan}
                        </p>
                        <p className="text-[10px] text-slate-400 font-bold uppercase tracking-tighter mt-0.5">
                          ID: {item.maDangKy?.slice(0, 8) || "PENDING"}
                        </p>
                      </div>
                    </div>
                  </td>

                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2 text-sm text-slate-600 font-medium">
                      <Phone size={14} className="text-slate-300" />
                      {item.soDienThoai}
                    </div>
                  </td>

                  <td className="py-4 px-6">
                    <span className="inline-flex items-center gap-2 px-4 py-1.5 rounded-xl bg-blue-50 text-blue-600 border border-blue-100 text-[11px] font-black uppercase tracking-wider">
                      <Syringe size={12} />
                      {item.tenVacXin || "Chưa xác định"}
                    </span>
                  </td>

                  <td className="py-4 px-6 text-right">
                    <button className="p-2.5 hover:bg-white rounded-xl text-slate-300 hover:text-blue-600 transition-all border border-transparent hover:border-slate-100 shadow-transparent hover:shadow-sm">
                      <MoreVertical size={18} />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              /* Empty State */
              <tr>
                <td colSpan="4" className="py-20 text-center">
                  <div className="flex flex-col items-center gap-4">
                    <div className="w-16 h-16 bg-slate-50 rounded-[2rem] flex items-center justify-center text-slate-200">
                      <SearchX size={32} />
                    </div>
                    <div className="space-y-1">
                      <p className="text-slate-800 font-black uppercase text-xs">
                        Không có kết quả
                      </p>
                      <p className="text-slate-400 text-[11px] italic font-medium">
                        Vui lòng kiểm tra lại từ khóa hoặc chọn ngày khác
                      </p>
                    </div>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* --- FOOTER --- */}
      <div className="p-4 bg-slate-50/30 border-t border-slate-50 flex justify-between items-center text-[10px] text-slate-400 font-bold uppercase tracking-widest">
        <span>Dữ liệu thời gian thực</span>
        <span>Cập nhật: {new Date().toLocaleTimeString("vi-VN")}</span>
      </div>
    </div>
  );
};

export default RegistrationTable;
