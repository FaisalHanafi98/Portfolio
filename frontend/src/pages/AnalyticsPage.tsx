import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAnalyticsSummary } from '../api/hooks';
import QueryContent from '../components/ui/QueryContent';
import { AnalyticsSkeleton } from '../components/ui/Skeleton';
import { pageTransition } from '../lib/animations';
import type { AnalyticsSummary, ProjectFunnelMetric } from '../types';

const PROJECT_TITLES: Record<string, string> = {
  creams: 'CREAMS',
  sen2nal: 'Sen2Nal',
  baiti: 'BAiTI',
  'auto-recruit': 'Auto-Recruit',
  ringgitsense: 'RinggitSense',
};

const TOPIC_TITLES: Record<string, string> = {
  software_engineering: 'Software Engineering',
  data_analytics: 'Data Analytics',
  devops: 'DevOps',
  ai_retrieval: 'AI & Retrieval',
};

function formatUtcTimestamp(iso: string): string {
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return iso;
  return `${date.toISOString().slice(0, 19).replace('T', ' ')} UTC`;
}

function formatProjectTitle(slug: string): string {
  return PROJECT_TITLES[slug] ?? slug;
}

function formatTopicTitle(label: string): string {
  return TOPIC_TITLES[label] ?? label.replace(/_/g, ' ');
}

function formatConversionCell(metric: ProjectFunnelMetric): string {
  if (metric.caseStudyOpens <= 0 || metric.conversionRatePercent === null) {
    return `Insufficient sample (${metric.caseStudyOpens} opens)`;
  }
  return `${metric.conversionRatePercent.toFixed(2)}% (${metric.meaningfulReads} / ${metric.caseStudyOpens})`;
}

interface AnalyticsViewProps {
  summary: AnalyticsSummary;
  isRefreshing: boolean;
  onRefresh: () => void;
}

function AnalyticsView({ summary, isRefreshing, onRefresh }: AnalyticsViewProps) {
  const retentionLabel = `${summary.rawRetentionDays}-day raw / ${summary.aggregateRetentionMonths}-month aggregate`;
  const lastRecordedLabel = summary.lastRecordedAt
    ? formatUtcTimestamp(summary.lastRecordedAt)
    : 'No events recorded yet';
  const suppressionNoun = summary.suppressedTopicCellCount === 1 ? 'cell' : 'cells';

  return (
    <div className="space-y-8">
      <section
        aria-labelledby="analytics-overview-heading"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <div className="flex flex-wrap items-start justify-between gap-4 mb-6">
          <div className="min-w-0">
            <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-wider font-semibold px-3 py-1 rounded-full border border-green-dark/40 bg-green/15 text-green-dark mb-2">
              First-Party Aggregate Summary
            </div>
            <h2
              id="analytics-overview-heading"
              className="text-xl font-bold text-charcoal break-words"
            >
              Sample Size, Freshness &amp; Retention
            </h2>
            <p className="font-mono text-xs text-charcoal-light mt-1 tabular-nums break-words">
              Snapshot generated {formatUtcTimestamp(summary.generatedAt)} · Rolling {summary.windowDays}-day window
            </p>
          </div>
          <button
            type="button"
            aria-label="Refresh analytics summary"
            aria-disabled={isRefreshing}
            onClick={() => {
              if (!isRefreshing) onRefresh();
            }}
            className="inline-flex items-center justify-center min-h-[44px] px-4 py-2 rounded-xl border-2 border-cream-darker bg-cream hover:bg-cream-dark text-sm font-mono font-semibold text-charcoal transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue aria-disabled:opacity-60"
          >
            Refresh summary
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <p className="font-mono text-xs uppercase tracking-wider text-charcoal-light">
              Total Recorded Events
            </p>
            <p className="text-2xl font-bold font-mono tabular-nums text-charcoal mt-1 break-words">
              {summary.totalEvents.toLocaleString('en-US')}
            </p>
            <p className="font-mono text-xs text-charcoal-light mt-1 break-words">
              {summary.windowDays}-day aggregate window
            </p>
          </div>

          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <p className="font-mono text-xs uppercase tracking-wider text-charcoal-light">
              Last Recorded Event
            </p>
            <p className="text-base font-bold font-mono tabular-nums text-charcoal mt-1.5 break-words">
              {lastRecordedLabel}
            </p>
            <p className="font-mono text-xs text-charcoal-light mt-1 break-words">
              Server UTC timestamp
            </p>
          </div>

          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <p className="font-mono text-xs uppercase tracking-wider text-charcoal-light">
              Retention Policy
            </p>
            <p className="text-base font-bold font-mono text-charcoal mt-1.5 break-words">
              {retentionLabel}
            </p>
            <p className="font-mono text-xs text-charcoal-light mt-1 break-words">
              Scheduled UTC expiry cleanup
            </p>
          </div>

          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <p className="font-mono text-xs uppercase tracking-wider text-charcoal-light">
              Topic Privacy Floor
            </p>
            <p className="text-base font-bold font-mono tabular-nums text-charcoal mt-1.5 break-words">
              Minimum {summary.minTopicCellCount} events
            </p>
            <p className="font-mono text-xs text-charcoal-light mt-1 break-words">
              {summary.suppressedTopicCellCount} sparse topic {suppressionNoun} suppressed (&lt; {summary.minTopicCellCount} events)
            </p>
          </div>
        </div>
      </section>

      <section
        aria-labelledby="analytics-funnel-heading"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <div className="mb-4 min-w-0">
          <h2 id="analytics-funnel-heading" className="text-xl font-bold text-charcoal">
            Case-Study Engagement Funnel &amp; Read Depth
          </h2>
          <p className="text-sm text-charcoal-light mt-1 leading-relaxed break-words">
            Compares active 30s+ visible reads (<code className="font-mono text-xs">meaningful_read</code>) against explicit opening denominators (<code className="font-mono text-xs">case_study_open</code>).
          </p>
        </div>

        {summary.totalEvents === 0 ? (
          <p
            role="status"
            className="mb-4 rounded-xl border border-yellow-dark bg-yellow/25 px-4 py-3 text-sm text-charcoal break-words"
          >
            No engagement events have been recorded in the active window yet. Case-study denominators start at 0 opens until case studies are read.
          </p>
        ) : null}

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <caption className="text-left font-mono text-xs text-charcoal-light mb-3">
              Case-study funnel conversions and monotonic read-depth milestones (25%, 50%, 75%, 100%) across {summary.windowDays} days.
            </caption>
            <thead>
              <tr className="border-b-2 border-cream-darker font-mono text-xs uppercase tracking-wider text-charcoal-light">
                <th scope="col" className="py-2.5 pr-4">Case Study</th>
                <th scope="col" className="py-2.5 pr-4">Opens (Denominator)</th>
                <th scope="col" className="py-2.5 pr-4">Meaningful Reads (30s+)</th>
                <th scope="col" className="py-2.5 pr-4">Conversion Rate</th>
                <th scope="col" className="py-2.5">Read Depth Milestones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-cream-darker/70">
              {summary.projectFunnel.map((item) => (
                <tr key={item.projectSlug} className="align-top">
                  <th scope="row" className="py-3 pr-4 font-semibold text-charcoal whitespace-nowrap">
                    <Link
                      to={`/projects/${item.projectSlug}`}
                      className="inline-flex items-center min-h-[44px] text-blue-dark hover:text-charcoal underline underline-offset-2 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
                    >
                      {formatProjectTitle(item.projectSlug)}
                    </Link>
                  </th>
                  <td className="py-3 pr-4 font-mono tabular-nums text-charcoal whitespace-nowrap">
                    {item.caseStudyOpens} opens
                  </td>
                  <td className="py-3 pr-4 font-mono tabular-nums text-charcoal whitespace-nowrap">
                    {item.meaningfulReads}
                  </td>
                  <td className="py-3 pr-4 font-mono tabular-nums text-charcoal break-words">
                    {formatConversionCell(item)}
                  </td>
                  <td className="py-3 font-mono text-xs tabular-nums text-charcoal-light break-words">
                    <div className="flex flex-wrap gap-2">
                      <span className="px-2 py-0.5 rounded bg-cream border border-cream-darker text-charcoal">
                        25%: {item.depth25Count}
                      </span>
                      <span className="px-2 py-0.5 rounded bg-cream border border-cream-darker text-charcoal">
                        50%: {item.depth50Count}
                      </span>
                      <span className="px-2 py-0.5 rounded bg-cream border border-cream-darker text-charcoal">
                        75%: {item.depth75Count}
                      </span>
                      <span className="px-2 py-0.5 rounded bg-cream border border-cream-darker text-charcoal">
                        100%: {item.depth100Count}
                      </span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section
        aria-labelledby="analytics-topics-heading"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <div className="mb-4 min-w-0">
          <h2 id="analytics-topics-heading" className="text-xl font-bold text-charcoal">
            Controlled Topic Categories
          </h2>
          <p className="text-sm text-charcoal-light mt-1 leading-relaxed break-words">
            Only coarse allowlisted topic categories meeting at least {summary.minTopicCellCount} events are published; free-text queries and prompts are never accepted or stored.
          </p>
        </div>

        {summary.suppressedTopicCellCount > 0 ? (
          <p className="mb-4 rounded-xl border border-yellow-dark bg-yellow/25 px-4 py-3 font-mono text-xs text-charcoal break-words">
            {summary.suppressedTopicCellCount} sparse topic {suppressionNoun} suppressed (&lt; {summary.minTopicCellCount} events).
          </p>
        ) : null}

        {summary.topics.length > 0 ? (
          <ul className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {summary.topics.map((topic) => (
              <li
                key={topic.topicLabel}
                className="rounded-xl border border-cream-darker bg-cream/40 p-4 flex items-center justify-between gap-4 min-w-0"
              >
                <div className="min-w-0">
                  <p className="font-semibold text-charcoal break-words">
                    {formatTopicTitle(topic.topicLabel)}
                  </p>
                  <p className="font-mono text-xs text-charcoal-light break-words">
                    {topic.topicLabel}
                  </p>
                </div>
                <span className="font-mono text-sm font-bold tabular-nums text-charcoal whitespace-nowrap">
                  {topic.eventCount} events
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="rounded-xl border border-cream-darker bg-cream/40 px-4 py-3 text-sm text-charcoal-light break-words">
            No topic categories currently meet the minimum reporting threshold of {summary.minTopicCellCount} events.
          </p>
        )}
      </section>

      <section
        aria-labelledby="analytics-methodology-heading"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <h2 id="analytics-methodology-heading" className="text-xl font-bold text-charcoal mb-4">
          Measurement Boundaries &amp; Privacy Design
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <h3 className="font-mono text-xs uppercase tracking-wider font-semibold text-green-dark mb-2">
              Stored &amp; Aggregated
            </h3>
            <ul className="space-y-2 text-sm text-charcoal-light">
              <li className="break-words">
                Allowlisted event types, schema version (<code className="font-mono text-xs">1</code>), project slug, section key, read-depth bucket, and controlled topic label.
              </li>
              <li className="break-words">
                Unique client <code className="font-mono text-xs">eventId</code> for idempotent retry deduplication paired with authoritative server UTC timestamps.
              </li>
              <li className="break-words">
                Nightly UTC retention purge enforcing {summary.rawRetentionDays}-day raw expiry and {summary.aggregateRetentionMonths}-month daily rollup retention.
              </li>
            </ul>
          </div>

          <div className="rounded-xl border border-cream-darker bg-cream/40 p-4 min-w-0">
            <h3 className="font-mono text-xs uppercase tracking-wider font-semibold text-charcoal mb-2">
              Explicitly Excluded
            </h3>
            <ul className="space-y-2 text-sm text-charcoal-light">
              <li className="break-words">
                Zero raw IP addresses, zero User-Agent strings, zero URLs with query parameters, and zero advertising cookies or cross-site tracking tokens.
              </li>
              <li className="break-words">
                Zero free-text search inputs, chat prompts, or job description text; unknown JSON properties are rejected at validation (<code className="font-mono text-xs">400 Bad Request</code>).
              </li>
              <li className="break-words">
                In-memory rate limiting uses an ephemeral daily-salted hash only and clears automatically when the process restarts.
              </li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  );
}

export default function AnalyticsPage() {
  const analyticsQuery = useAnalyticsSummary();

  return (
    <motion.div
      variants={pageTransition}
      initial="initial"
      animate="animate"
      exit="exit"
      className="pt-28 pb-16 px-6"
    >
      <div className="max-w-5xl mx-auto">
        <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-blue hover:text-blue-dark active:scale-[0.98] font-mono text-sm transition-[color,transform] duration-150 ease-out min-h-[44px] px-1 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Back to Portfolio
          </Link>
          <Link
            to="/status"
            className="inline-flex items-center gap-2 text-blue-dark hover:text-charcoal font-mono text-sm transition-colors min-h-[44px] px-2 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
          >
            System Status
          </Link>
        </div>

        <header className="mb-10">
          <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-blue-dark bg-blue/10 px-3 py-1 rounded-full mb-3">
            <span>First-Party SQL Telemetry</span>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold text-charcoal mb-3">
            Analytics Evidence
          </h1>
          <p className="text-charcoal-light max-w-3xl leading-relaxed">
            Read-only aggregate report from{' '}
            <code className="font-mono text-xs px-1.5 py-0.5 rounded bg-cream-dark border border-cream-darker text-charcoal">
              GET /api/v1/analytics/summary
            </code>
            . Measures real case-study engagement funnels, visible read depth, and bounded topic demand while keeping raw IPs, query strings, and free-text input out of storage.
          </p>
        </header>

        <QueryContent
          query={analyticsQuery}
          resource="analytics summary"
          loading={<AnalyticsSkeleton />}
        >
          {(summary) => (
            <AnalyticsView
              summary={summary}
              isRefreshing={analyticsQuery.fetchStatus !== 'idle'}
              onRefresh={() => {
                if (analyticsQuery.fetchStatus === 'idle') {
                  void analyticsQuery.refetch({ cancelRefetch: false });
                }
              }}
            />
          )}
        </QueryContent>
      </div>
    </motion.div>
  );
}
