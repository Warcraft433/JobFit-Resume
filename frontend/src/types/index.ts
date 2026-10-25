// JobFit Resume — Global TypeScript Interfaces

export interface User {
  id: string;
  fullName: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  error?: {
    code: string;
    details: string;
    validationErrors?: Record<string, string>;
  };
  timestamp: string;
}

// Profile types
export interface UserProfile {
  id: string;
  userId: string;
  professionalHeadline: string | null;
  contactEmail: string | null;
  phone: string | null;
  city: string | null;
  country: string | null;
  professionalSummary: string | null;
  linkedinUrl: string | null;
  githubUrl: string | null;
  portfolioUrl: string | null;
  customLinks?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Education {
  id: string;
  userId: string;
  institution: string;
  degree: string;
  fieldOfStudy: string | null;
  startDate: string | null;
  endDate: string | null;
  currentlyStudying: boolean;
  gradeType: string | null;
  gradeValue: string | null;
  showGradeOnResume?: boolean;
  coursework: string | null;
  description: string | null;
  displayOrder: number;
}

export type SkillCategory =
  | 'PROGRAMMING_LANGUAGE'
  | 'BACKEND'
  | 'FRONTEND'
  | 'DATABASE'
  | 'FRAMEWORK_LIBRARY'
  | 'CLOUD_DEVOPS'
  | 'DEV_TOOLS'
  | 'SOFT_SKILL'
  | 'OTHER';

export interface UserSkill {
  id: string;
  userId: string;
  skillId?: string | null;
  skillName: string;
  canonicalName?: string;
  category: SkillCategory;
  proficiency?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  yearsExperience?: number;
  notes?: string;
}

export interface Experience {
  id: string;
  userId: string;
  employer: string;
  jobTitle: string;
  location: string | null;
  startDate: string | null;
  endDate: string | null;
  currentRole: boolean;
  description: string | null;
  achievements: string | null;
  technologies: string | null;
  displayOrder: number;
}

export interface Project {
  id: string;
  userId: string;
  name: string;
  description: string | null;
  problemStatement: string | null;
  technicalStack: string | null;
  responsibilities: string | null;
  outcomes: string | null;
  githubUrl: string | null;
  demoUrl: string | null;
  startDate: string | null;
  endDate: string | null;
  category: string | null;
  featured: boolean;
  displayOrder: number;
}

export interface Certification {
  id: string;
  userId: string;
  name: string;
  issuer: string;
  issueDate: string | null;
  expirationDate: string | null;
  credentialId: string | null;
  credentialUrl: string | null;
  description: string | null;
}

export interface Achievement {
  id: string;
  userId: string;
  title: string;
  issuer: string | null;
  achievementDate: string | null;
  description: string | null;
  url: string | null;
}

export interface ProfileCompleteness {
  score: number; // 0 - 100
  sections: {
    personalInfo: boolean;
    education: boolean;
    skills: boolean;
    experience: boolean;
    projects: boolean;
    certifications: boolean;
  };
  recommendations: string[];
}

// Job & Analysis types
export interface Job {
  id: string;
  userId: string;
  company: string;
  jobTitle: string;
  location: string | null;
  jobDescription: string;
  jobUrl: string | null;
  salaryText: string | null;
  savedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface RequirementMatch {
  requirementText: string;
  category: string;
  importance: 'REQUIRED' | 'PREFERRED' | 'NICE_TO_HAVE';
  matched: boolean;
  evidenceText?: string;
  evidenceSource?: 'SKILL' | 'EXPERIENCE' | 'PROJECT' | 'EDUCATION';
}

export interface JobAnalysisResult {
  jobId: string;
  jobTitle: string;
  company: string;
  overallScore: number; // 0 - 100
  scoreBreakdown: {
    technicalSkillsScore: number;
    experienceScore: number;
    educationScore: number;
    keywordScore: number;
  };
  extractedSkills: string[];
  matchedSkills: string[];
  missingSkills: string[];
  extractedExperienceYears?: string;
  extractedDegrees?: string[];
  requirements: RequirementMatch[];
  improvementSuggestions: string[];
}

export interface LearningTopic {
  skillName: string;
  category: string;
  summary: string;
  keyConcepts: string[];
  suggestedAction: string;
}

// Resume types
export type ResumeTemplateKey = 'classic' | 'modern' | 'compact';

export interface ResumeContent {
  headline: string;
  contact: {
    fullName: string;
    email: string;
    phone?: string;
    location?: string;
    linkedinUrl?: string;
    githubUrl?: string;
    portfolioUrl?: string;
    customLinks?: string;
  };
  summary: string;
  selectedSkills: {
    category: string;
    skills: string[];
  }[];
  selectedExperience: {
    employer: string;
    jobTitle: string;
    location?: string;
    startDate?: string;
    endDate?: string;
    currentRole: boolean;
    responsibilities: string[];
    technologies?: string[];
  }[];
  selectedProjects: {
    name: string;
    description?: string;
    technicalStack?: string;
    outcomes?: string;
    githubUrl?: string;
    demoUrl?: string;
  }[];
  selectedEducation: {
    institution: string;
    degree: string;
    fieldOfStudy?: string;
    startDate?: string;
    endDate?: string;
    grade?: string;
    showGrade?: boolean;
  }[];
  selectedCertifications: {
    name: string;
    issuer: string;
    issueDate?: string;
  }[];
  visibleSections: {
    summary: boolean;
    skills: boolean;
    experience: boolean;
    projects: boolean;
    education: boolean;
    certifications: boolean;
  };
  sectionOrder: string[];
}

export interface Resume {
  id: string;
  userId: string;
  jobId: string | null;
  title: string;
  templateKey: ResumeTemplateKey;
  createdAt: string;
  updatedAt: string;
  currentVersion?: ResumeVersion;
}

export interface ResumeVersion {
  id: string;
  resumeId: string;
  versionNumber: number;
  content: ResumeContent;
  createdAt: string;
}

// Application Tracker types
export type ApplicationStatus =
  | 'SAVED'
  | 'PREPARING'
  | 'APPLIED'
  | 'ASSESSMENT'
  | 'INTERVIEW'
  | 'OFFER'
  | 'REJECTED'
  | 'WITHDRAWN';

export interface JobApplication {
  id: string;
  userId: string;
  jobId: string;
  job?: Job;
  resumeVersionId: string | null;
  resumeVersion?: ResumeVersion;
  status: ApplicationStatus;
  appliedAt: string | null;
  followUpAt: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  profileCompleteness: number;
  totalSavedJobs: number;
  applicationsByStatus: Record<ApplicationStatus, number>;
  totalApplications: number;
  recentApplications: JobApplication[];
  recentResumes: Resume[];
}

export type Application = JobApplication;

export interface AiAssistantRequest {
  prompt: string;
  targetJobId?: string;
  currentSummary?: string;
  tone?: 'professional' | 'concise' | 'warm';
  action?: 'GENERAL' | 'POLISH_SUMMARY' | 'SHORTEN_SUMMARY' | 'TAILOR_SKILLS' | 'DRAFT_EMAIL' | 'GAP_ANALYSIS';
}

export interface AiAssistantResponse {
  reply: string;
  action: string;
  modifiedSummary?: string;
  emailSubject?: string;
  emailBody?: string;
  suggestedSkills?: string[];
  missingSkills?: string[];
}
