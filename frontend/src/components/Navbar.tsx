import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ThemeToggle } from './ThemeToggle';
import { LogOut, User as UserIcon, Menu } from 'lucide-react';

interface NavbarProps {
  onToggleMobileMenu?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleMobileMenu }) => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-30 w-full border-b border-slate-200 dark:border-surface-800 bg-white/90 dark:bg-[#111119]/90 backdrop-blur-md transition-colors duration-150">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-3 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2.5">
          {/* Hamburger button for mobile */}
          {isAuthenticated && onToggleMobileMenu && (
            <button
              onClick={onToggleMobileMenu}
              className="md:hidden inline-flex items-center justify-center p-2 rounded-xl border border-slate-200 dark:border-surface-700 bg-slate-100 dark:bg-surface-800 text-slate-700 dark:text-slate-200 hover:text-brand-600 focus:outline-none"
              aria-label="Toggle mobile menu"
            >
              <Menu className="h-5 w-5" />
            </button>
          )}

          <Link to={isAuthenticated ? '/dashboard' : '/'} className="flex items-center gap-2.5 group">
            <div className="w-[36px] h-[36px] rounded-[11px] bg-[var(--accent)] flex items-center justify-center font-black text-lg text-white shadow-[0_0_18px_rgba(225,6,0,0.3)] group-hover:scale-105 transition-transform flex-shrink-0">
              T
            </div>
            <div>
              <span className="text-base sm:text-lg font-bold tracking-tight text-slate-900 dark:text-white transition-colors">
                Tailor<span className="text-[var(--accent)]">CV</span>
              </span>
              <span className="hidden sm:inline-block ml-2 text-[10px] uppercase tracking-wider px-2 py-0.5 rounded-full bg-[var(--accentSoft)] text-[var(--accent)] border border-[var(--accent)]/20 font-semibold">
                AI Studio
              </span>
            </div>
          </Link>
        </div>

        <div className="flex items-center gap-2 sm:gap-3">
          {/* Light / Dark Mode Toggle */}
          <ThemeToggle />

          {isAuthenticated ? (
            <div className="flex items-center gap-2 sm:gap-3">
              <div className="hidden sm:flex flex-col text-right">
                <span className="text-xs sm:text-sm font-medium text-slate-800 dark:text-slate-200 truncate max-w-[140px] sm:max-w-[200px]">
                  {user?.fullName}
                </span>
                <span className="text-[11px] text-slate-500 dark:text-slate-400 truncate max-w-[140px] sm:max-w-[200px]">
                  {user?.email}
                </span>
              </div>
              <div
                title={user?.fullName || 'Profile'}
                className="flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-full bg-slate-100 dark:bg-surface-800 text-brand-600 dark:text-brand-400 border border-slate-200 dark:border-surface-700 font-semibold text-xs"
              >
                {user?.fullName ? user.fullName.charAt(0).toUpperCase() : <UserIcon className="h-4 w-4" />}
              </div>
              <button
                onClick={handleLogout}
                title="Log out"
                className="flex items-center gap-1.5 rounded-lg px-2.5 sm:px-3 py-1.5 text-xs font-medium text-slate-600 dark:text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 border border-transparent hover:border-rose-200 dark:hover:border-rose-900/40 transition-all"
              >
                <LogOut className="h-4 w-4" />
                <span className="hidden sm:inline">Sign Out</span>
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2 sm:gap-3">
              <Link
                to="/login"
                className="px-2.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-colors"
              >
                Sign In
              </Link>
              <Link
                to="/register"
                className="rounded-lg bg-brand-600 px-3 sm:px-4 py-2 text-xs sm:text-sm font-medium text-white shadow-md shadow-brand-600/30 hover:bg-brand-500 transition-all"
              >
                Get Started
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
