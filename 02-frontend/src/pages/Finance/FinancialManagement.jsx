import React, { useState, useDeferredValue } from "react";
import { useNavigate } from "react-router-dom";
import {
  Wallet,
  ArrowLeft,
  Search,
  Plus,
  Tag,
  Users,
  Truck,
  Download,
  Calendar,
  FileText,
} from "lucide-react";

// Components con
import VaccinePriceTab from "./components/VaccinePriceTab";
import CustomerTransactionTab from "./components/CustomerTransactionTab";
import SupplierTransactionTab from "./components/SupplierTransactionTab";

const FinancialManagement = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("prices");
  const [searchTerm, setSearchTerm] = useState("");

  // 1. Tối ưu tìm kiếm: Trì hoãn việc render/gọi API khi đang gõ phím
  const deferredSearchTerm = useDeferredValue(searchTerm);

  // 2. Trigger mở Modal cho các Tab con
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  const [dateRange, setDateRange] = useState({
    start: new Date().toISOString().split("T")[0],
    end: new Date().toISOString().split("T")[0],
  });

  // Xác định text của nút "Tạo mới" dựa trên Tab hiện tại
  const getCreateButtonLabel = () => {
    switch (activeTab) {
      case "prices":
        return "Thêm Vắc-xin";
      case "customers":
        return "Tạo Hóa đơn";
      case "suppliers":
        return "Phiếu nhập kho";
      default:
        return "Tạo mới";
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6 animate-in fade-in duration-500 p-4 md:p-0">
      {/* --- HEADER SECTION --- */}
      <div className="bg-white p-6 md:p-8 rounded-[2rem] md:rounded-[2.5rem] shadow-sm border border-slate-100">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate(-1)}
              className="p-2.5 bg-white border border-slate-200 rounded-2xl text-slate-600 hover:bg-slate-50 hover:text-emerald-600 transition-all shadow-sm group"
            >
              <ArrowLeft
                size={22}
                className="group-hover:-translate-x-1 transition-transform"
              />
            </button>

            <div>
              <h1 className="text-xl md:text-2xl font-black text-slate-800 flex items-center gap-3">
                <div className="p-2 bg-emerald-100 text-emerald-600 rounded-xl hidden sm:block">
                  <Wallet size={24} />
                </div>
                QUẢN LÝ TÀI CHÍNH
              </h1>
              <p className="text-slate-500 text-xs md:text-sm mt-1 font-medium italic">
                {activeTab === "prices"
                  ? "Điều chỉnh danh mục & đơn giá"
                  : "Theo dõi dòng tiền giao dịch"}
              </p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            {/* Bộ lọc ngày (Chỉ hiện khi ở Tab Giao dịch) */}
            {activeTab !== "prices" && (
              <div className="flex items-center gap-2 bg-slate-50 p-1.5 rounded-2xl border border-slate-200 shadow-inner">
                <Calendar size={16} className="ml-2 text-slate-400" />
                <input
                  type="date"
                  className="bg-transparent border-none text-[11px] font-black text-slate-600 outline-none w-28"
                  value={dateRange.start}
                  onChange={(e) =>
                    setDateRange({ ...dateRange, start: e.target.value })
                  }
                />
                <span className="text-slate-300">-</span>
                <input
                  type="date"
                  className="bg-transparent border-none text-[11px] font-black text-slate-600 outline-none w-28 pr-2"
                  value={dateRange.end}
                  onChange={(e) =>
                    setDateRange({ ...dateRange, end: e.target.value })
                  }
                />
              </div>
            )}

            <button
              onClick={() => console.log("Exporting...")}
              className="p-3 bg-white text-slate-600 border border-slate-200 rounded-2xl hover:bg-slate-50 transition-all shadow-sm"
              title="Xuất Excel báo cáo"
            >
              <Download size={20} />
            </button>

            <button
              onClick={() => setIsCreateOpen(true)}
              className="flex items-center gap-2 bg-emerald-600 text-white px-6 py-3.5 rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-emerald-700 transition-all shadow-lg shadow-emerald-100 active:scale-95"
            >
              <Plus size={18} strokeWidth={3} />
              <span>{getCreateButtonLabel()}</span>
            </button>
          </div>
        </div>

        {/* --- NAVIGATION TABS --- */}
        <div className="flex items-center gap-2 mt-8 p-1.5 bg-slate-100/80 rounded-2xl w-fit border border-slate-200 overflow-x-auto no-scrollbar">
          <TabButton
            active={activeTab === "prices"}
            onClick={() => setActiveTab("prices")}
            icon={<Tag size={18} />}
            label="Danh mục Vắc xin"
          />
          <TabButton
            active={activeTab === "customers"}
            onClick={() => setActiveTab("customers")}
            icon={<Users size={18} />}
            label="Giao dịch Khách"
          />
          <TabButton
            active={activeTab === "suppliers"}
            onClick={() => setActiveTab("suppliers")}
            icon={<Truck size={18} />}
            label="Nhập hàng NCC"
          />
        </div>
      </div>

      {/* --- SEARCH BAR --- */}
      <div className="relative group px-2 md:px-0">
        <Search
          className="absolute left-8 md:left-6 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-emerald-500 transition-colors"
          size={20}
        />
        <input
          type="text"
          placeholder={`Tìm nhanh theo tên, mã hoặc nội dung ${activeTab === "prices" ? "vắc-xin" : "giao dịch"}...`}
          className="w-full pl-14 pr-6 py-4 md:py-5 bg-white border border-slate-100 rounded-[1.5rem] md:rounded-[2rem] shadow-sm focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 outline-none font-bold text-slate-700 transition-all"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* --- CONTENT AREA --- */}
      <div className="min-h-[600px] pb-10">
        {activeTab === "prices" && (
          <VaccinePriceTab
            searchTerm={deferredSearchTerm}
            isCreateOpen={isCreateOpen}
            setIsCreateOpen={setIsCreateOpen}
          />
        )}
        {activeTab === "customers" && (
          <CustomerTransactionTab
            searchTerm={deferredSearchTerm}
            dateRange={dateRange}
            isCreateOpen={isCreateOpen}
            setIsCreateOpen={setIsCreateOpen}
          />
        )}
        {activeTab === "suppliers" && (
          <SupplierTransactionTab
            searchTerm={deferredSearchTerm}
            dateRange={dateRange}
            isCreateOpen={isCreateOpen}
            setIsCreateOpen={setIsCreateOpen}
          />
        )}
      </div>
    </div>
  );
};

// Component con Helper: Nút chuyển Tab
const TabButton = ({ active, onClick, icon, label }) => (
  <button
    onClick={onClick}
    className={`flex items-center gap-2 px-6 py-2.5 rounded-xl transition-all font-black text-[11px] uppercase tracking-wider whitespace-nowrap ${
      active
        ? "bg-white text-emerald-600 shadow-sm ring-1 ring-slate-200/50"
        : "text-slate-500 hover:text-slate-800 hover:bg-white/40"
    }`}
  >
    {icon}
    <span>{label}</span>
  </button>
);

export default FinancialManagement;
