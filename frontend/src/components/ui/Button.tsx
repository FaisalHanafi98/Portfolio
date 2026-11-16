import React, { ReactNode, useRef } from 'react';
import { motion, HTMLMotionProps, useMotionValue, useReducedMotion, useSpring } from 'framer-motion';

interface ButtonProps extends Omit<HTMLMotionProps<'button'>, 'children'> {
  children: ReactNode;
  variant?: 'primary' | 'secondary' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  href?: string;
  external?: boolean;
  magnetic?: boolean;
}

const variants = {
  primary: 'border-2 border-charcoal text-charcoal hover:bg-charcoal hover:text-cream',
  secondary: 'bg-charcoal text-cream hover:bg-charcoal-light',
  ghost: 'text-charcoal-light hover:text-charcoal border border-charcoal/20 hover:border-charcoal/40 hover:bg-cream-dark/40',
};

const sizes = {
  sm: 'px-4 py-2 text-xs min-h-[36px]',
  md: 'px-6 py-3 text-sm min-h-[44px]',
  lg: 'px-8 py-4 text-base min-h-[48px]',
};

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
  const prefersReducedMotion = useReducedMotion();
  const enableMagnetic = magnetic && !prefersReducedMotion;

  const x = useMotionValue(0);
  const y = useMotionValue(0);
  const springX = useSpring(x, { stiffness: 320, damping: 24 });
  const springY = useSpring(y, { stiffness: 320, damping: 24 });

  const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
    if (!enableMagnetic || !ref.current) return;
    const rect = ref.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    x.set((e.clientX - centerX) * 0.12);
    y.set((e.clientY - centerY) * 0.12);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  const baseStyles = `
    inline-flex items-center justify-center
    font-mono rounded-lg transition-[background-color,color,border-color] duration-200 ease-out
    focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue
    ${variants[variant]}
    ${sizes[size]}
    ${className}
  `;

  const motionProps = {
    style: enableMagnetic ? { x: springX, y: springY } : undefined,
    whileHover: prefersReducedMotion ? undefined : { y: -2 },
    whileTap: prefersReducedMotion ? undefined : { scale: 0.98 },
    transition: { type: 'spring' as const, stiffness: 420, damping: 24 },
    onMouseMove: enableMagnetic ? handleMouseMove : undefined,
    onMouseLeave: enableMagnetic ? handleMouseLeave : undefined,
  };

  if (href) {
    return (
      <motion.a
        ref={ref as React.Ref<never>}
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
      ref={ref as React.Ref<never>}
      className={baseStyles}
      {...motionProps}
      {...props}
    >
      {children}
    </motion.button>
  );
}
