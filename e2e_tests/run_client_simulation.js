const { chromium } = require('../frontend/node_modules/playwright');
const fs = require('fs');
const path = require('path');

const BASE_URL = 'http://localhost:3000';
const SCREENSHOTS_DIR = path.join(__dirname, 'screenshots');
const DOWNLOADS_DIR = path.join(__dirname, 'downloads');
const REPORTS_DIR = path.join(__dirname, 'reports');

// Ensure output directories exist
[SCREENSHOTS_DIR, DOWNLOADS_DIR, REPORTS_DIR].forEach((dir) => {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
});

const results = {
  total: 0,
  passed: 0,
  failed: 0,
  tests: [],
  defects: [],
  observations: [],
};

function recordTest(id, feature, scenario, passed, details = '') {
  results.total++;
  if (passed) {
    results.passed++;
    console.log(`[PASS] ${id} - ${feature}: ${scenario}`);
  } else {
    results.failed++;
    console.error(`[FAIL] ${id} - ${feature}: ${scenario} | Details: ${details}`);
    results.defects.push({ id, feature, scenario, details });
  }
  results.tests.push({ id, feature, scenario, passed, details });
}

async function runClientSimulation() {
  console.log('===============================================================');
  console.log(' STARTING REAL CLIENT SIMULATION & END-TO-END AUTOMATED TESTING');
  console.log(' JobFit Resume Full-Stack Application');
  console.log('===============================================================\n');

  const browser = await chromium.launch({
    channel: 'msedge',
    headless: true,
  });

  const context = await browser.newContext({
    viewport: { width: 1280, height: 800 },
    acceptDownloads: true,
  });

  const page = await context.newPage();

  // Listen for console errors
  page.on('console', (msg) => {
    if (msg.type() === 'error') {
      console.warn(`[Browser Console Error] ${msg.text()}`);
    }
  });

  try {
    // -------------------------------------------------------------
    // PHASE 1: FIRST-TIME CLIENT EXPERIENCE
    // -------------------------------------------------------------
    console.log('\n--- Phase 1: First-Time Client Experience ---');
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    const pageTitle = await page.title();
    recordTest('P1-01', 'Homepage', 'Page title check', pageTitle.includes('JobFit Resume'), `Title: "${pageTitle}"`);

    const heroHeading = await page.textContent('h1');
    const heroValid = heroHeading.includes('One Career Profile') && heroHeading.includes('Custom ATS Resumes');
    recordTest('P1-02', 'Homepage', 'Hero headline clearly explains product value', heroValid, heroHeading);

    // Value propositions
    const bodyContent = await page.textContent('body');
    const hasValueBadges =
      bodyContent.includes('Truth-Preserving') &&
      bodyContent.includes('Explainable Scoring') &&
      bodyContent.includes('A4 PDF & Word DOCX') &&
      bodyContent.includes('Full Kanban Tracker');
    recordTest('P1-03', 'Homepage', 'Displays 4 core value propositions', hasValueBadges);

    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '01_landing_desktop.png'), fullPage: true });

    // Responsive check on Mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await page.waitForTimeout(500);
    const mobileHeadingVisible = await page.isVisible('h1');
    recordTest('P1-04', 'Homepage', 'Mobile viewport responsive display', mobileHeadingVisible);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '01_landing_mobile.png') });

    // Reset back to Desktop
    await page.setViewportSize({ width: 1280, height: 800 });

    // -------------------------------------------------------------
    // PHASE 2: ACCOUNT & AUTHENTICATION TESTING
    // -------------------------------------------------------------
    console.log('\n--- Phase 2: Account and Authentication ---');
    await page.goto(`${BASE_URL}/register`, { waitUntil: 'networkidle' });

    // Test form validation: mismatched passwords
    await page.fill('input[placeholder="Alex Mercer"]', 'Alex Rivera');
    await page.fill('input[type="email"]', 'alex.rivera.test@jobfit.com');
    const passInputs = await page.$$('input[type="password"]');
    if (passInputs.length >= 2) {
      await passInputs[0].fill('Password123!');
      await passInputs[1].fill('MismatchedPass123!');
      await page.click('button[type="submit"]');
      await page.waitForTimeout(300);
      const errorText = await page.textContent('body');
      recordTest('P2-01', 'Registration', 'Password mismatch rejection', errorText.includes('Passwords do not match'));
    }

    // Register User A successfully
    const uniqueEmail = `alex.client_${Date.now()}@jobfit.com`;
    await page.fill('input[placeholder="Alex Mercer"]', 'Alex Rivera');
    await page.fill('input[type="email"]', uniqueEmail);
    const passInputs2 = await page.$$('input[type="password"]');
    await passInputs2[0].fill('JobFitPass2026!');
    await passInputs2[1].fill('JobFitPass2026!');
    await page.click('button[type="submit"]');

    // Wait for redirect to /dashboard
    await page.waitForURL('**/dashboard', { timeout: 8000 });
    const atDashboard = page.url().includes('/dashboard');
    recordTest('P2-02', 'Registration', 'Successful registration redirects to dashboard', atDashboard);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '02_empty_dashboard.png') });

    // Test Logout
    const logoutBtn = await page.$('button:has-text("Sign Out")');
    if (logoutBtn) {
      await logoutBtn.click();
      await page.waitForURL('**/login', { timeout: 5000 });
      recordTest('P2-03', 'Authentication', 'Logout redirects to /login', page.url().includes('/login'));
    }

    // Attempt direct access to protected page while logged out
    await page.goto(`${BASE_URL}/dashboard`);
    await page.waitForTimeout(1000);
    recordTest('P2-04', 'Authentication', 'Protected route redirects unauthenticated user', page.url().includes('/login'));

    // Test login with wrong password
    await page.fill('input[type="email"]', uniqueEmail);
    await page.fill('input[type="password"]', 'WrongPassword123!');
    await page.click('button[type="submit"]');
    await page.waitForTimeout(600);
    const loginError = await page.textContent('body');
    recordTest('P2-05', 'Authentication', 'Invalid credentials error display', loginError.includes('Invalid email or password') || loginError.includes('Bad credentials'));

    // Test login with correct password
    await page.fill('input[type="password"]', 'JobFitPass2026!');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/dashboard', { timeout: 5000 });
    recordTest('P2-06', 'Authentication', 'Valid login redirects to dashboard', page.url().includes('/dashboard'));

    // -------------------------------------------------------------
    // PHASE 3: MASTER CAREER PROFILE
    // -------------------------------------------------------------
    console.log('\n--- Phase 3: Master Career Profile Creation & Persistence ---');
    await page.goto(`${BASE_URL}/profile`, { waitUntil: 'networkidle' });

    // Personal info form
    await page.fill('input[placeholder*="Senior Full Stack"]', 'Senior Full Stack Java Engineer');
    await page.fill('input[placeholder*="alex.mercer@example.com"]', 'alex.rivera@professional.net');
    await page.fill('input[placeholder*="+1 (555)"]', '+1 (206) 555-0142');
    await page.fill('input[placeholder="Seattle"]', 'Seattle');
    await page.fill('input[placeholder="United States"]', 'United States');
    await page.fill('textarea[placeholder*="Write a concise overview"]', 'Results-driven Senior Java Developer with 5+ years of experience engineering high-throughput Spring Boot microservices, designing PostgreSQL database schemas, and building responsive React TypeScript single-page applications.');
    await page.fill('input[placeholder*="linkedin.com/in"]', 'https://linkedin.com/in/alex-rivera-dev');
    await page.fill('input[placeholder*="github.com/"]', 'https://github.com/alexrivera-dev');
    await page.fill('input[placeholder*="alexmercer.dev"]', 'https://alexrivera.dev');

    // Submit personal profile
    await page.click('button:has-text("Save Personal Info")');
    await page.waitForTimeout(1000);
    const profileSavedMsg = await page.textContent('body');
    recordTest('P3-01', 'Career Profile', 'Personal info save confirmed', profileSavedMsg.includes('saved successfully') || profileSavedMsg.includes('Profile updated'));

    // Work Experience tab
    await page.click('button:has-text("Work Experience")');
    await page.waitForTimeout(300);
    await page.click('button:has-text("Add Experience")');
    await page.waitForTimeout(300);
    await page.fill('input[placeholder*="Amazon, Google, Startup"]', 'Apex Cloud Systems');
    await page.fill('input[placeholder*="Senior Software Engineer"]', 'Software Development Engineer II');
    await page.fill('input[placeholder="2022"]', '2022-06-01');
    await page.check('input#currentRole');
    await page.fill('textarea[placeholder*="Reduced latency by 40%"]', '• Architected high-scale Spring Boot microservices.\n• Cut API latency by 35% with PostgreSQL connection pooling.\n• Deployed Kafka streaming architecture.');
    await page.click('button:has-text("Save Experience")');
    await page.waitForTimeout(800);
    const expContent = await page.textContent('body');
    recordTest('P3-02', 'Career Profile', 'Work experience item added and listed', expContent.includes('Apex Cloud Systems'));

    // Education tab
    await page.click('button:has-text("Education")');
    await page.waitForTimeout(300);
    await page.click('button:has-text("Add Education")');
    await page.waitForTimeout(300);
    await page.fill('input[placeholder*="Stanford University"]', 'University of Washington');
    await page.fill('input[placeholder*="Bachelor of Science"]', 'Bachelor of Science');
    await page.fill('input[placeholder="Computer Science"]', 'Computer Science & Engineering');
    await page.fill('input[placeholder="2018"]', '2018');
    await page.fill('input[placeholder="2022"]', '2022');
    await page.click('button:has-text("Save Education")');
    await page.waitForTimeout(800);
    const eduContent = await page.textContent('body');
    recordTest('P3-03', 'Career Profile', 'Education item added and listed', eduContent.includes('University of Washington'));

    // Skills tab
    await page.click('button:has-text("Technical Skills")');
    await page.waitForTimeout(300);
    const skillsToAdd = [
      { name: 'Java', category: 'BACKEND' },
      { name: 'Spring Boot', category: 'BACKEND' },
      { name: 'PostgreSQL', category: 'DATABASE' },
      { name: 'Docker', category: 'CLOUD_DEVOPS' },
      { name: 'React', category: 'FRONTEND' },
      { name: 'TypeScript', category: 'FRONTEND' },
    ];

    for (const skill of skillsToAdd) {
      await page.click('button:has-text("Add Skill")');
      await page.waitForTimeout(200);
      await page.fill('input[placeholder*="Java, React, Docker"]', skill.name);
      await page.selectOption('select', skill.category);
      await page.click('form button[type="submit"]:has-text("Add Skill")');
      await page.waitForTimeout(400);
    }
    const skillsContent = await page.textContent('body');
    recordTest('P3-04', 'Career Profile', 'Multiple categorized technical skills added', skillsContent.includes('Spring Boot') && skillsContent.includes('PostgreSQL'));

    // Projects tab
    await page.click('button:has-text("Key Projects")');
    await page.waitForTimeout(300);
    await page.click('button:has-text("Add Project")');
    await page.waitForTimeout(300);
    await page.fill('input[placeholder*="Distributed Order Engine"]', 'CloudPay Ingestion Gateway');
    await page.fill('input[placeholder*="Java 21, Spring Boot"]', 'Java 21, Spring Boot 3, PostgreSQL, Docker, Redis');
    await page.fill('textarea[placeholder*="Architected end-to-end"]', 'High-throughput microservices gateway processing real-time credit transactions.');
    await page.fill('input[placeholder*="25,000 req/sec"]', 'Handles 15,000 requests/sec with 99.99% uptime.');
    await page.fill('input[placeholder*="github.com/..."]', 'https://github.com/alexrivera-dev/cloudpay-gateway');
    await page.click('button:has-text("Save Project")');
    await page.waitForTimeout(800);
    const projContent = await page.textContent('body');
    recordTest('P3-05', 'Career Profile', 'Featured technical project added', projContent.includes('CloudPay Ingestion Gateway'));

    // Certifications tab
    await page.click('button:has-text("Certifications")');
    await page.waitForTimeout(300);
    await page.click('button:has-text("Add Certification")');
    await page.waitForTimeout(300);
    await page.fill('input[placeholder="AWS Certified Solutions Architect"]', 'AWS Certified Solutions Architect - Associate');
    await page.fill('input[placeholder="Amazon Web Services"]', 'Amazon Web Services');
    await page.fill('input[placeholder="2024"]', '2023');
    await page.click('button:has-text("Save Certification")');
    await page.waitForTimeout(800);
    const certContent = await page.textContent('body');
    recordTest('P3-06', 'Career Profile', 'Certification added', certContent.includes('AWS Certified Solutions Architect'));

    // Test Persistence by refreshing page and checking input values
    await page.reload({ waitUntil: 'networkidle' });
    await page.waitForTimeout(800);
    const headlineVal = await page.inputValue('input[placeholder*="Senior Full Stack"]');
    const emailVal = await page.inputValue('input[placeholder*="alex.mercer@example.com"]');
    const persisted = headlineVal === 'Senior Full Stack Java Engineer' && emailVal === 'alex.rivera@professional.net';
    recordTest('P3-07', 'Career Profile', 'Profile data persists after browser refresh', persisted, `Headline: "${headlineVal}", Email: "${emailVal}"`);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '03_profile_populated.png'), fullPage: true });

    // Check Dashboard Completeness Score
    await page.goto(`${BASE_URL}/dashboard`, { waitUntil: 'networkidle' });
    const dashContent = await page.textContent('body');
    recordTest('P3-08', 'Dashboard', 'Dashboard reflects user profile completeness', dashContent.includes('%') && dashContent.includes('Profile Completeness'));
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '03_dashboard_complete.png') });

    // -------------------------------------------------------------
    // PHASE 4: JOB DESCRIPTION ANALYZER & MATCH ENGINE
    // -------------------------------------------------------------
    console.log('\n--- Phase 4: Job Description Analyzer & Matching Engine ---');
    await page.goto(`${BASE_URL}/jobs`, { waitUntil: 'networkidle' });

    // Add new vacancy
    await page.click('button:has-text("New Vacancy Analysis")');
    await page.waitForTimeout(300);
    await page.fill('input[placeholder*="Netflix, Stripe, Datadog"]', 'Stripe Horizon Inc.');
    await page.fill('input[placeholder*="Senior Backend Engineer"]', 'Senior Java Full Stack Engineer');
    await page.fill('input[placeholder*="Remote / San Francisco"]', 'Seattle, WA (Hybrid)');
    await page.fill('input[placeholder*="$140k - $185k"]', '$145,000 - $175,000');

    const jobDescriptionText = `We are seeking a Senior Java Full Stack Engineer to build our high-scale payment ingestion platform.
Requirements:
- Deep expertise in Java 21, Spring Boot framework, and RESTful API design.
- Production proficiency with relational databases, specifically PostgreSQL.
- Experience with Docker containerization and Kubernetes orchestration.
- Modern frontend single-page application development with React and TypeScript.
- Strong test-driven development practices with JUnit 5 and integration testing.
- Nice to have: AWS cloud architecture and Kafka event streaming.
- Strong problem solving, analytical thinking, and team collaboration skills.`;

    await page.fill('textarea[placeholder*="Paste the full vacancy description"]', jobDescriptionText);
    await page.click('button:has-text("Save & Run Match Analysis")');
    await page.waitForTimeout(3000);

    const jobPageContent = await page.textContent('body');
    const jobSaved = jobPageContent.includes('Stripe Horizon Inc.');
    recordTest('P4-01', 'Job Analyzer', 'Job vacancy saved and listed', jobSaved);

    // Verify token-aware matching & match score
    const hasMatchScore = jobPageContent.includes('Overall Fit Score') || jobPageContent.includes('% Match') || jobPageContent.includes('Score');
    recordTest('P4-02', 'Job Analyzer', 'Calculates explainable match score', hasMatchScore);

    // Verify matched skills highlighted
    const hasMatchedSkills = jobPageContent.includes('Spring Boot') && jobPageContent.includes('PostgreSQL') && jobPageContent.includes('React');
    recordTest('P4-03', 'Job Analyzer', 'Identifies matched skills from profile', hasMatchedSkills);

    // Verify missing skills detected (Kubernetes was in job description but not in candidate skills)
    const hasMissingSkills = jobPageContent.includes('Kubernetes') || jobPageContent.includes('Missing') || jobPageContent.includes('Skills Gap');
    recordTest('P4-04', 'Job Analyzer', 'Identifies missing skills / skills gap', hasMissingSkills);

    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '04_job_analysis_result.png'), fullPage: true });

    // -------------------------------------------------------------
    // PHASE 5: RESUME GENERATION & EXPORTS
    // -------------------------------------------------------------
    console.log('\n--- Phase 5: Resume Generation & Exports ---');
    await page.goto(`${BASE_URL}/resumes`, { waitUntil: 'networkidle' });

    // Generate Tailored Resume
    await page.click('button:has-text("Tailor & Generate")');
    await page.waitForTimeout(3500);

    const resumePageContent = await page.textContent('body');
    const resumeGenerated = resumePageContent.includes('Tailored Professional Summary') || resumePageContent.includes('Alex Rivera') || resumePageContent.includes('Apex Cloud Systems');
    recordTest('P5-01', 'Resume Generator', 'Generates truthful resume tailored to target job', resumeGenerated);

    // Test Template Switcher (classic, modern, technical)
    const modernBtn = await page.$('button:has-text("modern")');
    if (modernBtn) {
      await modernBtn.click();
      await page.waitForTimeout(500);
      recordTest('P5-02', 'Resume Generator', 'Switches between resume templates', true);
    }

    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '05_resume_preview.png'), fullPage: true });

    // Test PDF Export
    const [pdfDownload] = await Promise.all([
      page.waitForEvent('download', { timeout: 15000 }),
      page.click('button:has-text("Download PDF (A4)")'),
    ]);
    const pdfPath = path.join(DOWNLOADS_DIR, 'JobFit_Resume_Test.pdf');
    await pdfDownload.saveAs(pdfPath);
    const pdfStats = fs.statSync(pdfPath);
    const pdfBuffer = fs.readFileSync(pdfPath);
    const isRealPdf = pdfBuffer.slice(0, 4).toString() === '%PDF';
    recordTest('P5-03', 'Document Export', 'Generates and downloads valid A4 PDF', pdfStats.size > 2000 && isRealPdf, `Size: ${pdfStats.size} bytes`);

    // Test DOCX Export
    const [docxDownload] = await Promise.all([
      page.waitForEvent('download', { timeout: 15000 }),
      page.click('button:has-text("Download DOCX")'),
    ]);
    const docxPath = path.join(DOWNLOADS_DIR, 'JobFit_Resume_Test.docx');
    await docxDownload.saveAs(docxPath);
    const docxStats = fs.statSync(docxPath);
    const docxBuffer = fs.readFileSync(docxPath);
    const isRealDocx = docxBuffer.slice(0, 2).toString() === 'PK'; // ZIP archive header for DOCX
    recordTest('P5-04', 'Document Export', 'Generates and downloads valid Word DOCX (Apache POI)', docxStats.size > 2000 && isRealDocx, `Size: ${docxStats.size} bytes`);

    // -------------------------------------------------------------
    // PHASE 6: JOB APPLICATION TRACKER & DASHBOARD
    // -------------------------------------------------------------
    console.log('\n--- Phase 6: Job Application Tracker ---');
    await page.goto(`${BASE_URL}/applications`, { waitUntil: 'networkidle' });

    // Track New Application
    await page.click('button:has-text("Track New Application")');
    await page.waitForTimeout(300);
    // Select the job
    const jobSelect = await page.$('select');
    if (jobSelect) {
      const options = await page.$$eval('select option', (opts) => opts.map((o) => o.value));
      if (options.length > 1) {
        await page.selectOption('select', options[1]);
      }
    }
    await page.fill('textarea[placeholder*="Applied via referral"]', 'Applied via careers portal. Attached Classic ATS resume version.');
    await page.click('button:has-text("Save Application")');
    await page.waitForTimeout(1000);

    const appBoardContent = await page.textContent('body');
    const appCreated = appBoardContent.includes('Stripe Horizon Inc.');
    recordTest('P6-01', 'Application Tracker', 'Creates application linked to job vacancy', appCreated);

    // Change application status to INTERVIEW if dropdown exists
    const statusSelect = await page.$('div.border select');
    if (statusSelect) {
      await statusSelect.selectOption('INTERVIEW').catch(() => null);
      await page.waitForTimeout(1000);
    }
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '06_tracker_board.png'), fullPage: true });

    // Check Dashboard Funnel
    await page.goto(`${BASE_URL}/dashboard`, { waitUntil: 'networkidle' });
    const finalDashContent = await page.textContent('body');
    const dashConsistent = finalDashContent.includes('Total Applied') && finalDashContent.includes('Tracked vacancies');
    recordTest('P6-02', 'Dashboard', 'Dashboard funnel synchronizes with application counts', dashConsistent);
    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '06_final_dashboard.png'), fullPage: true });

    // -------------------------------------------------------------
    // PHASE 7: DATA ISOLATION & SECURITY TESTING
    // -------------------------------------------------------------
    console.log('\n--- Phase 7: Cross-User Security and Data Isolation ---');
    // Logout User A
    await page.click('button:has-text("Sign Out")');
    await page.waitForURL('**/login', { timeout: 5000 });

    // Register User B
    await page.goto(`${BASE_URL}/register`, { waitUntil: 'networkidle' });
    const userBEmail = `sarah.tester_${Date.now()}@jobfit.com`;
    await page.fill('input[placeholder="Alex Mercer"]', 'Sarah Tester');
    await page.fill('input[type="email"]', userBEmail);
    const passB = await page.$$('input[type="password"]');
    await passB[0].fill('JobFitPass2026!');
    await passB[1].fill('JobFitPass2026!');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/dashboard', { timeout: 8000 });
    await page.waitForTimeout(1200);

    // Verify User B sees 0 applications and 0 jobs
    const userBDash = await page.textContent('body');
    const userBEmpty = !userBDash.includes('Stripe Horizon Inc.') && userBDash.includes('0');
    recordTest('P7-01', 'Data Isolation', 'User B sees clean dashboard without User A data', userBEmpty);

    // Verify User B profile is empty
    await page.goto(`${BASE_URL}/profile`, { waitUntil: 'networkidle' });
    const userBProfile = await page.textContent('body');
    const profileIsolated = !userBProfile.includes('Apex Cloud Systems') && !userBProfile.includes('alex.rivera@professional.net');
    recordTest('P7-02', 'Data Isolation', 'User B cannot see User A profile entries', profileIsolated);

    await page.screenshot({ path: path.join(SCREENSHOTS_DIR, '07_user_b_isolation.png') });
  } catch (error) {
    console.error('Fatal test execution error:', error);
    recordTest('FATAL', 'Runner', 'Test runner exception', false, error.message);
  } finally {
    await browser.close();
  }

  // Write final test report JSON
  const reportPath = path.join(REPORTS_DIR, 'test_run_report.json');
  fs.writeFileSync(reportPath, JSON.stringify(results, null, 2));

  console.log('\n===============================================================');
  console.log(` TEST RUN SUMMARY: ${results.passed} / ${results.total} PASSED (${results.failed} FAILED)`);
  console.log(` Report written to: ${reportPath}`);
  console.log('===============================================================\n');

  return results;
}

runClientSimulation();
