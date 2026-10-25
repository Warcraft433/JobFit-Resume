import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';

interface ThemeToggleProps {
  className?: string;
}

export const ThemeToggle: React.FC<ThemeToggleProps> = ({ className = '' }) => {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
      title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
      className={`relative inline-flex h-9 w-9 items-center justify-center rounded-xl border transition-all focus:outline-none focus:ring-2 focus:ring-brand-500/50 ${
        theme === 'dark'
          ? 'border-surface-700 bg-surface-800 text-amber-400 hover:bg-surface-700 hover:text-amber-300'
          : 'border-slate-300 bg-white text-indigo-600 shadow-sm hover:bg-slate-100 hover:text-indigo-700'
      } ${className}`}
    >
      {theme === 'dark' ? (
        <Sun className="h-4 w-4 transition-transform duration-300 rotate-0 hover:rotate-45" />
      ) : (
        <Moon className="h-4 w-4 transition-transform duration-300 -rotate-12 hover:rotate-0" />
      )}
    </button>
  );
};
