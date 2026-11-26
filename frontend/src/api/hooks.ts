import { useQuery } from '@tanstack/react-query';
import { api } from './client';
import type {
  ProjectSummary,
  ProjectDetail,
  SkillCategory,
  Experience,
  StatusSnapshot,
} from '../types';

// Query keys for cache management
export const queryKeys = {
  projects: ['projects'] as const,
  project: (slug: string) => ['project', slug] as const,
  skills: ['skills'] as const,
  experience: ['experience'] as const,
  status: ['status'] as const,
};

// Projects hooks
export function useProjects() {
  return useQuery<ProjectSummary[]>({
    queryKey: queryKeys.projects,
    queryFn: async () => {
      const response = await api.getProjects();
      return response.data;
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

export function useProject(slug: string) {
  return useQuery<ProjectDetail>({
    queryKey: queryKeys.project(slug),
    queryFn: async () => {
      const response = await api.getProject(slug);
      return response.data;
    },
    staleTime: 5 * 60 * 1000,
    enabled: !!slug,
  });
}

// Skills hook
export function useSkills() {
  return useQuery<SkillCategory[]>({
    queryKey: queryKeys.skills,
    queryFn: async () => {
      const response = await api.getSkills();
      return response.data;
    },
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Experience hook
export function useExperience() {
  return useQuery<Experience[]>({
    queryKey: queryKeys.experience,
    queryFn: async () => {
      const response = await api.getExperience();
      return response.data;
    },
    staleTime: 10 * 60 * 1000,
  });
}

// Operational status hook (no fresh cache window; preserves cached fallback on refresh error)
export function useStatus() {
  return useQuery<StatusSnapshot>({
    queryKey: queryKeys.status,
    queryFn: async () => {
      const response = await api.getStatus();
      return response.data;
    },
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
  });
}


