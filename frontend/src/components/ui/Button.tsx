import React, { ReactNode, useRef } from 'react';
import { motion, HTMLMotionProps, useMotionValue, useSpring } from 'framer-motion';

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
  ghost: 'text-charcoal-light hover:text-charcoal border border-transparent hover:border-charcoal/20',
};

const sizes = {
  sm: 'px-4 py-2 text-xs',
  md: 'px-6 py-3 text-sm',
  lg: 'px-8 py-4 text-base',
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

  const x = useMotionValue(0);
  const y = useMotionValue(0);
  const springX = useSpring(x, { stiffness: 300, damping: 20 });
  const springY = useSpring(y, { stiffness: 300, damping: 20 });

  const handleMouseMove = (e: React.MouseEvent<HTMLElement>) => {
    if (!magnetic || !ref.current) return;
    const rect = ref.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    x.set((e.clientX - centerX) * 0.15);
    y.set((e.clientY - centerY) * 0.15);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  const baseStyles = `
    inline-flex items-center justify-center
    font-mono rounded-lg transition-all duration-300
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
