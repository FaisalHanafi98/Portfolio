import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import QueryContent from '../ui/QueryContent';
import { SkillsSkeleton } from '../ui/Skeleton';
import { useSkills } from '../../api/hooks';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

const categoryColors = ['text-blue', 'text-pink', 'text-green', 'text-purple', 'text-coral'];
const categoryBorders = ['border-blue/20 hover:border-blue/40', 'border-pink/20 hover:border-pink/40', 'border-green/20 hover:border-green/40', 'border-purple/20 hover:border-purple/40', 'border-coral/20 hover:border-coral/40'];
const categoryBgs = ['hover:bg-blue/5', 'hover:bg-pink/5', 'hover:bg-green/5', 'hover:bg-purple/5', 'hover:bg-coral/5'];

export default function SkillsSection() {
  const query = useSkills();

  return (
    <AnimatedSection
      id="skills"
      className="py-24 px-6 bg-cream-dark/70 border-b-2 border-cream-darker/80 notebook-lines relative"
    >
      <div className="max-w-4xl mx-auto">
        <div className="mb-12">
          <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-purple bg-purple/10 px-3 py-1 rounded-full mb-3">
            <span>03 / Technical Stack</span>
          </div>
          <div>
            <motion.h2
              variants={slideUp}
              className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
            >
              Skills &amp; Technologies
            </motion.h2>
          </div>
          <SketchUnderline width={200} className="text-purple/50" delay={0.2} />
        </div>

        <QueryContent
          query={query}
          resource="skills"
          loading={<SkillsSkeleton />}
          emptyMessage="No skills available."
        >
          {(skillCategories) => (
            <motion.div
              variants={staggerContainer}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
              className="space-y-10"
            >
              {skillCategories.map((category, catIndex) => (
                <motion.div
                  key={category.id}
                  variants={slideUp}
                  className="bg-white/95 p-6 rounded-xl border border-cream-darker shadow-sm"
                >
                  <h3 className={`text-lg font-semibold mb-4 font-hand text-xl ${categoryColors[catIndex % categoryColors.length]}`}>
                    {category.name}
                  </h3>
                  <div className="flex flex-wrap gap-3">
                    {category.skills.map((skill) => (
                      <motion.div
                        key={skill.id}
                        className="group relative"
                        whileHover={{ y: -2 }}
                        transition={{ duration: 0.18, ease: [0.22, 1, 0.36, 1] }}
                      >
                        <div className={`px-4 py-2 bg-white border rounded-lg transition-[border-color,background-color] duration-200 ease-out ${categoryBorders[catIndex % categoryBorders.length]} ${categoryBgs[catIndex % categoryBgs.length]}`}>
                          <span className="text-charcoal-light group-hover:text-charcoal transition-colors text-sm font-medium">
                            {skill.name}
                          </span>
                        </div>
                      </motion.div>
                    ))}
                  </div>
                </motion.div>
              ))}
            </motion.div>
          )}
        </QueryContent>
      </div>
    </AnimatedSection>
  );
}
