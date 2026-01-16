import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import SketchDivider from '../ui/SketchDivider';

const stats = [
  { label: 'Years Experience', value: '1+' },
  { label: 'Projects Completed', value: '3+' },
  { label: 'Technologies', value: '15+' },
];

const technologies = [
  'TypeScript',
  'React',
  'Spring Boot',
  'Laravel',
  'PostgreSQL',
  'Tailwind CSS',
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
              className="text-3xl md:text-4xl font-bold text-off-white mb-3 inline-block"
            >
              About Me
            </motion.h2>
            <SketchUnderline width={120} className="text-teal/50" delay={0.3} />
          </div>

        <div className="grid md:grid-cols-3 gap-12">
          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="md:col-span-2 space-y-4"
          >
            <motion.p variants={slideUp} className="text-grey-muted leading-relaxed">
              Hello! I'm Faisal, a <span className="text-off-white font-medium">Data Science graduate</span> turned{' '}
              <span className="text-off-white font-medium">full-stack developer</span> based in
              Kuala Lumpur, Malaysia. I enjoy creating things that live on the internet, whether
              that be websites, applications, or anything in between.
            </motion.p>

            <motion.p variants={slideUp} className="text-grey-muted leading-relaxed">
              I graduated from{' '}
              <span className="text-teal">International Islamic University Malaysia (IIUM)</span>{' '}
              with a CGPA of 3.72 and received the{' '}
              <span className="text-coral font-medium">Gold Medal</span> for Best Final Year Project in
              the Development category.
            </motion.p>

            <motion.p variants={slideUp} className="text-grey-muted leading-relaxed">
              Currently, I'm working as an Application Development Associate at{' '}
              <span className="text-teal">Accenture Technology Malaysia</span>, where I focus
              on building enterprise-grade applications using React, TypeScript, and SharePoint
              technologies.
            </motion.p>

            <motion.p variants={slideUp} className="text-grey-muted leading-relaxed">
              Here are a few technologies I've been working with recently:
            </motion.p>

            <motion.ul
              variants={slideUp}
              className="grid grid-cols-2 gap-2 font-mono text-sm"
            >
              {technologies.map((tech) => (
                <li key={tech} className="flex items-center gap-2 text-grey-muted">
                  <span className="text-teal">▹</span>
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
            <div className="relative z-10 bg-teal/20 rounded-lg overflow-hidden sketch-border">
              <div className="aspect-square bg-charcoal-light flex items-center justify-center">
                <span className="text-6xl">👨‍💻</span>
              </div>
              <div className="absolute inset-0 bg-teal/10 group-hover:bg-transparent transition-colors duration-300" />
            </div>
            <div className="absolute inset-0 border-2 border-teal rounded-lg translate-x-4 translate-y-4 -z-10 group-hover:translate-x-2 group-hover:translate-y-2 transition-transform duration-300" />
          </motion.div>
        </div>

        {/* Stats */}
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
              className="text-center p-6 rounded-lg sketch-border bg-charcoal-light/50"
            >
              <div className="text-3xl md:text-4xl font-bold mb-2 bg-gradient-to-r from-teal to-coral bg-clip-text text-transparent">
                {stat.value}
              </div>
              <div className="text-sm text-grey-muted font-mono">{stat.label}</div>
            </motion.div>
          ))}
        </motion.div>
        </div>
      </AnimatedSection>
    </>
  );
}
