import assert from 'node:assert/strict';
import { after, test } from 'node:test';
import { AxiosError } from 'axios';
import { onlineManager, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { createElement } from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { Route, Routes, StaticRouter } from 'react-router-dom';
import { createServer } from 'vite';

const vite = await createServer({
  configFile: false,
  cacheDir: 'node_modules/.cache/portfolio-content-tests',
  optimizeDeps: { noDiscovery: true, include: [] },
  server: { middlewareMode: true, hmr: false },
  ssr: { resolve: { externalConditions: ['node', 'module-sync'] } },
});
after(() => vite.close());

const { default: ProjectPage } = await vite.ssrLoadModule('/src/pages/ProjectPage.tsx');
const { default: HomePage } = await vite.ssrLoadModule('/src/pages/HomePage.tsx');
const { default: StatusPage } = await vite.ssrLoadModule('/src/pages/StatusPage.tsx');
const { default: AnalyticsPage } = await vite.ssrLoadModule('/src/pages/AnalyticsPage.tsx');
const { default: Navbar } = await vite.ssrLoadModule('/src/components/layout/Navbar.tsx');

const project = {
  id: 1, slug: 'fixture', title: 'Fixture project', shortDescription: 'Short description',
  fullDescription: 'Full description', period: '2026', featured: false, technologies: [], images: [],
};

function renderRoute(location, path, Page, queries) {
  const client = new QueryClient({
    defaultOptions: { queries: { retryOnMount: false, refetchOnMount: false } },
  });
  for (const [queryKey, state] of queries) {
    client.getQueryCache().build(client, { queryKey }).setState(state);
  }

  return renderToStaticMarkup(createElement(QueryClientProvider, { client },
    createElement(StaticRouter, { location },
      createElement(Routes, null,
        createElement(Route, { path, element: createElement(Page) }),
      ),
    ),
  ));
}

function renderProject(state) {
  return renderRoute('/projects/fixture', '/projects/:slug', ProjectPage, [[['project', 'fixture'], state]]);
}

function renderHome(queries) {
  return renderRoute('/', '/', HomePage, queries);
}

function httpError(status) {
  return new AxiosError('Fixture response', undefined, undefined, undefined, { status });
}

test('the mobile navigation button exposes the collapsed state, identifies its panel, and provides a safe resume request link when no PDF is bundled', () => {
  const html = renderRoute('/', '/', Navbar, []);

  assert.match(html, /<button[^>]+aria-controls="mobile-navigation"[^>]+aria-expanded="false"/);
  assert.match(html, /id="mobile-navigation"/);
  assert.match(html, /href="mailto:faisalhanafi\.dev@gmail\.com\?subject=Resume%20Request[^"]*"[^>]*>Request Resume/);
  assert.doesNotMatch(html, /href="\/resume\.pdf"/);
});

test('case-study detail renders structured section labels for problem, solution, architecture, and constraints', () => {
  const richProject = {
    ...project,
    slug: 'creams',
    problemStatement: 'Paper records caused lost progress.',
    solution: 'Built a 6-role Laravel platform.',
    architectureDecisions: ['6-role RBAC middleware'],
    challenges: ['Concurrent case updates'],
    costJustification: 'Zero infrastructure cost during university evaluation.',
  };
  const html = renderProject({ status: 'success', data: richProject });

  assert.match(html, /01 \/ Context &amp; Problem/);
  assert.match(html, /02 \/ Approach &amp; Delivery/);
  assert.match(html, /03 \/ System Design &amp; Trade-offs/);
  assert.match(html, /04 \/ Engineering Constraints/);
  assert.match(html, /05 \/ Operational Reality &amp; Outcome/);
});

test('experience exposes the selected company and its matching role', () => {
  const companies = [
    { id: 42, company: 'First company', role: 'First role', startDate: '2020-01-01', highlights: [] },
    { id: 17, company: 'Second company', role: 'Second role', startDate: '2021-01-01', highlights: [] },
  ];
  const html = renderHome([[['experience'], { status: 'success', data: companies }]]);

  assert.match(html, /aria-pressed="true"[^>]*>First company/);
  assert.match(html, /aria-pressed="false"[^>]*>Second company/);
  assert.match(html, /First role/);
  assert.doesNotMatch(html, /Second role/);
});

test('project detail does not pair a project with another project’s screenshot', () => {
  for (const slug of ['auto-recruit', 'portfolio']) {
    const html = renderProject({ status: 'success', data: { ...project, slug } });

    assert.match(html, /Fixture project/);
    assert.doesNotMatch(html, /<img[^>]+src="\/images\/(creams|ringgitsense)\.png"/);
  }
});

test('project detail offers a retry when the API returns HTTP 500', () => {
  const html = renderProject({ status: 'error', error: httpError(500) });

  assert.match(html, /Unable to load project/);
  assert.match(html, /<button[^>]*>Try again<\/button>/);
  assert.doesNotMatch(html, /Project Not Found/);
});

test('project detail retains a cached result after a failed refresh', () => {
  const html = renderProject({ status: 'error', error: httpError(503), data: project });

  assert.match(html, /Fixture project/);
  assert.match(html, /Showing the last loaded result/);
});

test('a confirmed project 404 takes precedence over a cached result', () => {
  const html = renderProject({ status: 'error', error: httpError(404), data: project });

  assert.match(html, /Project Not Found/);
  assert.match(html, /href="\/"[^>]*>Go Home/);
  assert.doesNotMatch(html, /Fixture project|Try again/);
});

test('loading a project announces progress and hides the decorative skeleton', () => {
  const html = renderProject({ status: 'pending', fetchStatus: 'fetching' });

  assert.match(html, /role="status"[^>]*>Loading project/);
  assert.match(html, /aria-hidden="true"/);
});

test('an initial request paused offline shows a connection message', () => {
  const wasOnline = onlineManager.isOnline();
  onlineManager.setOnline(false);
  try {
    const html = renderProject({ status: 'pending', fetchStatus: 'paused' });

    assert.match(html, /Waiting for a connection to load project/);
    assert.doesNotMatch(html, /Loading project|Project Not Found/);
  } finally {
    onlineManager.setOnline(wasOnline);
  }
});

test('home distinguishes empty successful lists from failed requests', () => {
  const empty = { status: 'success', data: [] };
  const html = renderHome([
    [['projects'], empty],
    [['skills'], empty],
    [['experience'], empty],
  ]);

  assert.match(html, /No projects available/);
  assert.match(html, /No skills available/);
  assert.match(html, /No experience data available/);
  assert.doesNotMatch(html, /Unable to load|Try again/);
});

test('home keeps cached projects, skills and experience visible after refresh failures', () => {
  const skillCategories = [{ id: 1, name: 'Fixture category', skills: [{ id: 1, name: 'Fixture skill', proficiency: 3 }] }];
  const experiences = [{ id: 1, company: 'Fixture company', role: 'Fixture role', startDate: '2020-01-01', highlights: [] }];
  const failure = { status: 'error', error: httpError(500) };
  const html = renderHome([
    [['projects'], { ...failure, data: [project] }],
    [['skills'], { ...failure, data: skillCategories }],
    [['experience'], { ...failure, data: experiences }],
  ]);

  for (const label of ['Fixture project', 'Fixture skill', 'Fixture company']) {
    assert.ok(html.includes(label), `Expected to preserve ${label}`);
  }
  assert.equal(html.match(/Showing the last loaded result/g)?.length, 3);
});

test('about section renders the interactive story carousel and omits static academic stat boxes', () => {
  const html = renderHome([]);

  assert.match(html, /How I Think, Work &amp; Build/);
  assert.match(html, /role="tablist"[^>]+aria-label="Professional story chapters"/);
  assert.match(html, /Spot the Friction, Build the Structure/);
  assert.doesNotMatch(html, /Dean&#x27;s List Semesters|FYP Recognition/);
});

test('the flagship trilogy leads and additional projects remain discoverable', () => {
  const projectsList = [
    { ...project, id: 10, slug: 'creams', title: 'CREAMS', featured: true },
    { ...project, id: 11, slug: 'sen2nal', title: 'Sen2Nal', featured: true },
    { ...project, id: 12, slug: 'baiti', title: 'BAiTI', featured: true },
    { ...project, id: 13, slug: 'auto-recruit', title: 'Auto-Recruit', featured: false },
    { ...project, id: 14, slug: 'portfolio', title: 'Portfolio Self Entry', featured: true },
  ];
  const html = renderHome([[['projects'], { status: 'success', data: projectsList }]]);

  assert.match(html, /CREAMS[\s\S]*Sen2Nal[\s\S]*BAiTI[\s\S]*Additional projects[\s\S]*Auto-Recruit/);
  assert.match(html, /01 \/ What This Project Is About/);
  assert.match(html, /02 \/ What I Did/);
  assert.match(html, /03 \/ Why People Care/);
  assert.doesNotMatch(html, /Portfolio Self Entry/);
});

test('status page renders loading, offline pause, insufficient data, measured p95, degraded DB, cached refresh failure, and all 5 GitHub poller states distinctly', () => {
  const baseStatus = {
    status: 'healthy',
    checkedAt: '2026-11-26T03:00:00Z',
    uptimeSeconds: 125,
    startedAt: '2026-11-26T02:57:55Z',
    revision: '53f0c21',
    database: { status: 'up', latencyMs: 2 },
    traffic: {
      windowSeconds: 300,
      requestCount: 4,
      minSamples: 20,
      p95Status: 'insufficient_data',
      p95Ms: null,
    },
    github: {
      status: 'healthy',
      repository: 'FaisalHanafi98/Portfolio',
      lastSyncedAt: '2026-11-26T02:55:00Z',
      snapshotAgeSeconds: 300,
      cacheTtlSeconds: 900,
      nextPollAt: '2026-11-26T03:10:00Z',
      stateReason: null,
      workflowRuns: [
        {
          id: 101,
          name: 'CI',
          branch: 'main',
          event: 'push',
          status: 'completed',
          conclusion: 'success',
          shortSha: '21497cd',
          updatedAt: '2026-11-26T02:54:00Z',
          htmlUrl: 'https://github.com/FaisalHanafi98/Portfolio/actions/runs/101',
        },
      ],
      recentCommits: [
        {
          shortSha: '21497cd',
          message: 'Feat(Status): Add public /status frontend view.',
          committedAt: '2026-11-26T02:50:00Z',
          htmlUrl: 'https://github.com/FaisalHanafi98/Portfolio/commit/21497cd',
        },
      ],
    },
  };

  const loadingHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], { status: 'pending', fetchStatus: 'fetching' }],
  ]);
  assert.match(loadingHtml, /role="status"[^>]*>Loading status/);
  assert.match(loadingHtml, /aria-hidden="true"/);

  const wasOnline = onlineManager.isOnline();
  onlineManager.setOnline(false);
  try {
    const pausedHtml = renderRoute('/status', '/status', StatusPage, [
      [['status'], { status: 'pending', fetchStatus: 'paused' }],
    ]);
    assert.match(pausedHtml, /Waiting for a connection to load status/);
  } finally {
    onlineManager.setOnline(wasOnline);
  }

  const healthyGitHubHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], { status: 'success', data: baseStatus }],
  ]);
  assert.match(healthyGitHubHtml, /Healthy/);
  assert.match(healthyGitHubHtml, /Database Up/);
  assert.match(healthyGitHubHtml, /GitHub Healthy/);
  assert.match(healthyGitHubHtml, /FaisalHanafi98\/Portfolio/);
  assert.match(healthyGitHubHtml, /15-minute server cache/);
  assert.match(healthyGitHubHtml, /5m 0s/);
  assert.match(healthyGitHubHtml, /Feat\(Status\): Add public \/status frontend view\./);
  assert.match(healthyGitHubHtml, /Insufficient data/);
  assert.match(healthyGitHubHtml, /4 of 20 minimum requests/);
  assert.match(healthyGitHubHtml, /5-minute rolling window/);
  assert.match(healthyGitHubHtml, /<table/);

  const staleAndDbDegradedHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], {
      status: 'error',
      error: httpError(503),
      data: {
        ...baseStatus,
        status: 'degraded',
        database: { status: 'degraded', latencyMs: null },
        traffic: {
          windowSeconds: 300,
          requestCount: 24,
          minSamples: 20,
          p95Status: 'measured',
          p95Ms: 14.25,
        },
        github: {
          ...baseStatus.github,
          status: 'stale',
          snapshotAgeSeconds: 1020,
          stateReason: 'upstream_rate_limited',
        },
      },
    }],
  ]);
  assert.match(staleAndDbDegradedHtml, /Degraded/);
  assert.match(staleAndDbDegradedHtml, /14\.25 ms/);
  assert.match(staleAndDbDegradedHtml, /GitHub Stale/);
  assert.match(staleAndDbDegradedHtml, /17m 0s/);
  assert.match(staleAndDbDegradedHtml, /rate limit/i);
  assert.match(staleAndDbDegradedHtml, /Showing the last loaded result/);
  assert.match(staleAndDbDegradedHtml, /<button[^>]*>Try again<\/button>/);

  const partialDegradedHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], {
      status: 'success',
      data: {
        ...baseStatus,
        github: {
          ...baseStatus.github,
          status: 'degraded',
          stateReason: 'partial_feed_error',
          recentCommits: [],
        },
      },
    }],
  ]);
  assert.match(partialDegradedHtml, /GitHub Degraded/);
  assert.match(partialDegradedHtml, /One GitHub feed failed/);

  const unavailableHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], {
      status: 'success',
      data: {
        ...baseStatus,
        github: {
          ...baseStatus.github,
          status: 'unavailable',
          lastSyncedAt: null,
          snapshotAgeSeconds: null,
          nextPollAt: null,
          stateReason: 'awaiting_first_sync',
          workflowRuns: [],
          recentCommits: [],
        },
      },
    }],
  ]);
  assert.match(unavailableHtml, /GitHub Unavailable/);
  assert.match(unavailableHtml, /No cached GitHub snapshot/);

  const emptyHtml = renderRoute('/status', '/status', StatusPage, [
    [['status'], {
      status: 'success',
      data: {
        ...baseStatus,
        github: {
          ...baseStatus.github,
          status: 'empty',
          snapshotAgeSeconds: 0,
          stateReason: 'empty_activity',
          workflowRuns: [],
          recentCommits: [],
        },
      },
    }],
  ]);
  assert.match(emptyHtml, /GitHub Empty/);
  assert.match(emptyHtml, /zero workflow runs/i);
});
function renderAnalytics(state) {
  return renderRoute('/analytics', '/analytics', AnalyticsPage, [[['analytics-summary'], state]]);
}

const analyticsSummaryFixture = {
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

test('loading analytics summary announces progress and hides the decorative skeleton', () => {
  const html = renderAnalytics({ status: 'pending', fetchStatus: 'fetching' });

  assert.match(html, /role="status"[^>]*>Loading analytics summary/);
  assert.match(html, /aria-hidden="true"/);
});

test('analytics summary paused offline shows a connection message', () => {
  const wasOnline = onlineManager.isOnline();
  onlineManager.setOnline(false);
  try {
    const html = renderAnalytics({ status: 'pending', fetchStatus: 'paused' });

    assert.match(html, /Waiting for a connection to load analytics summary/);
    assert.doesNotMatch(html, /Loading analytics summary/);
  } finally {
    onlineManager.setOnline(wasOnline);
  }
});

test('analytics page renders funnel denominators, read depths, sparse topic suppression, retention, and freshness', () => {
  const html = renderAnalytics({ status: 'success', data: analyticsSummaryFixture });

  assert.match(html, /Analytics Evidence/);
  assert.match(html, /CREAMS/);
  assert.match(html, /4 opens/);
  assert.match(html, /75\.00%/);
  assert.match(html, /3 \/ 4/);
  assert.match(html, /75%: 3/);
  assert.match(html, /Insufficient sample \(0 opens\)/);
  assert.match(html, /AI &amp; Retrieval/);
  assert.match(html, /2 sparse topic cells suppressed \(&lt; 5 events\)/);
  assert.match(html, /30-day raw \/ 12-month aggregate/);
  assert.match(html, /Last Recorded Event/);
  assert.match(html, /2026-11-27 12:30:00 UTC/);
});

test('analytics page renders explicit zero-events and zero-denominator copy when empty', () => {
  const html = renderAnalytics({
    status: 'success',
    data: {
      ...analyticsSummaryFixture,
      lastRecordedAt: null,
      totalEvents: 0,
      projectFunnel: [
        {
          projectSlug: 'creams',
          caseStudyOpens: 0,
          meaningfulReads: 0,
          conversionRatePercent: null,
          depth25Count: 0,
          depth50Count: 0,
          depth75Count: 0,
          depth100Count: 0,
        },
      ],
      topics: [],
      suppressedTopicCellCount: 0,
    },
  });

  assert.match(html, /No events recorded yet/);
  assert.match(html, /No engagement events have been recorded in the active window yet/);
  assert.match(html, /Insufficient sample \(0 opens\)/);
  assert.match(html, /No topic categories currently meet the minimum reporting threshold of 5 events/);
});

test('analytics page retains cached metrics and exposes retry when refresh returns 503', () => {
  const html = renderAnalytics({
    status: 'error',
    error: httpError(503),
    data: analyticsSummaryFixture,
  });

  assert.match(html, /Showing the last loaded result/);
  assert.match(html, /<button[^>]*>Try again<\/button>/);
  assert.match(html, /75\.00%/);
  assert.match(html, /CREAMS/);
});
