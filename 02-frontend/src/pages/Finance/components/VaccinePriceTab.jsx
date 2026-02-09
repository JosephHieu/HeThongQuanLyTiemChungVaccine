import React, { useState, useEffect, useCallback } from "react";
import {
  TrendingUp,
  Edit3,
  Trash2,
  Info,
  ShieldCheck,
  Zap,
  X,
  Save,
  PlusCircle,
  ChevronLeft,
  ChevronRight,
  AlertTriangle,
} from "lucide-react";
import financeApi from "../../../api/financeApi";
import inventoryApi from "../../../api/inventoryApi";
import { toast } from "react-hot-toast";

const VaccinePriceTab = ({ searchTerm, isCreateOpen, setIsCreateOpen }) => {
  const [prices, setPrices] = useState([]);
  const [totalInventoryValue, setTotalInventoryValue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 1,
    size: 7,
    totalElements: 0,
    totalPages: 0,
  });

  // --- MODAL STATES ---
  const [isModalOpen, setIsModalOpen] = useState(false); // Modal Thêm/Sửa
  const [editingVaccine, setEditingVaccine] = useState(null);
  const [categories, setCategories] = useState([]);
  const [deleteModal, setDeleteModal] = useState({
    isOpen: false,
    id: null,
    name: "",
  });
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const emptyVaccine = {
    tenVacXin: "",
    maLoaiVacXin: "",
    hanSuDung: "",
    hamLuong: "",
    phongNguaBenh: "",
    doTuoiTiemChung: "",
    donGia: 0,
    dieuKienBaoQuan: "",
  };

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [priceData, totalValue] = await Promise.all([
        financeApi.getVaccines(pagination.page, pagination.size, searchTerm),
        financeApi.getTotalInventoryValue(),
      ]);
      setPrices(priceData.data);
      setTotalInventoryValue(totalValue);
      setPagination((prev) => ({
        ...prev,
        totalElements: priceData.totalElements,
        totalPages: priceData.totalPages,
      }));
    } catch (error) {
      toast.error(error.message || "Lỗi tải dữ liệu");
    } finally {
      setLoading(false);
    }
  }, [pagination.page, pagination.size, searchTerm]);

  useEffect(() => {
    fetchData();
    const fetchCategories = async () => {
      try {
        const data = await inventoryApi.getAllVaccineTypes();
        setCategories(data);
      } catch (e) {
        console.error(e);
      }
    };
    fetchCategories();
  }, [fetchData]);

  // Lắng nghe tín hiệu tạo mới từ Header
  useEffect(() => {
    if (isCreateOpen) {
      setEditingVaccine(emptyVaccine);
      setIsModalOpen(true);
      setIsCreateOpen(false);
    }
  }, [isCreateOpen]);

  // --- HANDLERS ---
  const handleEditClick = (vax) => {
    setEditingVaccine({ ...vax });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      if (editingVaccine.maVacXin) {
        await financeApi.updateVaccine(editingVaccine.maVacXin, editingVaccine);
        toast.success("Cập nhật thành công!");
      } else {
        await financeApi.createVaccine(editingVaccine);
        toast.success("Thêm mới vắc-xin thành công!");
      }
      setIsModalOpen(false);
      fetchData();
    } catch (error) {
      toast.error(error.message || "Thao tác thất bại");
    } finally {
      setIsSaving(false);
    }
  };

  const confirmDelete = async () => {
    setIsDeleting(true);
    try {
      await financeApi.deleteVaccine(deleteModal.id);
      toast.success(`Đã xóa vắc-xin "${deleteModal.name}"`);
      setDeleteModal({ isOpen: false, id: null, name: "" });
      fetchData();
    } catch (error) {
      toast.error(error.message || "Không thể xóa do ràng buộc dữ liệu kho");
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-4 md:space-y-6">
      {/* 1. THẺ TÓM TẮT (SUMMARY) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <PriceSummaryCard
          label="Tổng loại"
          value={`${pagination.totalElements} loại`}
          icon={<Zap size={16} />}
          color="blue"
        />
        <PriceSummaryCard
          label="Giá trị kho"
          value={new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
          }).format(totalInventoryValue)}
          icon={<ShieldCheck size={16} />}
          color="amber"
        />
        <PriceSummaryCard
          label="Lợi nhuận"
          value="~15.5%"
          icon={<TrendingUp size={16} />}
          color="emerald"
        />
        <PriceSummaryCard
          label="Trạng thái"
          value="Đang hoạt động"
          icon={<Info size={16} />}
          color="blue"
        />
      </div>

      {/* 2. BẢNG DỮ LIỆU */}
      <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[800px]">
            <thead className="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <tr>
                <th className="px-8 py-5">Sản phẩm Vắc xin</th>
                <th className="px-6 py-5">Thông tin y tế</th>
                <th className="px-6 py-5">Giá bán</th>
                <th className="px-6 py-5 text-center">Hạn dùng</th>
                <th className="px-8 py-5 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 font-medium text-sm text-slate-600">
              {loading ? (
                <tr>
                  <td colSpan="5" className="text-center py-20 italic">
                    Đang tải dữ liệu...
                  </td>
                </tr>
              ) : (
                prices.map((vax) => (
                  <tr
                    key={vax.maVacXin}
                    className="hover:bg-slate-50/50 transition-colors"
                  >
                    <td className="px-8 py-4">
                      <p className="text-slate-800 font-bold">
                        {vax.tenVacXin}
                      </p>
                      <span className="text-[10px] bg-emerald-50 text-emerald-600 px-2 py-0.5 rounded font-black uppercase tracking-tighter">
                        {vax.tenLoaiVacXin}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <p className="font-bold">{vax.phongNguaBenh}</p>
                      <p className="text-[10px] text-slate-400 uppercase">
                        Hàm lượng: {vax.hamLuong}
                      </p>
                    </td>
                    <td className="px-6 py-4">
                      <p className="text-emerald-600 font-black">
                        {vax.donGia?.toLocaleString()} đ
                      </p>
                      <p className="text-[10px] text-slate-400 italic">
                        Đối tượng: {vax.doTuoiTiemChung}
                      </p>
                    </td>
                    <td className="px-6 py-4 text-center font-bold text-slate-400">
                      {vax.hanSuDung
                        ? new Date(vax.hanSuDung).toLocaleDateString("vi-VN")
                        : "---"}
                    </td>
                    <td className="px-8 py-4 text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => handleEditClick(vax)}
                          className="p-2.5 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all border border-slate-100"
                        >
                          <Edit3 size={16} />
                        </button>
                        <button
                          onClick={() =>
                            setDeleteModal({
                              isOpen: true,
                              id: vax.maVacXin,
                              name: vax.tenVacXin,
                            })
                          }
                          className="p-2.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition-all border border-slate-100"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* PHÂN TRANG */}
        <div className="p-6 bg-slate-50/30 flex items-center justify-between border-t border-slate-50">
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
            Trang {pagination.page} / {pagination.totalPages}
          </p>
          <div className="flex gap-2">
            <button
              disabled={pagination.page === 1}
              onClick={() => setPagination((p) => ({ ...p, page: p.page - 1 }))}
              className="p-2 bg-white border border-slate-200 rounded-lg disabled:opacity-30"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              disabled={pagination.page === pagination.totalPages}
              onClick={() => setPagination((p) => ({ ...p, page: p.page + 1 }))}
              className="p-2 bg-white border border-slate-200 rounded-lg disabled:opacity-30"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </div>

      {/* 3. MODAL CHÍNH SỬA / TẠO MỚI (UPSERT) */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-slate-900/60 backdrop-blur-sm p-0 sm:p-4">
          <div className="bg-white w-full max-w-3xl rounded-t-[2rem] sm:rounded-[2.5rem] shadow-2xl flex flex-col max-h-[95vh] animate-in slide-in-from-bottom sm:zoom-in duration-300">
            <div className="p-6 border-b border-slate-50 flex justify-between items-center">
              <h3 className="font-black text-slate-800 uppercase text-xs flex items-center gap-2">
                <PlusCircle size={18} className="text-emerald-500" />{" "}
                {editingVaccine.maVacXin
                  ? "Cập nhật vắc-xin"
                  : "Thêm vắc-xin mới"}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-2 hover:bg-slate-100 rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            <form
              onSubmit={handleSave}
              className="p-6 md:p-10 overflow-y-auto grid grid-cols-1 md:grid-cols-2 gap-6"
            >
              <FormField label="Tên Vắc-xin" colSpan="2">
                <input
                  type="text"
                  required
                  className="form-input-vax"
                  value={editingVaccine.tenVacXin}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      tenVacXin: e.target.value,
                    })
                  }
                />
              </FormField>

              <FormField label="Loại Vắc-xin">
                <select
                  required
                  className="form-input-vax"
                  value={editingVaccine.maLoaiVacXin}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      maLoaiVacXin: e.target.value,
                    })
                  }
                >
                  <option value="">-- Chọn loại --</option>
                  {categories.map((cat) => (
                    <option key={cat.maLoaiVacXin} value={cat.maLoaiVacXin}>
                      {cat.tenLoaiVacXin}
                    </option>
                  ))}
                </select>
              </FormField>

              <FormField label="Đơn giá (VNĐ)">
                <input
                  type="number"
                  required
                  className="form-input-vax font-black text-emerald-600"
                  value={editingVaccine.donGia}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      donGia: e.target.value,
                    })
                  }
                />
              </FormField>

              <FormField label="Hạn sử dụng">
                <input
                  type="date"
                  required
                  className="form-input-vax"
                  value={editingVaccine.hanSuDung}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      hanSuDung: e.target.value,
                    })
                  }
                />
              </FormField>

              <FormField label="Độ tuổi tiêm chủng">
                <input
                  type="text"
                  placeholder="VD: 0-24 tháng"
                  className="form-input-vax"
                  value={editingVaccine.doTuoiTiemChung}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      doTuoiTiemChung: e.target.value,
                    })
                  }
                />
              </FormField>

              <FormField label="Điều kiện bảo quản" colSpan="2">
                <select
                  required
                  className="form-input-vax"
                  value={editingVaccine.dieuKienBaoQuan}
                  onChange={(e) =>
                    setEditingVaccine({
                      ...editingVaccine,
                      dieuKienBaoQuan: e.target.value,
                    })
                  }
                >
                  <option value="">-- Chọn điều kiện bảo quản --</option>
                  <option value="2°C - 8°C (Tiêu chuẩn)">
                    2°C - 8°C (Tiêu chuẩn)
                  </option>
                  <option value="Tủ đông (-20°C)">Tủ đông (-20°C)</option>
                  <option value="Siêu âm (-70°C)">Siêu âm (-70°C)</option>
                </select>
              </FormField>

              <div className="md:col-span-2 mt-4">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="w-full bg-emerald-600 text-white py-4 rounded-2xl font-black uppercase text-[10px] tracking-widest hover:bg-emerald-700 shadow-lg shadow-emerald-100 flex items-center justify-center gap-2 transition-all"
                >
                  <Save size={18} />{" "}
                  {isSaving ? "Đang lưu..." : "Lưu thông tin"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 4. MODAL XÁC NHẬN XÓA (RIÊNG BIỆT) */}
      {deleteModal.isOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/60 backdrop-blur-md p-4">
          <div className="bg-white w-full max-w-sm rounded-[2.5rem] shadow-2xl overflow-hidden animate-in zoom-in duration-200">
            <div className="p-8 text-center">
              <div className="w-20 h-20 bg-rose-50 text-rose-500 rounded-3xl flex items-center justify-center mx-auto mb-6">
                <AlertTriangle size={40} />
              </div>
              <h3 className="text-lg font-black text-slate-800 uppercase">
                Xác nhận xóa?
              </h3>
              <p className="text-slate-500 text-sm mt-2 font-medium">
                Bạn sắp xóa{" "}
                <span className="text-rose-600 font-bold">
                  "{deleteModal.name}"
                </span>
                . Dữ liệu này sẽ không thể khôi phục!
              </p>
            </div>
            <div className="flex border-t border-slate-50">
              <button
                onClick={() =>
                  setDeleteModal({ isOpen: false, id: null, name: "" })
                }
                className="flex-1 py-5 text-xs font-black text-slate-400 hover:bg-slate-50 uppercase tracking-widest"
              >
                Hủy
              </button>
              <button
                onClick={confirmDelete}
                disabled={isDeleting}
                className="flex-1 py-5 bg-rose-600 text-white text-xs font-black hover:bg-rose-700 uppercase tracking-widest"
              >
                {isDeleting ? "Đang xóa..." : "Đồng ý xóa"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// HELPER COMPONENTS
const FormField = ({ label, children, colSpan = "1" }) => (
  <div className={`space-y-1.5 ${colSpan === "2" ? "md:col-span-2" : ""}`}>
    <label className="text-[10px] font-black text-slate-400 uppercase ml-1 tracking-tighter">
      {label}
    </label>
    {children}
  </div>
);

const PriceSummaryCard = ({ label, value, icon, color }) => {
  const colorMap = {
    blue: "bg-blue-50 text-blue-600",
    emerald: "bg-emerald-50 text-emerald-600",
    amber: "bg-amber-50 text-amber-600",
  };
  return (
    <div className="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-sm flex flex-col items-start">
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
