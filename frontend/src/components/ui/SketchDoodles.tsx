import { motion } from 'framer-motion';

interface DoodleProps {
  className?: string;
  delay?: number;
}

/**
 * Collection of hand-drawn style decorative elements
 * These add personality and visual interest without being distracting
 *
 * Significance: Small touches that make the portfolio feel crafted
 * rather than template-generated
 */

// Sketchy star
export function SketchStar({ className = '', delay = 0 }: DoodleProps) {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      className={className}
      fill="none"
    >
      <motion.path
        d="M12 2 L14 10 L22 10 L16 15 L18 22 L12 17 L6 22 L8 15 L2 10 L10 10 Z"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        initial={{ pathLength: 0, rotate: -10 }}
        animate={{ pathLength: 1, rotate: 0 }}
        transition={{
          pathLength: { duration: 0.6, ease: [0.22, 1, 0.36, 1], delay },
          rotate: { duration: 0.35, ease: [0.22, 1, 0.36, 1], delay: delay + 0.2 },
        }}
      />
    </svg>
  );
}

