import React, { useState } from "react";

const SearchFilter = ({ onSearch }) => {
  const [criteria, setCriteria] = useState("tenVacXin"); // Mặc định tìm theo tên
  const [keyword, setKeyword] = useState("");

  const handleSearchClick = () => {
    // Gọi callback truyền ra ngoài Page để gọi API
    onSearch({ criteria, keyword });
  };

  return (
    <div className="flex items-center justify-center gap-4 mt-8">
      <span className="text-sm font-bold text-slate-700">Tìm kiếm</span>

      {/* Combo Box chọn tiêu chí */}
      <select
        value={criteria}
        onChange={(e) => setCriteria(e.target.value)}
        className="border border-slate-400 p-1 text-sm bg-white outline-none focus:border-blue-500"
      >
        <option value="maVacXin">Mã Vắc Xin</option>
        <option value="tenVacXin">Tên Vắc Xin</option>
        <option value="phongTriBenh">Phòng trị bệnh</option>
      </select>

      {/* TextBox nhập từ khóa */}
      <input
        type="text"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        onKeyPress={(e) => e.key === "Enter" && handleSearchClick()}
        className="border border-slate-400 p-1 w-80 text-sm outline-none focus:border-blue-500"
      />

      {/* Nút Tìm */}
      <button
        onClick={handleSearchClick}
        className="px-8 py-1 bg-slate-100 border border-slate-400 hover:bg-slate-200 font-bold text-sm transition-all"
      >
        Tìm
      </button>
    </div>
  );
};

export default SearchFilter;
