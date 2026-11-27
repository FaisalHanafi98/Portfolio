import { expect, test } from '@playwright/test';

const POPULATED_ANALYTICS_FIXTURE = {
  generatedAt: '2026-11-27T12:35:00Z',
  lastRecordedAt: '2026-11-27T12:30:00Z',
  windowDays: 30,
  rawRetentionDays: 30,
  aggregateRetentionMonths: 12,
  minTopicCellCount: 5,
  totalEvents: 29,
  projectFunnel: [
    {
      projectSlug: 'creams',
      caseStudyOpens: 4,
      meaningfulReads: 3,
      conversionRatePercent: 75,
      depth25Count: 4,
      depth50Count: 4,
      depth75Count: 3,
      depth100Count: 2,
    },
    {
      projectSlug: 'sen2nal',
      caseStudyOpens: 0,
      meaningfulReads: 0,
      conversionRatePercent: null,
      depth25Count: 0,
      depth50Count: 0,
      depth75Count: 0,
      depth100Count: 0,
    },
  ],
  topics: [
    {
      topicLabel: 'ai_retrieval',
      eventCount: 9,
    },
  ],
  suppressedTopicCellCount: 2,
};

test.beforeAll(async ({ request }) => {
  await expect.poll(async () => {
    const response = await request.get('/api/v1/analytics/summary');
    if (!response.ok()) return false;
    const body = await response.json();
    return Boolean(body?.success && Array.isArray(body?.data?.projectFunnel));
  }, { message: 'Start the local backend before running analytics browser tests', timeout: 30_000 }).toBe(true);
});

test('visitors open CREAMS case study, ingest an analytics event, and inspect persisted summary counts on /analytics', async ({ page, request }) => {
  const beforeRes = await request.get('/api/v1/analytics/summary');
  expect(beforeRes.ok()).toBe(true);
  const beforeBody = await beforeRes.json();
  const beforeOpens =
    beforeBody.data.projectFunnel.find((item: { projectSlug: string }) => item.projectSlug === 'creams')
      ?.caseStudyOpens ?? 0;

  const openEventPromise = page.waitForResponse((response) => {
    if (response.request().method() !== 'POST') return false;
    if (!response.url().includes('/api/v1/analytics/events')) return false;
    const body = response.request().postDataJSON() as { eventName?: string; projectSlug?: string } | null;
    return body?.eventName === 'case_study_open' && body?.projectSlug === 'creams';
  });

  await page.goto('/projects/creams');
  const openResponse = await openEventPromise;
  expect([200, 202]).toContain(openResponse.status());

  await page.locator('footer').getByRole('link', { name: 'Analytics Evidence', exact: true }).click();
  await expect(page).toHaveURL('/analytics');
  await expect(page.getByRole('heading', { level: 1, name: 'Analytics Evidence' })).toBeVisible();

  const creamsRow = page.locator('tbody tr').filter({ hasText: 'CREAMS' });
  await expect(creamsRow).toBeVisible();
  await expect.poll(async () => {
    const text = await creamsRow.locator('td').first().innerText();
    const match = /(\d+)\s+opens/.exec(text);
    return match ? Number(match[1]) : 0;
  }).toBeGreaterThanOrEqual(beforeOpens + 1);

  await expect(page.getByText(/\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2} UTC/).first()).toBeVisible();

  await page.getByRole('link', { name: 'System Status', exact: true }).first().click();
  await expect(page).toHaveURL('/status');
  await page.getByRole('link', { name: 'Analytics Evidence', exact: true }).first().click();
  await expect(page).toHaveURL('/analytics');
  await page.getByRole('link', { name: 'Back to Portfolio', exact: true }).click();
  await expect(page).toHaveURL('/');
});

test('analytics page renders funnel denominators, topic suppression notice, and preserves focus on manual refresh', async ({ page }) => {
  await page.route('**/api/v1/analytics/summary*', async (route) => {
    await route.fulfill({
      status: 200,
      json: {
        success: true,
        data: POPULATED_ANALYTICS_FIXTURE,
        timestamp: '2026-11-27T12:35:00Z',
      },
    });
  });

  await page.goto('/analytics');
  await expect(page.getByRole('heading', { level: 1, name: 'Analytics Evidence' })).toBeVisible();
  await expect(page.getByText('4 opens')).toBeVisible();
  await expect(page.getByText('75.00% (3 / 4)')).toBeVisible();
  await expect(page.getByText('75%: 3')).toBeVisible();
  await expect(page.getByText('Insufficient sample (0 opens)')).toBeVisible();
  await expect(page.getByText('AI & Retrieval')).toBeVisible();
  await expect(page.getByText('9 events')).toBeVisible();
  await expect(page.getByText('2 sparse topic cells suppressed (< 5 events)').first()).toBeVisible();
  await expect(page.getByText('30-day raw / 12-month aggregate')).toBeVisible();

  const refresh = page.getByRole('button', { name: 'Refresh analytics summary', exact: true });
  await refresh.focus();
  await refresh.press('Enter');
  await expect(refresh).toBeFocused();
});

test('analytics page retains cached summary on refresh 503 and recovers via keyboard retry', async ({ page }) => {
  let failRefresh = false;
  await page.route('**/api/v1/analytics/summary*', async (route) => {
    if (failRefresh) {
      await route.fulfill({
        status: 503,
        json: { success: false, data: null, error: 'Fixture unavailable' },
      });
    } else {
      await route.fulfill({
        status: 200,
        json: {
          success: true,
          data: POPULATED_ANALYTICS_FIXTURE,
          timestamp: '2026-11-27T12:35:00Z',
        },
      });
    }
  });

  await page.goto('/analytics');
  await expect(page.getByText('75.00% (3 / 4)')).toBeVisible();

  failRefresh = true;
  await page.getByRole('button', { name: 'Refresh analytics summary', exact: true }).click();
  await expect(page.getByText("Couldn't refresh analytics summary. Showing the last loaded result.")).toBeVisible();
  await expect(page.getByText('75.00% (3 / 4)')).toBeVisible();

  failRefresh = false;
  const retry = page.getByRole('button', { name: 'Try again to load analytics summary', exact: true });
  await retry.focus();
  await retry.press('Enter');
  await expect(page.getByText("Couldn't refresh analytics summary. Showing the last loaded result.")).toHaveCount(0);
  await expect(page.getByText('75.00% (3 / 4)')).toBeVisible();
});

test('analytics page avoids horizontal overflow on 375px mobile under reduced motion', async ({ page }) => {
  await page.route('**/api/v1/analytics/summary*', async (route) => {
    await route.fulfill({
      status: 200,
      json: {
        success: true,
        data: POPULATED_ANALYTICS_FIXTURE,
        timestamp: '2026-11-27T12:35:00Z',
      },
    });
  });

  await page.setViewportSize({ width: 375, height: 812 });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.goto('/analytics');
  await expect(page.getByRole('heading', { level: 1, name: 'Analytics Evidence' })).toBeVisible();
  await expect(page.getByRole('heading', { level: 2, name: 'Case-Study Engagement Funnel & Read Depth' })).toBeVisible();
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
