import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Tag from '../ui/Tag';
import { slideUp } from '../../lib/animations';
import type { ProjectSummary } from '../../types';

interface FeaturedProjectProps {
  project: ProjectSummary;
  index: number;
}

export default function FeaturedProject({ project, index }: FeaturedProjectProps) {
  const isEven = index % 2 === 0;

  return (
    <motion.div
      variants={slideUp}
      className={`relative grid md:grid-cols-12 items-center gap-4 ${
        isEven ? '' : 'md:text-right'
      }`}
    >
      {/* Image */}
      <div
        className={`md:col-span-7 ${
          isEven ? 'md:col-start-1' : 'md:col-start-6'
        } md:row-start-1`}
      >
        <Link to={`/projects/${project.slug}`} className="block relative group">
          <div className="relative overflow-hidden rounded-lg">
            {project.thumbnailUrl ? (
              <img
                src={project.thumbnailUrl}
                alt={project.title}
                className="w-full aspect-video object-cover"
              />
            ) : (
              <div className="w-full aspect-video bg-navy-light flex items-center justify-center">
                <span className="text-4xl">🚀</span>
              </div>
            )}
            <div className="absolute inset-0 bg-teal/20 group-hover:bg-transparent transition-colors duration-300" />
          </div>
        </Link>
      </div>

      {/* Content */}
      <div
        className={`md:col-span-6 md:row-start-1 relative z-10 ${
          isEven ? 'md:col-start-6 md:text-right' : 'md:col-start-1'
        }`}
      >
        <p className="font-mono text-teal text-sm mb-2">Featured Project</p>

        <Link to={`/projects/${project.slug}`}>
          <h3 className="text-2xl font-bold text-slate-light hover:text-teal transition-colors mb-4">
            {project.title}
          </h3>
        </Link>

        <div className="bg-navy-light p-6 rounded-lg shadow-xl mb-4">
          <p className="text-slate">{project.shortDescription}</p>
        </div>

        <div className={`flex flex-wrap gap-3 mb-4 ${isEven ? 'md:justify-end' : ''}`}>
          {project.technologies.map((tech) => (
            <Tag key={tech} size="sm">
              {tech}
            </Tag>
          ))}
        </div>

        <div className={`flex gap-4 ${isEven ? 'md:justify-end' : ''}`}>
          <Link
            to={`/projects/${project.slug}`}
            className="text-slate hover:text-teal transition-colors"
            aria-label="View project details"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
          </Link>
        </div>
      </div>
    </motion.div>
  );
}
