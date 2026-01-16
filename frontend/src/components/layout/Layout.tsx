import { ReactNode } from 'react';
import Navbar from './Navbar';
import Footer from './Footer';
import SocialSidebar from './SocialSidebar';
import EmailSidebar from './EmailSidebar';

interface LayoutProps {
  children: ReactNode;
}

/**
 * Main Layout with subtle grain overlay
 *
 * Significance: The grain-overlay class adds a very subtle paper-like
 * texture across the entire page, enhancing the casual sketch aesthetic
 * without being distracting. The overlay uses mix-blend-mode for subtle integration.
 */
export default function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen flex flex-col grain-overlay paper-texture">
      <Navbar />
      <SocialSidebar />
      <EmailSidebar />
      <main className="flex-1">{children}</main>
      <Footer />
    </div>
  );
}
