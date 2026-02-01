import React, { useState } from "react";
import { ClipboardList, Search, UserPlus, FileSignature } from "lucide-react";
import ViewTab from "./components/ViewTab";
import UpdateTab from "./components/UpdateTab";
import PrescribeTab from "./components/PrescribeTab";
import medicalApi from "../../api/medicalApi"; // Thay đổi ở đây
import toast from "react-hot-toast";

const MedicalRecord = () => {
  const [activeTab, setActiveTab] = useState("view");
  const [patientId, setPatientId] = useState("");
  const [patientData, setPatientData] = useState(null);
  const [loading, setLoading] = useState(false);

  // Hàm truy xuất dữ liệu (Tách riêng để có thể gọi lại sau khi Update/Kê đơn)
  const fetchPatientData = async (silent = false) => {
    if (!patientId.trim()) {
      if (!silent) toast.error("Vui lòng nhập ID bệnh nhân!");
      return;
    }

    if (!silent) setLoading(true);
    try {
      // Sử dụng medicalApi đã định nghĩa
      const data = await medicalApi.getRecord(patientId);
      setPatientData(data);
      if (!silent) toast.success("Đã truy xuất hồ sơ!");
    } catch (error) {
      setPatientData(null);
      // Lấy message lỗi từ AppException trả về
      toast.error(error.message || "Không tìm thấy bệnh nhân!");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchPatientData();
  };

  // Hàm được gọi sau khi thực hiện thành công ở các Tab con
  const handleRefresh = () => {
    fetchPatientData(true); // silent = true để reload ngầm, không hiện loading che màn hình
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6 animate-in fade-in duration-500">
      {/* --- SECTION 1: HEADER & TÌM KIẾM --- */}
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
              disabled={loading}
              className="px-6 py-3 bg-blue-600 text-white font-bold rounded-2xl hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 disabled:opacity-50"
            >
              {loading ? "ĐANG TẢI..." : "TRUY XUẤT"}
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

        {activeTab === "update" && (
          <UpdateTab
            data={patientData}
            onUpdateSuccess={handleRefresh} // Callback khi update xong
          />
        )}

        {activeTab === "prescribe" && (
          <PrescribeTab
            data={patientData}
            onPrescribeSuccess={handleRefresh} // Callback khi kê đơn xong
          />
        )}
      </div>
    </div>
  );
};

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
