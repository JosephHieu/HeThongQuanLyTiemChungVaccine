import React from "react";
import { Save, UserPlus, Trash2 } from "lucide-react";

const ScheduleForm = ({ selectedDate, onSave }) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
      <h2 className="text-xl font-bold text-gray-800 mb-6 border-b pb-4">
        Điều chỉnh lịch tiêm chủng
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Cột 1 */}
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Ngày tiêm
            </label>
            <input
              type="text"
              readOnly
              value={selectedDate.toLocaleDateString("vi-VN")}
              className="mt-1 block w-full bg-gray-50 border border-gray-300 rounded-md p-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Thời gian
            </label>
            <select className="mt-1 block w-full border border-gray-300 rounded-md p-2 focus:ring-blue-500">
              <option>Sáng (07:30 - 11:30)</option>
              <option>Chiều (13:30 - 17:00)</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Loại vắc xin
            </label>
            <select className="mt-1 block w-full border border-gray-300 rounded-md p-2">
              <option>AstraZeneca</option>
              <option>Vero Cell</option>
              <option>Pfizer</option>
            </select>
          </div>
        </div>

        {/* Cột 2 */}
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Địa điểm
            </label>
            <input
              type="text"
              className="mt-1 block w-full border border-gray-300 rounded-md p-2"
              placeholder="Ví dụ: Phòng tiêm số 1"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Số lượng người
              </label>
              <input
                type="number"
                className="mt-1 block w-full border border-gray-300 rounded-md p-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Độ tuổi
              </label>
              <input
                type="text"
                className="mt-1 block w-full border border-gray-300 rounded-md p-2"
                placeholder="VD: Trẻ em"
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Bác sĩ phụ trách
            </label>
            <div className="mt-1 p-2 border border-gray-300 rounded-md min-h-[40px] bg-gray-50 flex flex-wrap gap-2">
              <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded-md text-sm flex items-center">
                BS. Nguyễn Văn Ánh{" "}
                <button className="ml-1 text-red-500">&times;</button>
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="mt-8 flex justify-end gap-3 border-t pt-6">
        <button className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">
          <Trash2 size={18} /> Hủy bỏ
        </button>
        <button
          onClick={onSave}
          className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          <Save size={18} /> Lưu lại
        </button>
      </div>
    </div>
  );
};

export default ScheduleForm;
