import React, { useEffect, useState, useRef } from 'react';
import { apiClient } from '../api/client';
import type { ApiResponse, UserProfile, Education, Experience, UserSkill, Project, Certification, SkillCategory } from '../types';
import {
  User,
  Briefcase,
  GraduationCap,
  Wrench,
  FolderGit2,
  Award,
  Plus,
  Trash2,
  Save,
  CheckCircle2,
  AlertCircle,
  ExternalLink,
  Link as LinkIcon,
  Eye,
  EyeOff,
  ChevronDown,
  X,
} from 'lucide-react';

type ProfileTab = 'personal' | 'experience' | 'education' | 'skills' | 'projects' | 'certifications';

interface CustomLinkItem {
  id: string;
  label: string;
  url: string;
}

const COMMON_SKILLS: { name: string; category: SkillCategory }[] = [
  { name: 'Java', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'JavaScript', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'TypeScript', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'Python', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'Go', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'C++', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'C#', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'SQL', category: 'DATABASE' },
  { name: 'HTML & CSS', category: 'FRONTEND' },
  { name: 'Rust', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'Kotlin', category: 'PROGRAMMING_LANGUAGE' },
  { name: 'Spring Boot', category: 'BACKEND' },
  { name: 'Node.js', category: 'BACKEND' },
  { name: 'Express.js', category: 'BACKEND' },
  { name: 'Django', category: 'BACKEND' },
  { name: 'FastAPI', category: 'BACKEND' },
  { name: 'Microservices', category: 'BACKEND' },
  { name: 'REST APIs', category: 'BACKEND' },
  { name: 'GraphQL', category: 'BACKEND' },
  { name: 'Kafka', category: 'BACKEND' },
  { name: 'React', category: 'FRONTEND' },
  { name: 'Next.js', category: 'FRONTEND' },
  { name: 'Vue.js', category: 'FRONTEND' },
  { name: 'Angular', category: 'FRONTEND' },
  { name: 'Tailwind CSS', category: 'FRONTEND' },
  { name: 'Redux', category: 'FRONTEND' },
  { name: 'PostgreSQL', category: 'DATABASE' },
  { name: 'MySQL', category: 'DATABASE' },
  { name: 'MongoDB', category: 'DATABASE' },
  { name: 'Redis', category: 'DATABASE' },
  { name: 'Oracle', category: 'DATABASE' },
  { name: 'Docker', category: 'CLOUD_DEVOPS' },
  { name: 'Kubernetes', category: 'CLOUD_DEVOPS' },
  { name: 'AWS', category: 'CLOUD_DEVOPS' },
  { name: 'Google Cloud', category: 'CLOUD_DEVOPS' },
  { name: 'Azure', category: 'CLOUD_DEVOPS' },
  { name: 'CI/CD Pipelines', category: 'CLOUD_DEVOPS' },
  { name: 'Git & GitHub', category: 'DEV_TOOLS' },
  { name: 'Linux / Bash', category: 'DEV_TOOLS' },
  { name: 'Unit Testing (JUnit/Jest)', category: 'DEV_TOOLS' },
  { name: 'Agile & Scrum', category: 'SOFT_SKILL' },
  { name: 'System Design', category: 'BACKEND' },
];

const COMMON_TECH_STACK = [
  'React', 'Java', 'Spring Boot', 'TypeScript', 'Node.js', 'Python',
  'PostgreSQL', 'Docker', 'AWS', 'Kubernetes', 'GraphQL', 'Next.js',
  'Redis', 'MongoDB', 'Tailwind CSS', 'Vue.js', 'Kafka', 'Linux',
  'Git', 'Microservices', 'REST APIs', 'Express', 'MySQL', 'CI/CD'
];

export const ProfilePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<ProfileTab>('personal');
  const [statusMsg, setStatusMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Profile data states
  const [profile, setProfile] = useState<Partial<UserProfile>>({
    professionalHeadline: '',
    contactEmail: '',
    phone: '',
    city: '',
    country: '',
    professionalSummary: '',
    linkedinUrl: '',
    githubUrl: '',
    portfolioUrl: '',
    customLinks: '',
  });

  // Custom Links state
  const [customLinks, setCustomLinks] = useState<CustomLinkItem[]>([]);
  const [newLinkLabel, setNewLinkLabel] = useState('');
  const [newLinkUrl, setNewLinkUrl] = useState('');

  const [experiences, setExperiences] = useState<Experience[]>([]);
  const [educations, setEducations] = useState<Education[]>([]);
  const [userSkills, setUserSkills] = useState<UserSkill[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [certifications, setCertifications] = useState<Certification[]>([]);

  // Simple form states for adding items
  const [showModal, setShowModal] = useState<string | null>(null);

  // New item form buffers
  const [newExp, setNewExp] = useState({
    employer: '',
    jobTitle: '',
    location: '',
    startDate: '',
    endDate: '',
    currentRole: false,
    description: '',
    achievements: '',
    technologies: '',
  });

  const [newEdu, setNewEdu] = useState({
    institution: '',
    degree: '',
    fieldOfStudy: '',
    startDate: '',
    endDate: '',
    currentlyStudying: false,
    gradeType: 'CGPA',
    gradeValue: '',
    showGradeOnResume: true,
  });

  // Skill combobox states
  const [newSkill, setNewSkill] = useState({
    skillName: '',
    category: 'BACKEND' as SkillCategory,
    proficiency: 'ADVANCED' as any,
    yearsExperience: 3,
  });
  const [skillSearchQuery, setSkillSearchQuery] = useState('');
  const [isSkillDropdownOpen, setIsSkillDropdownOpen] = useState(false);
  const skillComboboxRef = useRef<HTMLDivElement>(null);

  // Project tech stack states
  const [newProj, setNewProj] = useState({
    name: '',
    description: '',
    technicalStack: '',
    outcomes: '',
    githubUrl: '',
    demoUrl: '',
    category: 'Full Stack',
  });
  const [projectTechTags, setProjectTechTags] = useState<string[]>([]);
  const [techStackQuery, setTechStackQuery] = useState('');
  const [isTechDropdownOpen, setIsTechDropdownOpen] = useState(false);
  const techComboboxRef = useRef<HTMLDivElement>(null);

  const [newCert, setNewCert] = useState({
    name: '',
    issuer: '',
    issueDate: '',
    credentialUrl: '',
    description: '',
  });

  // Click outside listener for comboboxes
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (skillComboboxRef.current && !skillComboboxRef.current.contains(e.target as Node)) {
        setIsSkillDropdownOpen(false);
      }
      if (techComboboxRef.current && !techComboboxRef.current.contains(e.target as Node)) {
        setIsTechDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchAllProfileData = async () => {
    try {
      const [pRes, expRes, eduRes, skRes, projRes, certRes] = await Promise.all([
        apiClient.get<ApiResponse<UserProfile>>('/profile'),
        apiClient.get<ApiResponse<Experience[]>>('/experience'),
        apiClient.get<ApiResponse<Education[]>>('/education'),
        apiClient.get<ApiResponse<UserSkill[]>>('/profile/skills'),
        apiClient.get<ApiResponse<Project[]>>('/projects'),
        apiClient.get<ApiResponse<Certification[]>>('/certifications'),
      ]);

      if (pRes.data.success && pRes.data.data) {
        setProfile(pRes.data.data);
        if (pRes.data.data.customLinks) {
          try {
            const parsed = JSON.parse(pRes.data.data.customLinks);
            if (Array.isArray(parsed)) setCustomLinks(parsed);
          } catch {
            // ignore
          }
        }
      }
      if (expRes.data.success) setExperiences(expRes.data.data);
      if (eduRes.data.success) setEducations(eduRes.data.data);
      if (skRes.data.success) setUserSkills(skRes.data.data);
      if (projRes.data.success) setProjects(projRes.data.data);
      if (certRes.data.success) setCertifications(certRes.data.data);
    } catch (err: any) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchAllProfileData();
  }, []);

  const showNotification = (text: string, type: 'success' | 'error' = 'success') => {
    setStatusMsg({ type, text });
    setTimeout(() => setStatusMsg(null), 3500);
  };

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        ...profile,
        customLinks: JSON.stringify(customLinks),
      };
      const res = await apiClient.put<ApiResponse<UserProfile>>('/profile', payload);
      if (res.data.success) {
        showNotification('Profile information saved successfully!');
      }
    } catch {
      showNotification('Failed to update profile.', 'error');
    }
  };

  // Custom Links handlers
  const handleAddCustomLink = () => {
    if (!newLinkLabel.trim() || !newLinkUrl.trim()) return;
    let url = newLinkUrl.trim();
    if (!/^https?:\/\//i.test(url)) {
      url = 'https://' + url;
    }
    const item: CustomLinkItem = {
      id: 'link-' + Date.now(),
      label: newLinkLabel.trim(),
      url,
    };
    const updated = [...customLinks, item];
    setCustomLinks(updated);
    setNewLinkLabel('');
    setNewLinkUrl('');
  };

  const handleDeleteCustomLink = (id: string) => {
    setCustomLinks(customLinks.filter((l) => l.id !== id));
  };

  // Add handlers
  const handleAddExperience = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post('/experience', newExp);
      setShowModal(null);
      setNewExp({
        employer: '',
        jobTitle: '',
        location: '',
        startDate: '',
        endDate: '',
        currentRole: false,
        description: '',
        achievements: '',
        technologies: '',
      });
      fetchAllProfileData();
      showNotification('Work experience added!');
    } catch {
      showNotification('Failed to add experience', 'error');
    }
  };

  const handleDeleteExperience = async (id: string) => {
    try {
      await apiClient.delete(`/experience/${id}`);
      fetchAllProfileData();
      showNotification('Experience removed');
    } catch {
      showNotification('Failed to delete', 'error');
    }
  };

  const handleAddEducation = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post('/education', newEdu);
      setShowModal(null);
      setNewEdu({
        institution: '',
        degree: '',
        fieldOfStudy: '',
        startDate: '',
        endDate: '',
        currentlyStudying: false,
        gradeType: 'CGPA',
        gradeValue: '',
        showGradeOnResume: true,
      });
      fetchAllProfileData();
      showNotification('Education added!');
    } catch {
      showNotification('Failed to add education', 'error');
    }
  };

  const handleDeleteEducation = async (id: string) => {
    try {
      await apiClient.delete(`/education/${id}`);
      fetchAllProfileData();
      showNotification('Education removed');
    } catch {
      showNotification('Failed to delete', 'error');
    }
  };

  const handleAddSkill = async (e: React.FormEvent) => {
    e.preventDefault();
    const finalSkillName = (newSkill.skillName || skillSearchQuery).trim();
    if (!finalSkillName) return;

    try {
      await apiClient.post('/profile/skills', {
        ...newSkill,
        skillName: finalSkillName,
      });
      setShowModal(null);
      setNewSkill({
        skillName: '',
        category: 'BACKEND',
        proficiency: 'ADVANCED',
        yearsExperience: 3,
      });
      setSkillSearchQuery('');
      fetchAllProfileData();
      showNotification('Skill added to profile!');
    } catch {
      showNotification('Failed to add skill', 'error');
    }
  };

  const handleDeleteSkill = async (id: string) => {
    try {
      await apiClient.delete(`/profile/skills/${id}`);
      fetchAllProfileData();
      showNotification('Skill removed');
    } catch {
      showNotification('Failed to delete', 'error');
    }
  };

  const handleAddProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        ...newProj,
        technicalStack: projectTechTags.join(', '),
      };
      await apiClient.post('/projects', payload);
      setShowModal(null);
      setNewProj({
        name: '',
        description: '',
        technicalStack: '',
        outcomes: '',
        githubUrl: '',
        demoUrl: '',
        category: 'Full Stack',
      });
      setProjectTechTags([]);
      setTechStackQuery('');
      fetchAllProfileData();
      showNotification('Project added!');
    } catch {
      showNotification('Failed to add project', 'error');
    }
  };

  const handleDeleteProject = async (id: string) => {
    try {
      await apiClient.delete(`/projects/${id}`);
      fetchAllProfileData();
      showNotification('Project removed');
    } catch {
      showNotification('Failed to delete', 'error');
    }
  };

  const handleAddCert = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post('/certifications', newCert);
      setShowModal(null);
      setNewCert({
        name: '',
        issuer: '',
        issueDate: '',
        credentialUrl: '',
        description: '',
      });
      fetchAllProfileData();
      showNotification('Certification added!');
    } catch {
      showNotification('Failed to add certification', 'error');
    }
  };

  const handleDeleteCert = async (id: string) => {
    try {
      await apiClient.delete(`/certifications/${id}`);
      fetchAllProfileData();
      showNotification('Certification removed');
    } catch {
      showNotification('Failed to delete', 'error');
    }
  };

  // Filtered skills for combobox
  const filteredCommonSkills = COMMON_SKILLS.filter((s) =>
    s.name.toLowerCase().includes(skillSearchQuery.toLowerCase())
  );

  // Filtered tech stacks for project combobox
  const filteredCommonTech = COMMON_TECH_STACK.filter(
    (t) =>
      t.toLowerCase().includes(techStackQuery.toLowerCase()) &&
      !projectTechTags.includes(t)
  );

  return (
    <div className="space-y-6 pb-12">
      {/* Toast Notification */}
      {statusMsg && (
        <div
          className={`fixed top-20 right-6 z-50 flex items-center gap-2.5 rounded-xl border p-4 shadow-xl backdrop-blur-md transition-all ${
            statusMsg.type === 'success'
              ? 'border-emerald-800 bg-emerald-950/90 text-emerald-200'
              : 'border-rose-800 bg-rose-950/90 text-rose-200'
          }`}
        >
          {statusMsg.type === 'success' ? (
            <CheckCircle2 className="h-5 w-5 text-emerald-400" />
          ) : (
            <AlertCircle className="h-5 w-5 text-rose-400" />
          )}
          <span className="text-sm font-medium">{statusMsg.text}</span>
        </div>
      )}

      {/* Pagehead */}
      <div className="pagehead">
        <div>
          <div className="eyebrow">YOUR SOURCE OF TRUTH</div>
          <h1>Professional profile</h1>
          <p>Keep your career information in one place. Your tailored resumes will only use verified details from here.</p>
        </div>
        <button type="button" className="btn primary" onClick={handleSaveProfile}>
          Save profile
        </button>
      </div>

      {/* Tabs */}
      <div className="flex flex-wrap gap-2 border-b border-[var(--border)] pb-3">
        {[
          { key: 'personal', label: 'Personal & Contact', icon: User },
          { key: 'experience', label: 'Work Experience', icon: Briefcase, count: experiences.length },
          { key: 'education', label: 'Education', icon: GraduationCap, count: educations.length },
          { key: 'skills', label: 'Technical Skills', icon: Wrench, count: userSkills.length },
          { key: 'projects', label: 'Key Projects', icon: FolderGit2, count: projects.length },
          { key: 'certifications', label: 'Certifications', icon: Award, count: certifications.length },
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key as ProfileTab)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                isActive
                  ? 'bg-[var(--accent)] text-white shadow-md shadow-[var(--accent)]/25 font-bold'
                  : 'text-[var(--muted)] hover:text-[var(--text)] hover:bg-slate-100 dark:hover:bg-surface-800/60 border border-[var(--border)]'
              }`}
            >
              <Icon className="h-4 w-4" />
              <span>{tab.label}</span>
              {tab.count !== undefined && (
                <span
                  className={`text-[11px] px-1.5 py-0.2 rounded-full ${
                    isActive ? 'bg-black/20 text-white' : 'bg-slate-200 dark:bg-surface-800 text-[var(--muted)]'
                  }`}
                >
                  {tab.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* TAB 1: Personal Info */}
      {activeTab === 'personal' && (
        <form onSubmit={handleSaveProfile} className="space-y-6 max-w-4xl">
          <div className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold mb-2">Headline & Contact Details</h3>

            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium mb-1.5">
                  Professional Headline
                </label>
                <input
                  type="text"
                  value={profile.professionalHeadline || ''}
                  onChange={(e) => setProfile({ ...profile, professionalHeadline: e.target.value })}
                  placeholder="e.g. Senior Full Stack Engineer | Java & React"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium mb-1.5">
                  Contact Email (Public on Resume)
                </label>
                <input
                  type="email"
                  value={profile.contactEmail || ''}
                  onChange={(e) => setProfile({ ...profile, contactEmail: e.target.value })}
                  placeholder="alex.mercer@example.com"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium mb-1.5">Phone Number</label>
                <input
                  type="text"
                  value={profile.phone || ''}
                  onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                  placeholder="+1 (555) 019-2834"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-medium mb-1.5">City</label>
                  <input
                    type="text"
                    value={profile.city || ''}
                    onChange={(e) => setProfile({ ...profile, city: e.target.value })}
                    placeholder="Seattle"
                    className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1.5">Country</label>
                  <input
                    type="text"
                    value={profile.country || ''}
                    onChange={(e) => setProfile({ ...profile, country: e.target.value })}
                    placeholder="United States"
                    className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium mb-1.5">
                Professional Summary (Master version)
              </label>
              <textarea
                rows={4}
                value={profile.professionalSummary || ''}
                onChange={(e) => setProfile({ ...profile, professionalSummary: e.target.value })}
                placeholder="Write a concise overview of your technical background, specialties, and business impact..."
                className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
              />
            </div>

            {/* Standard 3 Links */}
            <div className="grid sm:grid-cols-3 gap-4 pt-2">
              <div>
                <label className="block text-xs font-medium mb-1.5">LinkedIn Profile</label>
                <input
                  type="url"
                  value={profile.linkedinUrl || ''}
                  onChange={(e) => setProfile({ ...profile, linkedinUrl: e.target.value })}
                  placeholder="https://linkedin.com/in/alex"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1.5">GitHub URL</label>
                <input
                  type="url"
                  value={profile.githubUrl || ''}
                  onChange={(e) => setProfile({ ...profile, githubUrl: e.target.value })}
                  placeholder="https://github.com/alex"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1.5">Portfolio / Website</label>
                <input
                  type="url"
                  value={profile.portfolioUrl || ''}
                  onChange={(e) => setProfile({ ...profile, portfolioUrl: e.target.value })}
                  placeholder="https://alexmercer.dev"
                  className="block w-full rounded-xl border border-[var(--border)] p-2.5 text-sm focus:border-brand-500 focus:outline-none"
                />
              </div>
            </div>

            {/* REQUIREMENT 1: Additional Custom Links (Beyond the 3 standard ones) */}
            <div className="pt-4 border-t border-[var(--border)] space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
                    <LinkIcon className="h-3.5 w-3.5 text-brand-600 dark:text-brand-400" />
                    <span>Additional Professional Links</span>
                  </h4>
                  <p className="text-[11px] text-[var(--muted)]">
                    Add LeetCode, HackerRank, Medium, Substack, personal blog, or code sandbox profiles.
                  </p>
                </div>
              </div>

              {/* Add Custom Link Inputs */}
              <div className="flex flex-col sm:flex-row gap-2">
                <input
                  type="text"
                  value={newLinkLabel}
                  onChange={(e) => setNewLinkLabel(e.target.value)}
                  placeholder="Platform Label (e.g. LeetCode, Blog)"
                  className="sm:w-1/3 rounded-xl border border-[var(--border)] p-2 text-xs focus:border-brand-500 focus:outline-none"
                />
                <input
                  type="text"
                  value={newLinkUrl}
                  onChange={(e) => setNewLinkUrl(e.target.value)}
                  placeholder="URL (e.g. leetcode.com/u/alex)"
                  className="flex-1 rounded-xl border border-[var(--border)] p-2 text-xs focus:border-brand-500 focus:outline-none"
                />
                <button
                  type="button"
                  onClick={handleAddCustomLink}
                  className="rounded-xl bg-slate-800 dark:bg-surface-800 text-white px-4 py-2 text-xs font-semibold hover:bg-slate-700 transition-colors flex items-center justify-center gap-1"
                >
                  <Plus className="h-3.5 w-3.5" />
                  <span>Add Link</span>
                </button>
              </div>

              {/* List of Custom Links */}
              {customLinks.length > 0 && (
                <div className="flex flex-wrap gap-2 pt-1">
                  {customLinks.map((link) => (
                    <div
                      key={link.id}
                      className="inline-flex items-center gap-2 rounded-xl border border-[var(--border)] bg-slate-50 dark:bg-surface-950 px-3 py-1.5 text-xs group"
                    >
                      <span className="font-semibold text-slate-900 dark:text-white">{link.label}:</span>
                      <a
                        href={link.url}
                        target="_blank"
                        rel="noreferrer"
                        className="text-brand-600 dark:text-brand-400 truncate max-w-[160px] hover:underline flex items-center gap-1"
                      >
                        <span>{link.url}</span>
                        <ExternalLink className="h-3 w-3" />
                      </a>
                      <button
                        type="button"
                        onClick={() => handleDeleteCustomLink(link.id)}
                        className="text-slate-400 hover:text-rose-500 ml-1"
                        title="Remove link"
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              className="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-6 py-2.5 text-sm font-semibold text-white shadow-lg shadow-brand-600/30 hover:bg-brand-500 transition-all"
            >
              <Save className="h-4 w-4" />
              <span>Save Personal Info</span>
            </button>
          </div>
        </form>
      )}

      {/* TAB 2: Experience */}
      {activeTab === 'experience' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold">Work Experience ({experiences.length})</h3>
            <button
              onClick={() => setShowModal('experience')}
              className="inline-flex items-center gap-1.5 rounded-xl bg-brand-600 px-3.5 py-2 text-xs font-semibold text-white hover:bg-brand-500 transition-all"
            >
              <Plus className="h-4 w-4" />
              <span>Add Experience</span>
            </button>
          </div>

          {experiences.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-[var(--border)] p-8 text-center text-[var(--muted)]">
              No work experience added yet. Add your employment history to enrich ATS matching.
            </div>
          ) : (
            <div className="space-y-3">
              {experiences.map((exp) => (
                <div
                  key={exp.id}
                  className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 flex flex-col justify-between"
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <h4 className="text-base font-bold">
                        {exp.jobTitle} — <span className="text-brand-600 dark:text-brand-400">{exp.employer}</span>
                      </h4>
                      <p className="text-xs text-[var(--muted)] mt-0.5">
                        {exp.startDate} – {exp.currentRole ? 'Present' : exp.endDate}{' '}
                        {exp.location && `• ${exp.location}`}
                      </p>
                    </div>
                    <button
                      onClick={() => handleDeleteExperience(exp.id)}
                      className="text-slate-400 hover:text-rose-500 p-1.5 transition-colors"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                  {exp.description && (
                    <p className="mt-2 text-xs text-[var(--text)] leading-relaxed">{exp.description}</p>
                  )}
                  {exp.technologies && (
                    <div className="mt-3 flex flex-wrap gap-1.5">
                      {exp.technologies.split(',').map((t, idx) => (
                        <span
                          key={idx}
                          className="px-2 py-0.5 rounded text-[11px] bg-slate-100 dark:bg-surface-800 text-[var(--muted)] border border-[var(--border)]"
                        >
                          {t.trim()}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: Education */}
      {activeTab === 'education' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold">Education ({educations.length})</h3>
              <p className="text-xs text-[var(--muted)]">
                Degree records with optional CGPA / grade visibility control.
              </p>
            </div>
            <button
              onClick={() => setShowModal('education')}
              className="inline-flex items-center gap-1.5 rounded-xl bg-brand-600 px-3.5 py-2 text-xs font-semibold text-white hover:bg-brand-500 transition-all"
            >
              <Plus className="h-4 w-4" />
              <span>Add Education</span>
            </button>
          </div>

          {educations.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-[var(--border)] p-8 text-center text-[var(--muted)]">
              No education records listed. Add your university, bootcamp, or degrees.
            </div>
          ) : (
            <div className="space-y-3">
              {educations.map((edu) => (
                <div
                  key={edu.id}
                  className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 flex items-start justify-between"
                >
                  <div className="space-y-1">
                    <h4 className="text-base font-bold">
                      {edu.degree} {edu.fieldOfStudy && `in ${edu.fieldOfStudy}`}
                    </h4>
                    <p className="text-sm text-brand-600 dark:text-brand-400 font-medium">{edu.institution}</p>
                    <p className="text-xs text-[var(--muted)]">
                      {edu.startDate} – {edu.currentlyStudying ? 'Present' : edu.endDate}
                    </p>

                    {/* REQUIREMENT 2: CGPA Display with Show/Hide Indicator */}
                    {edu.gradeValue && (
                      <div className="pt-1 flex items-center gap-2">
                        <span className="text-xs font-semibold px-2.5 py-0.5 rounded-lg bg-slate-100 dark:bg-surface-800 border border-[var(--border)]">
                          {edu.gradeType || 'CGPA'}: {edu.gradeValue}
                        </span>
                        {edu.showGradeOnResume !== false ? (
                          <span className="inline-flex items-center gap-1 text-[11px] text-emerald-600 dark:text-emerald-400 font-medium">
                            <Eye className="h-3 w-3" />
                            <span>Shown on resume</span>
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 text-[11px] text-slate-400 font-medium">
                            <EyeOff className="h-3 w-3" />
                            <span>Hidden from resume</span>
                          </span>
                        )}
                      </div>
                    )}
                  </div>
                  <button
                    onClick={() => handleDeleteEducation(edu.id)}
                    className="text-slate-400 hover:text-rose-500 p-1.5 transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 4: Skills */}
      {activeTab === 'skills' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold">Technical & Professional Skills ({userSkills.length})</h3>
              <p className="text-xs text-[var(--muted)]">
                Autocomplete common technologies or type any custom skill.
              </p>
            </div>
            <button
              onClick={() => {
                setSkillSearchQuery('');
                setIsSkillDropdownOpen(false);
                setShowModal('skill');
              }}
              className="inline-flex items-center gap-1.5 rounded-xl bg-brand-600 px-3.5 py-2 text-xs font-semibold text-white hover:bg-brand-500 transition-all"
            >
              <Plus className="h-4 w-4" />
              <span>Add Skill</span>
            </button>
          </div>

          {userSkills.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-[var(--border)] p-8 text-center text-[var(--muted)]">
              No skills added yet. Add at least 5 skills to achieve strong ATS match ratings.
            </div>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
              {userSkills.map((s) => (
                <div
                  key={s.id}
                  className="rounded-xl border border-[var(--border)] bg-[var(--surface)] p-3 flex items-center justify-between group hover:border-brand-500/40 transition-all"
                >
                  <div>
                    <p className="text-sm font-semibold">{s.skillName}</p>
                    <div className="flex items-center gap-1.5 mt-0.5">
                      <span className="text-[10px] text-brand-600 dark:text-brand-400 font-medium">{s.category}</span>
                      {s.yearsExperience && (
                        <span className="text-[10px] text-[var(--muted)]">• {s.yearsExperience}y</span>
                      )}
                    </div>
                  </div>
                  <button
                    onClick={() => handleDeleteSkill(s.id)}
                    className="text-slate-400 hover:text-rose-500 p-1 transition-colors"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 5: Projects */}
      {activeTab === 'projects' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold">Key Technical Projects ({projects.length})</h3>
              <p className="text-xs text-[var(--muted)]">
                Showcase your applications with multi-select technology tags and outcomes.
              </p>
            </div>
            <button
              onClick={() => {
                setProjectTechTags([]);
                setTechStackQuery('');
                setShowModal('project');
              }}
              className="inline-flex items-center gap-1.5 rounded-xl bg-brand-600 px-3.5 py-2 text-xs font-semibold text-white hover:bg-brand-500 transition-all"
            >
              <Plus className="h-4 w-4" />
              <span>Add Project</span>
            </button>
          </div>

          {projects.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-[var(--border)] p-8 text-center text-[var(--muted)]">
              No projects added yet. Showcase your coding repositories and architectures.
            </div>
          ) : (
            <div className="grid sm:grid-cols-2 gap-4">
              {projects.map((proj) => (
                <div
                  key={proj.id}
                  className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-start justify-between">
                      <h4 className="text-base font-bold">{proj.name}</h4>
                      <button
                        onClick={() => handleDeleteProject(proj.id)}
                        className="text-slate-400 hover:text-rose-500 p-1"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                    {proj.technicalStack && (
                      <div className="flex flex-wrap gap-1 mt-2">
                        {proj.technicalStack.split(',').map((tech, idx) => (
                          <span
                            key={idx}
                            className="px-2 py-0.5 rounded text-[10px] bg-brand-50 dark:bg-brand-950 text-brand-600 dark:text-brand-400 border border-brand-200 dark:border-brand-800 font-semibold"
                          >
                            {tech.trim()}
                          </span>
                        ))}
                      </div>
                    )}
                    {proj.description && (
                      <p className="mt-2 text-xs text-[var(--text)] leading-relaxed">{proj.description}</p>
                    )}
                    {proj.outcomes && (
                      <p className="mt-2 text-xs text-emerald-600 dark:text-emerald-400 font-medium">Impact: {proj.outcomes}</p>
                    )}
                  </div>
                  {(proj.githubUrl || proj.demoUrl) && (
                    <div className="mt-4 pt-3 border-t border-[var(--border)] flex gap-3">
                      {proj.githubUrl && (
                        <a
                          href={proj.githubUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="text-xs text-[var(--muted)] hover:text-brand-600 flex items-center gap-1"
                        >
                          <ExternalLink className="h-3 w-3" />
                          <span>GitHub</span>
                        </a>
                      )}
                      {proj.demoUrl && (
                        <a
                          href={proj.demoUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="text-xs text-[var(--muted)] hover:text-brand-600 flex items-center gap-1"
                        >
                          <ExternalLink className="h-3 w-3" />
                          <span>Live Demo</span>
                        </a>
                      )}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 6: Certifications */}
      {activeTab === 'certifications' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold">Certifications & Licenses ({certifications.length})</h3>
            <button
              onClick={() => setShowModal('cert')}
              className="inline-flex items-center gap-1.5 rounded-xl bg-brand-600 px-3.5 py-2 text-xs font-semibold text-white hover:bg-brand-500 transition-all"
            >
              <Plus className="h-4 w-4" />
              <span>Add Certification</span>
            </button>
          </div>

          {certifications.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-[var(--border)] p-8 text-center text-[var(--muted)]">
              No certifications on record. Add AWS, Oracle, Google Cloud or other verified credentials.
            </div>
          ) : (
            <div className="grid sm:grid-cols-2 gap-4">
              {certifications.map((c) => (
                <div
                  key={c.id}
                  className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 flex items-start justify-between"
                >
                  <div>
                    <h4 className="text-base font-bold">{c.name}</h4>
                    <p className="text-sm text-brand-600 dark:text-brand-400 font-medium">{c.issuer}</p>
                    {c.issueDate && <p className="text-xs text-[var(--muted)] mt-0.5">Issued: {c.issueDate}</p>}
                    {c.credentialUrl && (
                      <a
                        href={c.credentialUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="mt-2 text-xs text-brand-600 dark:text-brand-400 flex items-center gap-1 hover:underline"
                      >
                        <ExternalLink className="h-3 w-3" />
                        <span>Verify Credential</span>
                      </a>
                    )}
                  </div>
                  <button
                    onClick={() => handleDeleteCert(c.id)}
                    className="text-slate-400 hover:text-rose-500 p-1.5"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ========================================================
          MODALS
         ======================================================== */}

      {/* MODAL: Add Experience */}
      {showModal === 'experience' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="w-full max-w-lg rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-2xl">
            <h3 className="text-lg font-bold mb-4">Add Work Experience</h3>
            <form onSubmit={handleAddExperience} className="space-y-3">
              <div>
                <label className="block text-xs font-medium mb-1">Job Title</label>
                <input
                  type="text"
                  required
                  value={newExp.jobTitle}
                  onChange={(e) => setNewExp({ ...newExp, jobTitle: e.target.value })}
                  placeholder="e.g. Senior Software Engineer"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium mb-1">Company / Employer</label>
                  <input
                    type="text"
                    required
                    value={newExp.employer}
                    onChange={(e) => setNewExp({ ...newExp, employer: e.target.value })}
                    placeholder="e.g. Acme Corp"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1">Location</label>
                  <input
                    type="text"
                    value={newExp.location}
                    onChange={(e) => setNewExp({ ...newExp, location: e.target.value })}
                    placeholder="New York, NY"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium mb-1">Start Date</label>
                  <input
                    type="text"
                    required
                    value={newExp.startDate}
                    onChange={(e) => setNewExp({ ...newExp, startDate: e.target.value })}
                    placeholder="2021-06"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1">End Date</label>
                  <input
                    type="text"
                    disabled={newExp.currentRole}
                    value={newExp.currentRole ? 'Present' : newExp.endDate}
                    onChange={(e) => setNewExp({ ...newExp, endDate: e.target.value })}
                    placeholder="2023-12"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs disabled:opacity-50 focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="currentRoleCheck"
                  checked={newExp.currentRole}
                  onChange={(e) => setNewExp({ ...newExp, currentRole: e.target.checked })}
                  className="rounded text-brand-600 focus:ring-brand-500"
                />
                <label htmlFor="currentRoleCheck" className="text-xs text-[var(--muted)]">
                  I currently work in this role
                </label>
              </div>
              <div>
                <label className="block text-xs font-medium mb-1">Technologies Used (comma separated)</label>
                <input
                  type="text"
                  value={newExp.technologies}
                  onChange={(e) => setNewExp({ ...newExp, technologies: e.target.value })}
                  placeholder="Java 21, Spring Boot, React, AWS, Docker"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1">Role Description / Achievements</label>
                <textarea
                  rows={3}
                  value={newExp.description}
                  onChange={(e) => setNewExp({ ...newExp, description: e.target.value })}
                  placeholder="Describe your architectural responsibilities, leadership, and outcomes..."
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(null)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-[var(--muted)] hover:text-[var(--text)]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500"
                >
                  Save Experience
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* REQUIREMENT 2 MODAL: Add Education with CGPA & Show/Hide Toggle */}
      {showModal === 'education' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="w-full max-w-lg rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-2xl">
            <h3 className="text-lg font-bold mb-4">Add Education</h3>
            <form onSubmit={handleAddEducation} className="space-y-3.5">
              <div>
                <label className="block text-xs font-medium mb-1">Institution</label>
                <input
                  type="text"
                  required
                  value={newEdu.institution}
                  onChange={(e) => setNewEdu({ ...newEdu, institution: e.target.value })}
                  placeholder="e.g. Stanford University or MIT"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium mb-1">Degree</label>
                  <input
                    type="text"
                    required
                    value={newEdu.degree}
                    onChange={(e) => setNewEdu({ ...newEdu, degree: e.target.value })}
                    placeholder="e.g. Bachelor of Technology (B.Tech)"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1">Field of Study</label>
                  <input
                    type="text"
                    value={newEdu.fieldOfStudy}
                    onChange={(e) => setNewEdu({ ...newEdu, fieldOfStudy: e.target.value })}
                    placeholder="Computer Science & Engineering"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium mb-1">Start Year</label>
                  <input
                    type="text"
                    value={newEdu.startDate}
                    onChange={(e) => setNewEdu({ ...newEdu, startDate: e.target.value })}
                    placeholder="2018"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1">End Year</label>
                  <input
                    type="text"
                    value={newEdu.endDate}
                    onChange={(e) => setNewEdu({ ...newEdu, endDate: e.target.value })}
                    placeholder="2022"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* CGPA / Grade Input Box with Show / Hide Option */}
              <div className="p-3.5 rounded-xl border border-slate-200 dark:border-surface-700 bg-slate-50 dark:bg-surface-950 space-y-2.5">
                <span className="text-xs font-bold text-slate-800 dark:text-slate-200 block">
                  Academic Performance (CGPA / Grade)
                </span>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-[11px] text-[var(--muted)] mb-1">Grade Type</label>
                    <select
                      value={newEdu.gradeType}
                      onChange={(e) => setNewEdu({ ...newEdu, gradeType: e.target.value })}
                      className="w-full rounded-lg border border-[var(--border)] p-2 text-xs focus:outline-none"
                    >
                      <option value="CGPA">CGPA (out of 10)</option>
                      <option value="GPA (4.0 Scale)">GPA (4.0 Scale)</option>
                      <option value="Percentage">Percentage (%)</option>
                      <option value="Grade">Letter Grade (A+, A)</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-[11px] text-[var(--muted)] mb-1">Score / CGPA Value</label>
                    <input
                      type="text"
                      value={newEdu.gradeValue}
                      onChange={(e) => setNewEdu({ ...newEdu, gradeValue: e.target.value })}
                      placeholder="e.g. 8.85 / 10 or 3.9"
                      className="w-full rounded-lg border border-[var(--border)] p-2 text-xs focus:outline-none"
                    />
                  </div>
                </div>

                {/* Show/Hide Toggle Checkbox */}
                <div className="flex items-center gap-2 pt-1">
                  <input
                    type="checkbox"
                    id="showGradeCheckbox"
                    checked={newEdu.showGradeOnResume}
                    onChange={(e) => setNewEdu({ ...newEdu, showGradeOnResume: e.target.checked })}
                    className="rounded text-brand-600 focus:ring-brand-500"
                  />
                  <label htmlFor="showGradeCheckbox" className="text-xs text-slate-700 dark:text-slate-300 font-medium cursor-pointer">
                    Display this CGPA / Grade on exported resumes
                  </label>
                </div>
                <p className="text-[10px] text-[var(--muted)]">
                  If unchecked, your CGPA remains saved in your profile but will be hidden on your resume.
                </p>
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(null)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-[var(--muted)] hover:text-[var(--text)]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500"
                >
                  Save Education
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* REQUIREMENT 4 MODAL: Add Skill with Interactive Filter Dropdown & Custom Add */}
      {showModal === 'skill' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-2xl">
            <h3 className="text-lg font-bold mb-1">Add Technical Skill</h3>
            <p className="text-xs text-[var(--muted)] mb-4">
              Type the first letter to filter popular skills or add your own custom skill.
            </p>

            <form onSubmit={handleAddSkill} className="space-y-3.5">
              {/* Combobox Search Input */}
              <div ref={skillComboboxRef} className="relative">
                <label className="block text-xs font-medium mb-1">Skill Name</label>
                <div className="relative">
                  <input
                    type="text"
                    required
                    value={skillSearchQuery}
                    onFocus={() => setIsSkillDropdownOpen(true)}
                    onChange={(e) => {
                      setSkillSearchQuery(e.target.value);
                      setNewSkill({ ...newSkill, skillName: e.target.value });
                      setIsSkillDropdownOpen(true);
                    }}
                    placeholder="Type first letter (e.g. J for Java, P for Python...)"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 pr-8 text-xs focus:border-brand-500 focus:outline-none"
                  />
                  <ChevronDown className="absolute right-2.5 top-3 h-4 w-4 text-slate-400 pointer-events-none" />
                </div>

                {/* Dropdown Options */}
                {isSkillDropdownOpen && (
                  <div
                    onMouseDown={(e) => e.preventDefault()}
                    className="absolute top-full left-0 right-0 mt-1 max-h-56 overflow-y-auto rounded-xl border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 shadow-xl z-50 p-1"
                  >
                    {/* Add Custom Skill Option */}
                    {skillSearchQuery.trim() && (
                      <button
                        type="button"
                        onClick={() => {
                          setNewSkill({ ...newSkill, skillName: skillSearchQuery.trim() });
                          setIsSkillDropdownOpen(false);
                        }}
                        className="w-full text-left px-3 py-2 rounded-lg text-xs font-semibold text-brand-600 dark:text-brand-400 hover:bg-brand-50 dark:hover:bg-brand-950 flex items-center gap-1.5"
                      >
                        <Plus className="h-3.5 w-3.5" />
                        <span>Add "{skillSearchQuery.trim()}" as custom skill</span>
                      </button>
                    )}

                    {filteredCommonSkills.map((sk) => (
                      <button
                        key={sk.name}
                        type="button"
                        onClick={() => {
                          setSkillSearchQuery(sk.name);
                          setNewSkill({
                            ...newSkill,
                            skillName: sk.name,
                            category: sk.category,
                          });
                          setIsSkillDropdownOpen(false);
                        }}
                        className="w-full text-left px-3 py-2 rounded-lg text-xs hover:bg-slate-100 dark:hover:bg-surface-800 flex items-center justify-between"
                      >
                        <span className="font-semibold text-slate-800 dark:text-slate-200">{sk.name}</span>
                        <span className="text-[10px] text-[var(--muted)]">{sk.category}</span>
                      </button>
                    ))}

                    {filteredCommonSkills.length === 0 && !skillSearchQuery.trim() && (
                      <div className="p-3 text-center text-xs text-[var(--muted)]">
                        Start typing to search skills
                      </div>
                    )}
                  </div>
                )}
              </div>

              <div>
                <label className="block text-xs font-medium mb-1">Category</label>
                <select
                  value={newSkill.category}
                  onChange={(e) => setNewSkill({ ...newSkill, category: e.target.value as any })}
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:outline-none"
                >
                  <option value="BACKEND">Backend</option>
                  <option value="FRONTEND">Frontend</option>
                  <option value="PROGRAMMING_LANGUAGE">Language</option>
                  <option value="DATABASE">Database</option>
                  <option value="CLOUD_DEVOPS">Cloud / DevOps</option>
                  <option value="DEV_TOOLS">Dev Tools</option>
                  <option value="SOFT_SKILL">Soft Skill</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium mb-1">Years of Experience</label>
                <input
                  type="number"
                  min="0"
                  max="40"
                  value={newSkill.yearsExperience}
                  onChange={(e) => setNewSkill({ ...newSkill, yearsExperience: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(null)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-[var(--muted)] hover:text-[var(--text)]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500"
                >
                  Add Skill
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* REQUIREMENT 3 MODAL: Add Project with Tech Stack Combobox / Tag Manager */}
      {showModal === 'project' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="w-full max-w-lg rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-bold mb-1">Add Key Project</h3>
            <p className="text-xs text-[var(--muted)] mb-4">
              Select technologies from the dropdown or type custom tools used in your build.
            </p>

            <form onSubmit={handleAddProject} className="space-y-3.5">
              <div>
                <label className="block text-xs font-medium mb-1">Project Name</label>
                <input
                  type="text"
                  required
                  value={newProj.name}
                  onChange={(e) => setNewProj({ ...newProj, name: e.target.value })}
                  placeholder="e.g. Distributed Order Engine"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>

              {/* Tech Stack Combobox with Multi-Select Tags */}
              <div ref={techComboboxRef} className="space-y-2">
                <label className="block text-xs font-medium">Technical Stack</label>

                {/* Selected Tags */}
                {projectTechTags.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 p-2 rounded-xl border border-slate-200 dark:border-surface-700 bg-slate-50 dark:bg-surface-950">
                    {projectTechTags.map((tag) => (
                      <span
                        key={tag}
                        className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-lg text-xs font-semibold bg-brand-600 text-white"
                      >
                        <span>{tag}</span>
                        <button
                          type="button"
                          onClick={() => setProjectTechTags(projectTechTags.filter((t) => t !== tag))}
                          className="hover:text-black/60"
                        >
                          <X className="h-3 w-3" />
                        </button>
                      </span>
                    ))}
                  </div>
                )}

                {/* Search input with Dropdown */}
                <div className="relative">
                  <input
                    type="text"
                    value={techStackQuery}
                    onFocus={() => setIsTechDropdownOpen(true)}
                    onChange={(e) => {
                      setTechStackQuery(e.target.value);
                      setIsTechDropdownOpen(true);
                    }}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        if (techStackQuery.trim() && !projectTechTags.includes(techStackQuery.trim())) {
                          setProjectTechTags([...projectTechTags, techStackQuery.trim()]);
                          setTechStackQuery('');
                        }
                      }
                    }}
                    placeholder="Select from dropdown or type custom tool & press Enter..."
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 pr-8 text-xs focus:border-brand-500 focus:outline-none"
                  />
                  <ChevronDown className="absolute right-2.5 top-3 h-4 w-4 text-slate-400 pointer-events-none" />

                  {/* Dropdown list */}
                  {isTechDropdownOpen && (
                    <div
                      onMouseDown={(e) => e.preventDefault()}
                      className="absolute top-full left-0 right-0 mt-1 max-h-48 overflow-y-auto rounded-xl border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 shadow-xl z-50 p-1"
                    >
                      {techStackQuery.trim() && !projectTechTags.includes(techStackQuery.trim()) && (
                        <button
                          type="button"
                          onClick={() => {
                            setProjectTechTags([...projectTechTags, techStackQuery.trim()]);
                            setTechStackQuery('');
                            setIsTechDropdownOpen(false);
                          }}
                          className="w-full text-left px-3 py-2 rounded-lg text-xs font-semibold text-brand-600 dark:text-brand-400 hover:bg-brand-50 dark:hover:bg-brand-950 flex items-center gap-1"
                        >
                          <Plus className="h-3.5 w-3.5" />
                          <span>Add "{techStackQuery.trim()}" to stack</span>
                        </button>
                      )}

                      {filteredCommonTech.map((tech) => (
                        <button
                          key={tech}
                          type="button"
                          onClick={() => {
                            setProjectTechTags([...projectTechTags, tech]);
                            setTechStackQuery('');
                            setIsTechDropdownOpen(false);
                          }}
                          className="w-full text-left px-3 py-1.5 rounded-lg text-xs hover:bg-slate-100 dark:hover:bg-surface-800 text-slate-800 dark:text-slate-200"
                        >
                          {tech}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium mb-1">Description</label>
                <textarea
                  rows={2}
                  value={newProj.description}
                  onChange={(e) => setNewProj({ ...newProj, description: e.target.value })}
                  placeholder="Architected end-to-end event-driven service with asynchronous processing..."
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium mb-1">Measurable Impact / Outcomes</label>
                <input
                  type="text"
                  value={newProj.outcomes}
                  onChange={(e) => setNewProj({ ...newProj, outcomes: e.target.value })}
                  placeholder="Handled 25,000 req/sec with 99.99% uptime"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium mb-1">GitHub Repo URL</label>
                  <input
                    type="url"
                    value={newProj.githubUrl}
                    onChange={(e) => setNewProj({ ...newProj, githubUrl: e.target.value })}
                    placeholder="https://github.com/..."
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium mb-1">Live Demo / App URL</label>
                  <input
                    type="url"
                    value={newProj.demoUrl}
                    onChange={(e) => setNewProj({ ...newProj, demoUrl: e.target.value })}
                    placeholder="https://demo.app.com"
                    className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(null)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-[var(--muted)] hover:text-[var(--text)]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500"
                >
                  Save Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: Add Certification */}
      {showModal === 'cert' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-6 shadow-2xl">
            <h3 className="text-lg font-bold mb-4">Add Certification</h3>
            <form onSubmit={handleAddCert} className="space-y-3">
              <div>
                <label className="block text-xs font-medium mb-1">Certification Name</label>
                <input
                  type="text"
                  required
                  value={newCert.name}
                  onChange={(e) => setNewCert({ ...newCert, name: e.target.value })}
                  placeholder="AWS Certified Solutions Architect"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1">Issuing Organization</label>
                <input
                  type="text"
                  required
                  value={newCert.issuer}
                  onChange={(e) => setNewCert({ ...newCert, issuer: e.target.value })}
                  placeholder="Amazon Web Services"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1">Issue Date</label>
                <input
                  type="text"
                  value={newCert.issueDate}
                  onChange={(e) => setNewCert({ ...newCert, issueDate: e.target.value })}
                  placeholder="2023-08"
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium mb-1">Credential URL</label>
                <input
                  type="url"
                  value={newCert.credentialUrl}
                  onChange={(e) => setNewCert({ ...newCert, credentialUrl: e.target.value })}
                  placeholder="https://credly.com/..."
                  className="w-full rounded-xl border border-[var(--border)] p-2.5 text-xs focus:border-brand-500 focus:outline-none"
                />
              </div>
              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(null)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-[var(--muted)] hover:text-[var(--text)]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500"
                >
                  Save Certification
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
