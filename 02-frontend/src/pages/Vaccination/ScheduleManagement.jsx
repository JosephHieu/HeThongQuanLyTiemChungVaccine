import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, StickyNote } from "lucide-react";
import CalendarSidebar from "./components/CalendarSidebar";
import ScheduleForm from "./components/ScheduleForm";
import RegistrationTable from "./components/RegistrationTable";
import { toast } from "react-hot-toast";

const ScheduleManagement = () => {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [registrations, setRegistrations] = useState([]);
  const [note, setNote] = useState(""); // State cho phần ghi chú mới

  const handleSave = async (formData) => {
    try {
      // Logic gọi API đã viết ở Backend
      toast.success("Lưu lịch tiêm thành công!");
    } catch (error) {
      toast.error("Lỗi khi lưu thông tin!");
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen space-y-6">
      {/* --- HEADER --- */}
      <header className="flex items-center gap-4 bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
        <button
          onClick={() => navigate("/admin/dashboard")}
          className="p-2.5 bg-white border border-gray-200 rounded-xl text-gray-600 hover:bg-gray-50 hover:text-blue-600 transition-all shadow-sm group"
        >
          <ArrowLeft
            size={22}
            className="group-hover:-translate-x-1 transition-transform"
          />
        </button>
        <div>
          <h1 className="text-xl font-bold text-gray-800 uppercase tracking-tight">
            Quản lý lịch tiêm chủng
          </h1>
          <p className="text-gray-500 text-xs font-medium">
            Hệ thống quản trị trung tâm
          </p>
        </div>
      </header>

      <div className="flex flex-col md:flex-row gap-6">
        {/* --- CỘT TRÁI: LỊCH & GHI CHÚ --- */}
        <div className="w-full md:w-1/3 space-y-4">
          <CalendarSidebar
            selectedDate={selectedDate}
            onDateChange={setSelectedDate}
          />

          {/* THAY THẾ THAO TÁC NHANH THÀNH GHI CHÚ TẠI ĐÂY */}
          <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
            <h3 className="font-bold text-gray-800 mb-3 flex items-center gap-2">
              <StickyNote size={18} className="text-amber-500" />
              Ghi chú ngày {selectedDate.toLocaleDateString("vi-VN")}
            </h3>
            <textarea
              className="w-full h-32 p-3 bg-slate-50 border-none rounded-xl text-sm text-slate-600 focus:ring-2 focus:ring-blue-500 outline-none resize-none transition-all"
              placeholder="Nhập ghi chú cho ngày này..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
            ></textarea>
            <p className="text-[10px] text-slate-400 mt-2 italic">
              * Ghi chú này sẽ được hiển thị khi xem chi tiết lịch.
            </p>
          </div>
        </div>

        {/* --- CỘT PHẢI: FORM CHI TIẾT & BẢNG --- */}
        <div className="w-full md:w-2/3 space-y-6">
          {/* Form này chứa các thông tin vắc xin, bác sĩ, địa điểm... */}
          <ScheduleForm selectedDate={selectedDate} onSave={handleSave} />
          <RegistrationTable data={registrations} />
        </div>
      </div>
    </div>
  );
};

export default ScheduleManagement;
