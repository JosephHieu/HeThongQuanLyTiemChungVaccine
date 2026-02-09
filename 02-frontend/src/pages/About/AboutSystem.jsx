import React from "react";
import {
  ShieldCheck,
  Code2,
  Cpu,
  Globe2,
  Users2,
  Server,
  Zap,
  HeartHandshake,
} from "lucide-react";

const AboutSystem = () => {
  const teamMembers = [
    { name: "Joseph Hieu", role: "Fullstack Developer", avatar: "JH" },
    { name: "Trung Tâm Tiêm Chủng", role: "Chủ đầu tư", avatar: "VC" },
  ];

  const techStack = [
    {
      name: "React 18",
      desc: "Giao diện người dùng",
      icon: <Zap className="text-blue-500" />,
    },
    {
      name: "Spring Boot 3",
      desc: "Hệ thống lõi & API",
      icon: <Cpu className="text-emerald-500" />,
    },
    {
      name: "MySQL 8",
      desc: "Lưu trữ dữ liệu",
      icon: <Server className="text-indigo-500" />,
    },
    {
      name: "Tailwind CSS",
      desc: "Thiết kế giao diện",
      icon: <Zap className="text-cyan-500" />,
    },
  ];

  return (
    <div className="p-6 md:p-8 bg-slate-50 min-h-screen space-y-8 animate-in fade-in duration-500">
      {/* --- HERO SECTION --- */}
      <section className="bg-white rounded-[2.5rem] p-8 md:p-12 shadow-sm border border-slate-100 flex flex-col md:flex-row items-center gap-10">
        <div className="flex-1 space-y-6 text-center md:text-left">
          <div className="inline-flex items-center gap-2 px-4 py-2 bg-blue-50 rounded-full text-blue-600 font-black text-xs uppercase tracking-widest">
            <ShieldCheck size={16} /> Vaccine Care System v1.2.0
          </div>
          <h1 className="text-4xl md:text-5xl font-black text-slate-800 leading-[1.1]">
            Nền tảng Quản trị <br />
            <span className="text-blue-600 text-3xl md:text-4xl">
              Tiêm Chủng Thông Minh
            </span>
          </h1>
          <p className="text-slate-500 text-lg leading-relaxed max-w-xl">
            Hệ thống được phát triển nhằm tối ưu hóa quy trình quản lý kho
            vắc-xin, điều phối lịch tiêm và tự động hóa báo cáo tài chính cho
            các cơ sở y tế hiện đại.
          </p>
        </div>
        <div className="w-full md:w-[400px] h-[300px] bg-blue-600 rounded-[3rem] shadow-2xl shadow-blue-200 flex items-center justify-center relative overflow-hidden group">
          <Globe2 className="text-white/20 absolute -right-10 -bottom-10 w-64 h-64 group-hover:rotate-12 transition-transform duration-1000" />
          <div className="text-white text-center z-10">
            <p className="text-6xl font-black mb-2">2026</p>
            <p className="uppercase font-bold tracking-[0.3em] opacity-80">
              Technology Edition
            </p>
          </div>
        </div>
      </section>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* --- CÔNG NGHỆ SỬ DỤNG --- */}
        <div className="lg:col-span-8 space-y-6">
          <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight flex items-center gap-2 ml-4">
            <Code2 className="text-blue-600" /> Công nghệ cốt lõi
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {techStack.map((tech, idx) => (
              <div
                key={idx}
                className="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md transition-all group"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform">
                    {tech.icon}
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-800">{tech.name}</h4>
                    <p className="text-xs text-slate-400 font-medium uppercase tracking-wider">
                      {tech.desc}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* --- ĐỘI NGŨ PHÁT TRIỂN --- */}
        <div className="lg:col-span-4 space-y-6">
          <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight flex items-center gap-2 ml-4">
            <Users2 className="text-blue-600" /> Đội ngũ
          </h3>
          <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
            {teamMembers.map((member, idx) => (
              <div
                key={idx}
                className="p-5 flex items-center gap-4 border-b border-slate-50 last:border-0 hover:bg-slate-50/50 transition-colors"
              >
                <div className="w-12 h-12 bg-blue-600 text-white rounded-full flex items-center justify-center font-black text-sm shadow-inner">
                  {member.avatar}
                </div>
                <div>
                  <h4 className="font-black text-slate-800">{member.name}</h4>
                  <p className="text-xs font-bold text-slate-400 uppercase">
                    {member.role}
                  </p>
                </div>
              </div>
            ))}
            <div className="p-6 bg-slate-50/50 text-center">
              <button className="text-blue-600 font-black text-[10px] uppercase tracking-widest flex items-center justify-center gap-2 w-full">
                <HeartHandshake size={14} /> Hỗ trợ kỹ thuật 24/7
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* --- FOOTER INFO --- */}
      <footer className="text-center py-10 space-y-2 border-t border-slate-200/50">
        <p className="text-slate-400 text-xs font-bold uppercase tracking-widest">
          Bản quyền thuộc về © 2026 Joseph Hieu Software. Bảo lưu mọi quyền.
        </p>
        <p className="text-slate-300 text-[10px] italic">
          Hệ thống tuân thủ các tiêu chuẩn bảo mật dữ liệu y tế quốc gia.
        </p>
      </footer>
    </div>
  );
};

export default AboutSystem;
