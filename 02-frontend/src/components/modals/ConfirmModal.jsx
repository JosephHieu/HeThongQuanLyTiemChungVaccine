import React from "react";
import { AlertTriangle, X, Lock, Unlock } from "lucide-react";

const ConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  type = "warning",
  loading,
}) => {
  if (!isOpen) return null;

  // Cấu hình màu sắc dựa trên loại thông báo
  const colors = {
    warning: "bg-amber-50 text-amber-600 border-amber-100",
    danger: "bg-rose-50 text-rose-600 border-rose-100",
    success: "bg-green-50 text-green-600 border-green-100",
  };

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <div className={`p-3 rounded-xl border ${colors[type]}`}>
              {type === "warning" ? <Lock size={24} /> : <Unlock size={24} />}
            </div>
            <button
              onClick={onClose}
              className="p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors cursor-pointer"
            >
              <X size={20} />
            </button>
          </div>

          <h3 className="text-xl font-bold text-slate-800 mb-2">{title}</h3>
          <p className="text-slate-600 leading-relaxed">{message}</p>
        </div>

        <div className="flex items-center justify-end gap-3 p-6 bg-slate-50 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-6 py-2.5 font-medium text-slate-600 hover:bg-slate-200 rounded-xl transition-colors cursor-pointer"
          >
            Hủy
          </button>
          <button
            type="button"
            disabled={loading}
            onClick={onConfirm}
            className={`px-8 py-2.5 font-bold text-white rounded-xl shadow-lg transition-all active:scale-95 cursor-pointer disabled:opacity-50
              ${type === "warning" ? "bg-amber-500 hover:bg-amber-600 shadow-amber-100" : "bg-green-500 hover:bg-green-600 shadow-green-100"}
            `}
          >
            {loading ? "Đang xử lý..." : "Xác nhận"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
