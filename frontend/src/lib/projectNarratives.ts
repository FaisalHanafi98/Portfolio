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
    'creams',
    {
      slug: 'creams',
      businessPillar: 'Rehabilitation Operations & Data Access',
      audienceTag: 'Malaysian community rehabilitation centres',
      headlineOutcome: 'One structured workspace for centre staff.',
      whatItIs: 'A Laravel workspace for daily attendance and rehabilitation-centre operations.',
      whatIDid: 'Built role-scoped workflows, central centre filters and private document handling.',
      whyItMatters: 'Staff can work with the records relevant to their centre and follow a consistent process.',
      engineeringHighlights: [
        'Eloquent CentreScope handles direct ownership, with relationship scopes for dependent asset records.',
        'Role-specific routes distinguish centre staff from documented administrator and background execution paths.',
        'Laravel 12, Blade, Bootstrap, MySQL, PHPUnit and Playwright.',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/CREAMS',
    },
  ],
  [
    'sen2nal',
    {
      slug: 'sen2nal',
      businessPillar: 'Market Data & Reproducible Research',
      audienceTag: 'Financial NLP and time-aware evaluation',
      headlineOutcome: 'An inspectable record of what information was available at a cutoff.',
      whatItIs: 'A Python research pipeline for market data, sentiment, calendar features and experiments.',
      whatIDid: 'Added run-correlated status, typed write contracts and SHA-256 manifests for evidence eligible at 08:00 New York time.',
      whyItMatters: 'The replay contract helps reviewers distinguish available evidence from later revisions. Live publication remains open. Research only. Not financial advice.',
      engineeringHighlights: [
        'Cutoff selection excludes late and ambiguous inputs before building a deterministic manifest.',
        'A failed stage rolls back pending writes and preserves earlier committed stages with their recorded outcomes.',
        'FastAPI, Pydantic, PostgreSQL and React. VADER scoring passed local fixture checks. FinBERT runtime verification remains open.',
      ],
      githubUrl: 'https://github.com/FaisalHanafi98/Sen2Nal',
    },
  ],
  [
    'baiti',
    {
      slug: 'baiti',
      businessPillar: 'Multi-Tenant Property Operations',
      audienceTag: 'Malaysian neighbourhood committees',
      headlineOutcome: 'A property workspace with a database-enforced tenant boundary.',
      whatItIs: 'A modular Spring Boot and React application for neighbourhood operations.',
      whatIDid: 'Built and verified the local authentication shell and property workflows for blocks and units.',
      whyItMatters: 'Committees can maintain property data inside an isolated community workspace. Procurement and policy assistance are planned.',
      engineeringHighlights: [
        'PostgreSQL FORCE ROW LEVEL SECURITY backs shared-schema tenancy.',
        'Flyway uses a separate migration role. Tenant-aware pooled connections use a restricted runtime role.',
        'Java 21, Spring Boot, PostgreSQL 16, React and TypeScript. Local PostgreSQL and browser checks cover the property flows.',
      ],
    },
  ],
]);
