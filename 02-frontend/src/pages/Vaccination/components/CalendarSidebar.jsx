import React, { useState } from "react";
import {
  format,
  addMonths,
  subMonths,
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
  isSameMonth,
  isSameDay,
  eachDayOfInterval,
} from "date-fns";
import { vi } from "date-fns/locale";
import {
  ChevronLeft,
  ChevronRight,
  Plus,
  Trash2,
  Calendar as CalendarIcon,
} from "lucide-react";
import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

// Hàm tiện ích để gộp class Tailwind
function cn(...inputs) {
  return twMerge(clsx(inputs));
}

const CalendarSidebar = ({ selectedDate, onDateChange }) => {
  const [currentMonth, setCurrentMonth] = useState(new Date());

  // Logic tạo lưới ngày trong tháng
  const renderDays = () => {
    const monthStart = startOfMonth(currentMonth);
    const monthEnd = endOfMonth(monthStart);
    const startDate = startOfWeek(monthStart);
    const endDate = endOfWeek(monthEnd);

    const calendarDays = eachDayOfInterval({ start: startDate, end: endDate });
    const daysHeader = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"];

    return (
      <div className="mt-4">
        {/* Header thứ trong tuần */}
        <div className="grid grid-cols-7 mb-2">
          {daysHeader.map((day) => (
            <div
              key={day}
              className="text-center text-xs font-bold text-gray-400 uppercase"
            >
              {day}
            </div>
          ))}
        </div>

        {/* Lưới các ngày */}
        <div className="grid grid-cols-7 gap-1">
          {calendarDays.map((day, idx) => {
            const isSelected = isSameDay(day, selectedDate);
            const isCurrentMonth = isSameMonth(day, monthStart);
            const isToday = isSameDay(day, new Date());

            // Giả lập ngày có lịch tiêm (sau này sẽ lấy từ API)
            const hasSchedule =
              [5, 12, 18, 25].includes(day.getDate()) && isCurrentMonth;

            return (
              <button
                key={idx}
                onClick={() => onDateChange(day)}
                className={cn(
                  "relative h-10 w-full flex flex-col items-center justify-center rounded-xl transition-all duration-200",
                  !isCurrentMonth && "text-gray-300",
                  isCurrentMonth && "text-gray-700 hover:bg-blue-50",
                  isSelected &&
                    "bg-blue-600 text-white hover:bg-blue-700 shadow-md shadow-blue-200",
                  isToday &&
                    !isSelected &&
                    "border border-blue-600 text-blue-600 font-bold",
                )}
              >
                <span className="text-sm">{format(day, "d")}</span>
                {hasSchedule && (
                  <span
                    className={cn(
                      "absolute bottom-1.5 h-1 w-1 rounded-full",
                      isSelected ? "bg-white" : "bg-blue-500",
                    )}
                  />
                )}
              </button>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 w-full max-w-sm">
      {/* Header điều hướng */}
      <div className="flex items-center justify-between px-2">
        <div className="flex items-center gap-2">
          <CalendarIcon className="text-blue-600" size={20} />
          <h2 className="font-bold text-gray-800 capitalize">
            {format(currentMonth, "MMMM, yyyy", { locale: vi })}
          </h2>
        </div>
        <div className="flex gap-1">
          <button
            onClick={() => setCurrentMonth(subMonths(currentMonth, 1))}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ChevronLeft size={18} className="text-gray-600" />
          </button>
          <button
            onClick={() => setCurrentMonth(addMonths(currentMonth, 1))}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ChevronRight size={18} className="text-gray-600" />
          </button>
        </div>
      </div>

      {/* Render Lưới Lịch */}
      {renderDays()}

      <div className="mt-8 pt-6 border-t border-gray-100 space-y-3">
        <button className="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-xl transition-all shadow-lg shadow-blue-100">
          <Plus size={18} /> Thêm lịch mới
        </button>
        <button className="w-full flex items-center justify-center gap-2 bg-white border border-red-100 text-red-500 hover:bg-red-50 font-semibold py-3 rounded-xl transition-all">
          <Trash2 size={18} /> Xóa lịch đã chọn
        </button>
      </div>
    </div>
  );
};

export default CalendarSidebar;
