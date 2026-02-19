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
