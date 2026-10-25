import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export const TemplatesPage: React.FC = () => {
  const [selectedTemplate, setSelectedTemplate] = useState('Clean ATS');
  const navigate = useNavigate();

  const templatesList = [
    {
      name: 'Clean ATS',
      desc: 'Single-column, straightforward and parser-friendly',
      cls: 'clean',
    },
    {
      name: 'Modern Professional',
      desc: 'Polished headings with subtle accent color',
      cls: 'red',
    },
    {
      name: 'Neon Accent',
      desc: 'Classic document with a restrained blue accent',
      cls: 'blue',
    },
  ];

  return (
    <div>
      <div className="pagehead">
        <div>
          <div className="eyebrow">DOCUMENT DESIGN</div>
          <h1>Resume templates</h1>
          <p>Start with a clear layout, then focus on the content that matters for the role.</p>
        </div>
        <button className="btn primary" onClick={() => navigate('/resumes')}>
          Use selected template →
        </button>
      </div>

      <div className="grid template-grid">
        {templatesList.map((t) => (
          <button
            key={t.name}
            type="button"
            className={`template ${selectedTemplate === t.name ? 'selected' : ''}`}
            onClick={() => setSelectedTemplate(t.name)}
          >
            <div className={`template-preview ${t.cls}`}>
              <span></span>
              <span></span>
              <span style={{ width: '92%' }}></span>
              <span style={{ width: '70%' }}></span>
              <span style={{ width: '85%', marginTop: '16px' }}></span>
              <span style={{ width: '75%' }}></span>
              <span style={{ width: '90%' }}></span>
              <span style={{ width: '55%', marginTop: '15px' }}></span>
            </div>
            <b>
              {t.name} {selectedTemplate === t.name ? '✓' : ''}
            </b>
            <small>{t.desc}</small>
          </button>
        ))}
      </div>

      <div className="callout" style={{ marginTop: '18px' }}>
        Template workflow: select a design → map profile fields into its existing LaTeX/Overleaf template → preview the populated resume → edit directly or ask the AI assistant → export. Template source code is never shown to job seekers.
      </div>

      <p className="template-note">
        These are illustrative template previews for this prototype. Your actual Overleaf/LaTeX template files can be connected later during implementation.
      </p>
    </div>
  );
};
