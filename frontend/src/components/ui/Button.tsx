import { ReactNode, useRef } from 'react';
import { motion, HTMLMotionProps, useMotionValue, useSpring } from 'framer-motion';

interface ButtonProps extends Omit<HTMLMotionProps<'button'>, 'children'> {
  children: ReactNode;
  variant?: 'primary' | 'secondary' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  href?: string;
  external?: boolean;
  magnetic?: boolean; // Enable magnetic hover effect
}

const variants = {
  primary: 'border-2 border-teal text-teal hover:bg-teal/10 hover:shadow-lg hover:shadow-teal/20',
  secondary: 'bg-teal text-charcoal hover:bg-teal-dark hover:shadow-lg hover:shadow-teal/30',
  ghost: 'text-grey-muted hover:text-teal border border-transparent hover:border-teal/30',
};

const sizes = {
  sm: 'px-4 py-2 text-xs',
  md: 'px-6 py-3 text-sm',
  lg: 'px-8 py-4 text-base',
};

/**
 * Enhanced Button with magnetic hover effect
 *
 * Significance: Magnetic effect makes buttons feel "alive" and responsive
 * - Button subtly moves toward cursor when nearby
 * - Spring physics creates natural, playful motion
 * - Adds personality without being distracting
 */
export default function Button({
  children,
  variant = 'primary',
  size = 'md',
  href,
  external,
  magnetic = true,
  className = '',
  ...props
}: ButtonProps) {
  const ref = useRef<HTMLButtonElement | HTMLAnchorElement>(null);

  // Magnetic effect motion values
  const x = useMotionValue(0);
  const y = useMotionValue(0);

  // Spring for smooth magnetic motion
  const springX = useSpring(x, { stiffness: 300, damping: 20 });
  const springY = useSpring(y, { stiffness: 300, damping: 20 });

  const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
    if (!magnetic || !ref.current) return;

    const rect = ref.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;

    // Calculate distance from center
    const distanceX = e.clientX - centerX;
    const distanceY = e.clientY - centerY;

    // Magnetic pull (max 12px in any direction)
    x.set(distanceX * 0.15);
    y.set(distanceY * 0.15);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  const baseStyles = `
    inline-flex items-center justify-center
    font-mono rounded transition-all duration-300
    ${variants[variant]}
    ${sizes[size]}
    ${className}
  `;

  const motionProps = {
    style: magnetic ? { x: springX, y: springY } : undefined,
    whileHover: { scale: 1.05 },
    whileTap: { scale: 0.95 },
    transition: { type: 'spring', stiffness: 400, damping: 17 },
    onMouseMove: magnetic ? handleMouseMove : undefined,
    onMouseLeave: magnetic ? handleMouseLeave : undefined,
  };

  if (href) {
    return (
      <motion.a
        ref={ref as any}
        href={href}
        target={external ? '_blank' : undefined}
        rel={external ? 'noopener noreferrer' : undefined}
        className={baseStyles}
        {...motionProps}
      >
        {children}
      </motion.a>
    );
  }

  return (
    <motion.button
      ref={ref as any}
      className={baseStyles}
      {...motionProps}
      {...props}
    >
      {children}
    </motion.button>
  );
}
