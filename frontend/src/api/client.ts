import axios, { AxiosError, AxiosInstance, AxiosResponse } from 'axios';
import type { ApiResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response) {
      const apiError = error.response.data;
      console.error('API Error:', apiError.error?.message || 'Unknown error');
    } else if (error.request) {
      console.error('Network Error: No response received');
    } else {
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

export const api = {
  // Health check
  healthCheck: async (): Promise<ApiResponse<{ status: string }>> => {
    const response = await apiClient.get<ApiResponse<{ status: string }>>('/health');
    return response.data;
  },

  // Projects
  getProjects: async (): Promise<ApiResponse<import('../types').ProjectSummary[]>> => {
    const response = await apiClient.get('/projects');
    return response.data;
  },

  getProject: async (slug: string): Promise<ApiResponse<import('../types').ProjectDetail>> => {
    const response = await apiClient.get(`/projects/${slug}`);
    return response.data;
  },

  // Skills
  getSkills: async (): Promise<ApiResponse<import('../types').SkillCategory[]>> => {
    const response = await apiClient.get('/skills');
    return response.data;
  },

  // Experience
  getExperience: async (): Promise<ApiResponse<import('../types').Experience[]>> => {
    const response = await apiClient.get('/experience');
    return response.data;
  },

  // About
  getAbout: async (): Promise<ApiResponse<import('../types').About>> => {
    const response = await apiClient.get('/about');
    return response.data;
  },
};

export default apiClient;
