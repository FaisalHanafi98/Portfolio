import { expect, test } from '@playwright/test';

const HEALTHY_GITHUB_FIXTURE = {
  status: 'healthy' as const,
  repository: 'FaisalHanafi98/Portfolio',
  lastSyncedAt: '2026-11-26T03:25:00Z',
  snapshotAgeSeconds: 300,
  cacheTtlSeconds: 900,
  nextPollAt: '2026-11-26T03:40:00Z',
  stateReason: null,
  workflowRuns: [
    {
      id: 44970309509,
      name: 'CI',
      branch: 'main',
      event: 'push',
      status: 'completed',
      conclusion: 'success',
      shortSha: '21497cd',
      updatedAt: '2026-11-26T03:24:00Z',
      htmlUrl: 'https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509',
    },
  ],
  recentCommits: [
    {
      shortSha: '21497cd',
      message: 'Feat(Status): Add public /status frontend view with accessible telemetry table.',
      committedAt: '2026-11-26T03:20:00Z',
      htmlUrl: 'https://github.com/FaisalHanafi98/Portfolio/commit/21497cd',
    },
  ],
};

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
  await expect(page.getByRole('heading', { level: 2, name: 'Repository CI & Activity Sync' })).toBeVisible();
  await expect(page.getByText(/Process uptime only/)).toBeVisible();
  await expect(page.getByRole('table')).toBeVisible();

  await page.getByRole('link', { name: 'Back to Portfolio', exact: true }).click();
  await expect(page).toHaveURL('/');
});

test('status page distinguishes degraded DB outage with healthy GitHub feed and preserves focus on manual refresh', async ({ page }) => {
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
          github: HEALTHY_GITHUB_FIXTURE,
        },
        timestamp: '2026-11-26T03:30:00Z',
      },
    });
  });

  await page.goto('/status');
  await expect(page.getByRole('cell', { name: 'Degraded', exact: true })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Database Degraded', exact: true })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'GitHub Healthy', exact: true })).toBeVisible();
  await expect(page.getByRole('link', { name: 'View workflow run (21497cd)' })).toBeVisible();
  await expect(page.getByText('18.40 ms').first()).toBeVisible();

  const refresh = page.getByRole('button', { name: 'Refresh snapshot', exact: true });
  await refresh.focus();
  await refresh.press('Enter');
  await expect(refresh).toBeFocused();
});

test('status page distinguishes independent GitHub stale, degraded, and empty states while DB stays healthy', async ({ page }) => {
  let step = 0;
  await page.route('**/api/v1/status', async (route) => {
    const githubPayloads = [
      {
        ...HEALTHY_GITHUB_FIXTURE,
        status: 'stale',
        snapshotAgeSeconds: 1020,
        stateReason: 'upstream_rate_limited',
      },
      {
        ...HEALTHY_GITHUB_FIXTURE,
        status: 'degraded',
        snapshotAgeSeconds: 900,
        stateReason: 'partial_feed_error',
        recentCommits: [],
      },
      {
        status: 'empty',
        repository: 'FaisalHanafi98/Portfolio',
        lastSyncedAt: '2026-11-26T03:31:00Z',
        snapshotAgeSeconds: 0,
        cacheTtlSeconds: 900,
        nextPollAt: '2026-11-26T03:46:00Z',
        stateReason: 'empty_activity',
        workflowRuns: [],
        recentCommits: [],
      },
    ];
    const github = githubPayloads[Math.min(step, githubPayloads.length - 1)];
    await route.fulfill({
      status: 200,
      json: {
        success: true,
        data: {
          status: 'healthy',
          checkedAt: '2026-11-26T03:31:00Z',
          uptimeSeconds: 240,
          startedAt: '2026-11-26T03:27:00Z',
          revision: '21497cd',
          database: { status: 'up', latencyMs: 2 },
          traffic: {
            windowSeconds: 300,
            requestCount: 5,
            minSamples: 20,
            p95Status: 'insufficient_data',
            p95Ms: null,
          },
          github,
        },
        timestamp: '2026-11-26T03:31:00Z',
      },
    });
  });

  await page.goto('/status');
  await expect(page.getByRole('cell', { name: 'Healthy', exact: true })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Database Up', exact: true })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'GitHub Stale', exact: true })).toBeVisible();
  await expect(page.getByText(/Snapshot age: 17m 0s/)).toBeVisible();
  await expect(page.getByRole('link', { name: 'View workflow run (21497cd)' })).toBeVisible();

  step = 1;
  await page.getByRole('button', { name: 'Refresh snapshot', exact: true }).click();
  await expect(page.getByRole('cell', { name: 'GitHub Degraded', exact: true })).toBeVisible();
  await expect(page.getByText(/One GitHub feed failed/)).toBeVisible();

  step = 2;
  await page.getByRole('button', { name: 'Refresh snapshot', exact: true }).click();
  await expect(page.getByRole('cell', { name: 'GitHub Empty', exact: true })).toBeVisible();
  await expect(page.getByText('No workflow runs have been recorded for this repository.')).toBeVisible();
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
  await page.route('**/api/v1/status', async (route) => {
    await route.fulfill({
      status: 200,
      json: {
        success: true,
        data: {
          status: 'healthy',
          checkedAt: '2026-11-26T03:30:00Z',
          uptimeSeconds: 185,
          startedAt: '2026-11-26T03:26:55Z',
          revision: '21497cd',
          database: { status: 'up', latencyMs: 2 },
          traffic: {
            windowSeconds: 300,
            requestCount: 4,
            minSamples: 20,
            p95Status: 'insufficient_data',
            p95Ms: null,
          },
          github: HEALTHY_GITHUB_FIXTURE,
        },
        timestamp: '2026-11-26T03:30:00Z',
      },
    });
  });

  await page.setViewportSize({ width: 375, height: 812 });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.goto('/status');
  await expect(page.getByRole('heading', { level: 1, name: 'System Status' })).toBeVisible();
  await expect(page.getByRole('heading', { level: 2, name: 'Repository CI & Activity Sync' })).toBeVisible();
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
