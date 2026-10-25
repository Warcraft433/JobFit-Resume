const { chromium } = require('../frontend/node_modules/playwright');

(async () => {
  const browser = await chromium.launch({ channel: 'msedge', headless: true });
  const page = await browser.newPage();
  await page.goto('http://localhost:3000/register');
  const email = `check_b_${Date.now()}@jobfit.com`;
  await page.fill('input[placeholder="Alex Mercer"]', 'Check User');
  await page.fill('input[type="email"]', email);
  const pass = await page.$$('input[type="password"]');
  await pass[0].fill('Password123!');
  await pass[1].fill('Password123!');
  await page.click('button[type="submit"]');
  await page.waitForURL('**/dashboard');
  await page.waitForTimeout(1000);
  const text = await page.textContent('body');
  console.log('--- USER B DASHBOARD TEXT ---');
  console.log('Includes Stripe:', text.includes('Stripe Horizon Inc.'));
  console.log('Includes 0:', text.includes('0'));
  console.log('Text excerpt:\n', text.substring(0, 800));
  await browser.close();
})();
