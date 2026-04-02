interface TagProps {
  children: string;
  variant?: 'default' | 'outline';
  size?: 'sm' | 'md';
  color?: 'blue' | 'pink' | 'green' | 'purple';
}

const colorMap = {
  blue: {
    default: 'bg-blue/10 text-blue-dark',
    outline: 'border border-blue/30 text-blue-dark',
  },
  pink: {
    default: 'bg-pink/10 text-pink-dark',
    outline: 'border border-pink/30 text-pink-dark',
  },
  green: {
    default: 'bg-green/10 text-green-dark',
    outline: 'border border-green/30 text-green-dark',
  },
  purple: {
    default: 'bg-purple/10 text-purple-dark',
    outline: 'border border-purple/30 text-purple-dark',
  },
};

const sizes = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-3 py-1 text-xs',
};

export default function Tag({ children, variant = 'default', size = 'md', color = 'blue' }: TagProps) {
  return (
    <span
      className={`
        inline-flex items-center rounded-full font-mono
        ${colorMap[color][variant]}
        ${sizes[size]}
      `}
    >
      {children}
    </span>
  );
}
