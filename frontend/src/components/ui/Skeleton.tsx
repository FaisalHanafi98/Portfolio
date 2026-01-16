interface SkeletonProps {
  className?: string;
  variant?: 'text' | 'circular' | 'rectangular';
  width?: string | number;
  height?: string | number;
}

export default function Skeleton({
  className = '',
  variant = 'text',
  width,
  height,
}: SkeletonProps) {
  const baseStyles = 'animate-pulse bg-navy-lighter';

  const variantStyles = {
    text: 'rounded h-4',
    circular: 'rounded-full',
    rectangular: 'rounded-lg',
  };

  return (
    <div
      className={`${baseStyles} ${variantStyles[variant]} ${className}`}
      style={{
        width: width,
        height: height,
      }}
    />
  );
}

export function ProjectCardSkeleton() {
  return (
    <div className="bg-navy-light rounded-lg p-6 space-y-4">
      <Skeleton variant="rectangular" height={200} className="w-full" />
      <Skeleton width="70%" />
      <Skeleton width="100%" />
      <Skeleton width="40%" />
      <div className="flex gap-2 pt-2">
        <Skeleton width={60} height={24} variant="rectangular" />
        <Skeleton width={60} height={24} variant="rectangular" />
        <Skeleton width={60} height={24} variant="rectangular" />
      </div>
    </div>
  );
}

export function SkillsSkeleton() {
  return (
    <div className="space-y-8">
      {[1, 2, 3].map((i) => (
        <div key={i} className="space-y-4">
          <Skeleton width={120} height={24} />
          <div className="flex flex-wrap gap-3">
            {[1, 2, 3, 4, 5].map((j) => (
              <Skeleton key={j} width={80} height={32} variant="rectangular" />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

export function ExperienceSkeleton() {
  return (
    <div className="space-y-8">
      {[1, 2].map((i) => (
        <div key={i} className="flex gap-4">
          <Skeleton variant="circular" width={12} height={12} />
          <div className="flex-1 space-y-3">
            <Skeleton width="60%" height={24} />
            <Skeleton width="40%" />
            <Skeleton width="100%" />
            <Skeleton width="80%" />
          </div>
        </div>
      ))}
    </div>
  );
}
