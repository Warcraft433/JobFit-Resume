import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { ApiResponse } from '../types';

export const ResumeBuilderPage: React.FC = () => {
  const [jobs, setJobs] = useState<any[]>([]);
  const [selectedJobId, setSelectedJobId] = useState<string>('');
  const [selectedTemplate, setSelectedTemplate] = useState<string>('Clean ATS');
  const [paperColor, setPaperColor] = useState<string>('#E10600');
  const [resumeTitle, setResumeTitle] = useState<string>('Frontend Developer Resume');
  const [targetRole, setTargetRole] = useState<string>('Frontend Developer');
  const [resumes, setResumes] = useState<any[]>([]);
  const [activeVersion, setActiveVersion] = useState<any | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);
  const [isDownloading, setIsDownloading] = useState<string | null>(null);

  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [jobsRes, resumesRes] = await Promise.all([
          apiClient.get<ApiResponse<any[]>>('/jobs').catch(() => null),
          apiClient.get<ApiResponse<any[]>>('/resumes').catch(() => null),
        ]);

        if (jobsRes?.data?.success) {
          setJobs(jobsRes.data.data);
          const queryJobId = searchParams.get('jobId');
          if (queryJobId) setSelectedJobId(queryJobId);
          else if (jobsRes.data.data.length > 0) setSelectedJobId(jobsRes.data.data[0].id);
        }

        if (resumesRes?.data?.success && resumesRes.data.data.length > 0) {
          setResumes(resumesRes.data.data);
          const first = resumesRes.data.data[0];
          if (first.title) setResumeTitle(first.title);
          const verRes = await apiClient.get<ApiResponse<any[]>>(`/resumes/${first.id}/versions`).catch(() => null);
          if (verRes?.data?.success && verRes.data.data.length > 0) {
            setActiveVersion(verRes.data.data[0]);
          }
        }
      } catch (err) {
        console.error(err);
      }
    };
    fetchData();
  }, [searchParams]);

  const handleGenerate = async () => {
    if (!selectedJobId) {
      alert('Select a target job first.');
      return;
    }
    setIsGenerating(true);
    try {
      const templateKey = selectedTemplate === 'Clean ATS' ? 'classic' : selectedTemplate === 'Neon Accent' ? 'technical' : 'modern';
      const res = await apiClient.post<ApiResponse<any>>(`/jobs/${selectedJobId}/generate-resume`, {
        templateKey,
      });
      if (res.data.success && res.data.data) {
        setActiveVersion(res.data.data);
        alert('Resume synthesized and saved successfully.');
      }
    } catch (err: any) {
      alert(err?.response?.data?.error?.details || 'Failed to generate resume');
    } finally {
      setIsGenerating(false);
    }
  };

  const handleDownloadHtml = () => {
    const paper = document.getElementById('paper');
    if (!paper) return;
    const content = `<!doctype html><meta charset="utf-8"><title>${resumeTitle}</title><style>body{font-family:Arial;max-width:760px;margin:40px auto;padding:24px;color:#222}h3{font-size:12px}p,li{font-size:12px;line-height:1.6}.line{height:2px;background:${paperColor}}</style>${paper.outerHTML}`;
    const blob = new Blob([content], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${resumeTitle.replace(/[^\w.-]+/g, '_')}.html`;
    a.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  };

  const handlePrint = () => {
    const paper = document.getElementById('paper');
    if (!paper) return;
    const w = window.open('', '_blank');
    if (!w) {
      alert('Allow pop-ups to print or save as PDF.');
      return;
    }
    w.document.write(
      `<html><head><title>${resumeTitle}</title><style>body{font-family:Arial,sans-serif;max-width:760px;margin:35px auto;padding:25px;color:#222}h2{font-size:24px}h3{font-size:11px;letter-spacing:1px;margin-top:22px}p,li{font-size:12px;line-height:1.6}.line{height:2px;background:${paperColor};margin:15px 0}@media print{body{margin:0}}</style></head><body>${paper.innerHTML}</body></html>`
    );
    w.document.close();
    w.focus();
    w.print();
  };

  const handleDownloadPdf = async () => {
    if (!activeVersion) return;
    setIsDownloading('pdf');
    try {
      const response = await apiClient.get(`/resume-versions/${activeVersion.id}/export/pdf`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${resumeTitle.replace(/[^\w.-]+/g, '_')}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch {
      alert('Failed to download PDF');
    } finally {
      setIsDownloading(null);
    }
  };

  const handleDownloadDocx = async () => {
    if (!activeVersion) return;
    setIsDownloading('docx');
    try {
      const response = await apiClient.get(`/resume-versions/${activeVersion.id}/export/docx`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(
        new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        })
      );
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${resumeTitle.replace(/[^\w.-]+/g, '_')}.docx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch {
      alert('Failed to download DOCX');
    } finally {
      setIsDownloading(null);
    }
  };

  const c = activeVersion?.content?.contact;
  const content = activeVersion?.content;

  return (
    <div>
      {/* Pagehead */}
      <div className="pagehead">
        <div>
          <div className="eyebrow">STEP 02 · BUILD YOUR DOCUMENT</div>
          <h1>Resume workspace</h1>
          <p>Edit the preview directly, select a template and prepare a clean, professional export.</p>
        </div>
        <span className="pill red">Editable preview</span>
      </div>

      <div className="content-layout">
        {/* Left Section: Live Preview Paper */}
        <section className="card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '10px' }}>
            <div>
              <h2>Live resume preview</h2>
              <p className="sub" style={{ marginBottom: 0 }}>Click text in the document to edit it.</p>
            </div>
            <span className="pill blue">ATS-first</span>
          </div>

          <div style={{ marginTop: '17px' }}>
            <div className="resume-paper" id="paper">
              <h2 contentEditable suppressContentEditableWarning>
                {c?.fullName ? c.fullName.toUpperCase() : 'JORDAN DAVIS'}
              </h2>

              <div className="contact" contentEditable suppressContentEditableWarning>
                {targetRole} · {c?.email || 'jordan@example.com'} · {c?.phone || '+1 (555) 019-2834'} · {c?.location || 'San Francisco, CA'} · LinkedIn · Portfolio
              </div>

              <div className="line" style={{ background: paperColor }}></div>

              <h3>PROFESSIONAL SUMMARY</h3>
              <p contentEditable suppressContentEditableWarning>
                {content?.professionalSummary ||
                  'Frontend developer focused on creating responsive, accessible web experiences and reusable interfaces. Experienced in building component-driven applications and collaborating across product teams.'}
              </p>

              <h3>CORE SKILLS</h3>
              <p contentEditable suppressContentEditableWarning>
                {content?.primarySkills && content.primarySkills.length > 0
                  ? [...content.primarySkills, ...(content.secondarySkills || [])].join(' · ')
                  : 'React · JavaScript · HTML · CSS · Accessibility · REST APIs · Git · Tailwind CSS'}
              </p>

              <h3>EXPERIENCE</h3>
              {content?.experiences && content.experiences.length > 0 ? (
                content.experiences.map((exp: any, i: number) => (
                  <div key={i} style={{ marginBottom: '12px' }}>
                    <div className="role" contentEditable suppressContentEditableWarning>
                      {exp.jobTitle} — {exp.employer} <span className="date">{exp.startDate} — {exp.currentRole ? 'Present' : exp.endDate}</span>
                    </div>
                    {exp.bulletPoints && exp.bulletPoints.length > 0 ? (
                      <ul>
                        {exp.bulletPoints.map((bp: string, bi: number) => (
                          <li key={bi} contentEditable suppressContentEditableWarning>
                            {bp}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p contentEditable suppressContentEditableWarning>{exp.description}</p>
                    )}
                  </div>
                ))
              ) : (
                <>
                  <div className="role" contentEditable suppressContentEditableWarning>
                    Frontend Developer <span className="date">2023 — Present</span>
                  </div>
                  <ul>
                    <li contentEditable suppressContentEditableWarning>
                      Built reusable interface components to improve consistency across product screens.
                    </li>
                    <li contentEditable suppressContentEditableWarning>
                      Collaborated with design and engineering teams to deliver responsive experiences.
                    </li>
                  </ul>
                </>
              )}

              <h3>SELECTED PROJECTS</h3>
              {content?.projects && content.projects.length > 0 ? (
                content.projects.map((p: any, i: number) => (
                  <div key={i} style={{ marginBottom: '10px' }}>
                    <div className="role" contentEditable suppressContentEditableWarning>
                      {p.name} {p.technicalStack && `· ${p.technicalStack}`}
                    </div>
                    <p contentEditable suppressContentEditableWarning>{p.description}</p>
                  </div>
                ))
              ) : (
                <>
                  <div className="role" contentEditable suppressContentEditableWarning>
                    Accessible Product Dashboard
                  </div>
                  <p contentEditable suppressContentEditableWarning>
                    Created a responsive dashboard layout with reusable components and accessible interaction patterns.
                  </p>
                </>
              )}

              <h3>EDUCATION & CERTIFICATIONS</h3>
              {content?.educations && content.educations.length > 0 ? (
                content.educations.map((edu: any, i: number) => (
                  <p key={i} contentEditable suppressContentEditableWarning>
                    {edu.degree} in {edu.fieldOfStudy} — {edu.institution} ({edu.startDate} — {edu.endDate})
                    {edu.grade && ` · CGPA: ${edu.grade}`}
                  </p>
                ))
              ) : (
                <p contentEditable suppressContentEditableWarning>
                  B.S. in Computer Science — University of Technology · 2019 — 2023
                </p>
              )}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '9px', flexWrap: 'wrap', marginTop: '15px' }}>
            <button className="btn primary" onClick={handleGenerate} disabled={isGenerating}>
              {isGenerating ? 'Synthesizing...' : 'Save resume'}
            </button>
            <button className="btn secondary" onClick={handleDownloadHtml}>
              Download preview
            </button>
            <button className="btn secondary" onClick={handlePrint}>
              Print / Save PDF
            </button>
            {activeVersion && (
              <>
                <button
                  className="btn secondary"
                  onClick={handleDownloadPdf}
                  disabled={isDownloading === 'pdf'}
                >
                  {isDownloading === 'pdf' ? 'Exporting...' : 'A4 PDF Export'}
                </button>
                <button
                  className="btn secondary"
                  onClick={handleDownloadDocx}
                  disabled={isDownloading === 'docx'}
                >
                  {isDownloading === 'docx' ? 'Exporting...' : 'DOCX Export'}
                </button>
              </>
            )}
          </div>

          <p className="hint" style={{ marginTop: '12px' }}>
            For DOCX export and production PDF generation, connect a document-generation library or backend service.
          </p>
        </section>

        {/* Right Section: Settings & Template style */}
        <aside>
          <div className="card">
            <h2>Resume settings</h2>
            <p className="sub">
              Keep your resume readable, concise and compatible with common applicant tracking systems.
            </p>

            <div className="field">
              <label>Resume title</label>
              <input
                id="resumeTitle"
                value={resumeTitle}
                onChange={(e) => setResumeTitle(e.target.value)}
              />
            </div>

            <div className="field">
              <label>Target vacancy ({resumes.length} saved)</label>
              <select
                value={selectedJobId}
                onChange={(e) => {
                  setSelectedJobId(e.target.value);
                  const found = jobs.find((j) => j.id === e.target.value);
                  if (found) setTargetRole(found.jobTitle);
                }}
              >
                <option value="">-- Select Vacancy --</option>
                {jobs.map((j) => (
                  <option key={j.id} value={j.id}>
                    {j.company} — {j.jobTitle}
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label>Target role</label>
              <input
                id="targetRole"
                value={targetRole}
                onChange={(e) => setTargetRole(e.target.value)}
              />
            </div>

            <div className="field">
              <label>Document layout</label>
              <select id="layoutSelect">
                <option>Single-column ATS-friendly</option>
                <option>Compact professional</option>
                <option>Modern two-column (less ATS-safe)</option>
              </select>
            </div>

            <div className="callout">
              Never include a skill, credential, date or achievement unless it is supported by your actual profile.
            </div>
          </div>

          <div className="card" style={{ marginTop: '17px' }}>
            <h2>Template style</h2>
            <p className="sub">
              Selected design: <b>{selectedTemplate}</b>. Choose a design in Templates or change the document accent.
            </p>

            <div className="chips">
              <button
                type="button"
                className="chip"
                style={{
                  borderColor: paperColor === '#E10600' ? '#E10600' : undefined,
                  fontWeight: paperColor === '#E10600' ? 700 : undefined,
                }}
                onClick={() => {
                  setPaperColor('#E10600');
                  setSelectedTemplate('Modern Professional');
                }}
              >
                <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#E10600', display: 'inline-block' }} />
                Signal red (Modern)
              </button>
              <button
                type="button"
                className="chip"
                style={{
                  borderColor: paperColor === '#00BFFF' ? '#00BFFF' : undefined,
                  fontWeight: paperColor === '#00BFFF' ? 700 : undefined,
                }}
                onClick={() => {
                  setPaperColor('#00BFFF');
                  setSelectedTemplate('Neon Accent');
                }}
              >
                <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#00BFFF', display: 'inline-block' }} />
                Neon blue (Accent)
              </button>
              <button
                type="button"
                className="chip"
                style={{
                  borderColor: paperColor === '#25252B' ? '#25252B' : undefined,
                  fontWeight: paperColor === '#25252B' ? 700 : undefined,
                }}
                onClick={() => {
                  setPaperColor('#25252B');
                  setSelectedTemplate('Clean ATS');
                }}
              >
                <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#25252B', display: 'inline-block' }} />
                Classic charcoal (ATS)
              </button>
            </div>

            <button
              className="btn secondary"
              style={{ width: '100%', marginTop: '12px' }}
              onClick={() => navigate('/templates')}
            >
              Browse sample designs →
            </button>
          </div>
        </aside>
      </div>
    </div>
  );
};
