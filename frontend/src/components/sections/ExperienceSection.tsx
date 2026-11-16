import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import QueryContent from '../ui/QueryContent';
import { ExperienceSkeleton } from '../ui/Skeleton';
import { useExperience } from '../../api/hooks';
import { slideUp } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import type { Experience } from '../../types';

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
}

export default function ExperienceSection() {
  const query = useExperience();

  return (
    <AnimatedSection
      id="experience"
      className="py-24 px-6 bg-white/90 border-b-2 border-cream-darker/80 relative"
    >
      <div className="max-w-4xl mx-auto">
        <div className="mb-12">
          <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-green bg-green/10 px-3 py-1 rounded-full mb-3">
            <span>04 / Career &amp; Education</span>
          </div>
          <div>
            <motion.h2
              variants={slideUp}
              className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
            >
              Where I&apos;ve Worked
            </motion.h2>
          </div>
          <SketchUnderline width={180} className="text-green/50" delay={0.2} />
        </div>

        <QueryContent
          query={query}
          resource="experience"
          loading={<ExperienceSkeleton />}
          emptyMessage="No experience data available."
        >
          {(experiences) => <ExperienceTimeline experiences={experiences} />}
        </QueryContent>
      </div>
    </AnimatedSection>
  );
}

function ExperienceTimeline({ experiences }: { experiences: Experience[] }) {
  const [selectedId, setSelectedId] = useState<number>();
  const activeExperience = experiences.find((experience) => experience.id === selectedId)
    ?? experiences[0];

  return (
    <div className="flex flex-col md:flex-row gap-8">
      <div
        role="group"
        aria-label="Companies"
        className="flex md:flex-col overflow-x-auto md:overflow-visible border-b md:border-b-0 md:border-l-2 border-cream-darker"
      >
        {experiences.map((exp) => (
          <button
            key={exp.id}
            type="button"
            onClick={() => setSelectedId(exp.id)}
            aria-pressed={activeExperience.id === exp.id}
            className={`
              px-4 py-3 min-h-[44px] text-left font-mono text-sm whitespace-nowrap
              transition-[color,background-color,transform] duration-200 ease-out relative
              active:scale-[0.99] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-[-2px] focus-visible:outline-blue
              ${
                activeExperience.id === exp.id
                  ? 'text-blue bg-blue/10 font-medium'
                  : 'text-charcoal-light hover:text-charcoal hover:bg-cream-dark/50'
              }
            `}
          >
            {exp.company}
            <span
              className={`
                absolute left-0 bottom-0 md:bottom-auto md:left-0 md:top-0
                h-0.5 md:h-full md:w-0.5 w-full
                transition-[background-color,transform] duration-200 ease-out
                ${activeExperience.id === exp.id ? 'bg-blue scale-100' : 'bg-transparent scale-95'}
              `}
            />
          </button>
        ))}
      </div>

      <div className="flex-1 min-h-[300px] min-w-0">
        <AnimatePresence mode="wait">
          <motion.div
            key={activeExperience.id}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={{ duration: 0.2, ease: [0.22, 1, 0.36, 1] }}
          >
            <h3 className="text-xl font-semibold text-charcoal break-words">
              {activeExperience.role}{' '}
              <span className="text-blue">@ {activeExperience.company}</span>
            </h3>

            <p className="font-mono text-sm text-charcoal-light mt-1 mb-4">
              {formatDate(activeExperience.startDate)} -{' '}
              {activeExperience.endDate ? formatDate(activeExperience.endDate) : 'Present'}
              {activeExperience.location ? ` · ${activeExperience.location}` : ''}
            </p>

            <p className="text-charcoal-light mb-4 leading-relaxed">{activeExperience.description}</p>

            <ul className="space-y-2.5">
              {activeExperience.highlights.map((highlight, i) => (
                <li
                  key={i}
                  className="flex items-start gap-2.5 text-charcoal-light leading-relaxed"
                >
                  <span className="text-green mt-1 shrink-0" aria-hidden="true">&#9656;</span>
                  <span className="min-w-0 break-words">{highlight}</span>
                </li>
              ))}
            </ul>
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  );
}
