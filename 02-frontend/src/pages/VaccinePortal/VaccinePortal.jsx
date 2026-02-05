import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import VaccineInfoTable from "./components/VaccineInfoTable";
import SearchFilter from "./components/SearchFilter";
// Thay đổi 1: Import Modal mới thay cho RegistrationModal cũ
import LookupRegistrationModal from "./components/modals/LookupRegistrationModal";
import userVaccineApi from "../../api/userVaccineApi";
import toast from "react-hot-toast";

const VaccinePortal = () => {
  const [vaccines, setVaccines] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedVaccine, setSelectedVaccine] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [regLoading, setRegLoading] = useState(false); // Thêm loading riêng cho việc đăng ký

  const navigate = useNavigate();

  const fetchVaccines = async (searchParams = { keyword: "" }) => {
    setLoading(true);
    try {
      const response = await userVaccineApi.getAvailableVaccines({
        keyword: searchParams.keyword,
        page: 0,
        size: 100,
      });
      setVaccines(response.content || response || []);
    } catch (error) {
      toast.error(error.message || "Lỗi khi kết nối đến máy chủ");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVaccines();
  }, []);

  const handleOpenRegister = (vaccine) => {
    console.log("Dữ liệu vắc-xin từ bảng: ", vaccine);
    setSelectedVaccine(vaccine);
    setIsModalOpen(true);
  };

  // Thay đổi 2: Hàm xử lý gửi dữ liệu đăng ký thực tế lên Backend
  const handleConfirmRegistration = async (bookingDate) => {
    setRegLoading(true);
    try {
      // KIỂM TRA: selectedVaccine phải có maLo (UUID)
      if (!selectedVaccine || !selectedVaccine.maLo) {
        toast.error("Không tìm thấy thông tin lô vắc-xin!");
        return;
      }

      const requestData = {
        // 1. Cần phải có mã lô vắc-xin (Đây là cái đang thiếu!)
        maLoVacXin: selectedVaccine.maLo,

        // 2. Thời gian tiêm (Đã có)
        thoiGianCanTiem: bookingDate,

        // 3. Ghi chú (Đã có)
        ghiChu: "Đăng ký từ tra cứu vắc-xin trực tuyến",

        // 4. Nếu Backend bắt buộc có maBenhNhan trong Body (không lấy từ Token)
        // thì bạn phải thêm maBenhNhan: localStorage.getItem("userId") vào đây
      };

      console.log("Payload chuẩn bị gửi đi:", requestData); // Log để kiểm tra lại lần nữa

      await userVaccineApi.registerVaccination(requestData);

      toast.success("Đăng ký thành công!");
      setIsModalOpen(false);
      fetchVaccines();
    } catch (error) {
      toast.error(error.message || "Đăng ký thất bại!");
    } finally {
      setRegLoading(false);
    }
  };

  return (
    <div className="p-6 bg-slate-100 min-h-screen">
      <div className="max-w-7xl mx-auto bg-white shadow-xl border border-slate-300 overflow-hidden">
        <div className="bg-[#1e4e8c] p-2 text-white text-center font-bold text-lg uppercase border-b border-slate-400">
          Tra cứu thông tin các loại vắc xin
        </div>

        <div className="p-6 space-y-8">
          <VaccineInfoTable
            data={vaccines}
            loading={loading}
            onRegister={handleOpenRegister}
          />

          <div className="bg-slate-50 p-6 border border-slate-200 rounded-lg shadow-inner">
            <div className="text-sm font-bold text-slate-700 mb-2 italic">
              * Nhập tên vắc xin hoặc bệnh cần phòng để tìm kiếm:
            </div>
            <SearchFilter onSearch={fetchVaccines} />
          </div>

          <div className="flex justify-center mt-6">
            <button
              onClick={() => navigate("/user")}
              className="px-12 py-2 bg-slate-300 border border-slate-400 hover:bg-slate-400 font-bold text-slate-800 shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none transition-all"
            >
              QUAY LẠI TRANG CHỦ
            </button>
          </div>
        </div>
      </div>

      {/* Thay đổi 3: Sử dụng Modal mới với các Props mới */}
      {isModalOpen && (
        <LookupRegistrationModal
          vaccine={selectedVaccine}
          onClose={() => setIsModalOpen(false)}
          onConfirm={handleConfirmRegistration} // Truyền hàm xử lý ngày vào đây
          loading={regLoading}
        />
      )}
    </div>
  );
};

export default VaccinePortal;
