import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';

// Lazy load the project detail page — only needed when user navigates to /projects/:slug
const ProjectPage = lazy(() => import('./pages/ProjectPage'));

function LazyFallback() {
  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="w-8 h-8 border-2 border-charcoal-lighter border-t-blue rounded-full animate-spin" />
    </div>
  );
}

function App() {
  return (
    <Layout>
      <AnimatePresence mode="wait">
        <Suspense fallback={<LazyFallback />}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/projects/:slug" element={<ProjectPage />} />
          </Routes>
        </Suspense>
      </AnimatePresence>
    </Layout>
  );
}

export default App;
