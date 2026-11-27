import assert from 'node:assert/strict';
import { after, test } from 'node:test';
import { createServer } from 'vite';

const vite = await createServer({
  configFile: false,
  cacheDir: 'node_modules/.cache/portfolio-analytics-tests',
  optimizeDeps: { noDiscovery: true, include: [] },
  server: { middlewareMode: true, hmr: false, ws: false },
  ssr: { resolve: { externalConditions: ['node', 'module-sync'] } },
});
after(() => vite.close());

const {
  buildAnalyticsPayload,
  createCaseStudyVisibilityMonitor,
  createEventId,
  postAnalyticsEvent,
} = await vite.ssrLoadModule('/src/lib/analytics.ts');

test('buildAnalyticsPayload enforces allowlists and omits URLs, query strings, and free text', () => {
  const valid = buildAnalyticsPayload({
    eventName: 'case_study_open',
    projectSlug: 'creams',
    sectionKey: 'architecture',
    depthBucket: '75',
    topicLabel: 'software_engineering',
  });

  assert.ok(valid);
  assert.match(valid.eventId, /^[A-Za-z0-9_-]{12,64}$/);
  assert.equal(valid.schemaVersion, 1);
  assert.equal(valid.eventName, 'case_study_open');
  assert.equal(valid.projectSlug, 'creams');
  assert.deepEqual(Object.keys(valid).sort(), [
    'depthBucket',
    'eventId',
    'eventName',
    'projectSlug',
    'schemaVersion',
    'sectionKey',
    'topicLabel',
  ]);

  assert.equal(
    buildAnalyticsPayload({
      eventName: 'case_study_open',
      projectSlug: 'unknown-slug?token=secret',
    }),
    null,
  );
  assert.equal(
    buildAnalyticsPayload({
      eventName: 'free_text_event',
      projectSlug: 'creams',
    }),
    null,
  );
});

test('visibility monitor distinguishes active reading time from background tabs and emits each depth once', () => {
  const emitted = [];
  let visible = false;
  const monitor = createCaseStudyVisibilityMonitor({
    projectSlug: 'sen2nal',
    isVisible: () => visible,
    emit: (payload) => emitted.push(payload),
  });

  monitor.tick(60_000);
  assert.equal(emitted.length, 0);

  visible = true;
  monitor.tick(15_000);
  assert.equal(emitted.filter((e) => e.eventName === 'meaningful_read').length, 0);

  monitor.tick(15_000);
  monitor.tick(30_000);
  const meaningful = emitted.filter((e) => e.eventName === 'meaningful_read');
  assert.equal(meaningful.length, 1);
  assert.equal(meaningful[0].projectSlug, 'sen2nal');
  assert.equal(meaningful[0].depthBucket, 'meaningful_30s');

  monitor.recordScrollPercent(80);
  monitor.recordScrollPercent(85);
  const depths = emitted.filter((e) => e.eventName === 'read_depth').map((e) => e.depthBucket);
  assert.deepEqual(depths, ['25', '50', '75']);

  monitor.recordSection('architecture');
  monitor.recordSection('architecture');
  assert.equal(emitted.filter((e) => e.eventName === 'section_view').length, 1);
});

test('postAnalyticsEvent reuses the exact eventId on transient retry and stays safe when offline', async () => {
  const payload = buildAnalyticsPayload({
    eventName: 'page_view',
    projectSlug: '__site__',
    sectionKey: 'hero',
    eventId: createEventId(),
  });
  assert.ok(payload);

  const sentIds = [];
  let attempt = 0;
  const result = await postAnalyticsEvent(payload, {
    isOnline: () => true,
    sendFetch: async (_url, init) => {
      attempt += 1;
      sentIds.push(JSON.parse(init.body).eventId);
      if (attempt === 1) {
        return { ok: false, status: 503 };
      }
      return { ok: true, status: 202 };
    },
  });

  assert.equal(result, true);
  assert.equal(sentIds.length, 2);
  assert.equal(sentIds[0], sentIds[1]);

  let calledWhenOffline = false;
  const offlineResult = await postAnalyticsEvent(payload, {
    isOnline: () => false,
    sendFetch: async () => {
      calledWhenOffline = true;
      return { ok: true, status: 202 };
    },
  });
  assert.equal(offlineResult, false);
  assert.equal(calledWhenOffline, false);
});
