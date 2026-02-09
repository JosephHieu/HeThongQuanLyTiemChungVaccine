import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, StickyNote, LayoutDashboard } from "lucide-react";
import { toast } from "react-hot-toast";
import { format } from "date-fns";

// Import Components
import CalendarSidebar from "./components/CalendarSidebar";
import ScheduleForm from "./components/ScheduleForm";
import RegistrationTable from "./components/RegistrationTable";
import ScheduleList from "./components/ScheduleList";
import ConfirmModal from "./components/ConfirmModal";

// Import API
import vaccinationApi from "../../api/vaccinationApi";

const ScheduleManagement = () => {
  const navigate = useNavigate();

  // --- States ---
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [scheduleData, setScheduleData] = useState(null);
  const [registrations, setRegistrations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [selectedShift, setSelectedShift] = useState("Sáng (07:30 - 11:30)");
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [idToDelete, setIdToDelete] = useState(null);

  // --- States mới cho Danh sách ---
  const [viewMode, setViewMode] = useState("calendar"); // "calendar" | "list"
  const [listData, setListData] = useState({ data: [], totalPages: 0 });
  const [queryParams, setQueryParams] = useState({
    page: 1,
    size: 10,
    search: "",
  });

  // --- Logic Tải Dữ Liệu ---
  const fetchData = useCallback(async (date, shift) => {
    setLoading(true);
    const dateStr = format(date, "yyyy-MM-dd");
    try {
      const [schedule, regPageResponse] = await Promise.all([
        vaccinationApi.getScheduleByDate(dateStr, shift),
        vaccinationApi.getRegistrations(dateStr),
      ]);

      // 1. schedule bây giờ chính là Object LichTiemChung
      setScheduleData(schedule);

      // 2. regPageResponse chính là PageResponse { data: [...], totalPages: 5 }
      const actualRegistrations = regPageResponse?.data || [];

      console.log("Check dữ liệu bệnh nhân:", actualRegistrations); // Xem log này để chắc chắn
      setRegistrations(actualRegistrations);
    } catch (error) {
      console.error("Error fetching data:", error);
      setScheduleData(null);
      setRegistrations([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData(selectedDate, selectedShift);
  }, [selectedDate, selectedShift, fetchData]);

  // --- Logic tải danh sách tổng (phân trang) ---
  const fetchListData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await vaccinationApi.getAllSchedules(queryParams);

      // Log cho thấy res chính là PageResponse rồi
      console.log("Gán dữ liệu vào listData:", res);

      // Gán trực tiếp vì res có sẵn trường .data và .totalPages
      setListData(res);
    } catch (error) {
      console.error("Lỗi fetchListData:", error);
      toast.error("Không thể tải danh sách");
    } finally {
      setLoading(false);
    }
  }, [queryParams]);

  // Tự động tải lại khi đổi mode hoặc đổi trang/search
  useEffect(() => {
    if (viewMode === "list") {
      fetchListData();
    }
  }, [viewMode, fetchListData]);

  // --- Handlers ---
  const handleSave = async (formData) => {
    const payload = {
      ...formData,
      ngayTiem: format(selectedDate, "yyyy-MM-dd"),
    };

    // 1. Xác định hành động (Update hoặc Create)
    const action = scheduleData?.maLichTiemChung
      ? vaccinationApi.updateSchedule(scheduleData.maLichTiemChung, payload)
      : vaccinationApi.createSchedule(payload);

    // 2. Sử dụng toast.promise để quản lý 3 trạng thái
    toast.promise(
      action,
      {
        loading: "Đang gửi dữ liệu lên hệ thống...",
        success: (res) => {
          // res ở đây chính là ApiResponse { code, result, message } từ Backend
          setRefreshTrigger((prev) => prev + 1);
          fetchData(selectedDate);
          return <b>{res.message || "Lưu lịch thành công!"}</b>;
        },
        error: (err) => {
          // Lấy message lỗi từ ErrorCode backend trả về
          const errorMsg = err.response?.data?.message || "Lỗi thao tác!";
          return <b>{errorMsg}</b>;
        },
      },
      {
        // Tùy chỉnh Style cho đồng bộ với UI xanh của bạn
        success: {
          duration: 3000,
          iconTheme: { primary: "#2563eb", secondary: "#fff" },
        },
      },
    );
  };

  // 4. Hàm này sẽ được gọi khi người dùng chọn ca khác trong ScheduleForm
  const handleShiftChange = (newShift) => {
    setSelectedShift(newShift);
  };

  const handleEditFromList = (item) => {
    setSelectedDate(new Date(item.ngayTiem)); // Chuyển ngày
    setSelectedShift(item.thoiGian); // Chuyển ca
    setViewMode("calendar"); // Quay lại giao diện lịch để sửa
  };

  // 1. Hàm mở Modal xác nhận
  const requestDelete = (id) => {
    setIdToDelete(id || scheduleData?.maLichTiemChung);
    setIsDeleteModalOpen(true);
  };

  // 2. Hàm thực hiện xóa thực tế (gọi sau khi nhấn Confirm trên Modal)
  const confirmDelete = async () => {
    if (!idToDelete) return;

    setIsDeleteModalOpen(false); // Đóng modal trước
    const deleteAction = vaccinationApi.deleteSchedule(idToDelete);

    toast.promise(deleteAction, {
      loading: "Đang xóa dữ liệu...",
      success: (res) => {
        setRefreshTrigger((p) => p + 1);
        if (viewMode === "calendar") {
          setScheduleData(null);
          setRegistrations([]);
        } else {
          fetchListData();
        }
        return <b>{res.message || "Đã xóa thành công"}</b>;
      },
      error: (err) => (
        <b>Lỗi: {err.response?.data?.message || "Không thể xóa"}</b>
      ),
    });
  };

  return (
    <div className="p-4 md:p-6 lg:p-8 bg-slate-50 min-h-screen">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* --- HEADER --- */}
        <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-5 rounded-[2rem] shadow-sm border border-slate-100">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate(-1)}
              className="p-3 bg-slate-50 text-slate-600 rounded-2xl hover:bg-blue-600 hover:text-white transition-all"
            >
              <ArrowLeft size={20} />
            </button>
            <div>
              <h1 className="text-xl md:text-2xl font-black text-slate-800 uppercase flex items-center gap-2">
                <LayoutDashboard className="text-blue-600" size={24} /> Điều
                phối lịch tiêm
              </h1>
              <p className="text-slate-400 text-xs font-medium uppercase tracking-wider">
                Hệ thống quản trị trung tâm
              </p>
            </div>
          </div>

          {/* NÚT CHUYỂN ĐỔI CHẾ ĐỘ XEM */}
          <div className="bg-slate-100 p-1 rounded-2xl flex gap-1 border border-slate-200/50 self-end sm:self-auto">
            <button
              onClick={() => setViewMode("calendar")}
              className={`px-6 py-2 rounded-xl text-[10px] font-black uppercase transition-all ${
                viewMode === "calendar"
                  ? "bg-white text-blue-600 shadow-sm"
                  : "text-slate-400 hover:text-slate-600"
              }`}
            >
              Dạng lịch
            </button>
            <button
              onClick={() => setViewMode("list")}
              className={`px-6 py-2 rounded-xl text-[10px] font-black uppercase transition-all ${
                viewMode === "list"
                  ? "bg-white text-blue-600 shadow-sm"
                  : "text-slate-400 hover:text-slate-600"
              }`}
            >
              Danh sách tổng
            </button>
          </div>
        </header>

        {/* --- NỘI DUNG THAY ĐỔI THEO VIEW MODE --- */}
        {viewMode === "calendar" ? (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start animate-in fade-in zoom-in-95 duration-500">
            {/* CỘT TRÁI: SIDEBAR */}
            <aside className="lg:col-span-4 space-y-6 sticky top-8">
              <CalendarSidebar
                selectedDate={selectedDate}
                onDateChange={setSelectedDate}
                onAddNew={() => {
                  setScheduleData(null);
                  setRegistrations([]);
                }}
                onDelete={() => requestDelete()}
                refreshTrigger={refreshTrigger}
              />

              <div className="bg-white p-6 rounded-[2rem] shadow-sm border border-slate-100">
                <h3 className="font-bold text-slate-800 mb-3 flex items-center gap-2 text-sm uppercase tracking-tight">
                  <StickyNote size={18} className="text-amber-500" />
                  Ghi chú ngày {format(selectedDate, "dd/MM/yyyy")}
                </h3>
                <div className="p-4 bg-slate-50 rounded-2xl text-sm text-slate-500 min-h-[100px] border border-dashed border-slate-200 leading-relaxed italic">
                  {scheduleData?.ghiChu ||
                    "Không có ghi chú đặc biệt cho ngày này..."}
                </div>
              </div>
            </aside>

            {/* CỘT PHẢI: FORM & BẢNG BỆNH NHÂN */}
            <main className="lg:col-span-8 space-y-6">
              <ScheduleForm
                // key={`${selectedDate.toISOString()}-${selectedShift}`}
                initialData={scheduleData}
                selectedDate={selectedDate}
                onSave={handleSave}
                loading={loading}
                onShiftChange={handleShiftChange}
              />
              <RegistrationTable data={registrations} loading={loading} />
            </main>
          </div>
        ) : (
          /* HIỂN THỊ DẠNG DANH SÁCH TỔNG QUÁT */
          <ScheduleList
            listData={listData}
            onEdit={handleEditFromList} // Tận dụng hàm bạn đã viết
            onDelete={requestDelete}
            queryParams={queryParams}
            setQueryParams={setQueryParams}
          />
        )}
      </div>
      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={confirmDelete}
        title="Xác nhận xóa lịch trực"
        message="Bạn có chắc chắn muốn xóa? Toàn bộ danh sách đăng ký của bệnh nhân trong ca trực này cũng sẽ bị mất."
        loading={loading}
      />
    </div>
  );
};

export default ScheduleManagement;
