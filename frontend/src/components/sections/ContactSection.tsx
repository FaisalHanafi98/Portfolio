import { motion } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import Button from '../ui/Button';
import { slideUp, staggerContainer } from '../../lib/animations';

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
          className="font-mono text-teal mb-4"
        >
          05. What's Next?
        </motion.p>

        <motion.h2
          variants={slideUp}
          className="text-4xl md:text-5xl font-bold text-slate-light mb-6"
        >
          Get In Touch
        </motion.h2>

        <motion.p
          variants={slideUp}
          className="text-slate text-lg mb-12"
        >
          I'm currently open to new opportunities and would love to hear from you.
          Whether you have a question, a project idea, or just want to say hi,
          my inbox is always open. I'll do my best to get back to you!
        </motion.p>

        <motion.div variants={slideUp}>
          <Button
            href="mailto:faisal@example.com"
            size="lg"
          >
            Say Hello
          </Button>
        </motion.div>
      </motion.div>
    </AnimatedSection>
  );
}
