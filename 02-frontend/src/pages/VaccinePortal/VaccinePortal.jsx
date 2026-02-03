import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import VaccineInfoTable from "./components/VaccineInfoTable";
import SearchFilter from "./components/SearchFilter";
import RegistrationModal from "../../pages/VaccinePortal/components/modals/RegistrationModal";
import userVaccineApi from "../../api/userVaccineApi";
import toast from "react-hot-toast";

const VaccinePortal = () => {
  const [vaccines, setVaccines] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedVaccine, setSelectedVaccine] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const navigate = useNavigate();

  // 1. Hàm tải dữ liệu (có xử lý tìm kiếm)
  const fetchVaccines = async (searchParams = { keyword: "" }) => {
    setLoading(true);
    try {
      // Backend của bạn dùng 'keyword' để lọc chung cho các trường
      const response = await userVaccineApi.getAvailableVaccines({
        keyword: searchParams.keyword,
        page: 0,
        size: 100,
      });
      // response đã được unwrapped bởi axiosClient (lấy trực tiếp .result)
      setVaccines(response.content || []);
    } catch (error) {
      toast.error(error.message || "Lỗi khi kết nối đến máy chủ");
    } finally {
      setLoading(false);
    }
  };

  // 2. Tự động load dữ liệu khi mở trang
  useEffect(() => {
    fetchVaccines();
  }, []);

  // 3. Xử lý khi nhấn nút "Đăng ký" trong bảng
  const handleOpenRegister = (vaccine) => {
    setSelectedVaccine(vaccine);
    setIsModalOpen(true);
  };

  return (
    <div className="p-6 bg-slate-100 min-h-screen">
      <div className="max-w-7xl mx-auto bg-white shadow-xl rounded-none border border-slate-300 overflow-hidden">
        {/* Header xanh đậm chuẩn phong cách y tế */}
        <div className="bg-[#1e4e8c] p-2 text-white text-center font-bold text-lg uppercase border-b border-slate-400">
          Xem thông tin các loại vắc xin
        </div>

        <div className="p-6 space-y-8">
          {/* Bảng dữ liệu hiển thị bên trên */}
          <VaccineInfoTable
            data={vaccines}
            loading={loading}
            onRegister={handleOpenRegister}
          />

          {/* Thanh tìm kiếm hiển thị bên dưới bảng theo SRS */}
          <div className="bg-slate-50 p-6 border border-slate-200 rounded-lg">
            <SearchFilter onSearch={fetchVaccines} />
          </div>

          {/* Nút thoát/xác nhận cuối trang */}
          <div className="flex justify-center mt-6">
            <button
              onClick={() => navigate("/user")}
              className="px-12 py-1 bg-slate-200 border border-slate-400 hover:bg-slate-300 font-bold text-slate-700 shadow-[1px_1px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all"
            >
              OK
            </button>
          </div>
        </div>
      </div>

      {/* Modal đăng ký (Popup) */}
      {isModalOpen && (
        <RegistrationModal
          vaccine={selectedVaccine}
          onClose={() => setIsModalOpen(false)}
          onSuccess={fetchVaccines} // Cập nhật lại số lượng sau khi đăng ký thành công
        />
      )}
    </div>
  );
};

export default VaccinePortal;
