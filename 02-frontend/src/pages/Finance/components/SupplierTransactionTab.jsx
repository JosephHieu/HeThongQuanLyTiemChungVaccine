import React from "react";
import {
  Truck,
  FileText,
  Download,
  Eye,
  CheckCircle2,
  Clock,
  AlertTriangle,
  PackageSearch,
  ArrowUpRight,
} from "lucide-react";

const SupplierTransactionTab = ({ searchTerm }) => {
  // Dữ liệu mẫu (Thực tế sẽ fetch từ api: /api/v1/finance/supplier-transactions)
  const transactions = [
    {
      id: "PO-2026-001",
      supplierName: "Công ty Dược phẩm VNVC",
      items: "6 trong 1 (1.000 liều), Phế cầu (500 liều)",
      date: "05/02/2026",
      totalAmount: 1250000000, // 1.25 tỷ
      status: "completed", // completed, pending, overdue
      paymentMethod: "Hợp đồng trả chậm",
    },
    {
      id: "PO-2026-002",
      supplierName: "Sanofi Pasteur Vietnam",
      items: "Cúm mùa Vaxigrip (2.000 liều)",
      date: "08/02/2026",
      totalAmount: 480000000,
      status: "pending",
      paymentMethod: "Chuyển khoản",
    },
    {
      id: "PO-2026-003",
      supplierName: "GlaxoSmithKline (GSK)",
      items: "Vắc xin Thủy đậu (300 liều)",
      date: "01/02/2026",
      totalAmount: 150000000,
      status: "overdue",
      paymentMethod: "Đối soát cuối tháng",
    },
  ];

  // Logic lọc theo nhà cung cấp hoặc mã đơn hàng
  const filteredData = transactions.filter(
    (item) =>
      item.supplierName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.id.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const getStatusBadge = (status) => {
    switch (status) {
      case "completed":
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-emerald-50 text-emerald-600 rounded-full text-[10px] font-black uppercase border border-emerald-100">
            <CheckCircle2 size={12} /> Đã nhập kho
          </span>
        );
      case "pending":
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-[10px] font-black uppercase border border-blue-100">
            <Clock size={12} /> Đang giao hàng
          </span>
        );
      default:
        return (
          <span className="flex items-center gap-1.5 px-3 py-1 bg-rose-50 text-rose-600 rounded-full text-[10px] font-black uppercase border border-rose-100">
            <AlertTriangle size={12} /> Quá hạn thanh toán
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* 1. THỐNG KÊ CHI PHÍ NHẬP HÀNG */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
              Tổng chi nhập hàng tháng 2
            </p>
            <ArrowUpRight size={16} className="text-rose-500" />
          </div>
          <p className="text-2xl font-black text-slate-800 tracking-tight">
            1,880,000,000 đ
          </p>
          <p className="text-[9px] font-bold text-slate-400 mt-1 uppercase">
            Tăng 15% so với tháng trước
          </p>
        </div>

        <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
              Công nợ NCC còn lại
            </p>
            <PackageSearch size={16} className="text-emerald-500" />
          </div>
          <p className="text-2xl font-black text-rose-600 tracking-tight">
            630,000,000 đ
          </p>
          <p className="text-[9px] font-bold text-amber-600 mt-1 uppercase italic">
            * 2 hóa đơn sắp đến hạn
          </p>
        </div>

        <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex items-center justify-center border-dashed border-2 hover:bg-slate-50 transition-all cursor-pointer group">
          <div className="text-center">
            <div className="w-10 h-10 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform">
              <Truck size={20} />
            </div>
            <p className="text-xs font-black text-slate-600 uppercase">
              Tạo đơn nhập mới
            </p>
          </div>
        </div>
      </div>

      {/* 2. BẢNG DỮ LIỆU NHẬP HÀNG */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-50 flex items-center justify-between">
          <h3 className="font-black text-slate-800 flex items-center gap-2 uppercase text-sm">
            <FileText size={18} className="text-emerald-500" /> Lịch sử nhập kho
            & Thanh toán NCC
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50/50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-4">Mã đơn hàng</th>
                <th className="px-6 py-4">Nhà cung cấp</th>
                <th className="px-6 py-4">Nội dung hàng hóa</th>
                <th className="px-6 py-4 text-center">Trạng thái</th>
                <th className="px-8 py-4 text-right">Giá trị đơn</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm">
              {filteredData.map((item) => (
                <tr
                  key={item.id}
                  className="hover:bg-slate-50/30 transition-colors"
                >
                  <td className="px-8 py-4">
                    <span className="text-slate-400 text-[10px] font-bold block mb-0.5">
                      {item.date}
                    </span>
                    <span className="text-slate-800 font-black">{item.id}</span>
                  </td>
                  <td className="px-6 py-4">
                    <p className="text-slate-700 font-bold">
                      {item.supplierName}
                    </p>
                    <p className="text-[10px] text-slate-400 font-bold uppercase">
                      {item.paymentMethod}
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <p
                      className="text-slate-600 text-xs line-clamp-1 max-w-[200px]"
                      title={item.items}
                    >
                      {item.items}
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex justify-center">
                      {getStatusBadge(item.status)}
                    </div>
                  </td>
                  <td className="px-8 py-4 text-right">
                    <div className="flex flex-col items-end gap-1">
                      <p className="text-slate-900 font-black">
                        {item.totalAmount.toLocaleString()} đ
                      </p>
                      <div className="flex gap-2">
                        <button
                          className="text-slate-400 hover:text-emerald-600 transition-colors"
                          title="Tải hóa đơn PDF"
                        >
                          <Download size={14} />
                        </button>
                        <button
                          className="text-slate-400 hover:text-blue-600 transition-colors"
                          title="Xem chi tiết"
                        >
                          <Eye size={14} />
                        </button>
                      </div>
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

export default SupplierTransactionTab;
