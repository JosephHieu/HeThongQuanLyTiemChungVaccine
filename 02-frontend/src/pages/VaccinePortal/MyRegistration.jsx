import React, { useEffect, useState } from "react";
import userVaccineApi from "../../api/userVaccineApi";
import toast from "react-hot-toast";
import Swal from "sweetalert2";
import {
  ClipboardList,
  Trash2,
  Clock,
  CheckCircle,
  XCircle,
} from "lucide-react";

const MyRegistrations = () => {
  const [registrations, setRegistrations] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchHistory = async () => {
    setLoading(true);
    try {
      const data = await userVaccineApi.getMyRegistrations();
      setRegistrations(data);
    } catch (error) {
      toast.error(error.message || "Không thể tải lịch sử");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleCancel = (id) => {
    Swal.fire({
      title: "Xác nhận hủy?",
      text: "Bạn sẽ hoàn trả lại 1 suất tiêm vào kho vắc-xin.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Đồng ý hủy",
      cancelButtonText: "Đóng",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await userVaccineApi.cancelRegistration(id);
          toast.success("Đã hủy đăng ký thành công");
          fetchHistory(); // Tải lại danh sách
        } catch (error) {
          toast.error(error.message || "Không thể hủy lịch này");
        }
      }
    });
  };

  return (
    <div className="p-6 bg-slate-50 min-h-screen">
      <div className="max-w-6xl mx-auto bg-white shadow-md border border-slate-200">
        <div className="bg-[#1e4e8c] p-4 text-white flex items-center gap-2 font-bold uppercase">
          <ClipboardList size={20} /> Lịch sử đăng ký tiêm chủng
        </div>

        <div className="p-6">
          <div className="overflow-x-auto">
            <table className="w-full text-sm border-collapse">
              <thead className="bg-slate-100">
                <tr>
                  <th className="border p-3 text-center w-12">STT</th>
                  <th className="border p-3 text-left">Vắc-xin</th>
                  <th className="border p-3 text-center">Thời gian</th>
                  <th className="border p-3 text-center">Trạng thái</th>
                  <th className="border p-3 text-center">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {registrations.map((item, index) => (
                  <tr key={item.maDangKy} className="hover:bg-slate-50">
                    <td className="border p-3 text-center">{index + 1}</td>
                    <td className="border p-3">
                      <p className="font-bold text-blue-900">
                        {item.tenVacXin}
                      </p>
                      <p className="text-[10px] text-slate-400">
                        Lô: {item.soLo}
                      </p>
                    </td>
                    <td className="border p-3 text-center">
                      {item.ngayTiem} <br />{" "}
                      <span className="text-xs text-emerald-600 font-medium">
                        {item.thoiGian}
                      </span>
                    </td>
                    <td className="border p-3 text-center">
                      <span
                        className={`px-2 py-1 rounded-full text-[10px] font-bold uppercase border
                        ${
                          item.trangThai === "REGISTERED"
                            ? "bg-blue-50 text-blue-600 border-blue-200"
                            : item.trangThai === "COMPLETED"
                              ? "bg-green-50 text-green-600 border-green-200"
                              : "bg-slate-100 text-slate-500 border-slate-200"
                        }`}
                      >
                        {item.trangThai}
                      </span>
                    </td>
                    <td className="border p-3 text-center">
                      {item.trangThai === "REGISTERED" && (
                        <button
                          onClick={() => handleCancel(item.maDangKy)}
                          className="text-rose-600 hover:bg-rose-50 p-2 rounded-full transition-colors"
                          title="Hủy đăng ký"
                        >
                          <Trash2 size={18} />
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyRegistrations;
