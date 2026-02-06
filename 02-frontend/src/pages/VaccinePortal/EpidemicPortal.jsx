import React, { useState, useEffect } from "react";
import {
  Search,
  MapPin,
  Activity,
  Wind,
  AlertCircle,
  Syringe,
  Calendar,
  Info,
  ShieldAlert,
  StickyNote, // Thêm StickyNote
} from "lucide-react";
import epidemicApi from "../../api/epidemicApi";
import toast from "react-hot-toast";

const EpidemicPortal = () => {
  const [epidemics, setEpidemics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");

  const loadData = async (query = "") => {
    setLoading(true);
    try {
      const data = query
        ? await epidemicApi.searchByLocation(query)
        : await epidemicApi.getAll();
      setEpidemics(data);
    } catch (error) {
      toast.error(
        "Không thể tải thông tin dịch tễ địa phương: " + error.message,
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => loadData(searchQuery), 500);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  return (
    <div className="max-w-7xl mx-auto space-y-8 p-4 md:p-6 animate-in fade-in duration-700">
      {/* Header Section */}
      <div className="bg-white rounded-[2rem] p-8 border border-slate-100 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="space-y-2">
          <h1 className="text-3xl font-black text-slate-800 uppercase tracking-tight flex items-center gap-3">
            <ShieldAlert size={36} className="text-rose-500" />
            Tình hình dịch bệnh địa phương
          </h1>
          <p className="text-slate-500 font-medium">
            Tra cứu thông tin dịch bệnh tại địa phương để chủ động phòng ngừa.
          </p>
        </div>

        <div className="relative w-full md:w-96 group">
          <Search
            className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-rose-500 transition-colors"
            size={20}
          />
          <input
            type="text"
            placeholder="Tìm theo địa bàn của bạn..."
            className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:ring-4 focus:ring-rose-500/10 focus:border-rose-500 transition-all font-bold text-slate-700"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Grid Danh sách thẻ */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3].map((i) => (
            <div
              key={i}
              className="h-64 bg-slate-100 animate-pulse rounded-[2rem]"
            />
          ))}
        </div>
      ) : epidemics.length === 0 ? (
        <div className="text-center py-20 bg-white rounded-[3rem] border border-slate-100 shadow-sm">
          <Info size={48} className="mx-auto text-slate-200 mb-4" />
          <p className="text-slate-400 font-bold text-xl">
            Hiện chưa ghi nhận dịch bệnh tại khu vực này.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {epidemics.map((item) => (
            <div
              key={item.maDichBenh}
              className="bg-white rounded-[2rem] border border-slate-100 p-6 shadow-sm hover:shadow-xl hover:-translate-y-2 transition-all duration-300 flex flex-col"
            >
              <div className="flex justify-between items-start mb-6">
                <span className="bg-rose-50 text-rose-600 px-3 py-1 rounded-xl text-[10px] font-black uppercase tracking-widest flex items-center gap-1">
                  <Activity size={12} /> Số ca: {item.soNguoiBiNhiem}
                </span>
                <span className="text-[10px] font-bold text-slate-400 flex items-center gap-1 uppercase">
                  <Calendar size={12} /> {item.thoiDiemKhaoSat}
                </span>
              </div>

              <div className="mb-6">
                <h3 className="text-xl font-black text-slate-800 uppercase leading-tight mb-2">
                  {item.tenDichBenh}
                </h3>
                <div className="flex items-center gap-1 text-rose-500 font-bold text-xs uppercase italic">
                  <MapPin size={14} /> {item.diaChi}
                </div>
              </div>

              {/* Chi tiết dịch tễ */}
              <div className="space-y-3 flex-1 border-t border-slate-50 pt-4 mb-6">
                <div className="bg-blue-50/50 p-3 rounded-xl">
                  <span className="text-[10px] font-black text-blue-400 uppercase block mb-1">
                    Đường lây nhiễm
                  </span>
                  <p className="text-xs font-bold text-slate-700 italic flex items-center gap-1">
                    <Wind size={14} className="text-blue-500" />{" "}
                    {item.duongLayNhiem}
                  </p>
                </div>

                <div className="bg-amber-50/50 p-3 rounded-xl">
                  <span className="text-[10px] font-black text-amber-500 uppercase block mb-1">
                    Tác hại sức khỏe
                  </span>
                  <p className="text-xs font-bold text-slate-700 italic flex items-center gap-1">
                    <AlertCircle size={14} className="text-amber-500" />{" "}
                    {item.tacHaiSucKhoe}
                  </p>
                </div>

                {/* THÊM PHẦN GHI CHÚ TẠI ĐÂY */}
                {item.ghiChu && (
                  <div className="bg-slate-50 p-3 rounded-xl border border-dashed border-slate-200">
                    <span className="text-[10px] font-black text-slate-400 uppercase block mb-1">
                      Lưu ý từ y tế
                    </span>
                    <p className="text-[11px] font-medium text-slate-500 italic flex items-start gap-1">
                      <StickyNote size={13} className="mt-0.5 text-slate-400" />{" "}
                      {item.ghiChu}
                    </p>
                  </div>
                )}
              </div>

              {/* Vắc-xin gợi ý */}
              <div className="mt-auto space-y-2">
                <span className="text-[10px] font-black text-emerald-600 uppercase tracking-widest flex items-center gap-1">
                  <Syringe size={12} /> Vắc-xin phòng bệnh:
                </span>
                <div className="flex flex-wrap gap-2">
                  {item.vacXinGoiY?.length > 0 ? (
                    item.vacXinGoiY.map((v, i) => (
                      <span
                        key={i}
                        className="px-3 py-1 bg-emerald-50 text-emerald-600 text-[9px] font-black rounded-lg border border-emerald-100 italic uppercase"
                      >
                        {v}
                      </span>
                    ))
                  ) : (
                    <span className="text-[9px] text-slate-300 italic font-medium">
                      Đang cập nhật vắc-xin...
                    </span>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EpidemicPortal;
