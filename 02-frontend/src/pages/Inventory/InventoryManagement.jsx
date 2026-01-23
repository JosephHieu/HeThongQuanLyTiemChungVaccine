import React, { useState } from "react";
import ImportVaccineModal from "./components/ImportVaccineModal";
import { useNavigate } from "react-router-dom";
import ExportVaccine from "./components/ExportVaccine";
import {
  Search,
  Package,
  AlertTriangle,
  CheckCircle,
  Filter,
  Download,
  Plus,
  MoreHorizontal,
  ChevronLeft,
  ChevronRight,
  ArrowLeft,
} from "lucide-react";

const InventoryManagement = () => {
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState("view");
  const [searchCriteria, setSearchCriteria] = useState("name");

  // Dữ liệu mẫu (sau này sẽ lấy từ API)
  const mockInventory = [
    {
      code: "11357",
      name: "Phòng bệnh lao",
      type: "BCG",
      amount: "500",
      expiry: "1 năm",
      status: "Còn",
    },
    {
      code: "23456",
      name: "Phòng Viêm gan B",
      type: "ENGERIX B",
      amount: "0",
      expiry: "1 năm",
      status: "Hết",
    },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* 1. HEADER */}

      <header className="flex items-center gap-4 bg-white p-4 rounded-2xl shadow-sm border border-slate-100">
        <button
          onClick={() => navigate("/admin/dashboard")} // Quay về dashboard
          className="p-2.5 bg-white border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 hover:text-blue-600 transition-all shadow-sm group"
          title="Quay lại Dashboard"
        >
          <ArrowLeft
            size={22}
            className="group-hover:-translate-x-1 transition-transform"
          />
        </button>

        <div className="flex-1 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-xl font-bold text-slate-800 uppercase tracking-tight">
              Quản lý kho vắc-xin
            </h1>
            <p className="text-slate-500 text-xs font-medium">
              Hệ thống kiểm soát và điều phối nguồn lực vật tư y tế
            </p>
          </div>
          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center justify-center gap-2 bg-blue-600 text-white px-6 py-2.5 rounded-xl hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all font-bold text-sm"
          >
            <Plus size={20} /> Nhập vắc-xin
          </button>
        </div>
      </header>

      {/* 2. TABS SELECTION */}
      <div className="flex border-b border-slate-200 bg-white px-4 rounded-t-2xl">
        {["view", "export"].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-8 py-4 text-sm font-bold transition-all border-b-2 capitalize ${
              activeTab === tab
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-slate-400 hover:text-slate-600"
            }`}
          >
            {tab === "view" ? "Xem tình hình kho" : "Xuất vắc-xin điều phối"}
          </button>
        ))}
      </div>

      {/* 3. NỘI DUNG THAY ĐỔI THEO TAB */}
      {activeTab === "view" ? (
        <div className="space-y-6 animate-in slide-in-from-bottom-4 duration-500">
          {/* STATS */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <StatCard
              icon={<Package className="text-blue-600" />}
              label="Tổng tồn kho"
              value="1,500 liều"
              color="bg-blue-50"
            />
            <StatCard
              icon={<AlertTriangle className="text-amber-600" />}
              label="Sắp hết hạn"
              value="2 lô"
              color="bg-amber-50"
            />
            <StatCard
              icon={<CheckCircle className="text-green-600" />}
              label="Tình trạng"
              value="Ổn định"
              color="bg-green-50"
            />
          </div>

          {/* SEARCH & FILTER */}
          <div className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 flex flex-col md:flex-row gap-4 items-center">
            <div className="relative flex-1 w-full">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                size={18}
              />
              <input
                type="text"
                placeholder="Tìm kiếm nhanh..."
                className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border-none rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div className="flex items-center gap-2 bg-slate-50 px-3 py-1 rounded-xl border border-slate-100">
              <span className="text-[10px] font-black text-slate-400 uppercase">
                Lọc:
              </span>
              <select className="bg-transparent border-none text-sm font-bold text-slate-700 focus:ring-0 cursor-pointer py-2">
                <option>Tên vắc-xin</option>
                <option>Mã lô</option>
              </select>
            </div>
            <button className="w-full md:w-auto px-8 py-2.5 bg-slate-800 text-white rounded-xl font-bold text-sm hover:bg-slate-900 transition-all">
              Tìm kiếm
            </button>
          </div>

          {/* TABLE & PAGINATION */}
          <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
            <table className="w-full text-left">
              <thead className="bg-slate-50/50 border-b border-slate-100">
                <tr>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-widest">
                    Mã lô
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-widest">
                    Thông tin vắc-xin
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-widest">
                    Số lượng
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-widest text-center">
                    Tình trạng
                  </th>
                  <th className="p-4"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {mockInventory.map((row, idx) => (
                  <InventoryRow key={idx} {...row} />
                ))}
              </tbody>
            </table>

            {/* PHÂN TRANG */}
            <div className="p-4 bg-slate-50/50 flex flex-col md:flex-row justify-between items-center gap-4">
              <span className="text-xs font-bold text-slate-400">
                Hiển thị 2 trong số 150 kết quả
              </span>
              <div className="flex items-center gap-1">
                <button className="p-2 hover:bg-white rounded-lg transition-all text-slate-400">
                  <ChevronLeft size={16} />
                </button>
                <button className="w-8 h-8 bg-blue-600 text-white rounded-lg text-xs font-bold shadow-md shadow-blue-100">
                  1
                </button>
                <button className="w-8 h-8 hover:bg-white text-slate-500 rounded-lg text-xs font-bold transition-all">
                  2
                </button>
                <button className="p-2 hover:bg-white rounded-lg transition-all text-slate-400">
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="animate-in slide-in-from-right-4 duration-500">
          <ExportVaccine inventoryData={mockInventory} />
        </div>
      )}

      {/* 4. MODAL NHẬP KHO */}
      <ImportVaccineModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      />
    </div>
  );
};

// Component con cho Thẻ thống kê
const StatCard = ({ icon, label, value, color }) => (
  <div
    className={`${color} p-6 rounded-2xl border border-white flex items-center gap-4 shadow-sm`}
  >
    <div className="p-3 bg-white rounded-xl shadow-sm">{icon}</div>
    <div>
      <p className="text-xs font-bold text-slate-500 uppercase tracking-wider">
        {label}
      </p>
      <p className="text-xl font-black text-slate-800">{value}</p>
    </div>
  </div>
);

// Component con cho dòng trong bảng
const InventoryRow = ({ code, name, type, amount, expiry, status }) => (
  <tr className="hover:bg-slate-50/50 transition-colors group">
    <td className="p-4 text-sm font-bold text-slate-700">{code}</td>
    <td className="p-4">
      <p className="text-sm font-bold text-slate-800">{name}</p>
      <p className="text-[10px] text-slate-400 font-medium uppercase">{type}</p>
    </td>
    <td className="p-4 text-sm font-medium text-slate-600">{amount} liều</td>
    <td className="p-4 text-sm text-slate-500">{expiry}</td>
    <td className="p-4 text-center">
      <span
        className={`px-3 py-1 rounded-full text-[10px] font-black uppercase ${status === "Còn" ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}
      >
        {status}
      </span>
    </td>
    <td className="p-4 text-right">
      <button className="p-2 text-slate-400 hover:text-slate-800 transition-colors">
        <MoreHorizontal size={18} />
      </button>
    </td>
  </tr>
);

export default InventoryManagement;
