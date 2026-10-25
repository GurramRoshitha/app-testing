import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Palette,
  LogOut,
  Sun,
  Moon,
  Monitor
} from 'lucide-react';
import AuthService from '../data/services/AuthService';

const SettingsScreen = () => {
  const navigate = useNavigate();
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'system');

  const applyTheme = (newTheme) => {
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);

    const root = window.document.documentElement;
    if (newTheme === 'dark' || (newTheme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  };

  const handleLogout = async () => {
    await AuthService.logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24 px-6">
      <div className="max-w-xl mx-auto">
        <header className="py-8 flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
            <ArrowLeft size={24} />
          </button>
          <h1 className="text-3xl font-black text-black dark:text-white">Settings</h1>
        </header>

        <div className="space-y-10">
          {/* Appearance */}
          <section>
            <div className="flex items-center gap-3 text-primary mb-6">
              <Palette size={20} />
              <h3 className="font-bold text-lg">Appearance</h3>
            </div>

            <div className="space-y-3">
              {[
                { id: 'light', label: 'Light Mode', icon: Sun },
                { id: 'dark', label: 'Dark Mode', icon: Moon },
                { id: 'system', label: 'System Default', icon: Monitor }
              ].map((item) => (
                <button
                  key={item.id}
                  onClick={() => applyTheme(item.id)}
                  className={`w-full flex items-center justify-between p-4 rounded-2xl border transition-all ${
                    theme === item.id
                    ? 'border-primary bg-primary/5 text-primary'
                    : 'border-gray-100 dark:border-surface-variant text-gray-500'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <item.icon size={20} />
                    <span className="font-bold">{item.label}</span>
                  </div>
                  <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${theme === item.id ? 'border-primary' : 'border-gray-200'}`}>
                    {theme === item.id && <div className="w-2.5 h-2.5 bg-primary rounded-full"></div>}
                  </div>
                </button>
              ))}
            </div>
          </section>

          {/* Logout */}
          <section className="pt-6">
            <button
              onClick={handleLogout}
              className="w-full flex items-center justify-center gap-2 py-4 text-red-500 font-black uppercase tracking-widest bg-red-50 dark:bg-red-900/10 rounded-2xl active:scale-95 transition-all"
            >
              <LogOut size={20} />
              Logout
            </button>
          </section>
        </div>
      </div>
    </div>
  );
};

export default SettingsScreen;
