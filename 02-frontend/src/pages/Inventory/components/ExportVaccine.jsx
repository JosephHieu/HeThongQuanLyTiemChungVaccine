import React, { useState, useMemo } from "react";
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
import inventoryApi from "../../../api/inventoryApi"; // Import API

const ExportVaccine = ({ inventoryData = [], onExportSuccess }) => {
  const [selectedBatch, setSelectedBatch] = useState(null);
  const [exportQuantity, setExportQuantity] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [noiNhan, setNoiNhan] = useState("");
  const [ghiChu, setGhiChu] = useState("");

  // 1. Logic lọc danh sách tại chỗ (Frontend Search)
  const filteredData = useMemo(() => {
    return inventoryData.filter(
      (item) =>
        item.tenVacXin.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.maLo.toLowerCase().includes(searchTerm.toLowerCase()),
    );
  }, [inventoryData, searchTerm]);

  // 2. Logic gọi API Xuất kho
  const handleExport = async () => {
    if (!selectedBatch) return toast.error("Vui lòng chọn lô vắc-xin!");

    // Kiểm tra nơi nhận (Vì Backend yêu cầu @NotBlank)
    if (!noiNhan.trim()) return toast.error("Vui lòng nhập nơi nhận vắc-xin!");

    const quantity = parseInt(exportQuantity);
    if (!exportQuantity || isNaN(quantity) || quantity <= 0) {
      return toast.error("Số lượng xuất phải là số dương");
    }

    setIsSubmitting(true);
    try {
      // Gửi payload ĐẦY ĐỦ theo DTO mới
      await inventoryApi.exportVaccine({
        maLo: selectedBatch.maLo,
        soLuongXuat: quantity,
        noiNhan: noiNhan, // <--- Trường mới
        ghiChu: ghiChu, // <--- Trường mới
        maNhanVien: null, // Có thể bổ sung ID nhân viên từ AuthContext sau
      });

      toast.success(`Đã tạo phiếu xuất kho cho ${selectedBatch.tenVacXin}`);
      handleCancel();
      if (onExportSuccess) onExportSuccess();
    } catch (error) {
      toast.error(error.message || "Lỗi hệ thống khi xuất kho");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setSelectedBatch(null);
    setExportQuantity("");
    setNoiNhan("");
    setGhiChu("");
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
            placeholder="Tìm vắc-xin hoặc mã lô..."
            className="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Bảng danh sách */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="overflow-x-auto max-h-[60vh]">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-100 font-bold text-slate-500 uppercase">
                <tr>
                  <th className="p-4">Vắc-xin</th>
                  <th className="p-4">Số lô</th>
                  <th className="p-4">Tồn kho</th>
                  <th className="p-4">Hạn dùng</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {filteredData.length > 0 ? (
                  filteredData.map((item) => (
                    <tr
                      key={item.maLo}
                      onClick={() => setSelectedBatch(item)}
                      className={`cursor-pointer transition-all ${
                        selectedBatch?.maLo === item.maLo
                          ? "bg-blue-50 ring-1 ring-inset ring-blue-200"
                          : "hover:bg-slate-50"
                      }`}
                    >
                      <td className="p-4">
                        <p className="font-bold text-slate-800">
                          {item.tenVacXin}
                        </p>
                        <span className="text-[10px] bg-blue-100 text-blue-600 px-1.5 py-0.5 rounded font-bold uppercase">
                          {item.tenLoaiVacXin}
                        </span>
                      </td>
                      <td className="p-4 font-mono text-slate-400">
                        {item.soLo}
                      </td>
                      <td className="p-4 font-bold text-blue-600">
                        {item.soLuong.toLocaleString()}{" "}
                        <span className="text-[10px] text-slate-400">liều</span>
                      </td>
                      <td className="p-4">
                        {/* CẢNH BÁO MÀU ĐỎ NẾU SẮP HẾT HẠN */}
                        <span
                          className={
                            new Date(item.hanSuDung) < new Date()
                              ? "text-red-500 font-bold"
                              : "text-slate-500"
                          }
                        >
                          {new Date(item.hanSuDung).toLocaleDateString("vi-VN")}
                        </span>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan="4"
                      className="p-10 text-center text-slate-400 italic"
                    >
                      Không tìm thấy lô vắc-xin nào khớp với từ khóa...
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Panel Lệnh xuất kho */}
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
                <div className="flex justify-between text-[10px] text-blue-700 items-start gap-2">
                  <span className="break-all flex-1">
                    Mã định danh:{" "}
                    <span className="font-mono">{selectedBatch.maLo}</span>
                  </span>
                  <span className="font-bold shrink-0">
                    Tồn: {selectedBatch.soLuong} liều
                  </span>
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1">
                  Số lượng cần xuất
                </label>
                <input
                  type="number"
                  min="1"
                  max={selectedBatch?.soLuong}
                  className="w-full bg-slate-50 border-none rounded-xl p-3 text-lg font-bold text-slate-700 focus:ring-2 focus:ring-blue-500 outline-none"
                  placeholder="Nhập số liều..."
                  value={exportQuantity}
                  onChange={(e) => setExportQuantity(e.target.value)}
                />
              </div>

              <div className="space-y-4 pt-4 border-t border-slate-100">
                <div className="space-y-2">
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                    Đơn vị tiếp nhận (Nơi nhận)
                  </label>
                  <input
                    type="text"
                    className="w-full bg-slate-50 border-none rounded-xl p-3 text-sm font-bold text-slate-700 focus:ring-2 focus:ring-blue-500 outline-none"
                    placeholder="VD: Trạm y tế Phường 1..."
                    value={noiNhan}
                    onChange={(e) => setNoiNhan(e.target.value)}
                  />
                </div>

                <div className="space-y-2">
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                    Ghi chú lý do xuất
                  </label>
                  <textarea
                    rows="2"
                    className="w-full bg-slate-50 border-none rounded-xl p-3 text-sm text-slate-700 focus:ring-2 focus:ring-blue-500 outline-none resize-none"
                    placeholder="Nhập ghi chú nếu có..."
                    value={ghiChu}
                    onChange={(e) => setGhiChu(e.target.value)}
                  />
                </div>
              </div>

              <div className="flex flex-col gap-2 pt-4">
                <button
                  onClick={handleExport}
                  disabled={isSubmitting}
                  className="flex items-center justify-center gap-2 w-full py-3 bg-blue-600 text-white rounded-xl font-bold shadow-lg shadow-blue-200 hover:bg-blue-700 transition-all active:scale-95 disabled:opacity-50"
                >
                  {isSubmitting ? (
                    "Đang xử lý..."
                  ) : (
                    <>
                      <Send size={18} /> Xác nhận xuất
                    </>
                  )}
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
                Hãy chọn một lô vắc-xin từ danh sách bên trái để bắt đầu lệnh
                xuất kho.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ExportVaccine;
