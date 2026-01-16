// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: {
    code: string;
    message: string;
  };
  timestamp: string;
}

// Project types
export interface Technology {
  id: number;
  name: string;
  category: string;
  iconUrl?: string;
}

export interface ProjectImage {
  id: number;
  url: string;
  altText: string;
  displayOrder: number;
}

export interface ProjectMetrics {
  databaseTables?: number;
  userRoles?: number;
  linesOfCode?: number;
  complexityReduction?: string;
  recognition?: string;
}

export interface ProjectLinks {
  github?: string;
  live?: string;
}

export interface ProjectSummary {
  id: number;
  slug: string;
  title: string;
  shortDescription: string;
  technologies: string[];
  thumbnailUrl?: string;
  featured: boolean;
}

export interface ProjectDetail {
  id: number;
  slug: string;
  title: string;
  shortDescription: string;
  fullDescription: string;
  problemStatement: string;
  solution: string;
  keyFeatures: string[];
  technologies: Technology[];
  metrics: ProjectMetrics;
  images: ProjectImage[];
  links: ProjectLinks;
  period: string;
  featured: boolean;
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

// About types
export interface About {
  name: string;
  tagline: string;
  bio: string;
  email: string;
  linkedin: string;
  github: string;
  resumeUrl: string;
  photoUrl?: string;
}

// Theme type
export type Theme = 'dark' | 'light';
