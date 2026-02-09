import React from "react";
import {
  ReceiptText,
  Printer,
  Eye,
  CheckCircle2,
  Clock,
  AlertCircle,
  CreditCard,
  Banknote,
  TrendingUp,
} from "lucide-react";

const CustomerTransactionTab = ({ searchTerm }) => {
  // Dữ liệu mẫu (Thực tế sẽ fetch từ API: /api/v1/finance/transactions)
  const transactions = [
    {
      id: "HD-8802",
      patientName: "Trần Thị Kiều Anh",
      phone: "0923188311",
      date: "09/02/2026",
      vaccine: "6 trong 1 (Infanrix)",
      amount: 1100000,
      method: "Chuyển khoản",
      status: "paid",
    },
    {
      id: "HD-8801",
      patientName: "Nguyễn Văn Hùng",
      phone: "0988222333",
      date: "09/02/2026",
      vaccine: "Phế cầu (Synflorix)",
      amount: 1020000,
      method: "Tiền mặt",
      status: "pending",
    },
    {
      id: "HD-8799",
      patientName: "Lê Minh Tâm",
      phone: "0911555666",
      date: "08/02/2026",
      vaccine: "Cúm mùa (Influvac)",
      amount: 300000,
      method: "Chuyển khoản",
      status: "paid",
    },
  ];

  // Logic lọc theo từ khóa (Mã hóa đơn hoặc Tên khách)
  const filteredData = transactions.filter(
    (item) =>
      item.patientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.id.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const getStatusBadge = (status) => {
    switch (status) {
      case "paid":
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-emerald-50 text-emerald-600 rounded-full text-[10px] font-black uppercase border border-emerald-100">
            <CheckCircle2 size={12} /> Đã thanh toán
          </span>
        );
      case "pending":
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-amber-50 text-amber-600 rounded-full text-[10px] font-black uppercase border border-amber-100">
            <Clock size={12} /> Chờ xử lý
          </span>
        );
      default:
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-rose-50 text-rose-600 rounded-full text-[10px] font-black uppercase border border-rose-100">
            <AlertCircle size={12} /> Đã hủy
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* 1. THỐNG KÊ NHANH (MINI CARDS) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <StatsCard
          label="Doanh thu hôm nay"
          value="2,420,000 đ"
          trend="+12.5%"
          color="emerald"
        />
        <StatsCard
          label="Hóa đơn chờ"
          value="01 đơn"
          trend="Cần xử lý"
          color="amber"
        />
        <StatsCard
          label="Thanh toán Online"
          value="66%"
          trend="Ổn định"
          color="blue"
        />
      </div>

      {/* 2. BẢNG GIAO DỊCH */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-50 flex items-center justify-between">
          <h3 className="font-black text-slate-800 flex items-center gap-2 uppercase text-sm">
            <ReceiptText size={18} className="text-emerald-500" /> Nhật ký giao
            dịch khách hàng
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50/50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-4">Mã hóa đơn</th>
                <th className="px-6 py-4">Khách hàng</th>
                <th className="px-6 py-4">Chi tiết tiêm</th>
                <th className="px-6 py-4">Phương thức</th>
                <th className="px-6 py-4 text-center">Trạng thái</th>
                <th className="px-8 py-4 text-right">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm">
              {filteredData.map((item) => (
                <tr
                  key={item.id}
                  className="hover:bg-slate-50/30 transition-colors"
                >
                  <td className="px-8 py-4">
                    <span className="text-slate-400 text-[10px] font-bold block mb-0.5 uppercase tracking-tighter">
                      {item.date}
                    </span>
                    <span className="text-slate-800 font-black tracking-tight">
                      {item.id}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <p className="text-slate-700 font-bold">
                      {item.patientName}
                    </p>
                    <p className="text-[10px] text-slate-400 font-medium italic">
                      {item.phone}
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <p className="text-slate-600 text-xs mb-1">
                      {item.vaccine}
                    </p>
                    <p className="text-emerald-600 font-black">
                      {item.amount.toLocaleString()} đ
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-1.5 text-slate-500 text-xs font-bold">
                      {item.method === "Chuyển khoản" ? (
                        <CreditCard size={14} />
                      ) : (
                        <Banknote size={14} />
                      )}
                      {item.method}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex justify-center">
                      {getStatusBadge(item.status)}
                    </div>
                  </td>
                  <td className="px-8 py-4 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all"
                        title="Xem chi tiết"
                      >
                        <Eye size={18} />
                      </button>
                      <button
                        className="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all"
                        title="In hóa đơn"
                      >
                        <Printer size={18} />
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

// Component con cho các thẻ thống kê
const StatsCard = ({ label, value, trend, color }) => {
  const colors = {
    emerald: "text-emerald-600 bg-emerald-50",
    amber: "text-amber-600 bg-amber-50",
    blue: "text-blue-600 bg-blue-50",
  };

  return (
    <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex flex-col justify-between h-32">
      <div className="flex justify-between items-start">
        <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none">
          {label}
        </p>
        <TrendingUp size={14} className="text-slate-300" />
      </div>
      <div>
        <p className="text-2xl font-black text-slate-800 tracking-tight">
          {value}
        </p>
        <span
          className={`text-[9px] font-black px-2 py-0.5 rounded-md uppercase ${colors[color]}`}
        >
          {trend}
        </span>
      </div>
    </div>
  );
};

export default CustomerTransactionTab;
