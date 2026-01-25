import React, { useState, useEffect, useCallback } from "react";
import ImportVaccineModal from "./components/ImportVaccineModal";
import VaccineDetailModal from "./components/VaccineDetailModal";
import { useNavigate } from "react-router-dom";
import ExportVaccine from "./components/ExportVaccine";
import inventoryApi from "../../api/inventoryApi";

import {
  Search,
  Package,
  AlertTriangle,
  CheckCircle,
  Plus,
  MoreHorizontal,
  ChevronLeft,
  ChevronRight,
  ArrowLeft,
  Info,
} from "lucide-react";
import toast from "react-hot-toast";

const InventoryManagement = () => {
  const navigate = useNavigate();

  // States cho dữ liệu và giao diện
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState("view");
  const [selectedDetail, setSelectedDetail] = useState(null);

  // States cho Phân trang & Tìm kiếm
  const [searchCriteria, setSearchCriteria] = useState("name");
  const [searchValue, setSearchValue] = useState("");
  const [activeSearch, setActiveSearch] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // 1. Hàm lấy dữ liệu từ API
  const fetchInventory = useCallback(async () => {
    setLoading(true);
    try {
      const data = await inventoryApi.getInventory(
        searchCriteria,
        activeSearch,
        page,
        10,
      );
      // Backend trả về object Page: { content, totalPages, totalElements, ... }
      setInventory(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (error) {
      toast.error(error || "Không thể tải dữ liệu kho");
    } finally {
      setLoading(false);
    }
  }, [searchCriteria, searchValue, page]);

  // Gọi API khi component mount hoặc thay đổi trang/ tìm kiếm
  useEffect(() => {
    fetchInventory();
  }, [fetchInventory]);

  // Xử lý khi nhấn nút Tìm kiếm
  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0); // Quay về trang đầu tiên khi tìm kiếm
    setActiveSearch(searchValue);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
    // Cuộn nhẹ lên đầu trang để người dùng thấy dữ liệu mới
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

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
              value={totalElements}
              color="bg-blue-50"
            />
            <StatCard
              icon={<AlertTriangle className="text-amber-600" />}
              label="Lô sắp hết hạn"
              value="--"
              color="bg-amber-50"
            />
            <StatCard
              icon={<CheckCircle className="text-green-600" />}
              label="Tình trạng"
              value="Sẵn sàng"
              color="bg-green-50"
            />
          </div>

          {/* SEARCH & FILTER */}
          <form
            onClick={handleSearch}
            className="bg-white p-5 rounded-2xl shadow-sm border border-slate-100 flex flex-col md:flex-row gap-4 items-center"
          >
            <div className="relative flex-1 w-full">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                size={18}
              />
              <input
                type="text"
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                placeholder="Tìm kiếm nhanh..."
                className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border-none rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div className="flex items-center gap-2 bg-slate-50 px-3 py-1 rounded-xl border border-slate-100">
              <span className="text-[10px] font-black text-slate-400 uppercase">
                Lọc:
              </span>
              <select
                value={searchCriteria}
                onChange={(e) => setSearchCriteria(e.target.value)}
                className="bg-transparent border-none text-sm font-bold text-slate-700 focus:ring-0 cursor-pointer py-2"
              >
                <option value="name">Tên vắc-xin</option>
                <option value="type">Loại vắc-xin</option>
                <option value="origin">Nước sản xuất</option>
              </select>
            </div>
            <button
              type="submit"
              className="w-full md:w-auto px-8 py-2.5 bg-slate-800 text-white rounded-xl font-bold text-sm hover:bg-slate-900 transition-all"
            >
              Tìm kiếm
            </button>
          </form>

          {/* TABLE & PAGINATION */}
          <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden relative">
            {loading && (
              <div className="absolute inset-0 bg-white/50 backdrop-blur-sm flex items-center justify-center z-10">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              </div>
            )}
            <table className="w-full text-left">
              <thead className="bg-slate-50/50 border-b border-slate-100">
                <tr>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase">
                    Mã lô
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase">
                    Vắc-xin & Phòng bệnh
                  </th>{" "}
                  {/* Gộp hoặc tách */}
                  <th className="p-4 text-xs font-black text-slate-500 uppercase">
                    Số liều
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase">
                    Tình Trạng
                  </th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase text-center">
                    Chi Tiết
                  </th>
                  <th className="p-4"></th>
                  <th className="p-4"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {inventory.length > 0 ? (
                  inventory.map((item) => (
                    <InventoryRow
                      key={item.maLo}
                      data={item}
                      onDetail={setSelectedDetail}
                    />
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan="5"
                      className="p-10 text-center text-slate-400 font-medium"
                    >
                      Không tìm thấy dữ liệu phù hợp
                    </td>
                  </tr>
                )}
              </tbody>
            </table>

            {/* PAGINATION */}
            <div className="p-4 bg-slate-50/50 flex flex-col md:flex-row justify-between items-center gap-4">
              <span className="text-xs font-bold text-slate-400">
                Trang {page + 1} / {totalPages} (Tổng {totalElements} kết quả)
              </span>
              <div className="flex items-center gap-1">
                <button
                  disabled={page === 0}
                  onClick={() => handlePageChange(page - 1)}
                  className="p-2 hover:bg-white rounded-lg transition-all text-slate-400 disabled:opacity-30"
                >
                  <ChevronLeft size={16} />
                </button>

                {/* Các nút số trang */}
                {[...Array(totalPages)]
                  .map((_, i) => (
                    <button
                      key={i}
                      onClick={() => handlePageChange(i)}
                      className={`w-8 h-8 rounded-lg text-xs font-bold transition-all ${
                        page === i
                          ? "bg-blue-600 text-white shadow-md shadow-blue-100"
                          : "hover:bg-white text-slate-500"
                      }`}
                    >
                      {i + 1}
                    </button>
                  ))
                  .slice(Math.max(0, page - 2), Math.min(totalPages, page + 3))}
                <button
                  disabled={page >= totalPages - 1}
                  onClick={() => handlePageChange(page + 1)}
                  className="p-2 hover:bg-white rounded-lg transition-all text-slate-400 disabled:opacity-30"
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="animate-in slide-in-from-right-4 duration-500">
          <ExportVaccine
            inventoryData={inventory}
            onExportSuccess={fetchInventory}
          />
        </div>
      )}

      {/* 4. MODAL NHẬP KHO */}
      <ImportVaccineModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={fetchInventory} // Load lại data khi nhập kho thành công
      />

      {/* 5. MODAL CHI TIẾT (Thêm vào đây) */}
      <VaccineDetailModal
        isOpen={!!selectedDetail}
        data={selectedDetail}
        onClose={() => setSelectedDetail(null)}
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
const InventoryRow = ({ data, onDetail }) => {
  const { maLo, tenVacXin, tenLoaiVacXin, soLuong, tinhTrang } = data;

  return (
    <tr
      onClick={() => onDetail(data)} // Click vào bất kỳ đâu trên dòng để xem chi tiết
      className="hover:bg-blue-50/50 transition-all cursor-pointer group border-b border-slate-50"
    >
      <td className="p-4 text-xs font-mono font-bold text-slate-400">
        {maLo?.substring(0, 8)}...
      </td>
      <td className="p-4">
        <p className="text-sm font-bold text-slate-800 group-hover:text-blue-600 transition-colors">
          {tenVacXin}
        </p>
        <span className="text-[10px] bg-slate-100 text-slate-500 px-1.5 py-0.5 rounded font-bold uppercase">
          {tenLoaiVacXin}
        </span>
      </td>
      <td className="p-4">
        <p className="text-sm font-black text-slate-700">
          {soLuong.toLocaleString()} liều
        </p>
      </td>
      <td className="p-4 text-center">
        <span
          className={`px-3 py-1 rounded-full text-[10px] font-black uppercase ${
            tinhTrang === "Còn"
              ? "bg-green-100 text-green-700"
              : "bg-red-100 text-red-700"
          }`}
        >
          {tinhTrang}
        </span>
      </td>
      <td className="p-4 text-right">
        <button className="p-2 text-slate-300 group-hover:text-blue-500 transition-colors">
          <Info size={18} />
        </button>
      </td>
    </tr>
  );
};

export default InventoryManagement;
