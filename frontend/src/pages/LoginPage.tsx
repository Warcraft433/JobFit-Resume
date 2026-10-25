import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ThemeToggle } from '../components/ThemeToggle';
import { Lock, Mail, ArrowRight, AlertCircle, Sparkles } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login, register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err?.response?.data?.error?.details || err?.message || 'Invalid email or password.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDemoLogin = async () => {
    setError(null);
    setIsSubmitting(true);
    const demoEmail = 'alex.mercer@example.com';
    const demoPass = 'Password123!';

    try {
      await login(demoEmail, demoPass);
      navigate('/dashboard');
    } catch {
      // If demo user does not exist in local H2 database yet, auto-register and login!
      try {
        await register('Alex Mercer', demoEmail, demoPass);
        navigate('/dashboard');
      } catch (err: any) {
        setError(err?.message || 'Demo login failed.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#080c14] text-slate-800 dark:text-slate-100 flex flex-col justify-center py-10 px-4 sm:px-6 lg:px-8 relative transition-colors duration-150">
      <div className="absolute top-4 right-4 sm:top-6 sm:right-6">
        <ThemeToggle />
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <Link to="/" className="inline-flex items-center gap-2.5 mb-4 group">
          <div className="brandmark">T</div>
          <span className="text-2xl font-extrabold tracking-tight text-[var(--text)]">
            Tailor<span style={{ color: 'var(--accent)' }}>CV</span>
          </span>
        </Link>
        <h2 className="text-2xl font-bold tracking-tight text-[var(--text)]">Welcome back</h2>
        <p className="mt-1 text-xs text-[var(--muted)]">
          Sign in to access your career profile and tailored resumes
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="border border-slate-200 dark:border-surface-800 bg-white dark:bg-surface-900/60 backdrop-blur-xl py-8 px-5 sm:px-10 shadow-xl dark:shadow-2xl rounded-2xl transition-colors">
          {error && (
            <div className="mb-5 flex items-center gap-2.5 rounded-xl border border-rose-200 dark:border-rose-900/60 bg-rose-50 dark:bg-rose-950/40 p-3.5 text-xs text-rose-700 dark:text-rose-300">
              <AlertCircle className="h-4 w-4 flex-shrink-0 text-rose-600 dark:text-rose-400" />
              <span>{error}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                Email address
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Mail className="h-4 w-4" />
                </div>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  className="block w-full rounded-xl border border-slate-300 dark:border-surface-700 bg-slate-50 dark:bg-surface-950/80 py-2.5 pl-10 pr-3 text-sm text-slate-900 dark:text-white placeholder-slate-400 dark:placeholder-slate-500 focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1.5">
                Password
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Lock className="h-4 w-4" />
                </div>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="block w-full rounded-xl border border-slate-300 dark:border-surface-700 bg-slate-50 dark:bg-surface-950/80 py-2.5 pl-10 pr-3 text-sm text-slate-900 dark:text-white placeholder-slate-400 dark:placeholder-slate-500 focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-brand-600 to-indigo-600 py-2.5 px-4 text-sm font-semibold text-white shadow-lg shadow-brand-600/30 hover:from-brand-500 hover:to-indigo-500 transition-all disabled:opacity-50"
            >
              <span>{isSubmitting ? 'Signing in...' : 'Sign In'}</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </form>

          <div className="mt-6 pt-6 border-t border-slate-200 dark:border-surface-800">
            <button
              type="button"
              onClick={handleDemoLogin}
              disabled={isSubmitting}
              className="w-full flex items-center justify-center gap-2 rounded-xl border border-brand-200 dark:border-brand-700/50 bg-brand-50 dark:bg-brand-950/40 py-2.5 px-4 text-xs font-semibold text-brand-700 dark:text-brand-300 hover:bg-brand-100 dark:hover:bg-brand-900/40 transition-all"
            >
              <Sparkles className="h-3.5 w-3.5 text-brand-600 dark:text-brand-400" />
              <span>Instant Demo Account (One Click)</span>
            </button>
          </div>

          <p className="mt-6 text-center text-xs text-slate-500 dark:text-slate-400">
            Don't have an account?{' '}
            <Link to="/register" className="font-semibold text-brand-600 dark:text-brand-400 hover:text-brand-500 dark:hover:text-brand-300">
              Sign up free
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
