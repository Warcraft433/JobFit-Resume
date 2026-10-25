import React from 'react';
import { useTheme } from '../context/ThemeContext';

export const SettingsPage: React.FC = () => {
  const { theme, setTheme } = useTheme();

  return (
    <div>
      <div className="pagehead">
        <div>
          <div className="eyebrow">PREFERENCES</div>
          <h1>Appearance & settings</h1>
          <p>Personalize your workspace. The resume document remains print-friendly in every theme.</p>
        </div>
      </div>

      <div className="content-layout">
        <section className="card">
          <h2>Appearance</h2>
          <p className="sub">Switch the application theme instantly.</p>
          <div className="steps">
            <div
              className="step"
              onClick={() => setTheme('light')}
              role="button"
              tabIndex={0}
            >
              <div className="stepicon">☀</div>
              <div className="steptext">
                <b>Light Professional</b>
                <p>Charcoal · Moonlit Silver · Signal Red</p>
              </div>
              <span className={`pill ${theme === 'light' ? 'red' : ''}`}>
                {theme === 'light' ? 'Active' : 'Select'}
              </span>
            </div>

            <div
              className="step"
              onClick={() => setTheme('dark')}
              role="button"
              tabIndex={0}
            >
              <div className="stepicon">☾</div>
              <div className="steptext">
                <b>Dark Neon</b>
                <p>Jet Black · Neon Red · Neon Blue</p>
              </div>
              <span className={`pill ${theme === 'dark' ? 'blue' : ''}`}>
                {theme === 'dark' ? 'Active' : 'Select'}
              </span>
            </div>
          </div>
        </section>

        <aside className="card">
          <h2>Privacy & integrity</h2>
          <p className="sub">A trustworthy resume workflow should preserve user control.</p>
          <div className="resultbox">
            <b style={{ fontSize: '12px' }}>✓ Verified information only</b>
            <p className="hint">
              The AI can rewrite and reorder verified information, but it must not fabricate skills or credentials.
            </p>
          </div>
          <div className="resultbox">
            <b style={{ fontSize: '12px' }}>✓ Document export</b>
            <p className="hint">
              Exported resumes should use a clean white background, selectable text and predictable page breaks.
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
};
