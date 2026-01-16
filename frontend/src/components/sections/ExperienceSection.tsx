import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { ExperienceSkeleton } from '../ui/Skeleton';
import { useExperience } from '../../api/hooks';
import { slideUp } from '../../lib/animations';

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
}

export default function ExperienceSection() {
  const { data: experiences, isLoading, error } = useExperience();
  const [activeIndex, setActiveIndex] = useState(0);

  return (
    <AnimatedSection id="experience" className="py-24 px-6">
      <div className="max-w-4xl mx-auto">
        <motion.h2
          variants={slideUp}
          className="section-heading flex items-center gap-4"
        >
          <span className="text-teal font-mono text-xl">04.</span>
          Where I've Worked
          <span className="flex-1 h-px bg-navy-lighter ml-4" />
        </motion.h2>

        {isLoading ? (
          <ExperienceSkeleton />
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-slate">Unable to load experience. Please try again later.</p>
          </div>
        ) : experiences && experiences.length > 0 ? (
          <div className="flex flex-col md:flex-row gap-8">
            {/* Tab List */}
            <div className="flex md:flex-col overflow-x-auto md:overflow-visible border-b md:border-b-0 md:border-l border-navy-lighter">
              {experiences.map((exp, index) => (
                <button
                  key={exp.id}
                  onClick={() => setActiveIndex(index)}
                  className={`
                    px-4 py-3 text-left font-mono text-sm whitespace-nowrap
                    transition-all duration-300 relative
                    ${
                      activeIndex === index
                        ? 'text-teal bg-navy-light/50'
                        : 'text-slate hover:text-teal hover:bg-navy-light/30'
                    }
                  `}
                >
                  {exp.company}
                  <span
                    className={`
                      absolute left-0 bottom-0 md:bottom-auto md:left-0 md:top-0
                      h-0.5 md:h-full md:w-0.5 w-full md:w-0.5
                      transition-all duration-300
                      ${activeIndex === index ? 'bg-teal' : 'bg-transparent'}
                    `}
                  />
                </button>
              ))}
            </div>

            {/* Tab Panels */}
            <div className="flex-1 min-h-[300px]">
              <AnimatePresence mode="wait">
                {experiences.map(
                  (exp, index) =>
                    activeIndex === index && (
                      <motion.div
                        key={exp.id}
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -10 }}
                        transition={{ duration: 0.2 }}
                      >
                        <h3 className="text-xl font-semibold text-slate-light">
                          {exp.role}{' '}
                          <span className="text-teal">@ {exp.company}</span>
                        </h3>

                        <p className="font-mono text-sm text-slate mt-1 mb-4">
                          {formatDate(exp.startDate)} -{' '}
                          {exp.endDate ? formatDate(exp.endDate) : 'Present'}
                        </p>

                        <p className="text-slate mb-4">{exp.description}</p>

                        <ul className="space-y-2">
                          {exp.highlights.map((highlight, i) => (
                            <li
                              key={i}
                              className="flex items-start gap-2 text-slate"
                            >
                              <span className="text-teal mt-1.5">▹</span>
                              {highlight}
                            </li>
                          ))}
                        </ul>
                      </motion.div>
                    )
                )}
              </AnimatePresence>
            </div>
          </div>
        ) : (
          <p className="text-slate text-center">No experience data available.</p>
        )}
      </div>
    </AnimatedSection>
  );
}
