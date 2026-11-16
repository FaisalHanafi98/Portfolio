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

  return (
    <AnimatedSection
      id="projects"
      className="py-24 px-6 bg-cream border-b-2 border-cream-darker/80 relative"
    >
      <div className="max-w-6xl mx-auto">
        <div className="mb-14">
          <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-pink bg-pink/10 px-3 py-1 rounded-full mb-3">
            <span>02 / Selected Engineering Work</span>
          </div>
          <div>
            <motion.h2
              variants={slideUp}
              className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
            >
              Some Things I&apos;ve Built
            </motion.h2>
          </div>
          <SketchUnderline width={200} className="text-pink/50" delay={0.2} />
          <p className="text-charcoal-light mt-4 max-w-2xl">
            Production systems, enterprise workflow automation, multi-tenant platforms, and stage-gated AI/ML pipelines built across my repositories.
          </p>
        </div>

        <QueryContent
          query={query}
          resource="projects"
          loading={(
            <div className="space-y-24">
              <ProjectCardSkeleton />
              <ProjectCardSkeleton />
            </div>
          )}
          emptyMessage="No projects available."
        >
          {(projects) => {
            const featuredProjects = projects.filter((project) => project.featured);
            const otherProjects = projects.filter((project) => !project.featured);

            return (
              <>
                {/* Featured Projects */}
                <motion.div
                  variants={staggerContainer}
                  initial="hidden"
                  whileInView="visible"
                  viewport={{ once: true }}
                  className="space-y-24"
                >
                  {featuredProjects.map((project, index) => (
                    <FeaturedProject
                      key={project.id}
                      project={project}
                      index={index}
                    />
                  ))}
                </motion.div>

                {/* Other Projects */}
                {otherProjects.length > 0 && (
                  <div className="mt-24">
                    <motion.h3
                      variants={slideUp}
                      initial="hidden"
                      whileInView="visible"
                      viewport={{ once: true }}
                      className="text-2xl font-semibold text-charcoal text-center mb-12"
                    >
                      Other Noteworthy Projects
                    </motion.h3>

                    <motion.div
                      variants={staggerContainer}
                      initial="hidden"
                      whileInView="visible"
                      viewport={{ once: true }}
                      className="grid md:grid-cols-2 lg:grid-cols-3 gap-6"
                    >
                      {otherProjects.map((project) => (
                        <ProjectCard key={project.id} project={project} />
                      ))}
                    </motion.div>
                  </div>
                )}
              </>
            );
          }}
        </QueryContent>
      </div>
    </AnimatedSection>
  );
}
