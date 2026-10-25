import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff, AlertCircle, Mail, Lock, ArrowLeft, ArrowRight, Mountain } from 'lucide-react';
import AuthService from '../data/services/AuthService';

const LoginScreen = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!email || !password) return;

    setLoading(true);
    setError(null);

    const result = await AuthService.signIn(email, password);
    setLoading(false);

    if (result.isSuccess) {
      navigate('/dashboard');
    } else {
      setError("Email or password is incorrect. Check your details and try again.");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-navy flex flex-col px-6 py-8">
      {/* Top Bar */}
      <div className="flex items-center gap-3 mb-6 max-w-md mx-auto w-full">
        <button onClick={() => navigate('/')} className="p-2 hover:bg-slate-200 dark:hover:bg-slate-800 rounded-full transition-colors">
          <ArrowLeft size={22} className="text-slate-700 dark:text-slate-200" />
        </button>
        <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Welcome back</h2>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md space-y-6">
        {/* Roamly Logo Branding */}
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-2xl bg-primary text-white flex items-center justify-center shadow-lg shadow-primary/20">
            <Mountain size={28} />
          </div>
          <span className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">roamly</span>
        </div>

        <div>
          <h1 className="text-3xl font-black text-slate-900 dark:text-white mb-1">Good to see you, explorer.</h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm font-medium">Your next great escape is waiting.</p>
        </div>

        {/* Hero Banner Image */}
        <div className="rounded-[28px] overflow-hidden h-40 shadow-md">
          <img
            src="https://images.unsplash.com/photo-1593693397690-362cb9666fc2?auto=format&fit=crop&w=800&q=80"
            alt="Munnar"
            className="w-full h-full object-cover"
          />
        </div>

        {error && (
          <div className="p-4 bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800/60 rounded-2xl flex items-center gap-3 text-red-600 dark:text-red-400">
            <AlertCircle size={20} className="shrink-0" />
            <p className="text-xs font-semibold">{error}</p>
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5 ml-1">Email address</label>
            <div className="relative flex items-center">
              <Mail className="absolute left-4 text-primary pointer-events-none z-10" size={18} />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="input-field !pl-12"
                placeholder="Enter your email address"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5 ml-1">Password</label>
            <div className="relative flex items-center">
              <Lock className="absolute left-4 text-primary pointer-events-none z-10" size={18} />
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input-field !pl-12 !pr-12"
                placeholder="Enter your password"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 text-slate-400 hover:text-primary p-1 z-10"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full mt-6 py-4 text-base"
          >
            <span>{loading ? "Logging in..." : "Login"}</span>
            <ArrowRight size={18} />
          </button>
        </form>

        <p className="text-center text-xs text-slate-500 font-medium pt-2">
          New to Roamly?{' '}
          <button
            onClick={() => navigate('/register')}
            className="font-extrabold text-primary hover:underline ml-1"
          >
            Create Account
          </button>
        </p>

        <p className="text-center text-[10px] text-slate-400 dark:text-slate-500 pt-6">
          A world of possibilities. One thoughtful plan.
        </p>
      </div>
    </div>
  );
};

export default LoginScreen;
