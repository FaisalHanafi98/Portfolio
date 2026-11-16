import { useParams, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import ProjectDetail from '../components/projects/ProjectDetail';
import QueryContent from '../components/ui/QueryContent';
import { ProjectCardSkeleton } from '../components/ui/Skeleton';
import { useProject } from '../api/hooks';
import { pageTransition } from '../lib/animations';

export default function ProjectPage() {
  const { slug } = useParams<{ slug: string }>();
  const projectQuery = useProject(slug ?? '');
  const notFound = (
    <div className="max-w-4xl mx-auto text-center py-12">
      <h1 className="text-2xl font-bold text-charcoal mb-4">Project Not Found</h1>
      <p className="text-charcoal-light mb-8">The project you're looking for doesn't exist or has been removed.</p>
      <Link to="/" className="btn-primary">
        Go Home
      </Link>
    </div>
  );

  return (
    <motion.div
      variants={pageTransition}
      initial="initial"
      animate="animate"
      exit="exit"
      className="pt-24 pb-12 px-6"
    >
      {/* Back Link */}
      <div className="max-w-4xl mx-auto mb-8">
        <Link
          to="/#projects"
          className="inline-flex items-center gap-2 text-blue hover:text-blue-dark active:scale-[0.98] font-mono text-sm transition-[color,transform] duration-150 ease-out min-h-[44px] px-1 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Projects
        </Link>
      </div>

      {slug ? (
        <QueryContent
          query={projectQuery}
          resource="project"
          loading={<div className="max-w-4xl mx-auto"><ProjectCardSkeleton /></div>}
          notFound={notFound}
        >
          {(project) => <ProjectDetail project={project} />}
        </QueryContent>
      ) : notFound}
    </motion.div>
  );
}
