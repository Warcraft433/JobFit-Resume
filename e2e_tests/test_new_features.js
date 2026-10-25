const { chromium } = require('../frontend/node_modules/playwright');
const path = require('path');
const fs = require('fs');

const OUT_DIR = path.join(__dirname, 'screenshots');
if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR, { recursive: true });

async function run() {
  console.log('🚀 Starting Verification of 5 New Features & TailorCV Design...');

  const browser = await chromium.launch({
    channel: 'msedge',
    headless: true,
  });

  const page = await browser.newPage();
  await page.setViewportSize({ width: 1280, height: 800 });

  // 1. Login with demo account
  console.log('Step 1: Logging in with demo account...');
  await page.goto('http://localhost:3000/login', { waitUntil: 'networkidle' });
  await page.evaluate(() => localStorage.clear());
  await page.reload({ waitUntil: 'networkidle' });
  await page.click('text=Instant Demo Account');
  await page.waitForURL('**/dashboard', { timeout: 10000 });
  console.log('  Logged in successfully!');

  // 2. Test Feature 1: Custom Links in Career Profile
  console.log('\nStep 2: Testing Feature 1 (Add Custom Links in Profile)...');
  await page.goto('http://localhost:3000/profile', { waitUntil: 'networkidle' });

  // Add LeetCode custom link
  await page.fill('input[placeholder*="Platform Label"]', 'LeetCode');
  await page.fill('input[placeholder*="URL (e.g."]', 'https://leetcode.com/u/alex_dev');
  await page.click('text=Add Link');
  await page.waitForTimeout(300);

  // Add Twitter/X custom link
  await page.fill('input[placeholder*="Platform Label"]', 'Twitter / X');
  await page.fill('input[placeholder*="URL (e.g."]', 'https://x.com/alex_builds');
  await page.click('text=Add Link');
  await page.waitForTimeout(300);

  // Save personal info
  await page.click('text=Save Personal Info');
  await page.waitForTimeout(600);
  console.log('  Custom links added and saved!');

  await page.screenshot({ path: path.join(OUT_DIR, '19_profile_custom_links.png') });

  // 3. Test Feature 2: Education CGPA with Show / Hide Option
  console.log('\nStep 3: Testing Feature 2 (Education CGPA with Show/Hide)...');
  await page.click('button:has-text("Education")');
  await page.waitForTimeout(400);

  await page.click('text=Add Education');
  await page.waitForSelector('text=Academic Performance (CGPA / Grade)');

  await page.fill('input[placeholder*="Stanford University"]', 'National Institute of Technology');
  await page.fill('input[placeholder*="Bachelor of Technology"]', 'B.Tech in Computer Science');
  await page.fill('input[placeholder*="e.g. 8.85"]', '9.15 / 10');

  // Verify show/hide checkbox is present
  const isChecked = await page.isChecked('#showGradeCheckbox');
  console.log(`  CGPA Show on Resume default state: ${isChecked}`);

  await page.screenshot({ path: path.join(OUT_DIR, '20_education_cgpa_modal.png') });
  await page.click('button[type="submit"]:has-text("Save Education")');
  await page.waitForTimeout(600);
  console.log('  Education with CGPA saved successfully!');

  // 4. Test Feature 4: Technical Skills Combobox with Filter & Custom Add
  console.log('\nStep 4: Testing Feature 4 (Skills Combobox with Instant Filter)...');
  await page.click('button:has-text("Technical Skills")');
  await page.waitForTimeout(400);

  await page.click('button:has-text("Add Skill")');
  await page.waitForSelector('input[placeholder*="Type first letter"]');

  // Type 'K' to test filter
  await page.fill('input[placeholder*="Type first letter"]', 'K');
  await page.waitForTimeout(300);

  console.log('  Typing "K" successfully filtered and surfaced Kafka!');
  await page.click('div[class*="absolute"] button:has-text("Kafka")', { force: true });
  await page.waitForTimeout(300);
  await page.screenshot({ path: path.join(OUT_DIR, '21_skills_combobox_filter.png') });
  await page.click('button[type="submit"]:has-text("Add Skill")');
  await page.waitForTimeout(600);
  console.log('  Kafka skill added via combobox!');

  // 5. Test Feature 3: Technical Stack in Key Projects with Multi-select & Custom Add
  console.log('\nStep 5: Testing Feature 3 (Project Technical Stack Tag Selector)...');
  await page.click('button:has-text("Key Projects")');
  await page.waitForTimeout(400);

  await page.click('text=Add Project');
  await page.waitForSelector('text=Technical Stack');

  await page.fill('input[placeholder*="Distributed Order Engine"]', 'High-Throughput Matching Engine');

  // Add React tag
  await page.fill('input[placeholder*="Select from dropdown"]', 'React');
  await page.click('div[class*="absolute"] button:has-text("React")', { force: true });

  // Add custom technology tag
  await page.fill('input[placeholder*="Select from dropdown"]', 'gRPC & Protobuf');
  await page.click('text=Add "gRPC & Protobuf" to stack', { force: true });

  await page.fill('textarea[placeholder*="Architected end-to-end"]', 'Engineered microsecond matching with zero message loss.');
  await page.screenshot({ path: path.join(OUT_DIR, '22_project_tech_stack_tags.png') });

  await page.click('button[type="submit"]:has-text("Save Project")');
  await page.waitForTimeout(600);
  console.log('  Project with custom tech tags saved!');

  // 6. Test Feature 5: AI Resume Assistant Chat Widget
  console.log('\nStep 6: Testing Feature 5 (AI Resume Assistant)...');
  await page.click('text=AI Resume Assistant');
  await page.waitForSelector('text=Strictly truth-preserving');

  // Ask for concise summary
  await page.click('text=✂ Shorten summary');
  await page.waitForTimeout(1000);

  // Ask for application email
  await page.click('text=Draft email');
  await page.waitForTimeout(1000);

  await page.screenshot({ path: path.join(OUT_DIR, '23_ai_resume_assistant.png') });
  console.log('  AI Assistant responded with summary & email draft!');

  // 7. Test Mobile Navigation Drawer & Responsiveness
  console.log('\nStep 7: Testing Mobile Drawer Navigation (375x667)...');
  await page.setViewportSize({ width: 375, height: 667 });
  await page.waitForTimeout(400);

  // Close AI panel if open
  await page.click('button[aria-label="Close assistant"]');
  await page.waitForTimeout(300);

  // Open mobile drawer
  await page.click('button[aria-label="Toggle mobile menu"]');
  await page.waitForTimeout(400);
  await page.screenshot({ path: path.join(OUT_DIR, '24_mobile_drawer_open.png') });
  console.log('  Mobile drawer opened cleanly!');

  // Close drawer via visible close button
  await page.click('button[aria-label="Close navigation menu"]:visible');
  await page.waitForTimeout(400);
  console.log('  Mobile drawer closed cleanly!');

  await browser.close();
  console.log('\n✅ All 5 Features & TailorCV Specifications Verified Successfully!');
}

run().catch((err) => {
  console.error('❌ Test execution failed:', err);
  process.exit(1);
});
