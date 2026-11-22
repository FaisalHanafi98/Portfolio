import { expect, test } from '@playwright/test';

test.beforeAll(async ({ request }) => {
  await expect.poll(async () => {
    const response = await request.get('/api/v1/projects');
    if (!response.ok()) return false;
    const body = await response.json();
    return body.success && body.data.some((project: { slug: string }) => project.slug === 'creams');
  }, { message: 'Start the local seeded backend before running browser tests', timeout: 30_000 }).toBe(true);
});

test('visitors open a seeded project from the showcase and return to projects', async ({ page, request }) => {
  const response = await request.get('/api/v1/projects/creams');
  expect(response.ok()).toBe(true);
  const { data: project } = await response.json();
  await page.goto('/#projects');
  const projects = page.locator('#projects');
  await projects.getByRole('heading', { name: project.title, exact: true }).click();
  await expect(page).toHaveURL('/projects/creams');
  await expect(page.getByRole('heading', { level: 1 })).toHaveText(project.title);
  await page.getByRole('link', { name: 'Back to Projects' }).click();
  await expect(page).toHaveURL('/#projects');
  await expect(projects.getByRole('heading', { level: 2 })).toBeInViewport();
});

test('engineering details can be expanded without hiding the business story', async ({ page }) => {
  await page.goto('/#projects');
  const projects = page.locator('#projects');
  const depth = projects.getByRole('button', { name: '+ Engineering Depth', exact: true });
  await expect(projects.getByRole('article').first()).toBeVisible();
  await depth.click();
  await expect(depth).toHaveAttribute('aria-pressed', 'true');
  await expect(projects.getByText('Under the Hood — Engineering & Architecture Proof').first()).toBeVisible();
  await projects.getByRole('button', { name: 'Business & Impact View', exact: true }).click();
  await expect(depth).toHaveAttribute('aria-pressed', 'false');
  await expect(projects.getByText('Under the Hood — Engineering & Architecture Proof')).toHaveCount(0);
  await expect(projects.getByRole('article').first()).toContainText('What This Project Is About');
});

test('a missing project is confirmed by the API and offers a way home', async ({ page }) => {
  const result = page.waitForResponse('**/api/v1/projects/missing-e2e-project');
  await page.goto('/projects/missing-e2e-project');
  expect((await result).status()).toBe(404);
  await expect(page.getByRole('heading', { name: 'Project Not Found' })).toBeVisible();
  await expect(page.getByRole('button', { name: /Try again/ })).toHaveCount(0);
  await page.getByRole('link', { name: 'Go Home', exact: true }).click();
  await expect(page).toHaveURL('/');
});

test('a failed detail request can recover through a keyboard retry', async ({ page }) => {
  let unavailable = true;
  await page.route('**/api/v1/projects/creams', async (route) => {
    if (unavailable) {
      await route.fulfill({ status: 503, json: { success: false, data: null, error: 'Fixture unavailable' } });
    } else {
      await route.continue();
    }
  });
  await page.goto('/projects/creams');
  await expect(page.getByRole('status')).toHaveText('Unable to load project.');
  await expect(page.getByRole('heading', { name: 'Project Not Found' })).toHaveCount(0);
  unavailable = false;
  const retry = page.getByRole('button', { name: 'Try again to load project', exact: true });
  await retry.focus();
  await retry.press('Enter');
  await expect(page.getByRole('article')).toBeVisible();
  await expect(page.getByRole('heading', { level: 1 })).toContainText('CREAMS');
});

test('the mobile menu restores scrolling and focus across close and resize', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.goto('/');
  const toggle = page.getByRole('button', { name: 'Toggle menu', exact: true, includeHidden: true });
  await toggle.click();
  await expect(toggle).toHaveAttribute('aria-expanded', 'true');
  await expect(page.locator('body')).toHaveCSS('overflow', 'hidden');
  await page.locator('#mobile-navigation').getByRole('link', { name: 'About', exact: true }).focus();
  await page.keyboard.press('Escape');
  await expect(toggle).toBeFocused();
  await expect(toggle).toHaveAttribute('aria-expanded', 'false');
  await toggle.click();
  await page.setViewportSize({ width: 1024, height: 768 });
  await expect(toggle).toHaveAttribute('aria-expanded', 'false');
  await expect(page.locator('body')).not.toHaveCSS('overflow', 'hidden');
  await page.setViewportSize({ width: 375, height: 812 });
  await expect(toggle).toHaveAttribute('aria-expanded', 'false');
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
