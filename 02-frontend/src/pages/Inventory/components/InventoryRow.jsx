import React from "react";
import { Info } from "lucide-react";

const InventoryRow = ({ data, onDetail }) => {
  // Bóc tách dữ liệu từ props data
  const { soLo, tenVacXin, tenLoaiVacXin, soLuong, tinhTrang, phongNguaBenh } =
    data;

  return (
    <tr
      onClick={() => onDetail(data)} // Click vào dòng để xem chi tiết
      className="hover:bg-blue-50/50 transition-all cursor-pointer group border-b border-slate-50"
    >
      <td className="p-4 text-xs font-bold text-slate-500 uppercase tracking-wider">
        {soLo || "N/A"}
      </td>
      <td className="p-4">
        <p className="text-sm font-bold text-slate-800 group-hover:text-blue-600 transition-colors">
          {tenVacXin}
        </p>
        <div className="flex flex-col gap-0.5 mt-0.5">
          <span className="text-[10px] text-blue-500 font-medium italic">
            Phòng: {phongNguaBenh || "---"}
          </span>
          <span className="text-[9px] bg-slate-100 text-slate-500 px-1.5 py-0.5 rounded w-fit font-bold uppercase">
            {tenLoaiVacXin}
          </span>
        </div>
      </td>
      <td className="p-4">
        <p className="text-sm font-black text-slate-700">
          {soLuong?.toLocaleString()}{" "}
          <span className="text-[10px] text-slate-400 font-normal">liều</span>
        </p>
      </td>
      <td className="p-4 text-center">
        <span
          className={`px-3 py-1 rounded-full text-[10px] font-black uppercase ${
            tinhTrang === "Còn"
              ? "bg-green-100 text-green-700"
              : "bg-red-100 text-red-700"
          }`}
        >
          {tinhTrang}
        </span>
      </td>
      <td className="p-4 text-right">
        <button className="p-2 text-slate-300 group-hover:text-blue-500 transition-colors">
          <Info size={18} />
        </button>
      </td>
    </tr>
  );
};

export default InventoryRow;
