import { motion } from 'framer-motion';
import { useInView } from 'framer-motion';
import { useRef } from 'react';

interface SketchDividerProps {
  className?: string;
}

/**
 * Hand-drawn style section divider
 * Animates drawing on scroll into view
 *
 * Significance: Creates clear visual separation between sections
 * while maintaining the casual sketch aesthetic
 */
export default function SketchDivider({ className = '' }: SketchDividerProps) {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  return (
    <div ref={ref} className={`w-full flex justify-center my-16 ${className}`}>
      <svg
        width="300"
        height="20"
        viewBox="0 0 300 20"
        className="text-grey-subtle"
        fill="none"
      >
        {/* Main divider line with hand-drawn imperfection */}
        <motion.path
          d="M 10,10 Q 75,8 150,10 T 290,10"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
          initial={{ pathLength: 0, opacity: 0 }}
          animate={
            isInView
              ? { pathLength: 1, opacity: 1 }
              : { pathLength: 0, opacity: 0 }
          }
          transition={{
            pathLength: { duration: 1.5, ease: 'easeInOut' },
            opacity: { duration: 0.3 },
          }}
          style={{
            filter: 'url(#divider-sketch)',
          }}
        />

        {/* Decorative dots on either side */}
        <motion.circle
          cx="5"
          cy="10"
          r="2"
          fill="currentColor"
          initial={{ scale: 0, opacity: 0 }}
          animate={
            isInView
              ? { scale: 1, opacity: 0.6 }
              : { scale: 0, opacity: 0 }
          }
          transition={{ delay: 1.2, duration: 0.3 }}
        />
        <motion.circle
          cx="295"
          cy="10"
          r="2"
          fill="currentColor"
          initial={{ scale: 0, opacity: 0 }}
          animate={
            isInView
              ? { scale: 1, opacity: 0.6 }
              : { scale: 0, opacity: 0 }
          }
          transition={{ delay: 1.2, duration: 0.3 }}
        />

        {/* SVG filter for sketchy effect */}
        <defs>
          <filter id="divider-sketch">
            <feTurbulence
              type="fractalNoise"
              baseFrequency="0.6"
              numOctaves="2"
              result="noise"
            />
            <feDisplacementMap
              in="SourceGraphic"
              in2="noise"
              scale="1"
              xChannelSelector="R"
              yChannelSelector="G"
            />
          </filter>
        </defs>
      </svg>
    </div>
  );
}
