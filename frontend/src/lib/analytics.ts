const EVENT_NAMES = new Set([
  'page_view',
  'case_study_open',
  'meaningful_read',
  'read_depth',
  'section_view',
  'topic_category',
]);

const PROJECT_SLUGS = new Set([
  '__site__',
  'creams',
  'sen2nal',
  'baiti',
  'auto-recruit',
  'ringgitsense',
]);

const SECTION_KEYS = new Set([
  'none',
  'hero',
  'about',
  'projects',
  'skills',
  'experience',
  'contact',
  'status',
  'analytics',
  'problem',
  'solution',
  'architecture',
  'constraints',
  'overview',
]);

const DEPTH_BUCKETS = new Set([
  'none',
  '25',
  '50',
  '75',
  '100',
  'meaningful_30s',
]);

const TOPIC_LABELS = new Set([
  'none',
  'software_engineering',
  'data_analytics',
  'devops',
  'ai_retrieval',
]);

const EVENT_ID_PATTERN = /^[A-Za-z0-9_-]{12,64}$/;
const DEPTH_MILESTONES = [25, 50, 75, 100] as const;
const MEANINGFUL_READ_MS = 30_000;

export interface AnalyticsEventPayload {
  eventId: string;
  schemaVersion: number;
  eventName: string;
  projectSlug: string;
  sectionKey: string;
  depthBucket: string;
  topicLabel: string;
}

export interface AnalyticsEventInput {
  eventId?: string;
  eventName: string;
  projectSlug?: string;
  sectionKey?: string;
  depthBucket?: string;
  topicLabel?: string;
}

export function createEventId(): string {
  const randomUuid = globalThis.crypto?.randomUUID?.();
  if (randomUuid && EVENT_ID_PATTERN.test(randomUuid)) {
    return randomUuid;
  }
  const timePart = Date.now().toString(36);
  const randPart = Math.random().toString(36).slice(2, 12);
  return `evt-${timePart}-${randPart}`;
}

export function buildAnalyticsPayload(input: AnalyticsEventInput): AnalyticsEventPayload | null {
  const eventName = input.eventName?.trim() ?? '';
  if (!EVENT_NAMES.has(eventName)) return null;

  const projectSlug = input.projectSlug?.trim() || '__site__';
  if (!PROJECT_SLUGS.has(projectSlug)) return null;

  const sectionKey = input.sectionKey?.trim() || 'none';
  if (!SECTION_KEYS.has(sectionKey)) return null;

  const depthBucket = input.depthBucket?.trim() || 'none';
  if (!DEPTH_BUCKETS.has(depthBucket)) return null;

  const topicLabel = input.topicLabel?.trim() || 'none';
  if (!TOPIC_LABELS.has(topicLabel)) return null;

  const eventId = (input.eventId ?? createEventId()).trim();
  if (!EVENT_ID_PATTERN.test(eventId)) return null;

  return {
    eventId,
    schemaVersion: 1,
    eventName,
    projectSlug,
    sectionKey,
    depthBucket,
    topicLabel,
  };
}

interface FetchLikeResponse {
  ok: boolean;
  status: number;
}

export interface AnalyticsTransport {
  isOnline?: () => boolean;
  sendFetch?: (url: string, init: { method: string; headers: Record<string, string>; body: string; keepalive: boolean }) => Promise<FetchLikeResponse>;
}

function getAnalyticsEndpointUrl(): string {
  const env = (import.meta as { env?: { PROD?: boolean; VITE_API_URL?: string } }).env;
  const base = env?.PROD ? '/api/v1' : env?.VITE_API_URL || '/api/v1';
  return `${base}/analytics/events`;
}

export async function postAnalyticsEvent(
  payload: AnalyticsEventPayload | null,
  transport?: AnalyticsTransport,
): Promise<boolean> {
  if (!payload) return false;

  const isOnline = transport?.isOnline ?? (() => (typeof navigator !== 'undefined' ? navigator.onLine !== false : false));
  if (!isOnline()) return false;

  const sendFetch =
    transport?.sendFetch
    ?? (typeof fetch === 'function'
      ? (url, init) => fetch(url, init)
      : null);
  if (!sendFetch) return false;

  const endpoint = getAnalyticsEndpointUrl();
  const requestInit = {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
    keepalive: true,
  };

  for (let attempt = 0; attempt < 2; attempt += 1) {
    try {
      const response = await sendFetch(endpoint, requestInit);
      if (response.ok) return true;
      if (response.status >= 400 && response.status < 500) return false;
    } catch {
      // Best-effort retry once with identical eventId for idempotency
    }
  }
  return false;
}

export function createCaseStudyVisibilityMonitor(options: {
  projectSlug: string;
  isVisible: () => boolean;
  emit: (payload: AnalyticsEventPayload) => void;
}) {
  let visibleMs = 0;
  let sentMeaningfulRead = false;
  const sentDepths = new Set<number>();
  const sentSections = new Set<string>();

  return {
    tick(elapsedMs: number) {
      if (sentMeaningfulRead || elapsedMs <= 0 || !options.isVisible()) return;
      visibleMs += elapsedMs;
      if (visibleMs >= MEANINGFUL_READ_MS) {
        sentMeaningfulRead = true;
        const payload = buildAnalyticsPayload({
          eventName: 'meaningful_read',
          projectSlug: options.projectSlug,
          depthBucket: 'meaningful_30s',
        });
        if (payload) options.emit(payload);
      }
    },

    recordScrollPercent(percent: number) {
      for (const milestone of DEPTH_MILESTONES) {
        if (percent >= milestone && !sentDepths.has(milestone)) {
          sentDepths.add(milestone);
          const payload = buildAnalyticsPayload({
            eventName: 'read_depth',
            projectSlug: options.projectSlug,
            depthBucket: String(milestone),
          });
          if (payload) options.emit(payload);
        }
      }
    },

    recordSection(sectionKey: string) {
      if (sentSections.has(sectionKey)) return;
      const payload = buildAnalyticsPayload({
        eventName: 'section_view',
        projectSlug: options.projectSlug,
        sectionKey,
      });
      if (payload) {
        sentSections.add(sectionKey);
        options.emit(payload);
      }
    },
  };
}
