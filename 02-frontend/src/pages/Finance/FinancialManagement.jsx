import React, { useState } from "react";
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
  Filter,
} from "lucide-react";
import VaccinePriceTab from "./components/VaccinePriceTab";
import CustomerTransactionTab from "./components/CustomerTransactionTab";
import SupplierTransactionTab from "./components/SupplierTransactionTab";

const FinancialManagement = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("prices");
  const [searchTerm, setSearchTerm] = useState("");

  return (
    <div className="max-w-7xl mx-auto space-y-6 animate-in fade-in duration-500">
      {/* HEADER SECTION */}
      <div className="bg-white p-6 md:p-8 rounded-[2.5rem] shadow-sm border border-slate-100">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate(-1)}
              className="p-2.5 bg-white border border-slate-200 rounded-2xl text-slate-600 
                         hover:bg-slate-50 hover:text-emerald-600 transition-all shadow-sm group"
              title="Quay lại"
            >
              <ArrowLeft
                size={22}
                className="group-hover:-translate-x-1 transition-transform"
              />
            </button>

            <div>
              <h1 className="text-2xl font-black text-slate-800 flex items-center gap-3">
                <div className="p-2 bg-emerald-100 text-emerald-600 rounded-xl hidden sm:block">
                  <Wallet size={24} />
                </div>
                QUẢN LÝ TÀI CHÍNH
              </h1>
              <p className="text-slate-500 text-sm mt-1 font-medium italic">
                Theo dõi biến động giá và dòng tiền tiêm chủng
              </p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="relative flex-1 min-w-[240px]">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                size={18}
              />
              <input
                type="text"
                placeholder="Tìm hóa đơn, tên khách, vắc xin..."
                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-emerald-500 outline-none font-medium"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <button className="p-3 bg-slate-100 text-slate-600 rounded-2xl hover:bg-slate-200 transition-all">
              <Download size={20} />
            </button>
          </div>
        </div>

        {/* NAVIGATION TABS */}
        <div className="flex items-center gap-2 mt-8 p-1 bg-slate-100 rounded-2xl w-fit border border-slate-200 overflow-x-auto no-scrollbar">
          <TabButton
            active={activeTab === "prices"}
            onClick={() => setActiveTab("prices")}
            icon={<Tag size={18} />}
            label="Bảng giá Vắc xin"
          />
          <TabButton
            active={activeTab === "customers"}
            onClick={() => setActiveTab("customers")}
            icon={<Users size={18} />}
            label="Giao dịch Khách hàng"
          />
          <TabButton
            active={activeTab === "suppliers"}
            onClick={() => setActiveTab("suppliers")}
            icon={<Truck size={18} />}
            label="Nhập hàng NCC"
          />
        </div>
      </div>

      {/* CONTENT AREA */}
      <div className="min-h-[500px]">
        {activeTab === "prices" && <VaccinePriceTab searchTerm={searchTerm} />}
        {activeTab === "customers" && (
          <CustomerTransactionTab searchTerm={searchTerm} />
        )}
        {activeTab === "suppliers" && (
          <SupplierTransactionTab searchTerm={searchTerm} />
        )}
      </div>
    </div>
  );
};

const TabButton = ({ active, onClick, icon, label }) => (
  <button
    onClick={onClick}
    className={`flex items-center gap-2 px-5 py-2.5 rounded-xl transition-all font-bold text-sm whitespace-nowrap ${
      active
        ? "bg-white text-emerald-600 shadow-sm ring-1 ring-slate-200"
        : "text-slate-500 hover:text-slate-700"
    }`}
  >
    {icon}
    <span className="hidden md:inline">{label}</span>
  </button>
);

export default FinancialManagement;
