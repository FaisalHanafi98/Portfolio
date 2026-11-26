import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import QueryContent from '../components/ui/QueryContent';
import { StatusSkeleton } from '../components/ui/Skeleton';
import { useStatus } from '../api/hooks';
import { pageTransition } from '../lib/animations';
import type { StatusSnapshot } from '../types';

function formatUptime(totalSeconds: number): string {
  const days = Math.floor(totalSeconds / 86_400);
  const hours = Math.floor((totalSeconds % 86_400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = Math.floor(totalSeconds % 60);

  if (days > 0) return `${days}d ${hours}h ${minutes}m`;
  if (hours > 0) return `${hours}h ${minutes}m ${seconds}s`;
  if (minutes > 0) return `${minutes}m ${seconds}s`;
  return `${seconds}s`;
}

function formatUtcTimestamp(iso: string): string {
  const parsed = new Date(iso);
  if (Number.isNaN(parsed.getTime())) return iso;
  return parsed.toISOString().replace('T', ' ').slice(0, 19) + ' UTC';
}

function formatWindowLabel(windowSeconds: number): string {
  const minutes = Math.round(windowSeconds / 60);
  if (windowSeconds % 60 === 0 && minutes >= 1) {
    return `${minutes}-minute rolling window (${windowSeconds}s)`;
  }
  return `${windowSeconds}-second rolling window`;
}

function StatusView({
  snapshot,
  isRefreshing,
  onRefresh,
}: {
  snapshot: StatusSnapshot;
  isRefreshing: boolean;
  onRefresh: () => void;
}) {
  const isHealthy = snapshot.status === 'healthy';
  const isDbUp = snapshot.database.status === 'up';
  const isMeasuredP95 = snapshot.traffic.p95Status === 'measured' && snapshot.traffic.p95Ms != null;
  const windowLabel = formatWindowLabel(snapshot.traffic.windowSeconds);

  const latencySummary = isMeasuredP95
    ? `${snapshot.traffic.p95Ms!.toFixed(2)} ms`
    : 'Insufficient data';

  const sampleThresholdCopy = `${snapshot.traffic.requestCount} of ${snapshot.traffic.minSamples} minimum requests`;

  return (
    <div className="space-y-6">
      {/* Primary Service Banner */}
      <section
        aria-label="Current system status summary"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex flex-wrap items-center gap-3 min-w-0">
            <span
              className={`inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full border font-mono text-xs font-semibold uppercase tracking-wider ${
                isHealthy
                  ? 'bg-green/10 border-green/30 text-green'
                  : 'bg-coral/10 border-coral/30 text-coral'
              }`}
            >
              <span
                aria-hidden="true"
                className={`w-2 h-2 rounded-full ${isHealthy ? 'bg-green' : 'bg-coral'}`}
              />
              {isHealthy ? 'Healthy' : 'Degraded'}
            </span>
            <span className="font-mono text-xs px-3 py-1.5 rounded-full bg-cream-dark border border-cream-darker text-charcoal break-words">
              Revision: {snapshot.revision}
            </span>
          </div>

          <button
            type="button"
            onClick={onRefresh}
            aria-disabled={isRefreshing}
            className="btn-primary min-h-[44px] self-start sm:self-auto aria-disabled:opacity-60"
          >
            {isRefreshing ? 'Refreshing…' : 'Refresh snapshot'}
          </button>
        </div>

        <p className="font-mono text-xs text-charcoal-light mt-4 tabular-nums break-words">
          Snapshot checked at {formatUtcTimestamp(snapshot.checkedAt)} · Process started{' '}
          {formatUtcTimestamp(snapshot.startedAt)}
        </p>
      </section>

      {/* Three Operational Signal Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <article className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm flex flex-col justify-between min-w-0">
          <div>
            <p className="font-mono text-xs uppercase tracking-widest text-blue font-semibold mb-2">
              01 / Application Process
            </p>
            <p className="text-2xl font-bold font-mono tabular-nums text-charcoal mb-2 break-words">
              {formatUptime(snapshot.uptimeSeconds)}
            </p>
            <p className="font-mono text-xs text-charcoal-light tabular-nums">
              {snapshot.uptimeSeconds} seconds since JVM start
            </p>
          </div>
          <p className="text-xs text-charcoal-light mt-4 pt-3 border-t border-cream-darker/70">
            Process uptime only; not external multi-region availability.
          </p>
        </article>

        <article className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm flex flex-col justify-between min-w-0">
          <div>
            <p className="font-mono text-xs uppercase tracking-widest text-pink font-semibold mb-2">
              02 / Database Connectivity
            </p>
            <span
              className={`inline-flex items-center gap-2 px-3 py-1 rounded-full border font-mono text-xs font-semibold mb-3 ${
                isDbUp
                  ? 'bg-green/10 border-green/30 text-green'
                  : 'bg-coral/10 border-coral/30 text-coral'
              }`}
            >
              {isDbUp ? 'Database Up' : 'Database Degraded'}
            </span>
            <p className="text-lg font-bold font-mono tabular-nums text-charcoal break-words">
              {isDbUp && snapshot.database.latencyMs != null
                ? `${snapshot.database.latencyMs} ms probe`
                : 'Timeout or unavailable'}
            </p>
          </div>
          <p className="text-xs text-charcoal-light mt-4 pt-3 border-t border-cream-darker/70">
            Bounded JDBC validation query with redacted diagnostics.
          </p>
        </article>

        <article className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm flex flex-col justify-between min-w-0">
          <div>
            <p className="font-mono text-xs uppercase tracking-widest text-purple font-semibold mb-2">
              03 / API Latency (p95)
            </p>
            <span
              className={`inline-flex items-center gap-2 px-3 py-1 rounded-full border font-mono text-xs font-semibold mb-3 ${
                isMeasuredP95
                  ? 'bg-blue/10 border-blue/30 text-blue'
                  : 'bg-yellow/15 border-yellow-dark/40 text-yellow-dark'
              }`}
            >
              {isMeasuredP95 ? 'Measured' : 'Insufficient data'}
            </span>
            <p className="text-2xl font-bold font-mono tabular-nums text-charcoal break-words">
              {latencySummary}
            </p>
            <p className="font-mono text-xs text-charcoal-light mt-1 tabular-nums break-words">
              {sampleThresholdCopy}
            </p>
          </div>
          <p className="text-xs text-charcoal-light mt-4 pt-3 border-t border-cream-darker/70 break-words">
            {windowLabel} over bounded allowlisted route templates.
          </p>
        </article>
      </div>

      {/* Accessible Tabular Fallback / Detailed Metrics */}
      <section
        aria-labelledby="status-metrics-table-heading"
        className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm"
      >
        <h2
          id="status-metrics-table-heading"
          className="text-lg font-bold text-charcoal mb-4"
        >
          Operational Signals Table
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <caption className="text-left font-mono text-xs text-charcoal-light mb-3">
              Snapshot metrics over the {windowLabel}. Latency percentiles require at least{' '}
              {snapshot.traffic.minSamples} completed requests in the window.
            </caption>
            <thead>
              <tr className="border-b-2 border-cream-darker font-mono text-xs uppercase tracking-wider text-charcoal-light">
                <th scope="col" className="py-2.5 pr-4">Signal</th>
                <th scope="col" className="py-2.5 pr-4">Measured Value</th>
                <th scope="col" className="py-2.5 pr-4">Window / Scope</th>
                <th scope="col" className="py-2.5">State</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-cream-darker/80">
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  Overall Service
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal">
                  {isHealthy ? 'Serving requests' : 'Degraded dependency'}
                </td>
                <td className="py-3 pr-4 text-charcoal-light">Point-in-time snapshot</td>
                <td className="py-3 font-mono font-semibold text-charcoal">
                  {isHealthy ? 'Healthy' : 'Degraded'}
                </td>
              </tr>
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  Application Process Uptime
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal">
                  {formatUptime(snapshot.uptimeSeconds)} ({snapshot.uptimeSeconds}s)
                </td>
                <td className="py-3 pr-4 text-charcoal-light">Since JVM process start</td>
                <td className="py-3 font-mono font-semibold text-charcoal">Running</td>
              </tr>
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  Database Connectivity
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal">
                  {isDbUp && snapshot.database.latencyMs != null
                    ? `${snapshot.database.latencyMs} ms`
                    : 'Unavailable'}
                </td>
                <td className="py-3 pr-4 text-charcoal-light">Bounded JDBC probe</td>
                <td className="py-3 font-mono font-semibold text-charcoal">
                  {isDbUp ? 'Database Up' : 'Database Degraded'}
                </td>
              </tr>
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  API Request Count
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal">
                  {snapshot.traffic.requestCount} requests
                </td>
                <td className="py-3 pr-4 text-charcoal-light break-words">{windowLabel}</td>
                <td className="py-3 font-mono font-semibold text-charcoal">Active window</td>
              </tr>
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  API p95 Latency
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal">
                  {isMeasuredP95
                    ? `${snapshot.traffic.p95Ms!.toFixed(2)} ms`
                    : `${sampleThresholdCopy}`}
                </td>
                <td className="py-3 pr-4 text-charcoal-light break-words">{windowLabel}</td>
                <td className="py-3 font-mono font-semibold text-charcoal">
                  {isMeasuredP95 ? 'Measured' : 'Insufficient data'}
                </td>
              </tr>
              <tr>
                <th scope="row" className="py-3 pr-4 font-medium text-charcoal">
                  Deployed Revision
                </th>
                <td className="py-3 pr-4 font-mono tabular-nums text-charcoal break-words">
                  {snapshot.revision}
                </td>
                <td className="py-3 pr-4 text-charcoal-light">Build / environment metadata</td>
                <td className="py-3 font-mono font-semibold text-charcoal">Recorded</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

export default function StatusPage() {
  const statusQuery = useStatus();

  return (
    <motion.div
      variants={pageTransition}
      initial="initial"
      animate="animate"
      exit="exit"
      className="pt-28 pb-16 px-6"
    >
      <div className="max-w-5xl mx-auto">
        <div className="mb-8">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-blue hover:text-blue-dark active:scale-[0.98] font-mono text-sm transition-[color,transform] duration-150 ease-out min-h-[44px] px-1 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Back to Portfolio
          </Link>
        </div>

        <header className="mb-10">
          <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-blue bg-blue/10 px-3 py-1 rounded-full mb-3">
            <span>System Telemetry &amp; Operations</span>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold text-charcoal mb-3">
            System Status
          </h1>
          <p className="text-charcoal-light max-w-3xl leading-relaxed">
            Live read-only snapshot from{' '}
            <code className="font-mono text-xs px-1.5 py-0.5 rounded bg-cream-dark border border-cream-darker text-charcoal">
              GET /api/v1/status
            </code>
            . Reports JVM process uptime, bounded database connectivity, active build revision,
            and rolling 5-minute API latency percentiles while Actuator and Prometheus endpoints
            stay private.
          </p>
        </header>

        <QueryContent
          query={statusQuery}
          resource="status"
          loading={<StatusSkeleton />}
        >
          {(snapshot) => (
            <StatusView
              snapshot={snapshot}
              isRefreshing={statusQuery.fetchStatus !== 'idle'}
              onRefresh={() => {
                if (statusQuery.fetchStatus === 'idle') {
                  void statusQuery.refetch({ cancelRefetch: false });
                }
              }}
            />
          )}
        </QueryContent>
      </div>
    </motion.div>
  );
}
