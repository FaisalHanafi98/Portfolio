import { Link } from 'react-router-dom';
import { motion, useMotionValue, useSpring, useTransform } from 'framer-motion';
import { useRef } from 'react';
import Tag from '../ui/Tag';
import { slideUp } from '../../lib/animations';
import type { ProjectSummary } from '../../types';

interface ProjectCardProps {
  project: ProjectSummary;
}

export default function ProjectCard({ project }: ProjectCardProps) {
  const cardRef = useRef<HTMLDivElement>(null);

  const mouseX = useMotionValue(0);
  const mouseY = useMotionValue(0);

  const rotateX = useSpring(useTransform(mouseY, [-0.5, 0.5], [5, -5]), {
    stiffness: 300,
    damping: 30,
  });
  const rotateY = useSpring(useTransform(mouseX, [-0.5, 0.5], [-5, 5]), {
    stiffness: 300,
    damping: 30,
  });

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!cardRef.current) return;
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
    <motion.div variants={slideUp} style={{ perspective: '1000px' }}>
      <Link to={`/projects/${project.slug}`}>
        <motion.div
          ref={cardRef}
          className="sketch-card group h-full flex flex-col relative overflow-hidden"
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          style={{
            rotateX,
            rotateY,
            transformStyle: 'preserve-3d',
          }}
          whileHover={{
            scale: 1.02,
            transition: { duration: 0.3 },
          }}
        >
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <div className="text-blue">
              <svg className="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
              </svg>
            </div>
            <motion.span
              className="text-charcoal-lighter hover:text-blue transition-colors"
              whileHover={{ scale: 1.2, rotate: 5 }}
              transition={{ type: 'spring', stiffness: 400 }}
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </motion.span>
          </div>

          {/* Content */}
          <h3 className="text-xl font-semibold text-charcoal group-hover:text-blue transition-colors mb-2">
            {project.title}
          </h3>
          <p className="text-charcoal-light text-sm flex-1 mb-6 leading-relaxed">
            {project.shortDescription}
          </p>

          {/* Technologies */}
          <div className="flex flex-wrap gap-2 mt-auto">
            {project.technologies.slice(0, 4).map((tech, index) => (
              <motion.div
                key={tech}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: index * 0.05 }}
              >
                <Tag size="sm" variant="outline">
                  {tech}
                </Tag>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </Link>
    </motion.div>
  );
}
