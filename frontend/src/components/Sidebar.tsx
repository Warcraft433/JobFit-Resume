import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface SidebarProps {
  isOpen?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen = false, onClose }) => {
  const { user } = useAuth();
  const initials = user?.fullName
    ? user.fullName
        .split(' ')
        .map((n) => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    : 'JD';

  return (
    <aside className={`sidebar ${isOpen ? 'mobile-open' : ''}`}>
      {/* Brand Header */}
      <div className="brand" style={{ justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '11px' }}>
          <div className="brandmark">T</div>
          <div>
            <strong>TailorCV</strong>
            <small>AI Resume Studio</small>
          </div>
        </div>
        {onClose && (
          <button
            type="button"
            onClick={onClose}
            aria-label="Close navigation menu"
            style={{
              background: 'transparent',
              border: 0,
              color: '#b8b7c2',
              fontSize: '18px',
              cursor: 'pointer',
              padding: '4px',
            }}
          >
            ✕
          </button>
        )}
      </div>

      {/* Navigation section */}
      <div className="navlabel">Workspace</div>
      <nav className="nav" id="nav">
        <NavLink
          to="/dashboard"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">▦</span>
          <span className="navtext">Dashboard</span>
        </NavLink>

        <NavLink
          to="/profile"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">♙</span>
          <span className="navtext">My Profile</span>
        </NavLink>

        <NavLink
          to="/jobs"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">⌕</span>
          <span className="navtext">Job Analyzer</span>
        </NavLink>

        <NavLink
          to="/resumes"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">▤</span>
          <span className="navtext">Resume Builder</span>
        </NavLink>

        <NavLink
          to="/applications"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">▧</span>
          <span className="navtext">Job Tracker</span>
        </NavLink>

        <NavLink
          to="/templates"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">▦</span>
          <span className="navtext">Templates</span>
        </NavLink>

        <NavLink
          to="/settings"
          onClick={onClose}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          <span className="ico">⚙</span>
          <span className="navtext">Settings</span>
        </NavLink>
      </nav>

      {/* Sidebar bottom */}
      <div className="sidebar-bottom">
        <div className="helpbox">
          <b>✦ Make every application count</b>
          <p>Build a resume around your real strengths and the role you want.</p>
          <NavLink to="/jobs" onClick={onClose}>
            Start a new resume →
          </NavLink>
        </div>

        <div className="user">
          <div className="avatar">{initials}</div>
          <div className="usermeta">
            <b>{user?.fullName || 'Jordan Davis'}</b>
            <small>Free workspace</small>
          </div>
        </div>
      </div>
    </aside>
  );
};
