import { useEffect } from 'react';
import {
  buildAnalyticsPayload,
  createCaseStudyVisibilityMonitor,
  postAnalyticsEvent,
} from '../lib/analytics';

export function useCaseStudyTracker(projectSlug: string): void {
  useEffect(() => {
    if (typeof window === 'undefined' || typeof document === 'undefined') return undefined;

    const openPayload = buildAnalyticsPayload({
      eventName: 'case_study_open',
      projectSlug,
      sectionKey: 'overview',
    });
    if (!openPayload) return undefined;

    void postAnalyticsEvent(openPayload);

    const monitor = createCaseStudyVisibilityMonitor({
      projectSlug,
      isVisible: () => document.visibilityState === 'visible',
      emit: (payload) => {
        void postAnalyticsEvent(payload);
      },
    });

    let lastTickMs = Date.now();
    const timer = window.setInterval(() => {
      const now = Date.now();
      const delta = Math.max(0, Math.min(5000, now - lastTickMs));
      lastTickMs = now;
      monitor.tick(delta);
    }, 1000);

    const handleVisibility = () => {
      lastTickMs = Date.now();
    };

    const handleScroll = () => {
      const doc = document.documentElement;
      const scrollable = Math.max(1, doc.scrollHeight);
      const pct = Math.min(100, Math.round(((window.scrollY + window.innerHeight) / scrollable) * 100));
      monitor.recordScrollPercent(pct);
    };

    document.addEventListener('visibilitychange', handleVisibility);
    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();

    let observer: IntersectionObserver | null = null;
    if (typeof IntersectionObserver !== 'undefined') {
      observer = new IntersectionObserver(
        (entries) => {
          for (const entry of entries) {
            if (entry.isIntersecting) {
              const section = entry.target.getAttribute('data-analytics-section');
              if (section) monitor.recordSection(section);
            }
          }
        },
        { threshold: 0.35 },
      );

      document.querySelectorAll('[data-analytics-section]').forEach((el) => observer?.observe(el));
    }

    return () => {
      window.clearInterval(timer);
      document.removeEventListener('visibilitychange', handleVisibility);
      window.removeEventListener('scroll', handleScroll);
      observer?.disconnect();
    };
  }, [projectSlug]);
}
