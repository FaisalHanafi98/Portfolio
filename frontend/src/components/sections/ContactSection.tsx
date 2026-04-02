import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import Button from '../ui/Button';
import { slideUp, staggerContainer } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

export default function ContactSection() {
  return (
    <AnimatedSection id="contact" className="py-24 px-6">
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true }}
        className="max-w-2xl mx-auto text-center"
      >
        <motion.p
          variants={slideUp}
          className="font-hand text-blue text-xl mb-4"
        >
          What's Next?
        </motion.p>

        <motion.div variants={slideUp} className="mb-3 inline-block">
          <h2 className="text-4xl md:text-5xl font-bold text-charcoal">
            Get In Touch
          </h2>
        </motion.div>
        <motion.div variants={slideUp} className="flex justify-center mb-6">
          <SketchUnderline width={140} className="text-pink/50" delay={0.5} />
        </motion.div>

        <motion.p
          variants={slideUp}
          className="text-charcoal-light text-lg mb-12"
        >
          I'm currently open to new opportunities and would love to hear from you.
          Whether you have a question, a project idea, or just want to say hi,
          my inbox is always open. I'll do my best to get back to you!
        </motion.p>

        <motion.div variants={slideUp}>
          <Button
            href="mailto:faisalhanafi98@gmail.com"
            size="lg"
          >
            Say Hello
          </Button>
        </motion.div>
      </motion.div>
    </AnimatedSection>
  );
}
