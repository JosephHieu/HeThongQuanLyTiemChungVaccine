import React from "react";

const VaccineInfoTable = ({ data, loading, onRegister }) => {
  // Tiêu đề các cột
  const headers = [
    "STT",
    "Số lô",
    "Tên Vắc xin",
    "Phòng trị bệnh",
    "Số lượng",
    "Độ tuổi tiêm phòng",
    "Đăng ký",
  ];

  return (
    <div className="overflow-x-auto border border-slate-400 shadow-sm bg-white">
      <table className="w-full text-sm border-collapse">
        {/* Header của bảng với màu xám nhạt đặc trưng */}
        <thead>
          <tr className="bg-slate-200">
            {headers.map((header) => (
              <th
                key={header}
                className="border border-slate-400 p-2 text-center font-bold text-slate-800"
              >
                {header}
              </th>
            ))}
          </tr>
        </thead>

        {/* Thân bảng */}
        <tbody>
          {loading ? (
            <tr>
              <td
                colSpan={headers.length}
                className="p-8 text-center text-slate-500 italic"
              >
                Đang tải dữ liệu vắc-xin...
              </td>
            </tr>
          ) : data.length > 0 ? (
            data.map((item, index) => (
              <tr
                key={item.maVacXin}
                className="hover:bg-blue-50 transition-colors"
              >
                <td className="border border-slate-400 p-2 text-center">
                  {index + 1}
                </td>
                <td className="border border-slate-400 p-2 text-center font-bold text-blue-700">
                  {item.soLo || "Chưa có lô"}
                </td>
                <td className="border border-slate-400 p-2 font-bold text-blue-900">
                  {item.tenVacXin}
                </td>
                <td className="border border-slate-400 p-2">
                  {item.phongNguaBenh}
                </td>
                <td className="border border-slate-400 p-2 text-center">
                  {item.soLuongLieu > 0 ? (
                    <span className="text-green-700 font-medium">
                      {item.soLuongLieu} liều
                    </span>
                  ) : (
                    <span className="text-red-600 font-black">HẾT HÀNG</span>
                  )}
                </td>
                <td className="border border-slate-400 p-2 text-center italic text-slate-600">
                  {item.doTuoi || "Mọi lứa tuổi"}
                </td>
                <td className="border border-slate-400 p-2 text-center">
                  <button
                    onClick={() => onRegister(item)}
                    disabled={item.soLuongLieu <= 0}
                    className={`px-4 py-0.5 border border-slate-800 font-bold text-xs shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:shadow-none active:translate-x-[1px] active:translate-y-[1px] transition-all
                      ${
                        item.soLuongLieu > 0
                          ? "bg-white hover:bg-slate-800 hover:text-white"
                          : "bg-slate-200 text-slate-400 border-slate-400 cursor-not-allowed shadow-none"
                      }`}
                  >
                    Đăng ký
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={headers.length}
                className="p-8 text-center text-red-500 font-medium"
              >
                Không tìm thấy loại vắc-xin nào phù hợp!
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default VaccineInfoTable;
