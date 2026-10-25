import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import type { ApiResponse, ProfileCompleteness } from '../types';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [completeness, setCompleteness] = useState<ProfileCompleteness | null>(null);
  const [jobs, setJobs] = useState<any[]>([]);
  const [resumes, setResumes] = useState<any[]>([]);
  const [skillsCount, setSkillsCount] = useState<number>(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [compRes, jobsRes, resumesRes, skillsRes] = await Promise.all([
          apiClient.get<ApiResponse<ProfileCompleteness>>('/profile/completeness').catch(() => null),
          apiClient.get<ApiResponse<any[]>>('/jobs').catch(() => null),
          apiClient.get<ApiResponse<any[]>>('/resumes').catch(() => null),
          apiClient.get<ApiResponse<any[]>>('/profile/skills').catch(() => null),
        ]);

        if (compRes?.data?.success) setCompleteness(compRes.data.data);
        if (jobsRes?.data?.success) setJobs(jobsRes.data.data || []);
        if (resumesRes?.data?.success) setResumes(resumesRes.data.data || []);
        if (skillsRes?.data?.success && Array.isArray(skillsRes.data.data)) {
          setSkillsCount(skillsRes.data.data.length);
        }
      } catch {
        // ignore
      }
    };
    fetchData();
  }, []);

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  };

  const profilePct = completeness?.score ?? 0;
  const firstName = user?.fullName
    ? user.fullName.split(' ')[0]
    : user?.email
    ? user.email.split('@')[0]
    : 'there';
  const totalResumes = String(resumes.length).padStart(2, '0');
  const totalJobs = String(jobs.length).padStart(2, '0');

  const savedResumes = resumes.slice(0, 3).map((r) => ({
    role: r.title || 'Untitled Resume',
    date: r.updatedAt ? new Date(r.updatedAt).toLocaleDateString() : 'Updated recently',
    status: r.isBase ? 'Master' : 'Custom',
  }));

  return (
    <div>
      {/* Pagehead */}
      <div className="pagehead">
        <div>
          <div className="eyebrow">YOUR CAREER WORKSPACE</div>
          <h1>
            {getGreeting()}, {firstName} <span style={{ fontSize: '24px' }}>✦</span>
          </h1>
          <p>Make your next application your strongest one yet.</p>
        </div>
        <button className="btn primary" onClick={() => navigate('/jobs')}>
          ＋ Create tailored resume
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid stats">
        <div className="card">
          <div className="stat-top">
            <span>Resumes created</span>
            <span className="stat-icon">▤</span>
          </div>
          <div className="stat-num">{totalResumes}</div>
          <div className="stat-foot">Across your workspace</div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Profile completion</span>
            <span className="stat-icon">♙</span>
          </div>
          <div className="stat-num">{profilePct}%</div>
          <div className="stat-foot">
            <span className="good">↑</span> Keep your details current
          </div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Jobs analyzed</span>
            <span className="stat-icon">⌕</span>
          </div>
          <div className="stat-num">{totalJobs}</div>
          <div className="stat-foot">Job descriptions reviewed</div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Profile skills</span>
            <span className="stat-icon">✦</span>
          </div>
          <div className="stat-num">{skillsCount}</div>
          <div className="stat-foot">Verified skills on file</div>
        </div>
      </div>

      {/* Main Dashboard Grid */}
      <div className="grid dashboard-grid">
        <section>
          {/* Hero */}
          <div className="hero">
            <div className="eyebrow">✦ AI-POWERED RESUME STUDIO</div>
            <h2>
              One profile.
              <br />
              Every opportunity.
            </h2>
            <p>
              Tailor your resume to the job, highlight your real strengths, and apply with confidence. Your experience stays yours — the AI helps you present it better.
            </p>
            <div className="hero-actions">
              <button className="btn primary" onClick={() => navigate('/jobs')}>
                Create tailored resume ↗
              </button>
              <button className="btn secondary" onClick={() => navigate('/profile')}>
                Complete your profile
              </button>
            </div>
          </div>

          {/* Section Head */}
          <div className="section-head">
            <h2>Your next steps</h2>
            <span className="pill">3-step workflow</span>
          </div>

          {/* Steps */}
          <div className="steps">
            <div className="step" onClick={() => navigate('/profile')}>
              <div className="stepicon">♙</div>
              <div className="steptext">
                <b>Complete your profile</b>
                <p>Skills, projects, education and certifications</p>
              </div>
              <span className="arrow">↗</span>
            </div>

            <div className="step" onClick={() => navigate('/jobs')}>
              <div className="stepicon">⌕</div>
              <div className="steptext">
                <b>Analyze a job description</b>
                <p>Find relevant requirements and keywords</p>
              </div>
              <span className="arrow">↗</span>
            </div>

            <div className="step" onClick={() => navigate('/resumes')}>
              <div className="stepicon">▤</div>
              <div className="steptext">
                <b>Generate your tailored resume</b>
                <p>Review, edit and export your document</p>
              </div>
              <span className="arrow">↗</span>
            </div>
          </div>
        </section>

        <aside>
          {/* Profile Strength */}
          <div className="card">
            <div className="section-head" style={{ margin: 0 }}>
              <h2>Profile strength</h2>
              <span className="pill red">{profilePct}%</span>
            </div>
            <div className="profile-progress">
              <div className="progress">
                <span style={{ width: `${profilePct}%` }}></span>
              </div>
              <span className="progress-num">{profilePct}%</span>
            </div>
            <p className="sub" style={{ margin: '9px 0 15px' }}>
              A complete profile gives the AI more verified information to work with.
            </p>
            <button
              className="btn secondary"
              style={{ width: '100%' }}
              onClick={() => navigate('/profile')}
            >
              Review my profile →
            </button>
          </div>

          {/* Recent Resumes */}
          <div className="card" style={{ marginTop: '17px' }}>
            <div className="section-head" style={{ margin: '0 0 6px' }}>
              <h2>Recent resumes</h2>
              <Link to="/resumes" className="textlink">
                View all
              </Link>
            </div>
            {savedResumes.length > 0 ? (
              savedResumes.map((r, i) => (
                <div className="resume-row" key={i}>
                  <div className="docicon">▤</div>
                  <div className="meta">
                    <b>{r.role}</b>
                    <small>{r.date}</small>
                  </div>
                  <span className={`pill ${r.status === 'Draft' ? 'red' : 'blue'}`}>
                    {r.status}
                  </span>
                  <button
                    className="iconbtn"
                    onClick={() => navigate('/resumes')}
                    title="Edit resume"
                  >
                    ↗
                  </button>
                </div>
              ))
            ) : (
              <div style={{ padding: '24px 16px', color: 'var(--muted)', fontSize: '13px', textAlign: 'center' }}>
                <p style={{ margin: '0 0 12px' }}>No resumes created yet.</p>
                <button
                  className="btn secondary"
                  style={{ width: '100%', fontSize: '12px' }}
                  onClick={() => navigate('/jobs')}
                >
                  ＋ Create your first resume
                </button>
              </div>
            )}
            <button
              className="btn ghost"
              style={{ width: '100%', marginTop: '12px' }}
              onClick={() => navigate('/resumes')}
            >
              Open resume library
            </button>
          </div>

          {/* Built On Trust */}
          <div
            className="card"
            style={{
              marginTop: '17px',
              background: 'var(--surface2)',
              boxShadow: 'none',
            }}
          >
            <div className="eyebrow">BUILT ON TRUST</div>
            <h2 style={{ marginBottom: '8px' }}>Your experience. Your truth.</h2>
            <p className="sub" style={{ margin: 0 }}>
              We use your existing skills and achievements. Missing qualifications are never invented.
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
};
