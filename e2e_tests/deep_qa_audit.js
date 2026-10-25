const axios = require('../frontend/node_modules/axios');
const fs = require('fs');
const path = require('path');
const { PDFParse } = require('../frontend/node_modules/pdf-parse');

const BASE_API = 'http://localhost:8080/api/v1';
const OUTPUT_DIR = path.join(__dirname, 'audit_artifacts');
if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });

const auditReport = {
  timestamp: new Date().toISOString(),
  sections: {},
  defects: [],
  warnings: [],
  observations: [],
};

function addDefect(id, title, severity, category, description, reproduction) {
  auditReport.defects.push({ id, title, severity, category, description, reproduction });
  console.log(`[DEFECT - ${severity}] ${id}: ${title}`);
}

function addWarning(id, title, category, description) {
  auditReport.warnings.push({ id, title, category, description });
  console.log(`[WARNING] ${id}: ${title}`);
}

async function runDeepAudit() {
  console.log('================================================================');
  console.log(' STARTING PHASE 2: DEEP QUALITY AUDIT & TEST VALIDATION');
  console.log('================================================================\n');

  // -------------------------------------------------------------------------
  // 1. INSPECT PREVIOUS TEST RUN ASSERTIONS
  // -------------------------------------------------------------------------
  console.log('>>> Section 1: Previous Test Suite Assertions Audit');
  const prevRun = JSON.parse(fs.readFileSync(path.join(__dirname, 'reports/test_run_report.json'), 'utf8'));

  // Audit test P1-01
  const tP101 = prevRun.tests.find(t => t.id === 'P1-01');
  if (tP101 && tP101.details.includes('"frontend"')) {
    addDefect('AUD-01', 'Page title is generic default "frontend"', 'LOW', 'UI/Branding',
      'The browser page title in index.html is literally "frontend" instead of "JobFit Resume — ATS Resume Builder". The assertion pageTitle.length > 0 was a weak assertion that masked this.',
      'Open http://localhost:3000 and observe browser tab title.');
  }

  // Audit test P5-02
  addWarning('AUD-02', 'Template switch test has false-positive prone assertion', 'Test Suite Quality',
    'P5-02 asserts true immediately after clicking the template button without asserting that the preview DOM re-rendered with the new template layout or CSS classes.');

  // Audit P5-03 / P5-04
  addWarning('AUD-03', 'Document export tests only validated file size and magic bytes', 'Test Suite Quality',
    'P5-03 and P5-04 only verified that the download occurred and file size was > 2000 bytes. They did not parse document text, verify section presence, check layout clipping, or test all 3 templates.');

  // -------------------------------------------------------------------------
  // 2. HEALTH & HTTP API VALIDATION
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 2: Backend and Frontend API Error Handling');
  const userAEmail = `audit.user.a.${Date.now()}@jobfit.test`;
  const regResA = await axios.post(`${BASE_API}/auth/register`, {
    fullName: 'Alex Rivera Audit',
    email: userAEmail,
    password: 'Password123!',
  });
  const tokenA = regResA.data.data.token;
  const clientA = axios.create({
    baseURL: BASE_API,
    headers: { Authorization: `Bearer ${tokenA}` },
  });

  // Populate User A Master Profile
  await clientA.put('/profile', {
    professionalHeadline: 'Lead Full Stack Architect | Java 21 & React',
    contactEmail: 'alex.rivera@leadarchitect.io',
    phone: '+1 (555) 789-0123',
    city: 'San Francisco',
    country: 'United States',
    professionalSummary: 'Accomplished software engineer with 6+ years of expertise in high-concurrency Java microservices, PostgreSQL query tuning, and modern TypeScript single-page web applications.',
    linkedinUrl: 'https://linkedin.com/in/alex-rivera-architect',
    githubUrl: 'https://github.com/alexrivera-architect',
    portfolioUrl: 'https://alexrivera-architect.dev',
  });

  await clientA.post('/experience', {
    employer: 'Datadog Platforms',
    jobTitle: 'Principal Backend Engineer',
    location: 'San Francisco, CA',
    startDate: '2021-03',
    endDate: 'Present',
    currentRole: true,
    description: 'Spearheaded migration of legacy services to Java 21 virtual threads, increasing throughput by 240%.',
    achievements: '• Designed distributed cache reducing database reads by 60%\n• Mentored 12 junior and mid-level engineers',
    technologies: 'Java 21, Spring Boot, PostgreSQL, Docker, Kubernetes, Redis',
  });

  await clientA.post('/education', {
    institution: 'UC Berkeley',
    degree: 'B.S. in Electrical Engineering & Computer Sciences',
    fieldOfStudy: 'Computer Science',
    startDate: '2016',
    endDate: '2020',
    currentlyStudying: false,
    gradeValue: '3.92 GPA',
  });

  const skillsToPost = [
    { skillName: 'Java', category: 'BACKEND', proficiency: 'EXPERT', yearsExperience: 6 },
    { skillName: 'Spring Boot', category: 'BACKEND', proficiency: 'EXPERT', yearsExperience: 5 },
    { skillName: 'PostgreSQL', category: 'DATABASE', proficiency: 'ADVANCED', yearsExperience: 5 },
    { skillName: 'Docker', category: 'CLOUD_DEVOPS', proficiency: 'ADVANCED', yearsExperience: 4 },
    { skillName: 'React', category: 'FRONTEND', proficiency: 'INTERMEDIATE', yearsExperience: 3 },
    { skillName: 'TypeScript', category: 'FRONTEND', proficiency: 'INTERMEDIATE', yearsExperience: 3 },
  ];
  for (const s of skillsToPost) {
    await clientA.post('/profile/skills', s);
  }

  await clientA.post('/projects', {
    name: 'EventMesh Stream Gateway',
    description: 'High-throughput event gateway handling 50k events/sec with Apache Kafka and Spring Boot.',
    technicalStack: 'Java 21, Spring Boot 3, Kafka, PostgreSQL, Docker',
    outcomes: 'Zero message loss across 99.999% SLA benchmark.',
    githubUrl: 'https://github.com/alexrivera-architect/eventmesh-gateway',
    category: 'Backend',
  });

  // -------------------------------------------------------------------------
  // 3. JOB ANALYZER ACCURACY EXPERIMENTS
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 3: Job Analyzer NLP Accuracy Experiments');

  // Test 3.1: Java vs JavaScript distinction
  const jobJsOnly = await clientA.post('/jobs', {
    company: 'Frontend Studio',
    jobTitle: 'Senior Frontend Developer',
    jobDescription: 'Requirements: 4+ years of professional JavaScript and TypeScript development. React and CSS mastery. Do not apply if you only know Python.',
  });
  const anJsRes = await clientA.post(`/jobs/${jobJsOnly.data.data.id}/analyze`);
  const jsMatched = anJsRes.data.data.matchedSkills;

  if (jsMatched.includes('Java')) {
    addDefect('AUD-05', 'False Positive: Java matched when vacancy only asked for JavaScript', 'HIGH', 'Matching Accuracy',
      'The word "JavaScript" triggered a match for "Java". Candidate who knows Java is falsely credited for JavaScript vacancy.',
      'Analyze a vacancy mentioning only "JavaScript".');
  } else {
    console.log('✓ Java vs JavaScript distinction: PASS (JavaScript does not falsely trigger Java)');
  }

  // Test 3.2: Spring Boot alias variations
  const jobAliases = await clientA.post('/jobs', {
    company: 'FinTech Cloud',
    jobTitle: 'Cloud Engineer',
    jobDescription: 'Seeking developer with SpringBoot, spring-boot 3, and postgres experience.',
  });
  const anAliasRes = await clientA.post(`/jobs/${jobAliases.data.data.id}/analyze`);
  const aliasMatched = anAliasRes.data.data.matchedSkills;
  const recognizesSpringBoot = aliasMatched.includes('Spring Boot');
  const recognizesPostgres = aliasMatched.includes('PostgreSQL');
  if (!recognizesSpringBoot || !recognizesPostgres) {
    addDefect('AUD-06', 'Alias recognition failed for SpringBoot or postgres', 'MEDIUM', 'Matching Accuracy',
      `Expected Spring Boot and PostgreSQL. Got: ${JSON.stringify(aliasMatched)}`,
      'Submit job with "SpringBoot" and "postgres".');
  } else {
    console.log('✓ Spring Boot & PostgreSQL alias recognition: PASS (SpringBoot -> Spring Boot, postgres -> PostgreSQL)');
  }

  // Test 3.3: Experience calculation heuristic check
  addDefect('AUD-07', 'Candidate experience heuristic uses arbitrary (roles * 2) instead of date calculation', 'MEDIUM', 'Matching Accuracy',
    'JobMatchingService.calculateCandidateExperienceYears() computes candidate years as totalRoles * 2. It ignores actual start and end dates. A single 10-year job counts as 2 years; two 2-month summer internships count as 4 years.',
    'Inspect JobMatchingService.java line 186.');

  // Test 3.4: Score calculation consistency check
  const breakdown = anAliasRes.data.data.scoreBreakdown;
  const computedSum = Math.round((breakdown.skills + breakdown.experience + breakdown.education + breakdown.projectsKeywords) * 10) / 10;
  const reportedScore = anAliasRes.data.data.matchScore;
  if (Math.abs(computedSum - reportedScore) > 0.1) {
    addDefect('AUD-08', 'Score mismatch: Overall score does not equal sum of breakdown categories', 'MEDIUM', 'Scoring Consistency',
      `Reported: ${reportedScore}, Sum: ${computedSum}`,
      'Check scoreBreakdown vs matchScore in JobAnalysisResponse.');
  } else {
    console.log(`✓ Scoring Consistency: PASS (Overall ${reportedScore}% exactly matches category breakdown sum ${computedSum}%)`);
  }

  // -------------------------------------------------------------------------
  // 4. DEEP RESUME EXPORT VALIDATION ACROSS ALL 3 TEMPLATES
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 4: Deep Resume Export Validation across All 3 Templates');

  const targetJob = await clientA.post('/jobs', {
    company: 'Stripe Horizon',
    jobTitle: 'Senior Java Full Stack Engineer',
    jobDescription: `We need a Senior Java Full Stack Engineer to lead payments architecture.
Required Qualifications:
- 5+ years of Java, Spring Boot, and REST API development.
- Strong knowledge of PostgreSQL database indexing and transactions.
- Experience with Docker, Kubernetes, and AWS cloud services.
- Proficiency with React and TypeScript frontend development.`,
  });
  const jobId = targetJob.data.data.id;
  await clientA.post(`/jobs/${jobId}/analyze`);

  const templates = ['classic', 'modern', 'technical'];

  for (const t of templates) {
    console.log(`\nTesting Template: "${t.toUpperCase()}"...`);
    const genRes = await clientA.post(`/jobs/${jobId}/generate-resume`, { templateKey: t });
    const version = genRes.data.data;
    const versionId = version.id;

    // Export PDF
    const pdfRes = await clientA.get(`/resume-versions/${versionId}/export/pdf`, { responseType: 'arraybuffer' });
    const pdfBuffer = Buffer.from(pdfRes.data);
    const pdfFilePath = path.join(OUTPUT_DIR, `resume_${t}.pdf`);
    fs.writeFileSync(pdfFilePath, pdfBuffer);

    // Parse PDF
    const pdfParser = new PDFParse({ data: pdfBuffer });
    await pdfParser.load();
    const pdfText = await pdfParser.getText();
    const pdfFullStr = pdfText.text || '';
    await pdfParser.destroy();

    const hasName = pdfFullStr.includes('Alex Rivera');
    const hasCompany = pdfFullStr.includes('Datadog Platforms');
    const hasEdu = pdfFullStr.includes('UC Berkeley');
    const hasProject = pdfFullStr.includes('EventMesh Stream Gateway');
    const hasSkills = pdfFullStr.includes('Java') && pdfFullStr.includes('Spring Boot');

    console.log(`  PDF Size: ${pdfBuffer.length} bytes`);
    console.log(`  PDF Contains: Name (${hasName}), Company (${hasCompany}), Education (${hasEdu}), Project (${hasProject}), Skills (${hasSkills})`);

    // Detect layout bug: Float-to-top layout glitch
    const lines = pdfFullStr.split('\n').filter(l => l.trim().length > 0);
    const firstFiveLines = lines.slice(0, 5).join(' ');
    if (firstFiveLines.includes('Present') || firstFiveLines.includes('2020') || firstFiveLines.includes('Docker')) {
      addDefect(`AUD-09-${t}`, `PDF Template "${t}": Dates and metadata float to the top of the text stream`, 'HIGH', 'Document Generation',
        `In OpenHTMLtoPDF rendering, CSS float: right or flex emulation causes dates ("Present", "2016-2020") or tech tags to be emitted in the PDF text stream before the candidate name and header. First 5 lines: "${firstFiveLines}"`,
        `Export PDF for template "${t}" and extract text using PDF reader/parser.`);
    }

    // Export DOCX
    const docxRes = await clientA.get(`/resume-versions/${versionId}/export/docx`, { responseType: 'arraybuffer' });
    const docxBuffer = Buffer.from(docxRes.data);
    const docxFilePath = path.join(OUTPUT_DIR, `resume_${t}.docx`);
    fs.writeFileSync(docxFilePath, docxBuffer);
    const isDocxZip = docxBuffer.slice(0, 2).toString() === 'PK';
    console.log(`  DOCX Size: ${docxBuffer.length} bytes (Valid ZIP header: ${isDocxZip})`);
  }

  // -------------------------------------------------------------------------
  // 5. SNAPSHOT IMMUTABILITY TESTING
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 5: Resume Snapshot Immutability Audit');
  const resumeList = await clientA.get('/resumes');
  const userResume = resumeList.data.data[0];
  const versionsBefore = await clientA.get(`/resumes/${userResume.id}/versions`);
  const initialVersion = versionsBefore.data.data[0];

  await clientA.put('/profile', {
    professionalHeadline: 'MUTATED HEADLINE 999',
    professionalSummary: 'MUTATED SUMMARY - Should NEVER appear in previously saved resume versions!',
  });

  const versionAfter = await clientA.get(`/resume-versions/${initialVersion.id}`);
  const preserved = versionAfter.data.data.contentJson.professionalSummary !== 'MUTATED SUMMARY - Should NEVER appear in previously saved resume versions!';
  if (preserved) {
    console.log('✓ Snapshot Immutability: PASS (Profile edits do not overwrite previously generated resume snapshots)');
  } else {
    addDefect('AUD-10', 'Snapshot Leakage: Modifying profile altered historical resume version', 'CRITICAL', 'Data Integrity',
      'Historical resume versions must be immutable snapshots. Modifying the user profile caused previously exported versions to change.',
      'Generate a resume version, edit profile summary, re-fetch historical version.');
  }

  // -------------------------------------------------------------------------
  // 6. SECURITY & DATA ISOLATION (IDOR CHECKS)
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 6: Security and Insecure Direct Object Reference (IDOR) Audit');
  const userBEmail = `audit.user.b.${Date.now()}@jobfit.test`;
  const regResB = await axios.post(`${BASE_API}/auth/register`, {
    fullName: 'Sarah Attacker',
    email: userBEmail,
    password: 'Password123!',
  });
  const tokenB = regResB.data.data.token;
  const clientB = axios.create({
    baseURL: BASE_API,
    headers: { Authorization: `Bearer ${tokenB}` },
  });

  // Test IDOR 1: User B tries to read User A's job
  try {
    await clientB.get(`/jobs/${jobId}`);
    addDefect('AUD-11', 'IDOR Vulnerability: User B can read User A job details', 'CRITICAL', 'Security',
      `User B called GET /jobs/${jobId} and received User A job data.`,
      `GET /jobs/{other_user_job_id} using another user token.`);
  } catch (err) {
    if (err.response && (err.response.status === 404 || err.response.status === 403)) {
      console.log('✓ IDOR Check 1: PASS (User B cannot access User A job -> 404/403)');
    }
  }

  // Test IDOR 2: User B tries to read User A's resume version
  try {
    await clientB.get(`/resume-versions/${initialVersion.id}`);
    addDefect('AUD-12', 'IDOR Vulnerability: User B can read User A resume version snapshot', 'CRITICAL', 'Security',
      `User B called GET /resume-versions/${initialVersion.id} and received User A private resume data.`,
      `GET /resume-versions/{other_user_version_id} with another user token.`);
  } catch (err) {
    if (err.response && (err.response.status === 404 || err.response.status === 403)) {
      console.log('✓ IDOR Check 2: PASS (User B cannot read User A resume version -> 404/403)');
    }
  }

  // Test IDOR 3: User B tries to download User A's resume PDF
  try {
    await clientB.get(`/resume-versions/${initialVersion.id}/export/pdf`);
    addDefect('AUD-13', 'IDOR Vulnerability: User B can download User A resume PDF', 'CRITICAL', 'Security',
      `User B called GET /resume-versions/${initialVersion.id}/export/pdf and downloaded User A document.`,
      `GET /resume-versions/{other_user_version_id}/export/pdf.`);
  } catch (err) {
    if (err.response && (err.response.status === 404 || err.response.status === 403)) {
      console.log('✓ IDOR Check 3: PASS (User B cannot download User A PDF -> 404/403)');
    }
  }

  // Test Unauthenticated Access to protected endpoint
  try {
    await axios.get(`${BASE_API}/profile`);
    addDefect('AUD-14', 'Security Vulnerability: /profile accessible without token', 'CRITICAL', 'Security',
      'GET /profile did not reject unauthenticated request with 401.',
      'GET /profile without Authorization header.');
  } catch (err) {
    if (err.response && err.response.status === 401) {
      console.log('✓ Unauthenticated Route Check: PASS (GET /profile returns 401 Unauthorized)');
    }
  }

  // -------------------------------------------------------------------------
  // 7. DATABASE PERSISTENCE & ORPHANED RECORD AUDIT
  // -------------------------------------------------------------------------
  console.log('\n>>> Section 7: Database Persistence & Referential Integrity Audit');
  const appRes = await clientA.post('/applications', {
    jobId: jobId,
    status: 'APPLIED',
    notes: 'Audit application linked to resume',
  });
  const appId = appRes.data.data.id;

  const dashBefore = await clientA.get('/applications/analytics');
  await clientA.delete(`/applications/${appId}`);
  const dashAfter = await clientA.get('/applications/analytics');
  if (dashAfter.data.data.totalApplications === dashBefore.data.data.totalApplications - 1) {
    console.log('✓ Dashboard Metrics Synchronization: PASS (Dashboard counts decrement immediately upon application deletion)');
  }

  // Write detailed audit JSON
  const auditJsonPath = path.join(OUTPUT_DIR, 'deep_quality_audit_results.json');
  fs.writeFileSync(auditJsonPath, JSON.stringify(auditReport, null, 2));

  console.log('\n================================================================');
  console.log(` AUDIT SUMMARY: ${auditReport.defects.length} DEFECTS FOUND, ${auditReport.warnings.length} WARNINGS IDENTIFIED`);
  console.log(` Audit JSON written to: ${auditJsonPath}`);
  console.log('================================================================\n');

  return auditReport;
}

runDeepAudit().catch(err => {
  console.error('Audit fatal error:', err);
  process.exit(1);
});
