import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { SkillsSkeleton } from '../ui/Skeleton';
import { useSkills } from '../../api/hooks';
import { slideUp, staggerContainer } from '../../lib/animations';

export default function SkillsSection() {
  const { data: skillCategories, isLoading, error } = useSkills();

  return (
    <AnimatedSection id="skills" className="py-24 px-6">
      <div className="max-w-4xl mx-auto">
        <motion.h2
          variants={slideUp}
          className="section-heading flex items-center gap-4"
        >
          <span className="text-teal font-mono text-xl">03.</span>
          Skills & Technologies
          <span className="flex-1 h-px bg-navy-lighter ml-4" />
        </motion.h2>

        {isLoading ? (
          <SkillsSkeleton />
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-slate">Unable to load skills. Please try again later.</p>
          </div>
        ) : (
          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="space-y-10"
          >
            {skillCategories?.map((category) => (
              <motion.div key={category.id} variants={slideUp}>
                <h3 className="text-lg font-semibold text-slate-light mb-4 font-mono">
                  {category.name}
                </h3>
                <div className="flex flex-wrap gap-3">
                  {category.skills.map((skill) => (
                    <motion.div
                      key={skill.id}
                      className="group relative"
                      whileHover={{ scale: 1.05 }}
                      transition={{ type: 'spring', stiffness: 400 }}
                    >
                      <div className="px-4 py-2 bg-navy-light border border-navy-lighter rounded-lg hover:border-teal/50 transition-colors">
                        <span className="text-slate group-hover:text-teal transition-colors text-sm">
                          {skill.name}
                        </span>
                      </div>
                      {/* Proficiency indicator */}
                      <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 flex gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity">
                        {[1, 2, 3, 4, 5].map((level) => (
                          <div
                            key={level}
                            className={`w-1 h-1 rounded-full ${
                              level <= skill.proficiency
                                ? 'bg-teal'
                                : 'bg-navy-lighter'
                            }`}
                          />
                        ))}
                      </div>
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            ))}
          </motion.div>
        )}
      </div>
    </AnimatedSection>
  );
}
