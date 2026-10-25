const { chromium } = require('../frontend/node_modules/playwright');
const path = require('path');
const fs = require('fs');

const OUT_DIR = path.join(__dirname, 'screenshots');
if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR, { recursive: true });

async function run() {
  console.log('🚀 Starting Theme & Responsive Viewport Verification...');
  const browser = await chromium.launch({
    channel: 'msedge',
    headless: true,
  });
  const context = await browser.newContext();
  const page = await context.newPage();

  // Test 1: Desktop Dark & Light on Landing Page
  console.log('\n📱 Test 1: Landing Page Desktop (1280x800)');
  await page.setViewportSize({ width: 1280, height: 800 });
  await page.goto('http://localhost:3000', { waitUntil: 'networkidle' });
  await page.evaluate(() => localStorage.clear());
  await page.reload({ waitUntil: 'networkidle' });

  // Initial Dark Mode screenshot
  await page.screenshot({ path: path.join(OUT_DIR, '08_landing_desktop_dark.png') });
  console.log('  Saved: 08_landing_desktop_dark.png');

  // Toggle to Light Mode
  console.log('  Clicking Theme Toggle...');
  await page.click('button[aria-label*="Switch to"]');
  await page.waitForTimeout(400);

  const isLight = await page.evaluate(() => document.documentElement.classList.contains('light'));
  const storedTheme = await page.evaluate(() => localStorage.getItem('jobfit_resume_theme'));
  console.log(`  Document class contains 'light': ${isLight}`);
  console.log(`  LocalStorage theme: ${storedTheme}`);

  await page.screenshot({ path: path.join(OUT_DIR, '09_landing_desktop_light.png') });
  console.log('  Saved: 09_landing_desktop_light.png');

  // Test 2: Mobile Landing Page (375x667) in Light & Dark Mode
  console.log('\n📱 Test 2: Landing Page Mobile (375x667)');
  await page.setViewportSize({ width: 375, height: 667 });
  await page.screenshot({ path: path.join(OUT_DIR, '10_landing_mobile_light.png') });
  console.log('  Saved: 10_landing_mobile_light.png');

  // Switch back to dark on mobile
  await page.click('button[aria-label*="Switch to"]');
  await page.waitForTimeout(400);
  await page.screenshot({ path: path.join(OUT_DIR, '11_landing_mobile_dark.png') });
  console.log('  Saved: 11_landing_mobile_dark.png');

  // Test 3: Tablet Landing Page (768x1024)
  console.log('\n📱 Test 3: Landing Page Tablet (768x1024)');
  await page.setViewportSize({ width: 768, height: 1024 });
  await page.screenshot({ path: path.join(OUT_DIR, '12_landing_tablet_dark.png') });
  console.log('  Saved: 12_landing_tablet_dark.png');

  // Test 4: Authenticated Dashboard in Light and Dark Mode across Mobile and Desktop
  console.log('\n📱 Test 4: Dashboard & Workspaces');
  await page.goto('http://localhost:3000/login', { waitUntil: 'networkidle' });

  // One-click demo login
  const demoBtn = await page.waitForSelector('text=Instant Demo Account');
  await demoBtn.click();
  await page.waitForURL('**/dashboard', { timeout: 10000 });
  await page.waitForTimeout(800);

  // Desktop Dashboard Dark
  await page.setViewportSize({ width: 1280, height: 800 });
  await page.screenshot({ path: path.join(OUT_DIR, '13_dashboard_desktop_dark.png') });
  console.log('  Saved: 13_dashboard_desktop_dark.png');

  // Switch to Light Mode in Navbar
  await page.click('header button[aria-label*="Switch to"]');
  await page.waitForTimeout(400);
  await page.screenshot({ path: path.join(OUT_DIR, '14_dashboard_desktop_light.png') });
  console.log('  Saved: 14_dashboard_desktop_light.png');

  // Mobile Dashboard Light (375x667)
  await page.setViewportSize({ width: 375, height: 667 });
  await page.waitForTimeout(400);
  await page.screenshot({ path: path.join(OUT_DIR, '15_dashboard_mobile_light.png') });
  console.log('  Saved: 15_dashboard_mobile_light.png');

  // Check horizontal overflow on mobile
  const hasOverflow = await page.evaluate(() => document.body.scrollWidth > window.innerWidth);
  console.log(`  Mobile dashboard horizontal overflow detected: ${hasOverflow}`);

  // Test 5: Resume Studio in Light Mode
  await page.goto('http://localhost:3000/resumes', { waitUntil: 'networkidle' });
  await page.setViewportSize({ width: 1280, height: 800 });
  await page.waitForTimeout(600);
  await page.screenshot({ path: path.join(OUT_DIR, '16_resume_builder_desktop_light.png') });
  console.log('  Saved: 16_resume_builder_desktop_light.png');

  // Mobile Resume Studio
  await page.setViewportSize({ width: 375, height: 667 });
  await page.screenshot({ path: path.join(OUT_DIR, '17_resume_builder_mobile_light.png') });
  console.log('  Saved: 17_resume_builder_mobile_light.png');

  // Test 6: Job Tracker in Light Mode
  await page.goto('http://localhost:3000/applications', { waitUntil: 'networkidle' });
  await page.setViewportSize({ width: 1280, height: 800 });
  await page.waitForTimeout(600);
  await page.screenshot({ path: path.join(OUT_DIR, '18_tracker_desktop_light.png') });
  console.log('  Saved: 18_tracker_desktop_light.png');

  await browser.close();
  console.log('\n✅ All Theme & Responsive Viewport Tests Completed Successfully!');
}

run().catch((err) => {
  console.error('❌ Error during testing:', err);
  process.exit(1);
});
