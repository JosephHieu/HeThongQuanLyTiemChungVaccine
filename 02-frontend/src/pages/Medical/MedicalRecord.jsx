import React, { useState } from "react";
import { ClipboardList, Search, UserPlus, FileSignature } from "lucide-react";
import ViewTab from "./components/ViewTab";
import UpdateTab from "./components/UpdateTab";
import PrescribeTab from "./components/PrescribeTab";
import axiosClient from "../../api/axiosClient";
import toast from "react-hot-toast";

const MedicalRecord = () => {
  const [activeTab, setActiveTab] = useState("view"); // 'view' | 'update' | 'prescribe'
  const [patientId, setPatientId] = useState("");
  const [patientData, setPatientData] = useState(null);
  const [loading, setLoading] = useState(false);

  // Hàm tìm kiếm dùng chung cho cả 3 Tab
  const handleSearch = async (e) => {
    if (e) e.preventDefault();
    if (!patientId.trim()) {
      toast.error("Vui lòng nhập ID bệnh nhân!");
      return;
    }

    setLoading(true);
    try {
      const data = await axiosClient.get(`/medical-records/${patientId}`);
      setPatientData(data);
      toast.success("Đã truy xuất hồ sơ!");
    } catch (error) {
      setPatientData(null);
      toast.error("Không tìm thấy bệnh nhân hoặc lỗi kết nối!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6 animate-in fade-in duration-500">
      {/* --- SECTION 1: HEADER & TÌM KIẾM (Responsive) --- */}
      <div className="bg-white p-6 md:p-8 rounded-[2rem] shadow-sm border border-slate-100">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div>
            <h1 className="text-2xl font-black text-slate-800 flex items-center gap-3">
              <div className="p-2 bg-blue-100 text-blue-600 rounded-xl">
                <ClipboardList size={24} />
              </div>
              HỒ SƠ BỆNH ÁN
            </h1>
            <p className="text-slate-500 text-sm mt-1 font-medium italic">
              Quản lý và điều phối tiêm chủng
            </p>
          </div>

          <form
            onSubmit={handleSearch}
            className="flex gap-2 w-full lg:max-w-md"
          >
            <input
              type="text"
              placeholder="Nhập ID bệnh nhân..."
              className="flex-1 pl-6 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-blue-500 outline-none font-bold"
              value={patientId}
              onChange={(e) => setPatientId(e.target.value)}
            />
            <button
              type="submit"
              className="px-6 py-3 bg-blue-600 text-white font-bold rounded-2xl hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
            >
              TRUY XUẤT
            </button>
          </form>
        </div>

        {/* --- NAVIGATION TABS --- */}
        <div className="flex items-center gap-2 mt-8 p-1 bg-slate-50 rounded-2xl w-fit border border-slate-100">
          <TabButton
            active={activeTab === "view"}
            onClick={() => setActiveTab("view")}
            icon={<Search size={18} />}
            label="Xem hồ sơ"
          />
          <TabButton
            active={activeTab === "update"}
            onClick={() => setActiveTab("update")}
            icon={<UserPlus size={18} />}
            label="Cập nhật"
          />
          <TabButton
            active={activeTab === "prescribe"}
            onClick={() => setActiveTab("prescribe")}
            icon={<FileSignature size={18} />}
            label="Kê đơn"
          />
        </div>
      </div>

      {/* --- SECTION 2: HIỂN THỊ NỘI DUNG THEO TAB --- */}
      <div className="min-h-[400px]">
        {activeTab === "view" && (
          <ViewTab data={patientData} loading={loading} />
        )}
        {activeTab === "update" && <UpdateTab data={patientData} />}
        {activeTab === "prescribe" && <PrescribeTab data={patientData} />}
      </div>
    </div>
  );
};

// Component con cho Nút Tab
const TabButton = ({ active, onClick, icon, label }) => (
  <button
    onClick={onClick}
    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl transition-all font-bold text-sm ${
      active
        ? "bg-white text-blue-600 shadow-sm ring-1 ring-slate-200"
        : "text-slate-400 hover:text-slate-600"
    }`}
  >
    {icon}
    <span className="hidden sm:inline">{label}</span>
  </button>
);

export default MedicalRecord;
