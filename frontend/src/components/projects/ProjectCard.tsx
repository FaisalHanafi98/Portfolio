import { Link } from 'react-router-dom';
import { motion, useMotionValue, useReducedMotion, useSpring, useTransform } from 'framer-motion';
import { useRef } from 'react';
import Tag from '../ui/Tag';
import { slideUp } from '../../lib/animations';
import type { ProjectSummary } from '../../types';

interface ProjectCardProps {
  project: ProjectSummary;
}

export default function ProjectCard({ project }: ProjectCardProps) {
  const cardRef = useRef<HTMLDivElement>(null);
  const prefersReducedMotion = useReducedMotion();

  const mouseX = useMotionValue(0);
  const mouseY = useMotionValue(0);

  const rotateX = useSpring(useTransform(mouseY, [-0.5, 0.5], [3, -3]), {
    stiffness: 320,
    damping: 28,
  });
  const rotateY = useSpring(useTransform(mouseX, [-0.5, 0.5], [-3, 3]), {
    stiffness: 320,
    damping: 28,
  });

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (prefersReducedMotion || !cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    mouseX.set((e.clientX - centerX) / rect.width);
    mouseY.set((e.clientY - centerY) / rect.height);
  };

  const handleMouseLeave = () => {
    mouseX.set(0);
    mouseY.set(0);
  };

  return (
    <motion.div variants={slideUp} className="h-full min-w-0" style={{ perspective: '1000px' }}>
      <Link
        to={`/projects/${project.slug}`}
        className="block h-full rounded-lg focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
      >
        <motion.div
          ref={cardRef}
          className="sketch-card group h-full flex flex-col relative overflow-hidden active:scale-[0.99]"
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          style={
            prefersReducedMotion
              ? undefined
              : {
                  rotateX,
                  rotateY,
                  transformStyle: 'preserve-3d',
                }
          }
          whileHover={
            prefersReducedMotion
              ? undefined
              : {
                  y: -3,
                  transition: { duration: 0.2, ease: [0.22, 1, 0.36, 1] },
                }
          }
        >
          {/* Header */}
          <div className="flex items-center justify-between mb-5">
            <div className="text-blue">
              <svg className="w-9 h-9" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
              </svg>
            </div>
            <div className="flex items-center gap-2">
              {project.period && (
                <span className="font-mono text-xs text-charcoal-light">{project.period}</span>
              )}
              <span className="text-charcoal-light group-hover:text-blue group-hover:translate-x-0.5 transition-[color,transform] duration-200 ease-out">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </span>
            </div>
          </div>

          {/* Content */}
          <h3 className="text-xl font-semibold text-charcoal group-hover:text-blue transition-colors mb-2 break-words">
            {project.title}
          </h3>
          <p className="text-charcoal-light text-sm flex-1 mb-6 leading-relaxed break-words">
            {project.shortDescription}
          </p>

          {/* Technologies */}
          <div className="flex flex-wrap gap-2 mt-auto">
            {project.technologies.slice(0, 4).map((tech) => (
              <Tag key={tech} size="sm" variant="outline">
                {tech}
              </Tag>
            ))}
          </div>
        </motion.div>
      </Link>
    </motion.div>
  );
}
