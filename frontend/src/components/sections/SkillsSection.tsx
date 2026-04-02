import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { SkillsSkeleton } from '../ui/Skeleton';
import { useSkills } from '../../api/hooks';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

const categoryColors = ['text-blue', 'text-pink', 'text-green', 'text-purple', 'text-coral'];
const categoryBorders = ['border-blue/20 hover:border-blue/40', 'border-pink/20 hover:border-pink/40', 'border-green/20 hover:border-green/40', 'border-purple/20 hover:border-purple/40', 'border-coral/20 hover:border-coral/40'];
const categoryBgs = ['hover:bg-blue/5', 'hover:bg-pink/5', 'hover:bg-green/5', 'hover:bg-purple/5', 'hover:bg-coral/5'];

export default function SkillsSection() {
  const { data: skillCategories, isLoading, error } = useSkills();

  return (
    <AnimatedSection id="skills" className="py-24 px-6">
      <div className="max-w-4xl mx-auto">
        <div className="mb-12">
          <motion.h2
            variants={slideUp}
            className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
          >
            Skills & Technologies
          </motion.h2>
          <SketchUnderline width={200} className="text-purple/50" delay={0.3} />
        </div>

        {isLoading ? (
          <SkillsSkeleton />
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-charcoal-light">Unable to load skills. Please try again later.</p>
          </div>
        ) : (
          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="space-y-10"
          >
            {skillCategories?.map((category, catIndex) => (
              <motion.div key={category.id} variants={slideUp}>
                <h3 className={`text-lg font-semibold mb-4 font-hand text-xl ${categoryColors[catIndex % categoryColors.length]}`}>
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
                      <div className={`px-4 py-2 bg-white border rounded-lg transition-all ${categoryBorders[catIndex % categoryBorders.length]} ${categoryBgs[catIndex % categoryBgs.length]}`}>
                        <span className="text-charcoal-light group-hover:text-charcoal transition-colors text-sm">
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
                                ? categoryColors[catIndex % categoryColors.length].replace('text-', 'bg-')
                                : 'bg-cream-darker'
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
