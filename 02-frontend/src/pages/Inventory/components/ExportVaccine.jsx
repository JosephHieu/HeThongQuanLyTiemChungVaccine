import React, { useState } from "react";
import {
  Send,
  XCircle,
  Search,
  AlertCircle,
  Info,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import { toast } from "react-hot-toast";

const ExportVaccine = ({ inventoryData = [] }) => {
  const [selectedBatch, setSelectedBatch] = useState(null);
  const [exportQuantity, setExportQuantity] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const handleExport = () => {
    // Logic kiểm tra dữ liệu trước khi xuất
    if (!selectedBatch) {
      return toast.error("Vui lòng chọn một lô vắc-xin để xuất!");
    }
    if (!exportQuantity || isNaN(exportQuantity) || exportQuantity <= 0) {
      return toast.error("Số lô cần xuất phải nhập số và lớn hơn 0");
    }
    if (parseInt(exportQuantity) > selectedBatch.sl) {
      return toast.error("Số lô vắc-xin trong kho không đủ");
    }

    // Nếu thành công
    toast.success(`Xuất vắc-xin ${selectedBatch.tenVacXin} thành công!`);
    handleCancel();
  };

  const handleCancel = () => {
    setSelectedBatch(null);
    setExportQuantity(""); // Clear thông tin
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* Header & Search */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800">
            Xuất vắc-xin điều phối
          </h2>
          <p className="text-sm text-slate-500 font-medium">
            Chọn vắc-xin cần xuất từ danh sách bên dưới
          </p>
        </div>
        <div className="relative w-full md:w-72">
          <Search
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm vắc-xin để xuất..."
            className="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* Main Content Area */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Bảng danh sách vắc-xin có sẵn */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-100 font-bold text-slate-500 uppercase">
                <tr>
                  <th className="p-4">Tên vắc-xin</th>
                  <th className="p-4">Số lô</th>
                  <th className="p-4">Tồn kho</th>
                  <th className="p-4">Hạn dùng</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {inventoryData.map((item) => (
                  <tr
                    key={item.maLo}
                    onClick={() => setSelectedBatch(item)}
                    className={`cursor-pointer transition-colors ${selectedBatch?.maLo === item.maLo ? "bg-blue-50" : "hover:bg-slate-50"}`}
                  >
                    <td className="p-4 font-bold text-slate-800">
                      {item.tenVacXin}
                      <p className="text-[10px] text-slate-400 font-medium">
                        {item.loaiVacXin}
                      </p>
                    </td>
                    <td className="p-4 text-slate-600">{item.maLo}</td>
                    <td className="p-4 font-bold text-blue-600">
                      {item.sl} liều
                    </td>
                    <td className="p-4 text-slate-500">{item.hanSuDung}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* THANH PHÂN TRANG (PAGINATION BAR) */}
            <div className="px-6 py-4 bg-slate-50 border-t border-slate-100 flex flex-col md:flex-row justify-between items-center gap-4">
              <div className="text-sm text-slate-500 font-medium">
                Hiển thị{" "}
                <span className="text-slate-800 font-bold">1 - 10</span> trong
                số <span className="text-slate-800 font-bold">150</span> lô
                vắc-xin
              </div>

              <div className="flex items-center gap-2">
                {/* Nút Trước */}
                <button className="p-2 border border-slate-200 rounded-lg hover:bg-white disabled:opacity-50 disabled:cursor-not-allowed transition-all">
                  <ChevronLeft size={18} />
                </button>

                {/* Các số trang */}
                <div className="flex items-center gap-1">
                  {[1, 2, 3, "...", 15].map((page, index) => (
                    <button
                      key={index}
                      className={`w-9 h-9 flex items-center justify-center rounded-lg text-sm font-bold transition-all ${
                        page === 1
                          ? "bg-blue-600 text-white shadow-lg shadow-blue-200"
                          : "hover:bg-white text-slate-500"
                      }`}
                    >
                      {page}
                    </button>
                  ))}
                </div>

                {/* Nút Tiếp */}
                <button className="p-2 border border-slate-200 rounded-lg hover:bg-white transition-all">
                  <ChevronRight size={18} />
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Panel thực hiện xuất kho */}
        <div className="bg-white p-6 rounded-2xl shadow-lg border border-blue-100 h-fit sticky top-6">
          <h3 className="text-lg font-bold text-slate-800 mb-6 flex items-center gap-2">
            <Send className="text-blue-600" size={20} /> Lệnh xuất kho
          </h3>

          {selectedBatch ? (
            <div className="space-y-6">
              <div className="p-4 bg-blue-50 rounded-xl border border-blue-100 space-y-2">
                <p className="text-xs font-bold text-blue-400 uppercase tracking-widest">
                  Đang chọn xuất:
                </p>
                <p className="text-sm font-black text-blue-900">
                  {selectedBatch.tenVacXin}
                </p>
                <div className="flex justify-between text-xs text-blue-700">
                  <span>Mã lô: {selectedBatch.maLo}</span>
                  <span className="font-bold">
                    Tồn: {selectedBatch.sl} liều
                  </span>
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1">
                  Số lượng cần xuất
                </label>
                <input
                  type="number"
                  className="w-full bg-slate-50 border-none rounded-xl p-3 text-lg font-bold text-slate-700 focus:ring-2 focus:ring-blue-500 outline-none"
                  placeholder="0"
                  value={exportQuantity}
                  onChange={(e) => setExportQuantity(e.target.value)}
                />
              </div>

              <div className="flex flex-col gap-2 pt-4">
                <button
                  onClick={handleExport}
                  className="flex items-center justify-center gap-2 w-full py-3 bg-blue-600 text-white rounded-xl font-bold shadow-lg shadow-blue-200 hover:bg-blue-700 transition-all active:scale-95"
                >
                  <Send size={18} /> Xác nhận xuất
                </button>
                <button
                  onClick={handleCancel}
                  className="flex items-center justify-center gap-2 w-full py-3 text-slate-400 font-bold text-sm hover:bg-slate-100 rounded-xl transition-all"
                >
                  <XCircle size={18} /> Hủy bỏ
                </button>
              </div>
            </div>
          ) : (
            <div className="py-12 flex flex-col items-center text-center gap-4 text-slate-300">
              <Info size={48} />
              <p className="text-sm font-medium px-4">
                Hãy chọn một lô vắc-xin từ danh sách bên trái để bắt đầu lập
                lệnh xuất kho.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ExportVaccine;
