import React, { useState, useEffect } from "react";
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
  Sparkles,
} from "lucide-react";
import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import vaccinationApi from "../../../api/vaccinationApi";

function cn(...inputs) {
  return twMerge(clsx(inputs));
}

const CalendarSidebar = ({
  selectedDate,
  onDateChange,
  onAddNew,
  onDelete,
  refreshTrigger, // Nhận tín hiệu để load lại dấu chấm xanh
}) => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [activeDates, setActiveDates] = useState([]);

  // --- LOGIC TẢI DẤU CHẤM XANH (Ngày có lịch) ---
  useEffect(() => {
    let isMounted = true;

    const fetchActiveDates = async () => {
      try {
        const start = format(startOfMonth(currentMonth), "yyyy-MM-dd");
        const end = format(endOfMonth(currentMonth), "yyyy-MM-dd");
        const data = await vaccinationApi.getActiveDates(start, end);

        if (isMounted) {
          setActiveDates(data || []);
        }
      } catch (error) {
        console.error("Lỗi tải lịch tháng:", error);
      }
    };

    fetchActiveDates();
    return () => {
      isMounted = false;
    };
  }, [currentMonth, refreshTrigger]);

  const isSelectedDateHasSchedule = activeDates.includes(
    format(selectedDate, "yyyy-MM-dd"),
  );

  const renderDays = () => {
    const monthStart = startOfMonth(currentMonth);
    const monthEnd = endOfMonth(monthStart);
    const startDate = startOfWeek(monthStart, { weekStartsOn: 1 }); // Thứ 2 là đầu tuần
    const endDate = endOfWeek(monthEnd, { weekStartsOn: 1 });

    const calendarDays = eachDayOfInterval({ start: startDate, end: endDate });
    const daysHeader = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"];

    return (
      <div className="mt-6">
        <div className="grid grid-cols-7 mb-4">
          {daysHeader.map((day) => (
            <div
              key={day}
              className="text-center text-[10px] font-black text-slate-300 uppercase tracking-widest"
            >
              {day}
            </div>
          ))}
        </div>

        <div className="grid grid-cols-7 gap-1 sm:gap-2">
          {calendarDays.map((day, idx) => {
            const dateStr = format(day, "yyyy-MM-dd");
            const isSelected = isSameDay(day, selectedDate);
            const isCurrentMonth = isSameMonth(day, monthStart);
            const isToday = isSameDay(day, new Date());
            const hasSchedule = activeDates.includes(dateStr);

            return (
              <button
                key={idx}
                type="button"
                onClick={() => onDateChange(day)}
                className={cn(
                  "relative h-10 w-full flex flex-col items-center justify-center rounded-2xl transition-all duration-300 group",
                  !isCurrentMonth &&
                    "text-slate-200 opacity-20 pointer-events-none",
                  isCurrentMonth && "text-slate-600 hover:bg-blue-50",
                  isSelected &&
                    "bg-blue-600 text-white shadow-lg shadow-blue-200 scale-105 z-10",
                  isToday &&
                    !isSelected &&
                    "ring-2 ring-blue-100 text-blue-600 font-black",
                )}
              >
                <span className="text-sm font-bold">{format(day, "d")}</span>
                {hasSchedule && (
                  <span
                    className={cn(
                      "absolute bottom-2 h-1.5 w-1.5 rounded-full",
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
    <div className="bg-white p-6 rounded-[2rem] shadow-sm border border-slate-100 w-full animate-in fade-in zoom-in duration-500">
      {/* Header điều khiển */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-blue-50 text-blue-600 rounded-2xl">
            <CalendarIcon size={20} />
          </div>
          <div>
            <h2 className="font-black text-slate-800 capitalize text-base tracking-tight">
              {format(currentMonth, "MMMM, yyyy", { locale: vi })}
            </h2>
            <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
              Hệ thống điều phối
            </p>
          </div>
        </div>

        <div className="flex gap-1 bg-slate-50 p-1 rounded-2xl">
          <button
            onClick={() => setCurrentMonth(subMonths(currentMonth, 1))}
            className="p-2 hover:bg-white hover:shadow-sm rounded-xl transition-all text-slate-500"
          >
            <ChevronLeft size={18} />
          </button>
          <button
            onClick={() => setCurrentMonth(addMonths(currentMonth, 1))}
            className="p-2 hover:bg-white hover:shadow-sm rounded-xl transition-all text-slate-500"
          >
            <ChevronRight size={18} />
          </button>
        </div>
      </div>

      {renderDays()}

      {/* Hành động nhanh */}
      <div className="mt-8 pt-6 border-t border-slate-50 space-y-3">
        <button
          onClick={onAddNew}
          className="w-full flex items-center justify-center gap-3 bg-slate-900 hover:bg-blue-600 text-white font-black py-4 rounded-2xl transition-all shadow-xl shadow-slate-100 active:scale-95"
        >
          <Plus size={18} strokeWidth={3} />
          <span className="text-xs uppercase tracking-widest">
            Thêm lịch mới
          </span>
        </button>

        <button
          onClick={onDelete}
          disabled={!isSelectedDateHasSchedule}
          className={cn(
            "w-full flex items-center justify-center gap-3 font-black py-4 rounded-2xl transition-all border-2 text-xs uppercase tracking-widest",
            isSelectedDateHasSchedule
              ? "bg-white border-red-50 text-red-500 hover:bg-red-50 hover:border-red-100 active:scale-95"
              : "bg-slate-50 border-transparent text-slate-200 cursor-not-allowed",
          )}
        >
          <Trash2 size={18} />
          Xóa lịch chọn
        </button>
      </div>

      {/* Tip hữu ích */}
      <div className="mt-6 p-4 bg-blue-50/50 rounded-2xl border border-blue-100 flex items-center gap-3">
        <div className="w-8 h-8 bg-blue-100 rounded-xl flex items-center justify-center text-blue-600 shrink-0">
          <Sparkles size={16} />
        </div>
        <p className="text-[10px] text-blue-600 font-medium leading-relaxed">
          Nhấn <b>Thêm lịch mới</b> để thiết lập ca trực cho ngày chưa có lịch
          tiêm chủng.
        </p>
      </div>
    </div>
  );
};

export default CalendarSidebar;
