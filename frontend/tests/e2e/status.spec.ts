import { expect, test } from '@playwright/test';

test.beforeAll(async ({ request }) => {
  await expect.poll(async () => {
    const response = await request.get('/api/v1/status');
    return response.ok();
  }, { message: 'Start the local backend before running status browser tests', timeout: 30_000 }).toBe(true);
});

test('visitors open System Status from primary navigation and return cleanly', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('navigation', { name: 'Primary navigation' }).getByRole('link', { name: 'Status', exact: true }).click();
  await expect(page).toHaveURL('/status');
  await expect(page.getByRole('heading', { level: 1, name: 'System Status' })).toBeVisible();
  await expect(
    page.getByRole('region', { name: 'Current system status summary' }).getByText('Healthy', { exact: true }),
  ).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Healthy', exact: true })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Database Up', exact: true })).toBeVisible();
  await expect(page.getByText(/Process uptime only/)).toBeVisible();
  await expect(page.getByRole('table')).toBeVisible();

  await page.getByRole('link', { name: 'Back to Portfolio', exact: true }).click();
  await expect(page).toHaveURL('/');
});

test('status page distinguishes degraded DB state and preserves focus on manual refresh', async ({ page }) => {
  await page.route('**/api/v1/status', async (route) => {
    await route.fulfill({
      status: 200,
      json: {
        success: true,
        data: {
          status: 'degraded',
          checkedAt: '2026-11-26T03:30:00Z',
          uptimeSeconds: 185,
          startedAt: '2026-11-26T03:26:55Z',
          revision: '53f0c21',
          database: { status: 'degraded', latencyMs: null },
          traffic: {
            windowSeconds: 300,
            requestCount: 24,
            minSamples: 20,
            p95Status: 'measured',
            p95Ms: 18.4,
          },
        },
        timestamp: '2026-11-26T03:30:00Z',
      },
    });
  });

  await page.goto('/status');
  await expect(page.getByText('Degraded', { exact: true }).first()).toBeVisible();
  await expect(page.getByText('Database Degraded', { exact: true }).first()).toBeVisible();
  await expect(page.getByText('18.40 ms').first()).toBeVisible();

  const refresh = page.getByRole('button', { name: 'Refresh snapshot', exact: true });
  await refresh.focus();
  await refresh.press('Enter');
  await expect(refresh).toBeFocused();
});

test('status page keeps cached snapshot on refresh 503 and recovers via keyboard retry', async ({ page }) => {
  let failRefresh = false;
  await page.route('**/api/v1/status', async (route) => {
    if (failRefresh) {
      await route.fulfill({
        status: 503,
        json: { success: false, data: null, error: 'Fixture unavailable' },
      });
    } else {
      await route.continue();
    }
  });

  await page.goto('/status');
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Database Up', exact: true })).toBeVisible();

  failRefresh = true;
  await page.getByRole('button', { name: 'Refresh snapshot', exact: true }).click();
  await expect(page.getByRole('status')).toHaveText("Couldn't refresh status. Showing the last loaded result.");
  await expect(page.getByRole('table')).toBeVisible();

  failRefresh = false;
  const retry = page.getByRole('button', { name: 'Try again to load status', exact: true });
  await retry.focus();
  await retry.press('Enter');
  await expect(page.getByText("Couldn't refresh status. Showing the last loaded result.")).toHaveCount(0);
  await expect(page.getByRole('table')).toBeVisible();
});

test('status page avoids horizontal overflow on 375px mobile under reduced motion', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.goto('/status');
  await expect(page.getByRole('heading', { level: 1, name: 'System Status' })).toBeVisible();
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
