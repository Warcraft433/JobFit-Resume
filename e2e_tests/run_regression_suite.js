const axios = require('../frontend/node_modules/axios');
const fs = require('fs');
const path = require('path');
const { PDFParse } = require('../frontend/node_modules/pdf-parse');
const { chromium } = require('../frontend/node_modules/playwright');

const BASE_API = 'http://localhost:8080/api/v1';
const BASE_UI = 'http://localhost:3000';
const REGRESSION_DIR = path.join(__dirname, 'regression_artifacts');
if (!fs.existsSync(REGRESSION_DIR)) fs.mkdirSync(REGRESSION_DIR, { recursive: true });

const regressionResults = {
  total: 0,
  passed: 0,
  failed: 0,
  tests: [],
};

function record(id, description, passed, details = '') {
  regressionResults.total++;
  if (passed) {
    regressionResults.passed++;
    console.log(`[PASS] ${id}: ${description}`);
  } else {
    regressionResults.failed++;
    console.error(`[FAIL] ${id}: ${description} | ${details}`);
  }
  regressionResults.tests.push({ id, description, passed, details });
}

async function runRegressionSuite() {
  console.log('================================================================');
  console.log(' RUNNING POST-FIX REGRESSION TEST SUITE');
  console.log('================================================================\n');

  // -------------------------------------------------------------------------
  // TEST 1: REGRESSION FOR JFR-AUD-03 (Branded Page Title)
  // -------------------------------------------------------------------------
  console.log('>>> Regression Test 1: Page Title Branding');
  const browser = await chromium.launch({ channel: 'msedge', headless: true });
  const page = await browser.newPage();
  await page.goto(BASE_UI, { waitUntil: 'networkidle' });
  const pageTitle = await page.title();
  const titleCorrect = pageTitle.includes('JobFit Resume');
  record('REG-01', 'Browser tab title displays branded JobFit Resume product title', titleCorrect, `Actual title: "${pageTitle}"`);
  await browser.close();

  // -------------------------------------------------------------------------
  // TEST 2: REGRESSION FOR JFR-AUD-02 (Experience Chronological Date Math)
  // -------------------------------------------------------------------------
  console.log('\n>>> Regression Test 2: Chronological Experience Date Math');
  const testEmail = `reg.user.${Date.now()}@jobfit.test`;
  const regRes = await axios.post(`${BASE_API}/auth/register`, {
    fullName: 'Experience Math Tester',
    email: testEmail,
    password: 'Password123!',
  });
  const token = regRes.data.data.token;
  const client = axios.create({
    baseURL: BASE_API,
    headers: { Authorization: `Bearer ${token}` },
  });

  // Single job spanning 5 full years (2018-01 to 2023-01)
  await client.post('/experience', {
    employer: 'Long Tenure Enterprise',
    jobTitle: 'Senior Infrastructure Engineer',
    startDate: '2018-01',
    endDate: '2023-01',
    currentRole: false,
    description: 'Managed Kubernetes infrastructure for 5 consecutive years.',
  });

  // Target job requiring 5+ years experience
  const expJob = await client.post('/jobs', {
    company: 'FinTech Growth',
    jobTitle: 'Staff Architect',
    jobDescription: 'Required: 5+ years of experience in distributed infrastructure systems. Docker and Kubernetes.',
  });
  const anExpRes = await client.post(`/jobs/${expJob.data.data.id}/analyze`);
  const expAssessment = anExpRes.data.data.experienceAssessment || '';
  const expScore = anExpRes.data.data.scoreBreakdown?.experience;

  // The candidate has 5 years (60 months) of experience, so they should meet the 5-year requirement (full 25.0 points)
  const experiencePass = expScore === 25.0 && expAssessment.includes('Meets or exceeds requirement: 5 year(s)');
  record('REG-02', 'Chronological date calculation accurately computes 5 years from 2018-01 to 2023-01', experiencePass,
    `Score: ${expScore}, Assessment: "${expAssessment}"`);

  // -------------------------------------------------------------------------
  // TEST 3: REGRESSION FOR JFR-AUD-01 (PDF Stream Reading Order)
  // -------------------------------------------------------------------------
  console.log('\n>>> Regression Test 3: PDF Document Stream Reading Order');

  // Populate candidate profile for resume export
  await client.put('/profile', {
    professionalHeadline: 'Staff Infrastructure Architect',
    contactEmail: 'staff.architect@jobfit.test',
    phone: '+1 (555) 432-1098',
    city: 'Austin',
    country: 'United States',
    professionalSummary: 'Staff architect with proven 5-year track record in enterprise distributed systems.',
  });

  await client.post('/profile/skills', { skillName: 'Docker', category: 'CLOUD_DEVOPS' });
  await client.post('/profile/skills', { skillName: 'Kubernetes', category: 'CLOUD_DEVOPS' });

  await client.post('/education', {
    institution: 'University of Texas at Austin',
    degree: 'B.S. in Computer Science',
    startDate: '2014',
    endDate: '2018',
  });

  await client.post('/projects', {
    name: 'KubeMesh Orchestrator',
    technicalStack: 'Docker, Kubernetes, Go',
    description: 'Autonomous cluster scaling platform with sub-second failover.',
    outcomes: 'Scaled to 500 pods under load.',
  });

  const templatesToTest = ['classic', 'modern', 'technical'];

  for (const t of templatesToTest) {
    const genRes = await client.post(`/jobs/${expJob.data.data.id}/generate-resume`, { templateKey: t });
    const versionId = genRes.data.data.id;

    // Export PDF
    const pdfRes = await client.get(`/resume-versions/${versionId}/export/pdf`, { responseType: 'arraybuffer' });
    const pdfBuffer = Buffer.from(pdfRes.data);
    const pdfFilePath = path.join(REGRESSION_DIR, `regression_${t}.pdf`);
    fs.writeFileSync(pdfFilePath, pdfBuffer);

    // Parse PDF text stream
    const pdfParser = new PDFParse({ data: pdfBuffer });
    await pdfParser.load();
    const pdfText = await pdfParser.getText();
    const fullText = pdfText.text || '';
    await pdfParser.destroy();

    const lines = fullText.split('\n').map(l => l.trim()).filter(l => l.length > 0);
    const firstLine = lines[0] || '';

    // Verify first line is candidate name, NOT floating dates or skills
    const nameAtTop = firstLine.includes('Experience Math Tester');
    const noFloatingDatesAtTop = !firstLine.includes('2018') && !firstLine.includes('Present') && !firstLine.includes('Docker');

    record(`REG-03-${t}`, `PDF Template "${t}" text stream starts cleanly with candidate name`,
      nameAtTop && noFloatingDatesAtTop,
      `First line: "${firstLine}"`);
  }

  // -------------------------------------------------------------------------
  // TEST 4: DOCX INTEGRITY & READABILITY ACROSS TEMPLATES
  // -------------------------------------------------------------------------
  console.log('\n>>> Regression Test 4: DOCX Export Integrity');
  const genClassic = await client.post(`/jobs/${expJob.data.data.id}/generate-resume`, { templateKey: 'classic' });
  const docxRes = await client.get(`/resume-versions/${genClassic.data.data.id}/export/docx`, { responseType: 'arraybuffer' });
  const docxBuffer = Buffer.from(docxRes.data);
  const isDocxValid = docxBuffer.slice(0, 2).toString() === 'PK' && docxBuffer.length > 3000;
  record('REG-04', 'Word DOCX exports valid OpenXML document without corruption', isDocxValid, `Size: ${docxBuffer.length} bytes`);

  // Write report
  const reportPath = path.join(REGRESSION_DIR, 'regression_results.json');
  fs.writeFileSync(reportPath, JSON.stringify(regressionResults, null, 2));

  console.log('\n================================================================');
  console.log(` REGRESSION SUITE: ${regressionResults.passed} / ${regressionResults.total} PASSED (${regressionResults.failed} FAILED)`);
  console.log(` Results written to: ${reportPath}`);
  console.log('================================================================\n');

  return regressionResults;
}

runRegressionSuite().catch(err => {
  console.error('Regression suite fatal error:', err);
  process.exit(1);
});
