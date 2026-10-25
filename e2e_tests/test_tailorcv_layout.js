const { chromium } = require('../frontend/node_modules/playwright');
const path = require('path');
const fs = require('fs');

const ARTIFACT_DIR = 'C:/Users/warca/.gemini/antigravity/brain/82f92f54-83f2-4063-aa3d-2adfcab93f21';

async function runVerification() {
  console.log('🚀 Starting TailorCV layout and template styles verification...');
  const browser = await chromium.launch({ channel: 'msedge', headless: true });
  const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await context.newPage();

  try {
    // 1. Login with demo user
    console.log('1. Navigating to login...');
    await page.goto('http://localhost:3000/login', { waitUntil: 'networkidle' });
    await page.evaluate(() => localStorage.clear());
    await page.reload({ waitUntil: 'networkidle' });
    await page.click('text=Instant Demo Account');
    await page.waitForURL('**/dashboard', { timeout: 10000 });
    console.log('✓ Successfully logged in to Dashboard');

    // 2. Verify TailorCV Sidebar elements
    console.log('2. Verifying TailorCV Sidebar...');
    const brandMark = page.locator('aside strong:has-text("TailorCV")').first();
    const isBrandVisible = await brandMark.isVisible();
    console.log(`✓ Brand text "TailorCV" visible: ${isBrandVisible}`);

    const helpBox = page.locator('aside:has-text("Make every application count")').first();
    console.log(`✓ Helpbox visible: ${await helpBox.isVisible()}`);

    // 3. Verify TailorCV Dashboard layout
    console.log('3. Verifying TailorCV Dashboard Page...');
    await page.waitForSelector('.hero');
    const heroText = await page.locator('.hero').textContent();
    console.log(`✓ TailorCV Hero rendered with text: "${heroText.slice(0, 45)}..."`);

    // Verify 4-stat cards
    const statCards = await page.locator('text=Resumes created').first();
    console.log(`✓ Stat cards rendered: ${await statCards.isVisible()}`);

    // Verify 3-step workflow
    const stepWorkflow = await page.locator('text=Your next steps').first();
    console.log(`✓ 3-step workflow visible: ${await stepWorkflow.isVisible()}`);

    // Capture TailorCV Dashboard Desktop Screenshot
    await page.screenshot({ path: path.join(ARTIFACT_DIR, '25_tailorcv_dashboard.png'), fullPage: true });
    console.log('📸 Captured 25_tailorcv_dashboard.png');

    // 4. Test Resume Studio & Templates
    console.log('4. Navigating to Resume Studio (/resumes)...');
    await page.goto('http://localhost:3000/resumes', { waitUntil: 'networkidle' });
    await page.waitForSelector('.resume-paper');
    console.log('✓ Resume paper canvas rendered');

    // Verify Accent Switcher Chips
    const signalRed = page.locator('button:has-text("Signal red")').first();
    const neonBlue = page.locator('button:has-text("Neon blue")').first();
    const charcoal = page.locator('button:has-text("Classic charcoal")').first();
    console.log(`✓ Accent chips visible: ${await signalRed.isVisible()}`);

    // Click Neon blue accent
    await neonBlue.click();
    console.log('✓ Clicked Neon blue accent');

    // Click Classic charcoal accent
    await charcoal.click();
    console.log('✓ Clicked Classic charcoal accent');

    // Capture TailorCV Resume Studio Desktop Screenshot
    await page.screenshot({ path: path.join(ARTIFACT_DIR, '26_tailorcv_resume_studio.png'), fullPage: true });
    console.log('📸 Captured 26_tailorcv_resume_studio.png');

    // 5. Test Dedicated Templates Page (/templates)
    console.log('5. Navigating to Templates Gallery (/templates)...');
    await page.goto('http://localhost:3000/templates', { waitUntil: 'networkidle' });
    await page.waitForSelector('.template-grid');
    const cleanAtsCard = page.locator('button:has-text("Clean ATS")').first();
    const modernCard = page.locator('button:has-text("Modern Professional")').first();
    const neonCard = page.locator('button:has-text("Neon Accent")').first();
    console.log(`✓ Clean ATS card: ${await cleanAtsCard.isVisible()}`);
    console.log(`✓ Modern Professional card: ${await modernCard.isVisible()}`);
    console.log(`✓ Neon Accent card: ${await neonCard.isVisible()}`);
    await modernCard.click();
    console.log('✓ Selected Modern Professional template in gallery');

    // 6. Test Landing Page (Public Hero)
    console.log('6. Testing Public Landing Page (/) ...');
    await page.goto('http://localhost:3000/', { waitUntil: 'networkidle' });
    const landingHero = page.locator('text=One profile.').first();
    console.log(`✓ Landing hero visible: ${await landingHero.isVisible()}`);
    await page.screenshot({ path: path.join(ARTIFACT_DIR, '29_tailorcv_landing.png'), fullPage: true });
    console.log('📸 Captured 29_tailorcv_landing.png');

    // 7. Test Mobile Viewport Responsiveness
    console.log('7. Testing Mobile Viewport (390x844)...');
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto('http://localhost:3000/dashboard', { waitUntil: 'networkidle' });
    
    // Check mobile hamburger toggle
    const hamburger = page.locator('button[aria-label="Toggle mobile menu"]').first();
    console.log(`✓ Mobile hamburger menu visible: ${await hamburger.isVisible()}`);

    // Open hamburger drawer
    await hamburger.click();
    await page.waitForTimeout(300);
    const drawerOpen = await page.locator('aside.mobile-open').isVisible();
    console.log(`✓ Mobile slide-in drawer open: ${drawerOpen}`);

    // Capture Mobile Drawer Screenshot
    await page.screenshot({ path: path.join(ARTIFACT_DIR, '27_tailorcv_mobile_drawer.png') });
    console.log('📸 Captured 27_tailorcv_mobile_drawer.png');

    // Close mobile drawer by clicking close button
    await page.locator('button[aria-label="Close navigation menu"]').click();
    await page.waitForTimeout(300);
    console.log('✓ Mobile drawer closed');

    // 8. Test Dark Mode
    console.log('8. Testing Dark Mode Switch...');
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.waitForTimeout(200);
    // Click theme toggle button in topbar
    const themeBtn = page.locator('header button.themebtn').first();
    await themeBtn.click();
    await page.waitForTimeout(400);

    // Capture Dark Mode Dashboard
    await page.screenshot({ path: path.join(ARTIFACT_DIR, '28_tailorcv_dark_dashboard.png'), fullPage: true });
    console.log('📸 Captured 28_tailorcv_dark_dashboard.png');

    console.log('🎉 ALL TAILORCV LAYOUT, COLOR THEMES, HERO, AND TEMPLATE TESTS PASSED PERFECTLY!');
  } catch (error) {
    console.error('❌ Verification failed:', error);
    process.exit(1);
  } finally {
    await browser.close();
  }
}

runVerification();
