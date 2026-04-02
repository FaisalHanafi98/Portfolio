import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import SketchDivider from '../ui/SketchDivider';

const stats = [
  { label: 'Years Experience', value: '1+', color: 'text-blue' },
  { label: 'Projects Completed', value: '3+', color: 'text-pink' },
  { label: 'Technologies', value: '15+', color: 'text-green' },
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
                Hello! I'm Faisal, a <span className="text-charcoal font-medium">Data Science graduate</span> turned{' '}
                <span className="text-charcoal font-medium">full-stack developer</span> based in
                Kuala Lumpur, Malaysia. I enjoy creating things that live on the internet, whether
                that be websites, applications, or anything in between.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                I graduated from{' '}
                <span className="text-blue font-medium">International Islamic University Malaysia (IIUM)</span>{' '}
                with a CGPA of 3.72 and received the{' '}
                <span className="text-pink font-medium">Gold Medal</span> for Best Final Year Project in
                the Development category.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                Currently, I'm working as an Application Development Associate at{' '}
                <span className="text-blue font-medium">Accenture Technology Malaysia</span>, where I focus
                on building enterprise-grade applications using React, TypeScript, and SharePoint
                technologies.
              </motion.p>

              <motion.p variants={slideUp} className="text-charcoal-light leading-relaxed">
                Here are a few technologies I've been working with recently:
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
