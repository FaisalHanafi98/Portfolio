import { lazy, Suspense, useEffect } from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import { AnimatePresence, MotionConfig } from 'framer-motion';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';
import NotFoundPage from './pages/NotFoundPage';

// Lazy load secondary pages — only needed when user navigates away from home
const ProjectPage = lazy(() => import('./pages/ProjectPage'));
const StatusPage = lazy(() => import('./pages/StatusPage'));
const AnalyticsPage = lazy(() => import('./pages/AnalyticsPage'));

function LazyFallback() {
  return (
    <div className="min-h-screen flex items-center justify-center">
      <p role="status" className="sr-only">Loading page…</p>
      <div aria-hidden="true" className="w-8 h-8 border-2 border-charcoal-lighter border-t-blue rounded-full animate-spin" />
    </div>
  );
}

function App() {
  const { pathname, hash } = useLocation();

  useEffect(() => {
    if (hash) {
      document.getElementById(hash.slice(1))?.scrollIntoView();
    } else {
      window.scrollTo({ top: 0, behavior: 'instant' });
    }
  }, [pathname, hash]);

  return (
    <MotionConfig reducedMotion="user">
      <Layout>
        <AnimatePresence mode="wait">
          <Suspense fallback={<LazyFallback />}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/projects/:slug" element={<ProjectPage />} />
              <Route path="/status" element={<StatusPage />} />
              <Route path="/analytics" element={<AnalyticsPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </Suspense>
        </AnimatePresence>
      </Layout>
    </MotionConfig>
  );
}

export default App;
