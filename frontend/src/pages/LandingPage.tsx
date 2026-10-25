import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import { Sparkles, ArrowRight, ShieldCheck } from 'lucide-react';

export const LandingPage: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleInstantDemo = async () => {
    try {
      await login('alex.client@example.com', 'Password123!');
      navigate('/dashboard');
    } catch {
      navigate('/login');
    }
  };

  return (
    <div className="min-h-screen bg-[var(--bg)] text-[var(--text)] flex flex-col transition-colors duration-200">
      {/* Top Header */}
      <header className="border-b border-[var(--border)] bg-[var(--surface)] sticky top-0 z-40 transition-colors">
        <div className="max-w-[1440px] mx-auto px-4 sm:px-8 h-[74px] flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="brandmark">T</div>
            <div>
              <strong className="text-base tracking-tight text-[var(--text)] block font-extrabold">
                TailorCV
              </strong>
              <small className="text-[var(--muted)] text-[11px] block -mt-0.5">
                AI Resume Studio
              </small>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button className="themebtn" onClick={toggleTheme}>
              <span>{theme === 'light' ? '☾' : '☀'}</span>
              <span className="hidden sm:inline">
                {theme === 'light' ? 'Dark mode' : 'Light mode'}
              </span>
            </button>
            <Link
              to="/login"
              className="btn secondary"
            >
              Sign In
            </Link>
            <Link
              to="/register"
              className="btn primary"
            >
              Get Started
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="flex-1 max-w-[1440px] mx-auto px-4 sm:px-8 py-10 sm:py-16 w-full space-y-12">
        <div className="grid lg:grid-cols-12 gap-8 items-center">
          {/* Left Column: Hero Text & CTAs */}
          <div className="lg:col-span-7 space-y-6">
            <div className="eyebrow flex items-center gap-2">
              <Sparkles className="h-3.5 w-3.5 text-[var(--accent)]" />
              <span>AI-POWERED RESUME STUDIO</span>
            </div>

            <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-[var(--text)] leading-[1.08]">
              One profile.<br />
              <span style={{ color: 'var(--accent)' }}>Every opportunity.</span>
            </h1>

            <p className="text-base sm:text-lg text-[var(--muted)] leading-relaxed max-w-xl">
              Tailor your resume to the job, highlight your real strengths, and apply with confidence. Your experience stays yours — the AI helps you present it better.
            </p>

            <div className="flex flex-wrap gap-3 pt-2">
              <Link to="/register" className="btn primary" style={{ padding: '14px 22px', fontSize: '13px' }}>
                <span>Build your resume now</span>
                <ArrowRight className="h-4 w-4" />
              </Link>
              <button
                onClick={handleInstantDemo}
                className="btn secondary"
                style={{ padding: '14px 20px', fontSize: '13px' }}
              >
                Try Instant Demo ↗
              </button>
            </div>

            {/* Quick Proof Pills */}
            <div className="pt-4 flex flex-wrap gap-2.5 text-xs text-[var(--muted)]">
              <span className="pill blue">✓ Single-column ATS-friendly</span>
              <span className="pill red">✦ No Fabricated Skills</span>
              <span className="pill">✓ Pure Java 21 Engine</span>
            </div>
          </div>

          {/* Right Column: Live Resume Paper Demonstration */}
          <div className="lg:col-span-5">
            <div className="resume-paper" style={{ transform: 'rotate(0.5deg)', boxShadow: '0 16px 40px rgba(0,0,0,0.12)' }}>
              <h2>JORDAN DAVIS</h2>
              <div className="contact">
                Frontend Developer · jordan@example.com · LinkedIn · Portfolio
              </div>
              <div className="line" style={{ background: 'var(--accent)' }}></div>

              <h3>PROFESSIONAL SUMMARY</h3>
              <p>
                Frontend developer focused on creating responsive, accessible web experiences and reusable interfaces. Experienced in building component-driven applications and collaborating across product teams.
              </p>

              <h3>CORE SKILLS</h3>
              <p>
                React · TypeScript · JavaScript · HTML5 · CSS3 · Accessibility · REST APIs · Git · Tailwind
              </p>

              <h3>EXPERIENCE</h3>
              <div className="role">
                Frontend Developer <span className="date">2023 — Present</span>
              </div>
              <ul>
                <li>Built reusable interface components to improve consistency across product screens.</li>
                <li>Collaborated with design and engineering teams to deliver responsive experiences.</li>
              </ul>

              <h3>SELECTED PROJECTS</h3>
              <div className="role">Accessible Product Dashboard</div>
              <p>
                Created a responsive dashboard layout with reusable components and accessible interaction patterns.
              </p>
            </div>
          </div>
        </div>

        {/* 3 Pillars / Feature Cards */}
        <div className="grid sm:grid-cols-3 gap-5 pt-8">
          <div className="card">
            <div className="stat-top">
              <span className="font-bold text-xs uppercase tracking-wider text-[var(--accent)]">Step 01</span>
              <span className="stat-icon">♙</span>
            </div>
            <div className="stat-num" style={{ fontSize: '18px', margin: '12px 0 6px' }}>
              Master Career Profile
            </div>
            <p className="sub" style={{ margin: 0 }}>
              Enter your experience, education, skills, and projects once. It becomes your permanent source of truth.
            </p>
          </div>

          <div className="card">
            <div className="stat-top">
              <span className="font-bold text-xs uppercase tracking-wider text-[var(--accent)]">Step 02</span>
              <span className="stat-icon">⌕</span>
            </div>
            <div className="stat-num" style={{ fontSize: '18px', margin: '12px 0 6px' }}>
              Vacancy Requirement Matcher
            </div>
            <p className="sub" style={{ margin: 0 }}>
              Paste job descriptions to extract required keywords and compare them deterministically against your qualifications.
            </p>
          </div>

          <div className="card">
            <div className="stat-top">
              <span className="font-bold text-xs uppercase tracking-wider text-[var(--accent)]">Step 03</span>
              <span className="stat-icon">▤</span>
            </div>
            <div className="stat-num" style={{ fontSize: '18px', margin: '12px 0 6px' }}>
              Tailored Resume Studio
            </div>
            <p className="sub" style={{ margin: 0 }}>
              Generate ATS-optimized PDF and DOCX files highlighting verified relevant projects without fabricating data.
            </p>
          </div>
        </div>

        {/* Built on Trust Card */}
        <div className="card" style={{ background: 'var(--surface2)', padding: '28px', marginTop: '24px' }}>
          <div className="eyebrow flex items-center gap-2">
            <ShieldCheck className="h-4 w-4 text-[var(--accent)]" />
            <span>BUILT ON TRUST</span>
          </div>
          <h2 style={{ fontSize: '22px', margin: '8px 0' }}>Your experience. Your truth.</h2>
          <p className="sub" style={{ maxWidth: '700px', fontSize: '13px', margin: 0 }}>
            We believe candidates should stand out on genuine merit. The tailoring engine reorders, highlights, and formats your real achievements for maximum relevance — but never invents skills or false credentials.
          </p>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-[var(--border)] bg-[var(--surface)] py-8 text-center text-xs text-[var(--muted)]">
        <p>TailorCV — AI Resume Studio · Built with Java 21, Spring Boot & React</p>
      </footer>
    </div>
  );
};
