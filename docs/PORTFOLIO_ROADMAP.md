# Portfolio delivery roadmap

**Proposed · 24 November 2026**

The first milestone is a working public release of the existing Portfolio.
The following milestones add a visible operating history, aggregate analytics
and two AI experiences grounded in the CREAMS, Sen2Nal and BAiTI case studies.
Months are measured from the start of VPS work. The figures below are proposed
release targets.

| Window | Deliverable and skills on display | Release evidence |
|---|---|---|
| Weeks 1–6 | Deploy the trilogy on a Singapore VPS. Show the source commit, release and health history. Demonstrates software delivery and DevOps. | Rebuild a blank host, deploy a signed-off revision, observe public HTTPS and smoke checks, roll back to the previous image digests, and restore a database backup. |
| Months 2–3 | Launch a public `/status` view for measured service health. Demonstrates instrumentation and resilient UX. | Compare displayed metrics with controlled latency and database faults. Exercise stale GitHub data and missing telemetry. |
| Months 3–5 | Add first-party events and an aggregate dashboard. Demonstrates SQL, data quality and privacy-aware analytics. | Reconcile funnel and read-depth reports against known fixture counts, prove retry deduplication and expiry, and measure ingestion overhead. |
| Months 5–8 | Launch “Ask About My Work” against approved evidence. Demonstrates retrieval, provenance, bounded model use and evaluation. | Publish a dated retrieval and citation evaluation, test refusal and exhaustion paths, and trace answers to stable project-section links. |
| Months 8–12 | Add a JD-to-experience matcher and a public engineering write-up. Demonstrates structured AI output and evidence-based evaluation. | A recruiter can map requirements to evidence and gaps. Test the matcher against synthetic job descriptions and publish the measured results and limits. |

## Weeks 1–6 · Production delivery

- Rehearse on a 2 GB VPS and prefer 4 GB for production if the budget allows,
  especially before enabling pgvector and metrics retention. Choose Linux and
  keep builds on GitHub runners. Start with a 384–512 MB JVM heap and a small
  database pool. Measure total process memory, swap, disk and API p95 under
  a short concurrent smoke load and a backup. Raise the VM size before
  launch if the host lacks headroom during either operation. Record available RAM, disk, traffic, backups,
  monthly renewal cost and Singapore stock at purchase. Shock Hosting's 2 GB
  plan lists US$4.99 per month and Singapore as a selectable location. OVHcloud
  advertises 4 GB from S$5.78 per month before GST. Confirm the actual quote and
  stock. [Shock Hosting pricing](https://shockhosting.com/portal/store/ssd-virtual-servers) ·
  [Shock Hosting locations](https://shockhosting.com/vps) ·
  [OVHcloud Singapore](https://www.ovhcloud.com/en-sg/vps/vps-singapore/)
- Add an idempotent bootstrap for an agreed Linux distribution. Create a
  dedicated deploy account, verify the SSH host key, configure the firewall,
  install Docker and Compose, and provision service directories and credentials.
  Expose Caddy on 80/443. Keep the existing frontend image and same-origin API,
  with Spring and PostgreSQL on internal Compose networks. Persist Caddy's
  certificate data and the PostgreSQL volume. Validate automatic certificate
  issuance and renewal. [Caddy HTTPS](https://caddyserver.com/docs/automatic-https)
- Use GitHub Actions to test and publish frontend and backend images to GHCR.
  Grant package write permission only to the build job. Tag the tested commit,
  record image digests in a release manifest, and protect the deployment job
  with a GitHub environment and one deployment at a time. Deploy via a scoped
  SSH credential. Pull by digest and retain the previous known-good manifest.
  Place image publishing ahead of remote deployment and require the smoke gate
  before marking a release healthy. [GitHub image publishing](https://docs.github.com/en/actions/tutorials/publish-packages/publish-docker-images)
- Use Flyway with an explicit baseline for the existing schema before adding
  write endpoints. Separate schema migration credentials from runtime
  credentials and use expand/contract changes across releases. After a deploy,
  verify HTTPS, API and DB readiness, a populated trilogy, a deep link, and
  unknown-route recovery. Roll back application digests on failure once schema
  compatibility is confirmed.
- Run a nightly PostgreSQL custom-format dump. Stream it through authenticated
  encryption such as age and store the encrypted backup off-host. Store the
  recovery key separately. Retain 7 daily and 4 weekly backups, alert on a
  missed backup, and test a clean restore monthly. Initial targets are a
  24-hour recovery point and a 2-hour recovery time. Record measured drill
  outcomes. [PostgreSQL backup formats](https://www.postgresql.org/docs/current/app-pgdump.html)

## Months 2–3 · Live status and operations

- Keep Actuator and Prometheus endpoints private. Add a narrow public
  `/api/v1/status` snapshot with application uptime, coarse DB health,
  deployed revision, request count and API p95 from bounded Micrometer timer
  histograms. Define p95 over a rolling five-minute window and display
  insufficient data for low samples. Use bounded route templates as metric
  labels and cap scrape storage. [Micrometer histograms](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html)
- Poll public GitHub workflow and activity data in a scheduled server job
  with conditional requests, backoff and a 15-minute cache. Return the
  last successful snapshot with its age on failure. Keep GitHub quota and
  credentials server-side. Distinguish healthy, degraded, stale, unavailable
  and empty states in the API and UI. Verify DB outages and GitHub outages
  independently.
- Publish release and restore evidence on the status page. Report measured
  uptime with a stated observation window and distinguish app process uptime
  from availability measured outside the host. Use text as well as colour,
  tabular fallbacks for charts and no focus theft on refresh.

## Months 3–5 · First-party analytics

- Implement a bounded, same-origin Spring event endpoint with explicit event
  names, project and section allowlists, server timestamps, a unique event
  ID for retry deduplication, size and rate limits, and schema versioning.
  Ingest case-study views, read-depth buckets and controlled search/chat topic
  labels as searches and AI tools ship. Accept only locally derived coarse categories. Keep raw IPs, URLs
  with query strings, free-text prompts and JD text out of analytics storage.
- Store events in PostgreSQL and aggregate daily by date, project and event.
  A visibility timer and section intersections distinguish a page opening
  from a meaningful read. Report the denominator for every conversion and
  suppress sparse topic cells. Set 30-day raw retention and 12-month aggregate
  retention as proposed defaults. Expose aggregate evidence with sample size
  and freshness. Validate duplicate, malformed, offline/best-effort and
  clock-skew behaviour. Measure p95 overhead under a small concurrent load.

## Months 5–8 · Ask About My Work

- Index approved public architecture excerpts, case-study sections and ADRs
  with stable section IDs, original source, content hash, revision and
  embedding model version. Use an owner-reviewed ingestion allowlist and
  versioned corpus. Exclude private repos, client material, personal records,
  secrets, production data and unapproved excerpts. Publish cited excerpts
  through stable project anchors.
- Combine PostgreSQL full-text retrieval and pgvector similarity with
  reciprocal rank fusion. Start with exact vector search for the small
  corpus. Adopt an approximate index only after measuring latency and
  recall. Give the model only the selected passages and require structured
  claims with allowed source IDs. Validate each citation server-side, limit
  generated links to known project routes, and abstain when evidence is
  missing or contradictory. [pgvector hybrid search](https://github.com/pgvector/pgvector#hybrid-search)
- Treat retrieved text, pasted input and model output as untrusted. Provide
  no tools, browsing, credential access or writes to the model. Enforce
  input and output schemas, timeouts and output-length bounds in Spring.
  Trust client IP only from the controlled edge, and apply strict per-IP
  limits with a short-lived keyed identifier. Initial proposed limits are
  five requests per minute and 30 per day per IP, plus 20,000 model tokens
  per UTC day across chat and matching. Reserve each request's token ceiling
  atomically in PostgreSQL before any provider call, reconcile returned usage
  once, and retain the reservation when a timeout makes usage uncertain.
  Fail closed if accounting is unavailable. Display an accessible retry
  state and reset time for exhausted limits. Keep raw prompts out of logs and provide a short data-use
  notice. Make provider, price ceiling, retention and kill switch explicit
  before enabling traffic.
- Start with an owner-reviewed question set and synthetic adversarial cases.
  Propose top-five recall of at least 85% on answerable questions, valid
  citations for every published claim, and explicit abstention on cases with
  no supporting material. Record p95 latency and tokens. Re-run the set for
  every retrieval, prompt or embedding revision.

## Months 8–12 · JD-to-experience matcher

- Let the recruiter paste a JD into an accessible, bounded form. Parse
  requirements into a schema and reuse the same versioned corpus, rate
  limits and budget. For each requirement, show `demonstrated`, `adjacent`
  or `evidence unavailable` with specific cited excerpts. Keep human
  interpretation visible and allow copying the structured result. Keep the
  JD in request memory with redaction before any approved provider call.
- Evaluate against synthetic Software Dev, DevOps, Data Analytics and AI
  JDs. Include ambiguous requirements and a control with absent skills.
  Check citation coverage, correct gap reporting, partial responses, provider
  failure, and performance. Publish an evidence page with evaluation revision,
  sample counts, scoped results and known gaps.

Every phase ships in micro-commits with its required tests and the shared
message SOP. Keep the current loading, offline, stale-cache, retry, empty,
HTTP 404, keyboard and reduced-motion behaviour. Add WCAG 2.2 AA verification
for each new route with manual keyboard and representative screen-reader
checks alongside browser regressions. Retain the Compose smoke gate and
use versioned restore drills and benchmark data as portfolio evidence.
