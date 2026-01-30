import React from "react";
import { AlertCircle } from "lucide-react";

const ErrorMessage = ({ message }) =>
  message ? (
    <p className="flex items-center gap-1 text-[10px] text-red-500 font-bold mt-1 ml-1 animate-in fade-in slide-in-from-top-1">
      <AlertCircle size={10} /> {message}
    </p>
  ) : null;

export default ErrorMessage;
