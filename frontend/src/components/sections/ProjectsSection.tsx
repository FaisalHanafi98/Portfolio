import { useState } from 'react';
import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import QueryContent from '../ui/QueryContent';
import FeaturedProject from '../projects/FeaturedProject';
import ProjectCard from '../projects/ProjectCard';
import { ProjectCardSkeleton } from '../ui/Skeleton';
import { useProjects } from '../../api/hooks';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

export default function ProjectsSection() {
  const query = useProjects();
  const [showTechnicalDepth, setShowTechnicalDepth] = useState(false);

  return (
    <AnimatedSection
      id="projects"
      className="py-24 px-6 bg-cream border-b-2 border-cream-darker/80 relative"
    >
      <div className="max-w-6xl mx-auto">
        <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-14">
          <div>
            <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-pink bg-pink/10 px-3 py-1 rounded-full mb-3">
              <span>02 / Business Impact &amp; Case Studies</span>
            </div>
            <div>
              <motion.h2
                variants={slideUp}
                className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
              >
                Systems Built to Solve Real Business Problems
              </motion.h2>
            </div>
            <SketchUnderline width={220} className="text-pink/50" delay={0.2} />
            <p className="text-charcoal-light mt-4 max-w-2xl leading-relaxed">
              CREAMS, Sen2Nal and BAiTI show how I design software for sensitive records, reproducible research and tenant isolation.
            </p>
          </div>

          {/* Dual-View Toggle (Business Impact vs Engineering Depth) */}
          <div className="flex items-center gap-3 self-start lg:self-end bg-white p-2 rounded-xl border border-cream-darker shadow-sm">
            <button
              type="button"
              onClick={() => setShowTechnicalDepth(false)}
              aria-pressed={!showTechnicalDepth}
              className={`px-3.5 py-2 min-h-[44px] rounded-lg font-mono text-xs transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue ${
                !showTechnicalDepth
                  ? 'bg-charcoal text-cream font-semibold'
                  : 'text-charcoal-light hover:text-charcoal'
              }`}
            >
              Business &amp; Impact View
            </button>
            <button
              type="button"
              onClick={() => setShowTechnicalDepth(true)}
              aria-pressed={showTechnicalDepth}
              className={`px-3.5 py-2 min-h-[44px] rounded-lg font-mono text-xs transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue ${
                showTechnicalDepth
                  ? 'bg-charcoal text-cream font-semibold'
                  : 'text-charcoal-light hover:text-charcoal'
              }`}
            >
              + Engineering Depth
            </button>
          </div>
        </div>

        <QueryContent
          query={query}
          resource="projects"
          loading={(
            <div className="space-y-12">
              <ProjectCardSkeleton />
              <ProjectCardSkeleton />
            </div>
          )}
          emptyMessage="No projects available."
        >
          {(projects) => {
            const visibleProjects = projects.filter((project) => project.slug !== 'portfolio');
            const flagshipProjects = visibleProjects.filter((project) => project.featured);
            const additionalProjects = visibleProjects.filter((project) => !project.featured);

            return (
              <motion.div
                variants={staggerContainer}
                initial="hidden"
                whileInView="visible"
                viewport={{ once: true }}
                className="space-y-12"
              >
                {flagshipProjects.map((project, index) => (
                  <FeaturedProject
                    key={project.id}
                    project={project}
                    index={index}
                    showTechnicalDepth={showTechnicalDepth}
                  />
                ))}
                {additionalProjects.length > 0 && (
                  <section aria-labelledby="additional-projects">
                    <h3 id="additional-projects" className="text-xl font-semibold mb-5">
                      Additional projects
                    </h3>
                    <div className="grid gap-6 md:grid-cols-2">
                      {additionalProjects.map((project) => <ProjectCard key={project.id} project={project} />)}
                    </div>
                  </section>
                )}
              </motion.div>
            );
          }}
        </QueryContent>
      </div>
    </AnimatedSection>
  );
}
