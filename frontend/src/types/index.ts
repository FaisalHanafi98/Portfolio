// API Response wrapper
export interface ApiErrorObject {
  code?: string;
  message?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: string | ApiErrorObject;
  timestamp: string;
}

// Project types
export interface Technology {
  id: number;
  name: string;
  iconUrl?: string;
}

export interface ProjectImage {
  url: string;
  altText: string;
  displayOrder: number;
}

export interface ProjectSummary {
  id: number;
  slug: string;
  title: string;
  shortDescription: string;
  technologies: string[];
  featured: boolean;
  period: string;
}

export interface ProjectDetail {
  id: number;
  slug: string;
  title: string;
  shortDescription: string;
  fullDescription: string;
  problemStatement: string;
  solution: string;
  featured: boolean;
  period: string;
  githubUrl?: string;
  liveUrl?: string;
  technologies: Technology[];
  images: ProjectImage[];
  architectureDecisions?: string[];
  techStack?: Record<string, string[]>;
  challenges?: string[];
  costJustification?: string;
}

// Skill types
export interface Skill {
  id: number;
  name: string;
  proficiency: number;
  iconUrl?: string;
}

export interface SkillCategory {
  id: number;
  name: string;
  displayOrder: number;
  skills: Skill[];
}

// Experience types
export interface Experience {
  id: number;
  company: string;
  role: string;
  location: string;
  startDate: string;
  endDate?: string;
  description: string;
  highlights: string[];
}

// Operational status snapshot types
export interface StatusDatabase {
  status: string;
  latencyMs: number | null;
}

export interface StatusTraffic {
  windowSeconds: number;
  requestCount: number;
  minSamples: number;
  p95Status: string;
  p95Ms: number | null;
}

export type GitHubSyncStatus = 'healthy' | 'degraded' | 'stale' | 'unavailable' | 'empty';

export interface GitHubWorkflowRun {
  id: number;
  name: string;
  branch: string;
  event: string;
  status: string;
  conclusion: string | null;
  shortSha: string;
  updatedAt: string | null;
  htmlUrl: string | null;
}

export interface GitHubCommitActivity {
  shortSha: string;
  message: string;
  committedAt: string | null;
  htmlUrl: string | null;
}

export interface GitHubStatus {
  status: GitHubSyncStatus;
  repository: string;
  lastSyncedAt: string | null;
  snapshotAgeSeconds: number | null;
  cacheTtlSeconds: number;
  nextPollAt: string | null;
  stateReason: string | null;
  workflowRuns: GitHubWorkflowRun[];
  recentCommits: GitHubCommitActivity[];
}

export interface StatusSnapshot {
  status: string;
  checkedAt: string;
  uptimeSeconds: number;
  startedAt: string;
  revision: string;
  database: StatusDatabase;
  traffic: StatusTraffic;
  github: GitHubStatus;
}

export interface ProjectFunnelMetric {
  projectSlug: string;
  caseStudyOpens: number;
  meaningfulReads: number;
  conversionRatePercent: number | null;
  depth25Count: number;
  depth50Count: number;
  depth75Count: number;
  depth100Count: number;
}

export interface AnalyticsTopicMetric {
  topicLabel: string;
  eventCount: number;
}

export interface AnalyticsSummary {
  generatedAt: string;
  lastRecordedAt: string | null;
  windowDays: number;
  rawRetentionDays: number;
  aggregateRetentionMonths: number;
  minTopicCellCount: number;
  totalEvents: number;
  projectFunnel: ProjectFunnelMetric[];
  topics: AnalyticsTopicMetric[];
  suppressedTopicCellCount: number;
}
