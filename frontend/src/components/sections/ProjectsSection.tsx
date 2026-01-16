import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import FeaturedProject from '../projects/FeaturedProject';
import ProjectCard from '../projects/ProjectCard';
import { ProjectCardSkeleton } from '../ui/Skeleton';
import { useProjects } from '../../api/hooks';
import { slideUp, staggerContainer } from '../../lib/animations';

export default function ProjectsSection() {
  const { data: projects, isLoading, error } = useProjects();

  const featuredProjects = projects?.filter((p) => p.featured) || [];
  const otherProjects = projects?.filter((p) => !p.featured) || [];

  return (
    <AnimatedSection id="projects" className="py-24 px-6">
      <div className="max-w-6xl mx-auto">
        <motion.h2
          variants={slideUp}
          className="section-heading flex items-center gap-4"
        >
          <span className="text-teal font-mono text-xl">02.</span>
          Some Things I've Built
          <span className="flex-1 h-px bg-navy-lighter ml-4" />
        </motion.h2>

        {isLoading ? (
          <div className="space-y-24">
            <ProjectCardSkeleton />
            <ProjectCardSkeleton />
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-slate">Unable to load projects. Please try again later.</p>
          </div>
        ) : (
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
                  className="text-2xl font-semibold text-slate-light text-center mb-12"
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
        )}
      </div>
    </AnimatedSection>
  );
}
