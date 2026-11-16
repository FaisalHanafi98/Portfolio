export interface ProjectNarrative {
  slug: string;
  businessPillar: string;
  audienceTag: string;
  headlineOutcome: string;
  whatItIs: string;
  whatIDid: string;
  whyItMatters: string;
  engineeringHighlights: string[];
  githubUrl?: string;
}

export const projectNarratives = new Map<string, ProjectNarrative>([
  [
    'auto-recruit',
    {
      slug: 'auto-recruit',
      businessPillar: 'Enterprise Workflow & Approval Automation',
      audienceTag: 'Built at Accenture Malaysia',
      headlineOutcome: 'Replaced fragmented Excel hiring trackers with a single role-controlled pipeline handling 20,000+ candidate records.',
      whatItIs:
        'At Accenture, temporary recruiters, supervisors, and hiring managers were tracking hundreds of open positions across disconnected Excel files and email chains — causing candidates to get lost between handoffs and weekly reporting to take hours of manual work.',
      whatIDid:
        'Built and collaborated on refactoring an enterprise SharePoint application into a fast, role-based workspace for 6 user types. Added automated checks for duplicate applicants, client anti-poaching rules, and 6-month retry windows, plus one-click calendar invites and automated Friday pipeline reports.',
      whyItMatters:
        'Hiring teams stopped losing candidates in email threads, cut weekly spreadsheet reporting to a single click, experienced 68% faster page loads during daily data entry, and ran the entire platform at zero extra software license cost on existing Microsoft 365.',
      engineeringHighlights: [
        'Collaborated on refactoring a 2,520-line monolithic React component into 50+ modular components and 10 service modules (~77% main-component reduction).',
        'Split state into Auth, Data, and UI contexts with memoized selectors — cutting dashboard calculation time from 500 ms to 5 ms.',
        'Used server-side pagination ($top, $skiptoken), 7 indexed columns, and progressive 100-row loading to query 20,000+ records within SharePoint’s 5,000-item limit.',
      ],
    },
  ],
  [
    'creams',
    {
      slug: 'creams',
      businessPillar: 'Healthcare Operations & Budget Accountability',
      audienceTag: 'Malaysian PDK Rehabilitation Centres',
      headlineOutcome: 'Digitized daily trainee attendance, care progress, and grant budgets so staff spend less time on paper files and more time with children.',
      whatItIs:
        'Community-Based Rehabilitation Centres (PDK) in Malaysia support children and trainees across 6 disability categories, but staff were weighed down by paper attendance sheets, scattered progress notes, manual allowance checks, and disconnected budget spreadsheets.',
      whatIDid:
        'Built a multi-role web platform for Admins, Supervisors, Teachers, and Committee (AJK) members. Connected daily attendance directly to allowance eligibility checks, added Programme Funding & Budget tracking from grant intake to expenditure approval, and isolated sensitive records across 25 data models.',
      whyItMatters:
        'Teachers and supervisors get one structured system for daily roll call, Individualised Education Plans (IEP), and grant reporting — preventing duplicate allowance claims, protecting sensitive trainee medical records between centres, and making committee audits transparent.',
      engineeringHighlights: [
        'Centralized tenant isolation across 25 Eloquent models (23 direct CentreScope models + 2 relationship-scoped asset models).',
        'Implemented Programme Funding & Budget Management, Attendance-to-Allowance Reconciler, and Rehabilitation Progress & Rubric Assistant modules.',
        'Managed Laravel 10 -> 11 -> 12 upgrades, LiteSpeed logout/cache hardening, private PDF storage, and 400+ automated PHPUnit & Playwright checks.',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/CREAMS',
    },
  ],
  [
    'ringgitsense',
    {
      slug: 'ringgitsense',
      businessPillar: 'Financial Data ETL & Scenario Planning',
      audienceTag: 'Malaysian Personal Finance ("Sedar duit, sedar diri")',
      headlineOutcome: 'Turns raw Malaysian bank and e-wallet statements into clean spending insights, debt alerts, and exact-math savings scenarios.',
      whatItIs:
        'Most budgeting apps fail on Malaysian bank statements and ignore how locals actually spend — splitting money across Maybank/CIMB accounts, Touch \'n Go, GrabPay, SPayLater/Atome BNPL plans, and informal family transfers ("hutang"). Meanwhile, generic AI chatbots often hallucinate numbers.',
      whatIDid:
        'Built a FastAPI and React platform that automatically parses local bank statements, normalizes messy merchant descriptions, separates formal bank loans from BNPL and informal debt, and provides a Financial Goals & Scenario Planner plus a Tax-Relief Receipt Vault.',
      whyItMatters:
        'Users don\'t have to type expenses by hand or trust guesswork math. They immediately see hidden BNPL obligations, test realistic monthly savings plans backed by exact decimal arithmetic, and get transparent AI insights that pause and ask when a transfer is ambiguous.',
      engineeringHighlights: [
        'Extensible bank CSV/PDF parser architecture (Maybank, CIMB) with credit-safe SHA fingerprint deduplication so same-day refunds aren\'t dropped.',
        'Implemented Financial Goals & Scenario Planner, Merchant & Transaction Normaliser, and Tax-Relief Receipt Vault with deterministic query routing.',
        '6-agent Claude design with Pydantic output schemas, confidence scores (0.0–1.0), [NEEDS INPUT] abstention, and mandatory Malaysian disclaimers.',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/RinggitSense',
    },
  ],
  [
    'baiti',
    {
      slug: 'baiti',
      businessPillar: 'Multi-Tenant Property & Procurement Workflows',
      audienceTag: 'Malaysian Residents\' Associations (RA)',
      headlineOutcome: 'Replaces chaotic neighbourhood WhatsApp threads with auditable unit management, vendor quote comparisons, and database-isolated records.',
      whatItIs:
        'Residential committees and neighbourhood associations often manage unit occupancy, maintenance repairs, contractor quotes, and reserve funds across informal chat groups and spreadsheets — making it hard to audit spending or prevent cross-community data leaks.',
      whatIDid:
        'Built a multi-tenant Spring Boot and React platform where each neighbourhood has its own isolated workspace for blocks/units, maintenance-linked vendor RFQs and work orders, arrears and reserve-fund planning, and a cited community by-law lookup assistant.',
      whyItMatters:
        'Volunteer committee members and residents get transparent, auditable vendor selection and maintenance tracking, while the software operator can host dozens of neighbourhoods on a single database knowing PostgreSQL Row-Level Security blocks cross-community leaks.',
      engineeringHighlights: [
        'Dual PostgreSQL roles (baiti_migrator for 33 Flyway migrations vs baiti_app bound by FORCE ROW LEVEL SECURITY) with pool-safe TenantAwareDataSource.',
        'Implemented Vendor & Procurement Management (RFQ comparison, exact-decimal costs), Arrears & Reserve Planning, and Tenant-Scoped Policy Assistant.',
        'Verified against 330 backend unit/architecture/integration tests (including I1–I9 RLS isolation gates and a 168-case permission matrix).',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/BAiTI',
    },
  ],
  [
    'sen2nal',
    {
      slug: 'sen2nal',
      businessPillar: 'Market Intelligence & Auditable Signal Pipelines',
      audienceTag: 'Financial NLP & Research ("Signal over certainty")',
      headlineOutcome: 'Transforms noisy financial news and forum chatter into timestamped, replayable research snapshots without look-ahead bias.',
      whatItIs:
        'Financial news headlines, Reddit discussions, and market calendar effects are noisy and frequently contradict each other. Worse, many sentiment tools act as black boxes or accidentally leak tomorrow\'s news into yesterday\'s backtest.',
      whatIDid:
        'Built a 6-stage Python/FastAPI pipeline and React "NightOps Terminal" dashboard that scores financial news with FinBERT and VADER, checks exchange calendar seasonality, and locks daily pre-market evidence across 50 US companies into a Research Journal & Signal Replay module.',
      whyItMatters:
        'Instead of blindly trusting a "Buy/Sell" tip, an analyst can replay any historical day\'s snapshot as of 08:00 New York time, inspect the exact news articles and model version behind the score, and immediately see when sentiment and calendar signals conflict.',
      engineeringHighlights: [
        'Stage-gated 6-agent pipeline with Pydantic write contracts that automatically halts and rolls back pending writes if any critical stage fails.',
        'Research Journal & Signal Replay with SHA-256 frozen input manifests at an 08:00 America/New_York cutoff, plus Historical Evidence Search and Drift Monitor.',
        'Rolling walk-forward validation (252-day training / 21-day testing per ADR-005) and 336 offline pytest regression cases.',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/Sen2Nal',
    },
  ],
]);
