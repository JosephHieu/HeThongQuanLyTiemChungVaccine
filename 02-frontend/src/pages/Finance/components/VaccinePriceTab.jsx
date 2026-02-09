import React from "react";
import {
  TrendingUp,
  Edit3,
  Info,
  ArrowUpRight,
  ArrowDownRight,
  ShieldCheck,
  Zap,
} from "lucide-react";

const VaccinePriceTab = ({ searchTerm }) => {
  // Dữ liệu mẫu (Sẽ kết nối với API: /api/v1/finance/vaccines/prices)
  const vaccinePrices = [
    {
      id: 1,
      name: "Vắc xin 6 trong 1 (Infanrix)",
      category: "Gói tiêm lẻ",
      basePrice: 950000,
      currentPrice: 1100000,
      change: "+5%",
      isIncreasing: true,
      inventory: 120,
    },
    {
      id: 2,
      name: "Phế cầu (Synflorix)",
      category: "Gói tiêm lẻ",
      basePrice: 880000,
      currentPrice: 1020000,
      change: "+2%",
      isIncreasing: true,
      inventory: 45,
    },
    {
      id: 3,
      name: "Cúm mùa (Vaxigrip Tetra)",
      category: "Khuyến mãi",
      basePrice: 320000,
      currentPrice: 290000,
      change: "-10%",
      isIncreasing: false,
      inventory: 300,
    },
    {
      id: 4,
      name: "Thủy đậu (Varilrix)",
      category: "Gói tiêm lẻ",
      basePrice: 750000,
      currentPrice: 850000,
      change: "0%",
      isIncreasing: true,
      inventory: 15,
    },
  ];

  // Logic lọc theo tên vắc xin
  const filteredData = vaccinePrices.filter((item) =>
    item.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  return (
    <div className="space-y-6">
      {/* 1. THẺ TỔNG QUAN BIẾN ĐỘNG GIÁ */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <PriceSummaryCard
          label="Tổng loại Vắc xin"
          value={vaccinePrices.length}
          icon={<Zap size={16} />}
          color="blue"
        />
        <PriceSummaryCard
          label="Lợi nhuận trung bình"
          value="18.5%"
          icon={<TrendingUp size={16} />}
          color="emerald"
        />
        <PriceSummaryCard
          label="Vắc xin sắp hết hàng"
          value="02 loại"
          icon={<ArrowDownRight size={16} />}
          color="rose"
        />
        <PriceSummaryCard
          label="Giá trị kho dự kiến"
          value="1.2 tỷ"
          icon={<ShieldCheck size={16} />}
          color="amber"
        />
      </div>

      {/* 2. BẢNG QUẢN LÝ GIÁ */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-50 flex justify-between items-center bg-slate-50/30">
          <h3 className="font-black text-slate-800 flex items-center gap-2 uppercase text-sm">
            <TrendingUp size={18} className="text-emerald-500" /> Bảng kê khai &
            Điều chỉnh giá bán
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-4">Sản phẩm Vắc xin</th>
                <th className="px-6 py-4">Giá nhập (Dự kiến)</th>
                <th className="px-6 py-4">Giá bán niêm yết</th>
                <th className="px-6 py-4 text-center">Biến động</th>
                <th className="px-6 py-4 text-center">Tồn kho</th>
                <th className="px-8 py-4 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm">
              {filteredData.map((vax) => (
                <tr
                  key={vax.id}
                  className="hover:bg-slate-50/50 transition-colors"
                >
                  <td className="px-8 py-4">
                    <p className="text-slate-800 font-bold">{vax.name}</p>
                    <span className="text-[10px] bg-slate-100 px-2 py-0.5 rounded text-slate-500 font-bold uppercase">
                      {vax.category}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-slate-400 font-bold">
                    {vax.basePrice.toLocaleString()} đ
                  </td>
                  <td className="px-6 py-4">
                    <p className="text-emerald-600 font-black text-base">
                      {vax.currentPrice.toLocaleString()} đ
                    </p>
                    <p className="text-[9px] text-slate-400 italic">
                      Lợi nhuận:{" "}
                      {(
                        ((vax.currentPrice - vax.basePrice) / vax.basePrice) *
                        100
                      ).toFixed(1)}
                      %
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex justify-center">
                      <span
                        className={`flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-black ${
                          vax.isIncreasing
                            ? "bg-emerald-50 text-emerald-600"
                            : "bg-rose-50 text-rose-600"
                        }`}
                      >
                        {vax.isIncreasing ? (
                          <ArrowUpRight size={10} />
                        ) : (
                          <ArrowDownRight size={10} />
                        )}
                        {vax.change}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <span
                      className={`font-bold ${vax.inventory < 20 ? "text-rose-500 animate-pulse" : "text-slate-600"}`}
                    >
                      {vax.inventory} liều
                    </span>
                  </td>
                  <td className="px-8 py-4 text-right">
                    <div className="flex justify-end gap-2">
                      <button className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all">
                        <Info size={18} />
                      </button>
                      <button className="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all">
                        <Edit3 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

// Component con cho thẻ tóm tắt
const PriceSummaryCard = ({ label, value, icon, color }) => {
  const colorMap = {
    blue: "bg-blue-50 text-blue-600",
    emerald: "bg-emerald-50 text-emerald-600",
    rose: "bg-rose-50 text-rose-600",
    amber: "bg-amber-50 text-amber-600",
  };

  return (
    <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm">
      <div
        className={`w-8 h-8 rounded-xl flex items-center justify-center mb-3 ${colorMap[color]}`}
      >
        {icon}
      </div>
      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
        {label}
      </p>
      <p className="text-xl font-black text-slate-800">{value}</p>
    </div>
  );
};

export default VaccinePriceTab;
