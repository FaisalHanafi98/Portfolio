import type { ReactNode } from 'react';
import type { FetchStatus, UseQueryResult } from '@tanstack/react-query';
import { isAxiosError } from 'axios';

interface QueryContentProps<T> {
  query: UseQueryResult<T>;
  resource: string;
  loading: ReactNode;
  emptyMessage?: string;
  notFound?: ReactNode;
  children: (data: T) => ReactNode;
}

interface QueryFailureProps {
  message: string;
  resource: string;
  fetchStatus: FetchStatus;
  retry: () => void;
}

function QueryFailure({ message, resource, fetchStatus, retry }: QueryFailureProps) {
  return (
    <div className="text-center py-12">
      <p role="status" className="text-charcoal-light">
        {fetchStatus === 'paused'
          ? `Waiting for a connection to refresh ${resource}.`
          : fetchStatus === 'fetching' ? `Refreshing ${resource}…` : message}
      </p>
      <button
        type="button"
        className="btn-primary mt-4 min-h-[44px] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 aria-disabled:opacity-60"
        aria-label={`Try again to load ${resource}`}
        aria-disabled={fetchStatus !== 'idle'}
        onClick={() => {
          if (fetchStatus === 'idle') retry();
        }}
      >
        Try again
      </button>
    </div>
  );
}

export default function QueryContent<T>({
  query, resource, loading, emptyMessage, notFound, children,
}: QueryContentProps<T>) {
  const { data, error, fetchStatus, status } = query;

  if (notFound && isAxiosError(error) && error.response?.status === 404) {
    return notFound;
  }

  const retry = () => {
    void query.refetch({ cancelRefetch: false });
  };

  if (data != null) {
    return (
      <>
        {query.isRefetchError && (
          <QueryFailure
            message={`Couldn't refresh ${resource}. Showing the last loaded result.`}
            resource={resource}
            fetchStatus={fetchStatus}
            retry={retry}
          />
        )}
        {emptyMessage && Array.isArray(data) && data.length === 0
          ? <p role="status" className="text-charcoal-light text-center">{emptyMessage}</p>
          : children(data)}
      </>
    );
  }

  if (fetchStatus === 'paused') {
    return (
      <p role="status" className="text-charcoal-light text-center py-12">
        Waiting for a connection to load {resource}.
      </p>
    );
  }

  if (status === 'pending') {
    return (
      <>
        <p role="status" className="sr-only">Loading {resource}…</p>
        <div aria-hidden="true">{loading}</div>
      </>
    );
  }

  return (
    <QueryFailure
      message={`Unable to load ${resource}.`}
      resource={resource}
      fetchStatus={fetchStatus}
      retry={retry}
    />
  );
}
