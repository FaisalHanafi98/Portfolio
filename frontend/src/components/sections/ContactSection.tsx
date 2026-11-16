import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import Button from '../ui/Button';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

export default function ContactSection() {
  return (
    <AnimatedSection id="contact" className="py-24 px-6 bg-cream-dark/50 relative">
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true }}
        className="max-w-2xl mx-auto text-center bg-white p-8 md:p-12 rounded-2xl border-2 border-cream-darker shadow-sm"
      >
        <motion.div variants={slideUp} className="mb-3">
          <span className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-blue bg-blue/10 px-3 py-1 rounded-full">
            05 / What&apos;s Next?
          </span>
        </motion.div>

        <motion.div variants={slideUp} className="mb-3 inline-block">
          <h2 className="text-4xl md:text-5xl font-bold text-charcoal">
            Get In Touch
          </h2>
        </motion.div>
        <motion.div variants={slideUp} className="flex justify-center mb-6">
          <SketchUnderline width={140} className="text-pink/50" delay={0.3} />
        </motion.div>

        <motion.p
          variants={slideUp}
          className="text-charcoal-light text-lg mb-8 leading-relaxed"
        >
          I&apos;m currently open to new opportunities and collaborations.
          Whether you have a question about my systems, an engineering role, or just want to say hi,
          my inbox is always open.
        </motion.p>

        <motion.div variants={slideUp} className="flex flex-wrap justify-center gap-4">
          <Button
            href="mailto:faisalhanafi.dev@gmail.com"
            size="lg"
          >
            Say Hello
          </Button>
          <Button
            href="https://www.linkedin.com/in/faisalhanafidsa0105/"
            variant="ghost"
            size="lg"
            external
          >
            LinkedIn Profile
          </Button>
        </motion.div>
      </motion.div>
    </AnimatedSection>
  );
}
