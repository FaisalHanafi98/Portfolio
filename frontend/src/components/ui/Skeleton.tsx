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
  const baseStyles = 'animate-pulse bg-cream-darker';

  const variantStyles = {
    text: 'rounded h-4',
    circular: 'rounded-full',
    rectangular: 'rounded-lg',
  };

  return (
    <div
      className={`${baseStyles} ${variantStyles[variant]} ${className}`}
      style={{ width, height }}
    />
  );
}

export function ProjectCardSkeleton() {
  return (
    <div className="bg-white rounded-xl p-6 space-y-4 border border-cream-darker/60">
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

export function StatusSkeleton() {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl p-6 border-2 border-cream-darker space-y-3">
        <Skeleton width={160} height={28} variant="rectangular" />
        <Skeleton width="65%" />
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {[1, 2, 3].map((i) => (
          <div key={i} className="bg-white rounded-2xl p-6 border-2 border-cream-darker space-y-3">
            <Skeleton width={110} height={20} />
            <Skeleton width="55%" height={32} />
            <Skeleton width="85%" />
          </div>
        ))}
      </div>
      <div className="bg-white rounded-2xl p-6 border-2 border-cream-darker space-y-3">
        <Skeleton width="45%" height={24} />
        <Skeleton width="75%" />
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 pt-2">
          <Skeleton width="100%" height={72} variant="rectangular" />
          <Skeleton width="100%" height={72} variant="rectangular" />
        </div>
      </div>
      <div className="bg-white rounded-2xl p-6 border-2 border-cream-darker space-y-3">
        <Skeleton width="40%" height={24} />
        <Skeleton width="100%" height={36} variant="rectangular" />
        <Skeleton width="100%" height={36} variant="rectangular" />
      </div>
    </div>
  );
}

export function AnalyticsSkeleton() {
  return (
    <div className="space-y-8">
      <div className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm space-y-6">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="space-y-2">
            <Skeleton className="h-5 w-44 rounded-full" />
            <Skeleton className="h-7 w-72" />
            <Skeleton className="h-4 w-56" />
          </div>
          <Skeleton className="h-11 w-40 rounded-xl" />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Skeleton className="h-24 w-full rounded-xl" />
          <Skeleton className="h-24 w-full rounded-xl" />
          <Skeleton className="h-24 w-full rounded-xl" />
          <Skeleton className="h-24 w-full rounded-xl" />
        </div>
      </div>

      <div className="bg-white rounded-2xl border-2 border-cream-darker p-6 shadow-sm space-y-4">
        <Skeleton className="h-6 w-64" />
        <Skeleton className="h-48 w-full rounded-xl" />
      </div>
    </div>
  );
}
