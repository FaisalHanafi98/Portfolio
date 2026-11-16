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
  assert.match(html, /href="mailto:faisalhanafi98@gmail\.com\?subject=Resume%20Request[^"]*"[^>]*>Request Resume/);
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
