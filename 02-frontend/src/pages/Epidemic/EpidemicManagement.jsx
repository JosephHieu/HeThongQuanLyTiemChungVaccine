import React, { useState, useEffect } from "react";
import {
  Plus,
  Edit,
  Trash2,
  Search,
  Activity,
  MapPin,
  Calendar,
  Users,
  AlertCircle,
  X,
  Info,
  Wind,
} from "lucide-react";
import epidemicApi from "../../api/epidemicApi";
import toast from "react-hot-toast";

const EpidemicManagement = () => {
  const [epidemics, setEpidemics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");

  const [formData, setFormData] = useState({
    tenDichBenh: "",
    duongLayNhiem: "",
    tacHaiSucKhoe: "",
    soNguoiBiNhiem: 0,
    diaChi: "",
    ghiChu: "",
    thoiDiemKhaoSat: new Date().toISOString().split("T")[0],
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await epidemicApi.getAll();
      setEpidemics(data);
    } catch (error) {
      toast.error(error.message || "Lỗi tải danh sách dịch bệnh");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // --- LOGIC TÌM KIẾM ---
  const filteredEpidemics = epidemics.filter((item) => {
    const query = searchQuery.toLowerCase();
    return (
      item.tenDichBenh.toLowerCase().includes(query) ||
      item.diaChi.toLowerCase().includes(query) ||
      (item.tenNhanVienKhaoSat &&
        item.tenNhanVienKhaoSat.toLowerCase().includes(query))
    );
  });

  const openModal = (item = null) => {
    if (item) {
      setEditingId(item.maDichBenh);
      const [d, m, y] = item.thoiDiemKhaoSat.split("/");
      setFormData({
        ...item,
        thoiDiemKhaoSat: `${y}-${m}-${d}`,
      });
    } else {
      setEditingId(null);
      setFormData({
        tenDichBenh: "",
        duongLayNhiem: "",
        tacHaiSucKhoe: "",
        soNguoiBiNhiem: 0,
        diaChi: "",
        ghiChu: "",
        thoiDiemKhaoSat: new Date().toISOString().split("T")[0],
      });
    }
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await epidemicApi.update(editingId, formData);
        toast.success("Cập nhật thông tin thành công!");
      } else {
        await epidemicApi.create(formData);
        toast.success("Thêm mới bản ghi dịch tễ thành công!");
      }
      setIsModalOpen(false);
      loadData();
    } catch (error) {
      toast.error(error.message || "Thao tác thất bại");
    }
  };

  const handleDelete = async (id) => {
    if (
      window.confirm("Bạn có chắc chắn muốn xóa bản ghi khảo sát này không?")
    ) {
      try {
        await epidemicApi.delete(id);
        toast.success("Đã xóa bản ghi.");
        loadData();
      } catch (error) {
        toast.error(error.message || "Xóa thất bại");
      }
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      {/* Header & Action */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-black text-slate-800 uppercase flex items-center gap-2">
            <Activity className="text-orange-600" /> Quản lý khảo sát dịch bệnh
          </h2>
          <p className="text-slate-500 text-sm">
            Cập nhật và theo dõi tình hình dịch tễ tại các địa phương.
          </p>
        </div>
        <button
          onClick={() => openModal()}
          className="flex items-center gap-2 bg-orange-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-orange-700 transition-all shadow-lg shadow-orange-100"
        >
          <Plus size={20} /> Thêm khảo sát mới
        </button>
      </div>

      {/* Toolbar Tìm kiếm */}
      <div className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-3">
        <div className="relative flex-1">
          <Search
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            size={18}
          />
          <input
            type="text"
            placeholder="Tìm kiếm theo địa chỉ, tên bệnh hoặc người khảo sát..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-100 rounded-lg outline-none focus:ring-2 focus:ring-orange-500/20"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Bảng dữ liệu */}
      <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/50 border-b border-slate-100">
              <th className="p-4 text-[10px] font-black text-slate-400 uppercase">
                Thông tin dịch bệnh
              </th>
              <th className="p-4 text-[10px] font-black text-slate-400 uppercase">
                Địa chỉ & Thời gian
              </th>
              <th className="p-4 text-[10px] font-black text-slate-400 uppercase text-center">
                Số ca nhiễm
              </th>
              <th className="p-4 text-[10px] font-black text-slate-400 uppercase">
                Người khảo sát
              </th>
              <th className="p-4 text-[10px] font-black text-slate-400 uppercase text-right">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {loading ? (
              <tr>
                <td
                  colSpan="5"
                  className="p-10 text-center text-slate-400 font-medium"
                >
                  Đang tải dữ liệu...
                </td>
              </tr>
            ) : filteredEpidemics.length === 0 ? ( // Sửa từ epidemics sang filteredEpidemics
              <tr>
                <td
                  colSpan="5"
                  className="p-10 text-center text-slate-400 font-medium"
                >
                  Không tìm thấy kết quả phù hợp.
                </td>
              </tr>
            ) : (
              filteredEpidemics.map(
                (
                  item, // Sửa từ epidemics sang filteredEpidemics
                ) => (
                  <tr
                    key={item.maDichBenh}
                    className="hover:bg-slate-50/50 transition-colors"
                  >
                    <td className="p-4 max-w-xs">
                      <p className="font-black text-slate-800 uppercase text-sm">
                        {item.tenDichBenh}
                      </p>
                      <div className="mt-1 space-y-0.5">
                        <p className="text-[10px] text-slate-500 font-bold flex items-center gap-1">
                          <Wind size={10} className="text-blue-400" /> Lây qua:{" "}
                          {item.duongLayNhiem}
                        </p>
                        <p className="text-[10px] text-slate-500 font-bold flex items-center gap-1">
                          <AlertCircle size={10} className="text-amber-500" />{" "}
                          Tác hại: {item.tacHaiSucKhoe}
                        </p>
                      </div>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-1 text-xs font-bold text-slate-600">
                        <MapPin size={12} className="text-rose-500" />{" "}
                        {item.diaChi}
                      </div>
                      <div className="flex items-center gap-1 text-[10px] text-slate-400 font-bold mt-1">
                        <Calendar size={10} /> {item.thoiDiemKhaoSat}
                      </div>
                    </td>
                    <td className="p-4 text-center">
                      <span className="px-3 py-1 bg-rose-50 text-rose-600 rounded-full font-black text-xs">
                        {item.soNguoiBiNhiem} ca
                      </span>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center justify-center gap-2">
                        <p className="text-xs font-bold text-slate-600 italic uppercase">
                          {item.tenNhanVienKhaoSat || "Staff"}
                        </p>
                      </div>
                    </td>
                    <td className="p-4 text-right space-x-2">
                      <button
                        onClick={() => openModal(item)}
                        className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-all"
                      >
                        <Edit size={18} />
                      </button>
                      <button
                        onClick={() => handleDelete(item.maDichBenh)}
                        className="p-2 text-rose-600 hover:bg-rose-50 rounded-lg transition-all"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ),
              )
            )}
          </tbody>
        </table>
      </div>

      {/* Modal Form Thêm/Sửa */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in duration-300">
          <div className="bg-white w-full max-w-2xl rounded-[2.5rem] shadow-2xl overflow-hidden relative">
            <div className="p-8 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
              <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">
                {editingId ? "Cập nhật khảo sát" : "Tạo khảo sát dịch tễ mới"}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-2 hover:bg-slate-200 rounded-full transition-all"
              >
                <X size={24} className="text-slate-400" />
              </button>
            </div>

            <form
              onSubmit={handleSubmit}
              className="p-8 grid grid-cols-2 gap-6"
            >
              <div className="col-span-2 space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Tên dịch bệnh
                </label>
                <input
                  required
                  type="text"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500"
                  value={formData.tenDichBenh}
                  onChange={(e) =>
                    setFormData({ ...formData, tenDichBenh: e.target.value })
                  }
                />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Số ca nhiễm
                </label>
                <input
                  type="number"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500"
                  value={formData.soNguoiBiNhiem}
                  onChange={(e) =>
                    setFormData({ ...formData, soNguoiBiNhiem: e.target.value })
                  }
                />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Ngày khảo sát
                </label>
                <input
                  required
                  type="date"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500"
                  value={formData.thoiDiemKhaoSat}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      thoiDiemKhaoSat: e.target.value,
                    })
                  }
                />
              </div>

              <div className="col-span-2 space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Địa chỉ xảy ra dịch
                </label>
                <input
                  required
                  type="text"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500"
                  value={formData.diaChi}
                  onChange={(e) =>
                    setFormData({ ...formData, diaChi: e.target.value })
                  }
                />
              </div>

              {/* Tách biệt hoàn toàn hai ô TextArea */}
              <div className="col-span-2 space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Đường lây nhiễm
                </label>
                <textarea
                  rows="2"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500 resize-none"
                  placeholder="Ví dụ: Qua đường hô hấp, tiếp xúc trực tiếp..."
                  value={formData.duongLayNhiem}
                  onChange={(e) =>
                    setFormData({ ...formData, duongLayNhiem: e.target.value })
                  }
                />
              </div>

              <div className="col-span-2 space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">
                  Tác hại sức khỏe
                </label>
                <textarea
                  rows="2"
                  className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl font-bold outline-none focus:border-orange-500 resize-none"
                  placeholder="Ví dụ: Gây sốt cao, tổn thương phổi, suy giảm miễn dịch..."
                  value={formData.tacHaiSucKhoe}
                  onChange={(e) =>
                    setFormData({ ...formData, tacHaiSucKhoe: e.target.value })
                  }
                />
              </div>

              <div className="col-span-2 pt-4 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-6 py-3 font-bold text-slate-400 hover:text-slate-600 transition-all"
                >
                  Hủy bỏ
                </button>
                <button
                  type="submit"
                  className="px-10 py-3 bg-orange-600 text-white font-black rounded-xl shadow-lg shadow-orange-100 hover:bg-orange-700 transition-all uppercase tracking-widest"
                >
                  {editingId ? "Lưu thay đổi" : "Xác nhận tạo"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default EpidemicManagement;
