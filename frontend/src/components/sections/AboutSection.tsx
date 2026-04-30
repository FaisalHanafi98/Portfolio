import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import SketchDivider from '../ui/SketchDivider';

const stats = [
  { label: 'Dean\'s List Semesters', value: '9', color: 'text-blue' },
  { label: 'Page Load Improvement', value: '68%', color: 'text-pink' },
  { label: 'FYP Recognition', value: 'Gold', color: 'text-green' },
];

const technologies = [
  'SQL',
  'Power BI',
  'TypeScript',
  'React',
  'Spring Boot',
  'PostgreSQL',
  'Laravel',
  'AI Agents',
];

export default function AboutSection() {
  return (
    <>
      <SketchDivider />
      <AnimatedSection id="about" className="py-24 px-6">
        <div className="max-w-4xl mx-auto">
          <div className="mb-12">
            <motion.h2
              variants={slideUp}
              className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
            >
              About Me
            </motion.h2>
            <SketchUnderline width={120} className="text-blue/50" delay={0.3} />
          </div>

          <div className="grid md:grid-cols-3 gap-12">
            <motion.div
              variants={staggerContainer}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
              className="md:col-span-2 space-y-4"
            >
              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                I&apos;m Faisal, a <span className="text-charcoal font-medium">Data Science graduate</span>{' '}
                based in Kuala Lumpur who ended up building at the intersection of analytics,
                software, and business systems. I like taking problems that feel messy at first,
                whether they live in a workflow, a dataset, or a financial decision, and turning
                them into something clearer and more useful.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                Academically, I graduated from{' '}
                <span className="text-blue font-medium">International Islamic University Malaysia</span>{' '}
                with a CGPA of 3.72, made the Dean&apos;s List for 9 semesters, and received a{' '}
                <span className="text-pink font-medium">Gold Medal</span> for my final year project.
                What stayed with me most from that chapter was the moment I realized that building
                the right system can support progress in people&apos;s lives, not just check a technical
                box.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                Professionally, I work inside enterprise delivery at{' '}
                <span className="text-blue font-medium">Accenture Technology Malaysia</span>, where
                I&apos;ve learned how much trust depends on speed, validation, usability, and clean
                reporting. I don&apos;t just want systems that run, I want systems people can rely on
                when the stakes are real.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                I&apos;m also unusually careful with money and transactions, partly by temperament and
                partly because I know what it feels like to lose a substantial amount to a scam.
                That experience made financial clarity deeply personal to me. It&apos;s a big reason
                I&apos;m drawn to analytics, market tools, anomaly detection, and AI-driven financial
                systems that could one day help other people protect themselves better too.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                Here are a few areas I&apos;ve been working across recently:
              </motion.p>

              <motion.ul
                variants={slideUp}
                className="grid grid-cols-2 gap-2 font-mono text-sm"
              >
                {technologies.map((tech) => (
                  <li key={tech} className="flex items-center gap-2 text-charcoal-light">
                    <span className="text-blue">&#9656;</span>
                    {tech}
                  </li>
                ))}
              </motion.ul>
            </motion.div>

            <motion.div
              variants={slideUp}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
              className="relative group"
            >
              <div className="relative z-10 rounded-xl overflow-hidden border-2 border-cream-darker">
                <div className="aspect-square bg-cream-dark flex items-center justify-center">
                  <span className="text-6xl">&#128104;&#8205;&#128187;</span>
                </div>
              </div>
              <div className="absolute inset-0 border-2 border-blue/30 rounded-xl translate-x-4 translate-y-4 -z-10 group-hover:translate-x-2 group-hover:translate-y-2 transition-transform duration-300" />
            </motion.div>
          </div>

          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="grid grid-cols-3 gap-8 mt-16"
          >
            {stats.map((stat) => (
              <motion.div
                key={stat.label}
                variants={slideUp}
                className="text-center p-6 rounded-xl bg-white border border-cream-darker/60 hover:shadow-md transition-shadow"
              >
                <div className={`text-3xl md:text-4xl font-bold mb-2 ${stat.color}`}>
                  {stat.value}
                </div>
                <div className="text-sm text-charcoal-light font-mono">{stat.label}</div>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </AnimatedSection>
    </>
  );
}
