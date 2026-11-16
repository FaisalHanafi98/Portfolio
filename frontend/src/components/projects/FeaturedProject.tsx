import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Tag from '../ui/Tag';
import { slideUp } from '../../lib/animations';
import { projectImages } from '../../lib/projectImages';
import type { ProjectSummary } from '../../types';

interface FeaturedProjectProps {
  project: ProjectSummary;
  index: number;
}

export default function FeaturedProject({ project, index }: FeaturedProjectProps) {
  const isEven = index % 2 === 0;
  const imageSrc = projectImages.get(project.slug);
  const [imageError, setImageError] = useState(false);
  const showImage = Boolean(imageSrc && !imageError);

  return (
    <motion.div
      variants={slideUp}
      className={`relative grid md:grid-cols-12 items-center gap-6 ${
        isEven ? '' : 'md:text-right'
      }`}
    >
      {/* Image */}
      <div
        className={`md:col-span-7 min-w-0 ${
          isEven ? 'md:col-start-1' : 'md:col-start-6'
        } md:row-start-1`}
      >
        <Link
          to={`/projects/${project.slug}`}
          className="block relative group rounded-xl focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
        >
          <div className="relative overflow-hidden rounded-xl border-2 border-cream-darker/70 shadow-sm transition-[border-color,box-shadow] duration-200 ease-out group-hover:shadow-md group-hover:border-blue/60 bg-white">
            {showImage ? (
              <img
                src={imageSrc}
                alt={project.title}
                onError={() => setImageError(true)}
                className="w-full aspect-video object-cover object-top transition-transform duration-300 ease-out group-hover:scale-[1.02]"
                loading="lazy"
              />
            ) : (
              <div className="w-full aspect-video bg-cream-dark/70 flex flex-col items-center justify-center p-6 text-center">
                <span className="text-xs font-mono uppercase tracking-widest text-blue mb-2">
                  Architecture &amp; Case Study
                </span>
                <span className="text-lg text-charcoal font-semibold mb-2 break-words max-w-md">
                  {project.title}
                </span>
                <div className="flex flex-wrap justify-center gap-1.5 mt-2">
                  {project.technologies.slice(0, 4).map((tech) => (
                    <span
                      key={tech}
                      className="text-xs font-mono px-2.5 py-0.5 rounded-full bg-white/90 text-charcoal-light border border-cream-darker"
                    >
                      {tech}
                    </span>
                  ))}
                </div>
              </div>
            )}
            <div className="absolute inset-0 bg-gradient-to-t from-charcoal/20 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200 ease-out" />
          </div>
        </Link>
      </div>

      {/* Content */}
      <div
        className={`md:col-span-6 md:row-start-1 relative z-10 min-w-0 ${
          isEven ? 'md:col-start-6 md:text-right' : 'md:col-start-1'
        }`}
      >
        <div className={`flex items-center gap-3 mb-2 ${isEven ? 'md:justify-end' : ''}`}>
          <p className="font-hand text-lg text-blue font-semibold">Featured Project</p>
          {project.period && (
            <span className="font-mono text-xs text-charcoal-light px-2.5 py-0.5 rounded-full bg-cream-dark">
              {project.period}
            </span>
          )}
        </div>

        <Link
          to={`/projects/${project.slug}`}
          className="inline-block rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
        >
          <h3 className="text-2xl font-bold text-charcoal hover:text-blue transition-colors mb-4 break-words">
            {project.title}
          </h3>
        </Link>

        <div className="bg-white p-6 rounded-xl shadow-sm border border-cream-darker/60 mb-4 text-left">
          <p className="text-charcoal leading-relaxed break-words">{project.shortDescription}</p>
        </div>

        <div className={`flex flex-wrap gap-2.5 mb-4 ${isEven ? 'md:justify-end' : ''}`}>
          {project.technologies.map((tech) => (
            <Tag key={tech} size="sm">
              {tech}
            </Tag>
          ))}
        </div>

        <div className={`flex gap-4 ${isEven ? 'md:justify-end' : ''}`}>
          <Link
            to={`/projects/${project.slug}`}
            className="inline-flex items-center gap-2 font-mono text-xs font-medium text-blue hover:text-blue-dark active:scale-[0.98] transition-[color,transform] duration-150 ease-out min-h-[36px] px-1 rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
            aria-label={`Read case study for ${project.title}`}
          >
            <span>Read Case Study</span>
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
            </svg>
          </Link>
        </div>
      </div>
    </motion.div>
  );
}
