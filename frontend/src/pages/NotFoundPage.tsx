import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="max-w-4xl mx-auto px-6 pt-32 pb-16 text-center">
      <h1 className="text-2xl font-bold text-charcoal mb-4">Page Not Found</h1>
      <p className="text-charcoal-light mb-8">
        The address you opened doesn&apos;t match a page on this site.
      </p>
      <Link to="/" className="btn-primary">
        Go Home
      </Link>
    </div>
  );
}
