import React, { useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { AiResumeAssistant } from './AiResumeAssistant';
import { LogOut } from 'lucide-react';

export const Layout: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const location = useLocation();
  const navigate = useNavigate();

  const getCrumb = () => {
    const path = location.pathname;
    if (path.includes('profile')) return 'MY PROFILE';
    if (path.includes('jobs')) return 'JOB ANALYZER';
    if (path.includes('resumes')) return 'RESUME BUILDER';
    if (path.includes('applications')) return 'JOB TRACKER';
    if (path.includes('templates')) return 'TEMPLATES';
    if (path.includes('settings')) return 'SETTINGS';
    return 'DASHBOARD';
  };

  const initials = user?.fullName
    ? user.fullName
        .split(' ')
        .map((n) => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    : 'JD';

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app">
      {/* Sidebar (Desktop fixed & Mobile drawer) */}
      <Sidebar isOpen={mobileMenuOpen} onClose={() => setMobileMenuOpen(false)} />

      {/* Main Container */}
      <main className="main">
        {/* Mobile Backdrop */}
        <div
          className={`drawer-backdrop ${mobileMenuOpen ? 'open' : ''}`}
          onClick={() => setMobileMenuOpen(false)}
        />

        {/* Topbar */}
        <header className="topbar">
          <button
            className="mobile-menu-btn"
            onClick={() => setMobileMenuOpen((prev) => !prev)}
            aria-label="Toggle mobile menu"
          >
            ☰
          </button>

          <div className="crumb">
            WORKSPACE <span style={{ padding: '0 7px', color: 'var(--border)' }}>/</span>{' '}
            <b id="crumb">{getCrumb()}</b>
          </div>

          <div className="top-right">
            <button
              className="iconbtn"
              aria-label="Notifications"
              title="Notifications"
              onClick={() => alert("You're all caught up.")}
            >
              ♧
            </button>

            <button className="themebtn" onClick={toggleTheme}>
              <span>{theme === 'light' ? '☾' : '☀'}</span>
              <span>{theme === 'light' ? 'Dark mode' : 'Light mode'}</span>
            </button>

            <div className="top-avatar" title={user?.fullName || 'User Profile'}>
              {initials}
            </div>

            <button
              onClick={handleLogout}
              className="iconbtn text-rose-500 hover:text-rose-600"
              title="Log out"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        </header>

        {/* Content Area */}
        <div className="content">
          <Outlet />
        </div>
      </main>

      {/* Global AI Resume Assistant */}
      <AiResumeAssistant />
    </div>
  );
};
