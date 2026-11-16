import type { Variants, Transition } from 'framer-motion';

// Crisp ease-out curve for interruptible UI entrances
export const easeOutCurve: [number, number, number, number] = [0.22, 1, 0.36, 1];

// Default transition settings
const defaultTransition: Transition = {
  duration: 0.35,
  ease: easeOutCurve,
};

// Slide up animation
export const slideUp: Variants = {
  hidden: { opacity: 0, y: 16 },
  visible: {
    opacity: 1,
    y: 0,
    transition: defaultTransition,
  },
};

// Stagger container for child animations
export const staggerContainer: Variants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.06,
      delayChildren: 0.04,
    },
  },
};

// Hero text reveal animation
export const heroTextReveal: Variants = {
  hidden: { opacity: 0, y: 14 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.4,
      ease: easeOutCurve,
    },
  },
};

// Letter-by-letter reveal animation
export const letterReveal: Variants = {
  hidden: { opacity: 0, y: 12 },
  visible: (custom: number) => ({
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.28,
      delay: 0.1 + custom * 0.02, // Crisp 20ms stagger per letter
      ease: easeOutCurve,
    },
  }),
};

// Page transition
export const pageTransition: Variants = {
  initial: { opacity: 0, y: 8 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.28, ease: easeOutCurve },
  },
  exit: {
    opacity: 0,
    y: -6,
    transition: { duration: 0.18, ease: [0.4, 0, 1, 1] },
  },
};

// Viewport options for scroll-triggered animations
export const viewportOptions = {
  once: true,
  margin: '-80px',
};
