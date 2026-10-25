import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff, AlertCircle, User, Mail, Lock, ArrowLeft, ArrowRight } from 'lucide-react';
import AuthService from '../data/services/AuthService';

const RegisterScreen = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreeTerms, setAgreeTerms] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleRegister = async (e) => {
    e.preventDefault();
    if (!name || !email || !password) return;

    if (password !== confirmPassword) {
      setError("Passwords do not match. Please try again.");
      return;
    }

    if (!agreeTerms) {
      setError("You must agree to the Terms of Service and Privacy Policy.");
      return;
    }

    setLoading(true);
    setError(null);

    const result = await AuthService.signUp(email, password, name);
    setLoading(false);

    if (result.isSuccess) {
      navigate('/dashboard');
    } else {
      setError(result.error?.message || "Registration failed");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-navy flex flex-col px-6 py-8">
      {/* Top Bar */}
      <div className="flex items-center gap-3 mb-6 max-w-md mx-auto w-full">
        <button onClick={() => navigate('/login')} className="p-2 hover:bg-slate-200 dark:hover:bg-slate-800 rounded-full transition-colors">
          <ArrowLeft size={22} className="text-slate-700 dark:text-slate-200" />
        </button>
        <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Create account</h2>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md space-y-6">
        <div>
          <h1 className="text-3xl font-black text-slate-900 dark:text-white mb-1">Your next chapter starts here.</h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm font-medium">Join Roamly. Leave the planning to us.</p>
        </div>

        {error && (
          <div className="p-4 bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800/60 rounded-2xl flex items-center gap-3 text-red-600 dark:text-red-400">
            <AlertCircle size={20} className="shrink-0" />
            <p className="text-xs font-semibold">{error}</p>
          </div>
        )}

        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5 ml-1">Full name</label>
            <div className="relative flex items-center">
              <User className="absolute left-4 text-primary pointer-events-none z-10" size={18} />
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="input-field !pl-12"
                placeholder="Enter your full name"
                required
              />
            </div>
          </div>

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
                placeholder="Create a password"
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

          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5 ml-1">Confirm password</label>
            <div className="relative flex items-center">
              <Lock className="absolute left-4 text-primary pointer-events-none z-10" size={18} />
              <input
                type={showPassword ? "text" : "password"}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="input-field !pl-12 !pr-12"
                placeholder="Confirm your password"
                required
              />
            </div>
          </div>

          <div className="flex items-center gap-2.5 pt-1">
            <input
              type="checkbox"
              id="terms"
              checked={agreeTerms}
              onChange={(e) => setAgreeTerms(e.target.checked)}
              className="w-4 h-4 text-primary accent-primary rounded cursor-pointer"
            />
            <label htmlFor="terms" className="text-xs text-slate-500 dark:text-slate-400 cursor-pointer">
              I agree to the Terms of Service and Privacy Policy.
            </label>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full mt-6 py-4 text-base"
          >
            <span>{loading ? "Creating Account..." : "Create Account"}</span>
            <ArrowRight size={18} />
          </button>
        </form>

        <p className="text-center text-xs text-slate-500 font-medium pt-2">
          Already have an account?{' '}
          <button
            onClick={() => navigate('/login')}
            className="font-extrabold text-primary hover:underline ml-1"
          >
            Login
          </button>
        </p>
      </div>
    </div>
  );
};

export default RegisterScreen;
