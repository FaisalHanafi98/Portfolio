import { ReactNode } from 'react';
import { motion, Variants } from 'framer-motion';
import { slideUp, viewportOptions } from '../../lib/animations';

interface AnimatedSectionProps {
  children: ReactNode;
  className?: string;
  id?: string;
  variants?: Variants;
  delay?: number;
}

export default function AnimatedSection({
  children,
  className = '',
  id,
  variants = slideUp,
  delay = 0,
}: AnimatedSectionProps) {
  return (
    <motion.section
      id={id}
      className={className}
      initial="hidden"
      whileInView="visible"
      viewport={viewportOptions}
      variants={variants}
      transition={{ delay }}
    >
      {children}
    </motion.section>
  );
}
