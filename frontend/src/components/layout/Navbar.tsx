import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useScrolledPast } from '../../hooks/useScrollPosition';

const navLinks = [
  { name: 'About', href: '/#about' },
  { name: 'Projects', href: '/#projects' },
  { name: 'Skills', href: '/#skills' },
  { name: 'Experience', href: '/#experience' },
  { name: 'Contact', href: '/#contact' },
];

const resumeSubject = encodeURIComponent('Resume Request — Faisal Hanafi');
const resumeHref = import.meta.env.VITE_RESUME_URL || `mailto:faisalhanafi98@gmail.com?subject=${resumeSubject}`;
const hasBundledResume = Boolean(import.meta.env.VITE_RESUME_URL);

const desktopBreakpoint = '(min-width: 768px)'; // Tailwind's md breakpoint

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);
  const menuButton = useRef<HTMLButtonElement>(null);
  const scrolled = useScrolledPast(50);

  useEffect(() => {
    if (!isOpen) return;

    const desktop = window.matchMedia(desktopBreakpoint);
    if (desktop.matches) {
      setIsOpen(false);
      return;
    }

    const body = document.body;
    const closeOnDesktop = (event: MediaQueryListEvent) => {
      if (event.matches) setIsOpen(false);
    };
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key !== 'Escape') return;
      menuButton.current?.focus();
      setIsOpen(false);
    };

    body.classList.add('max-md:overflow-hidden');
    desktop.addEventListener('change', closeOnDesktop);
    document.addEventListener('keydown', closeOnEscape);
    return () => {
      body.classList.remove('max-md:overflow-hidden');
      desktop.removeEventListener('change', closeOnDesktop);
      document.removeEventListener('keydown', closeOnEscape);
    };
  }, [isOpen]);

  const handleNavClick = () => {
    setIsOpen(false);
  };

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-[background-color,border-color,box-shadow] duration-200 ease-out ${
        scrolled
          ? 'bg-cream/90 backdrop-blur-md shadow-sm border-b border-cream-darker/50'
          : 'bg-transparent'
      }`}
    >
      <nav aria-label="Primary navigation" className="max-w-6xl mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <Link
            to="/"
            onClick={handleNavClick}
            className="font-hand text-2xl text-charcoal hover:text-blue transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue rounded"
          >
            faisal.
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center gap-8">
            <ul className="flex items-center gap-6">
              {navLinks.map((link) => (
                <li key={link.name}>
                  <a
                    href={link.href}
                    className="text-charcoal-light hover:text-charcoal transition-colors text-sm font-medium link-underline py-1 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue rounded"
                  >
                    {link.name}
                  </a>
                </li>
              ))}
            </ul>

            <a
              href={resumeHref}
              target={hasBundledResume ? '_blank' : undefined}
              rel={hasBundledResume ? 'noopener noreferrer' : undefined}
              aria-label={hasBundledResume ? 'Open resume PDF' : 'Request resume PDF via email'}
              title={hasBundledResume ? 'Open resume PDF' : 'Resume PDF available upon request via email'}
              className="btn-primary"
            >
              {hasBundledResume ? 'Resume' : 'Request Resume'}
            </a>
          </div>

          {/* Mobile Menu Button - 44px Touch Target */}
          <button
            ref={menuButton}
            type="button"
            onClick={() => setIsOpen((open) => !open)}
            className="md:hidden p-2.5 min-w-[44px] min-h-[44px] flex items-center justify-center text-charcoal rounded-lg hover:bg-cream-dark/50 active:scale-95 transition-[background-color,transform] duration-150 ease-out focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
            aria-label="Toggle menu"
            aria-controls="mobile-navigation"
            aria-expanded={isOpen}
          >
            <svg
              className="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              aria-hidden="true"
            >
              {isOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        {/* Mobile Menu */}
        <div
          id="mobile-navigation"
          className="md:hidden"
          ref={(panel) => {
            if (panel) panel.inert = !isOpen;
          }}
        >
          <AnimatePresence>
            {isOpen && (
              <motion.div
                initial={{ opacity: 0, y: -8, scaleY: 0.96 }}
                animate={{ opacity: 1, y: 0, scaleY: 1 }}
                exit={{ opacity: 0, y: -6, scaleY: 0.96 }}
                transition={{ duration: 0.2, ease: [0.22, 1, 0.36, 1] }}
                style={{ transformOrigin: 'top' }}
                className="md:hidden overflow-hidden border-t border-cream-darker/60 mt-3 bg-cream/95 rounded-b-xl"
              >
                <ul className="flex flex-col items-center gap-2 py-5 px-4">
                  {navLinks.map((link) => (
                    <li key={link.name} className="w-full text-center">
                      <a
                        href={link.href}
                        onClick={handleNavClick}
                        className="text-charcoal-light hover:text-charcoal hover:bg-cream-dark/50 active:scale-[0.99] transition-[color,background-color,transform] duration-150 ease-out text-lg font-medium inline-flex items-center justify-center w-full min-h-[44px] py-2 rounded-lg focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
                      >
                        {link.name}
                      </a>
                    </li>
                  ))}
                  <li className="pt-2 w-full text-center">
                    <a
                      href={resumeHref}
                      target={hasBundledResume ? '_blank' : undefined}
                      rel={hasBundledResume ? 'noopener noreferrer' : undefined}
                      aria-label={hasBundledResume ? 'Open resume PDF' : 'Request resume PDF via email'}
                      title={hasBundledResume ? 'Open resume PDF' : 'Resume PDF available upon request via email'}
                      className="btn-primary inline-flex items-center justify-center min-h-[44px] px-8"
                      onClick={handleNavClick}
                    >
                      {hasBundledResume ? 'Resume' : 'Request Resume'}
                    </a>
                  </li>
                </ul>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </nav>
    </header>
  );
}
