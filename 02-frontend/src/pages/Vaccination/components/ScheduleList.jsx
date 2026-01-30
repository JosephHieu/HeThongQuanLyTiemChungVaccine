import React from "react";
import {
  Eye,
  Trash2,
  Calendar,
  Clock,
  MapPin,
  Search,
  Syringe,
  Users,
  Hash,
} from "lucide-react";

const ScheduleList = ({
  listData,
  onEdit,
  onDelete,
  queryParams,
  setQueryParams,
}) => {
  return (
    <div className="space-y-4 animate-in fade-in slide-in-from-bottom-4 duration-500">
      {/* --- THANH TÌM KIẾM & THỐNG KÊ NHANH --- */}
      <div className="flex flex-col md:flex-row gap-4 items-center bg-white p-5 rounded-[2rem] shadow-sm border border-slate-100">
        <div className="flex-1 relative w-full">
          <Search
            className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm theo địa điểm tổ chức hoặc thông tin lịch..."
            className="w-full pl-12 pr-4 py-3.5 bg-slate-50 rounded-2xl text-sm font-bold outline-none border-2 border-transparent focus:border-blue-100 focus:bg-white transition-all"
            value={queryParams.search}
            onChange={(e) =>
              setQueryParams({
                ...queryParams,
                search: e.target.value,
                page: 1,
              })
            }
          />
        </div>
        <div className="hidden md:flex items-center gap-6 px-4">
          <div className="text-center">
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
              Tổng lịch
            </p>
            <p className="text-sm font-black text-blue-600">
              {listData.totalElements || 0}
            </p>
          </div>
          <div className="w-px h-8 bg-slate-100"></div>
          <div className="text-center">
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
              Trang hiện tại
            </p>
            <p className="text-sm font-black text-slate-700">
              {listData.currentPage || 1}
            </p>
          </div>
        </div>
      </div>

      {/* --- BẢNG DỮ LIỆU --- */}
      <div className="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50/50 border-b border-slate-100">
                <th className="p-5 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Thời gian & Ca trực
                </th>
                <th className="p-5 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Vắc-xin & Số lô
                </th>
                <th className="p-5 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Địa điểm tổ chức
                </th>
                <th className="p-5 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                  Tỷ lệ Đăng ký
                </th>
                <th className="p-5 text-[10px] font-black text-slate-400 uppercase tracking-widest text-right">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {listData.data && listData.data.length > 0 ? (
                listData.data.map((item) => (
                  <tr
                    key={item.maLichTiemChung}
                    className="hover:bg-blue-50/20 transition-all group"
                  >
                    {/* Ngày & Ca trực */}
                    <td className="p-5">
                      <div className="flex flex-col gap-1.5">
                        <span className="font-black text-slate-700 text-sm flex items-center gap-2">
                          <Calendar size={14} className="text-blue-500" />{" "}
                          {item.ngayTiem}
                        </span>
                        <span
                          className={`w-fit px-2.5 py-1 rounded-lg text-[9px] font-black uppercase tracking-wider ${
                            item.thoiGian.includes("Sáng")
                              ? "bg-amber-50 text-amber-600 border border-amber-100"
                              : "bg-indigo-50 text-indigo-600 border border-indigo-100"
                          }`}
                        >
                          {item.thoiGian}
                        </span>
                      </div>
                    </td>

                    {/* Vắc-xin & Lô */}
                    <td className="p-5">
                      <div className="flex flex-col">
                        <span className="font-bold text-slate-800 text-sm flex items-center gap-1.5">
                          <Syringe size={14} className="text-slate-400" />{" "}
                          {item.tenVacXin}
                        </span>
                        <span className="text-[11px] font-bold text-blue-500 mt-0.5 ml-5">
                          Lô: {item.soLo}
                        </span>
                      </div>
                    </td>

                    {/* Địa điểm */}
                    <td className="p-5">
                      <div className="flex items-start gap-2 max-w-[200px]">
                        <MapPin
                          size={14}
                          className="text-slate-300 mt-1 shrink-0"
                        />
                        <span className="text-sm font-medium text-slate-600 leading-tight">
                          {item.diaDiem}
                        </span>
                      </div>
                    </td>

                    {/* Quy mô & Tiến độ đăng ký */}
                    <td className="p-5">
                      <div className="flex flex-col gap-2 w-32">
                        <div className="flex justify-between items-end">
                          <span className="text-[10px] font-black text-slate-400 uppercase flex items-center gap-1">
                            <Users size={12} /> {item.daDangKy || 0} /{" "}
                            {item.soLuong}
                          </span>
                          <span className="text-[10px] font-black text-blue-600 italic">
                            {Math.round(
                              ((item.daDangKy || 0) / item.soLuong) * 100,
                            )}
                            %
                          </span>
                        </div>
                        <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                          <div
                            className="h-full bg-blue-500 transition-all duration-700 ease-out"
                            style={{
                              width: `${Math.min(((item.daDangKy || 0) / item.soLuong) * 100, 100)}%`,
                            }}
                          ></div>
                        </div>
                      </div>
                    </td>

                    {/* Thao tác */}
                    <td className="p-5 text-right">
                      <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button
                          onClick={() => onEdit(item)}
                          className="p-3 text-blue-600 hover:bg-blue-600 hover:text-white rounded-2xl transition-all shadow-sm bg-blue-50"
                          title="Xem chi tiết và điều chỉnh"
                        >
                          <Eye size={18} />
                        </button>
                        <button
                          onClick={() => onDelete(item.maLichTiemChung)}
                          className="p-3 text-red-500 hover:bg-red-500 hover:text-white rounded-2xl transition-all shadow-sm bg-red-50"
                          title="Xóa lịch trực này"
                        >
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" className="p-20 text-center">
                    <div className="flex flex-col items-center gap-3">
                      <div className="p-4 bg-slate-50 rounded-full">
                        <Search size={40} className="text-slate-200" />
                      </div>
                      <p className="text-slate-400 font-bold italic text-sm">
                        Không tìm thấy lịch tiêm chủng nào khớp với dữ liệu tìm
                        kiếm...
                      </p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* --- PHÂN TRANG --- */}
        <div className="p-6 border-t border-slate-50 flex flex-col sm:flex-row justify-between items-center gap-4 bg-slate-50/30">
          <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">
            Trang <span className="text-slate-700">{queryParams.page}</span>{" "}
            trên tổng{" "}
            <span className="text-slate-700">{listData.totalPages || 1}</span>
          </p>
          <div className="flex gap-3">
            <button
              disabled={queryParams.page === 1}
              onClick={() =>
                setQueryParams({ ...queryParams, page: queryParams.page - 1 })
              }
              className="flex items-center gap-2 px-6 py-2.5 text-xs font-black uppercase bg-white border-2 border-slate-100 rounded-xl hover:border-blue-200 disabled:opacity-30 disabled:hover:border-slate-100 transition-all shadow-sm"
            >
              Trước đó
            </button>
            <button
              disabled={queryParams.page >= listData.totalPages}
              onClick={() =>
                setQueryParams({ ...queryParams, page: queryParams.page + 1 })
              }
              className="flex items-center gap-2 px-6 py-2.5 text-xs font-black uppercase bg-white border-2 border-slate-100 rounded-xl hover:border-blue-200 disabled:opacity-30 disabled:hover:border-slate-100 transition-all shadow-sm"
            >
              Tiếp theo
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ScheduleList;
