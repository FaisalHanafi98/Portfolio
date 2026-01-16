import { motion } from 'framer-motion';

interface SketchUnderlineProps {
  width?: number;
  className?: string;
  delay?: number;
}

/**
 * Hand-drawn style underline animation
 * Creates a sketchy, imperfect line that draws itself on
 *
 * Significance: Adds the "casual sketch" aesthetic to headings
 * without being overly perfect or rigid
 */
export default function SketchUnderline({
  width = 100,
  className = '',
  delay = 0
}: SketchUnderlineProps) {
  // SVG path for a hand-drawn looking underline
  // Uses bezier curves with slight irregularity for organic feel
  const pathData = `
    M 0,5
    Q ${width * 0.15},4 ${width * 0.3},5
    T ${width * 0.6},5
    Q ${width * 0.8},4.5 ${width},5
  `;

  return (
    <svg
      width={width}
      height="8"
      viewBox={`0 0 ${width} 8`}
      className={`overflow-visible ${className}`}
      style={{ display: 'block' }}
    >
      <motion.path
        d={pathData}
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        fill="none"
        initial={{ pathLength: 0, opacity: 0 }}
        animate={{ pathLength: 1, opacity: 1 }}
        transition={{
          pathLength: { duration: 0.8, ease: 'easeInOut', delay },
          opacity: { duration: 0.2, delay },
        }}
        style={{
          // Slight irregularity in the stroke
          filter: 'url(#sketch-filter)',
        }}
      />
      {/* SVG filter for sketchy effect */}
      <defs>
        <filter id="sketch-filter">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.8"
            numOctaves="2"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="0.5"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
      </defs>
    </svg>
  );
}
