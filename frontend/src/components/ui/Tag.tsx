interface TagProps {
  children: string;
  variant?: 'default' | 'outline';
  size?: 'sm' | 'md';
}

const variants = {
  default: 'bg-navy-lighter text-teal',
  outline: 'border border-teal/50 text-teal',
};

const sizes = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-3 py-1 text-xs',
};

export default function Tag({ children, variant = 'default', size = 'md' }: TagProps) {
  return (
    <span
      className={`
        inline-flex items-center rounded-full font-mono
        ${variants[variant]}
        ${sizes[size]}
      `}
    >
      {children}
    </span>
  );
}
