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

// Sketchy arrow pointing right
export function SketchArrow({ className = '', delay = 0 }: DoodleProps) {
  return (
    <svg
      width="40"
      height="20"
      viewBox="0 0 40 20"
      className={className}
      fill="none"
    >
      <motion.path
        d="M 2,10 Q 15,8 25,10 T 35,10"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        initial={{ pathLength: 0 }}
        animate={{ pathLength: 1 }}
        transition={{ duration: 0.8, ease: 'easeInOut', delay }}
      />
      <motion.path
        d="M 32,6 L 38,10 L 32,14"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.3, delay: delay + 0.6 }}
      />
    </svg>
  );
}

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
          pathLength: { duration: 1, ease: 'easeInOut', delay },
          rotate: { duration: 0.5, delay: delay + 0.5 },
        }}
      />
    </svg>
  );
}

// Sketchy circle/donut
export function SketchCircle({ className = '', delay = 0 }: DoodleProps) {
  return (
    <svg
      width="30"
      height="30"
      viewBox="0 0 30 30"
      className={className}
      fill="none"
    >
      <motion.circle
        cx="15"
        cy="15"
        r="12"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        initial={{ pathLength: 0, rotate: -90 }}
        animate={{ pathLength: 1, rotate: 0 }}
        transition={{
          pathLength: { duration: 1, ease: 'easeInOut', delay },
          rotate: { duration: 1, delay },
        }}
        style={{
          filter: 'url(#circle-sketch)',
        }}
      />
      <defs>
        <filter id="circle-sketch">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="1"
            numOctaves="1"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="0.8"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
      </defs>
    </svg>
  );
}

// Sketchy squiggle/underline accent
export function SketchSquiggle({ className = '', delay = 0 }: DoodleProps) {
  return (
    <svg
      width="60"
      height="15"
      viewBox="0 0 60 15"
      className={className}
      fill="none"
    >
      <motion.path
        d="M 2,8 Q 10,3 15,8 T 30,8 Q 40,3 45,8 T 58,8"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        initial={{ pathLength: 0 }}
        animate={{ pathLength: 1 }}
        transition={{ duration: 1.2, ease: 'easeInOut', delay }}
      />
    </svg>
  );
}

// Sketchy highlight effect (oval behind text)
export function SketchHighlight({ className = '', delay = 0 }: DoodleProps) {
  return (
    <svg
      width="120"
      height="40"
      viewBox="0 0 120 40"
      className={`absolute -z-10 ${className}`}
      fill="none"
      style={{ top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }}
    >
      <motion.ellipse
        cx="60"
        cy="20"
        rx="55"
        ry="15"
        stroke="currentColor"
        strokeWidth="2"
        fill="currentColor"
        fillOpacity="0.1"
        initial={{ pathLength: 0, opacity: 0 }}
        animate={{ pathLength: 1, opacity: 1 }}
        transition={{
          pathLength: { duration: 0.8, ease: 'easeInOut', delay },
          opacity: { duration: 0.3, delay },
        }}
        style={{
          filter: 'url(#highlight-sketch)',
        }}
      />
      <defs>
        <filter id="highlight-sketch">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="1.2"
            numOctaves="1"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="1.5"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
      </defs>
    </svg>
  );
}
