import { motion } from 'framer-motion';
import Button from '../ui/Button';
import { staggerContainer, heroTextReveal, letterReveal } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import { SketchStar } from '../ui/SketchDoodles';

const businessPillars = [
  {
    step: '01 / DATA & BI',
    title: 'Turn Messy Data Into Trusted Decisions',
    summary:
      'Profile, clean, and reconcile thousands of inconsistent spreadsheets, legacy exports, or financial statements into standardized schemas and Power BI dashboards.',
    proof: 'Proven across 5,687 sheets at AEM Enersol & internal BI at Accenture',
    accent: 'border-blue/30 bg-blue/5 text-blue',
  },
  {
    step: '02 / WORKFLOW SOFTWARE',
    title: 'Replace Manual Spreadsheets With Reliable Apps',
    summary:
      'Build role-based web applications, approval pipelines, and validation rules so teams stop losing records across email threads and shared Excel files.',
    proof: 'Centre-scoped workflows in CREAMS and PostgreSQL tenant isolation in BAiTI',
    accent: 'border-pink/30 bg-pink/5 text-pink',
  },
  {
    step: '03 / AUTOMATION & AI',
    title: 'Automate Constrained Operations Safely',
    summary:
      'Deliver zero-extra-license cleanroom/workstation automations, equipment-data links, and guardrailed AI pipelines backed by deterministic math and audit trails.',
    proof: 'Equipment reporting at ASE and timestamped research evidence in Sen2Nal',
    accent: 'border-green/30 bg-green/5 text-green',
  },
];

export default function HeroSection() {
  const nameText = 'Faisal Hanafi.';

  return (
    <section id="intro" className="min-h-screen flex items-center justify-center px-6 pt-28 pb-20 relative">
      {/* Decorative sketch star */}
      <div className="absolute top-32 right-12 text-pink/30 hidden lg:block" aria-hidden="true">
        <SketchStar delay={0.8} />
      </div>

      <motion.div
        className="max-w-6xl w-full relative"
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
        {/* Top Status & Location Pill */}
        <motion.div variants={heroTextReveal} className="mb-5 flex flex-wrap items-center gap-3">
          <span className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-green/10 border border-green/30 font-mono text-xs font-medium text-green">
            <span className="w-2 h-2 rounded-full bg-green animate-pulse" aria-hidden="true" />
            Available Immediately · Kuala Lumpur / Klang Valley
          </span>
          <span className="font-mono text-xs text-charcoal-light px-3 py-1.5 rounded-full bg-cream-dark border border-cream-darker">
            Data Engineering · Full-Stack Software · Process Automation
          </span>
        </motion.div>

        {/* Name with letter-by-letter reveal */}
        <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-charcoal mb-2 tracking-tight">
          {nameText.split('').map((char, index) => (
            <motion.span
              key={`${char}-${index}`}
              variants={letterReveal}
              custom={index}
              className="inline-block"
              style={{ display: 'inline-block' }}
            >
              {char === ' ' ? '\u00A0' : char}
            </motion.span>
          ))}
        </h1>

        <div className="mb-5">
          <SketchUnderline width={160} className="text-blue/40" delay={0.4} />
        </div>

        <motion.h2
          variants={heroTextReveal}
          className="text-2xl sm:text-3xl md:text-4xl lg:text-5xl font-bold text-charcoal-light mb-6 max-w-4xl leading-tight"
        >
          I turn messy data and manual workflows into{' '}
          <span className="text-charcoal">software teams actually rely on.</span>
        </motion.h2>

        <motion.p
          variants={heroTextReveal}
          className="text-charcoal-light max-w-3xl text-base md:text-lg mb-8 leading-relaxed"
        >
          Computer Science (Data Science) graduate from{' '}
          <span className="text-charcoal font-medium">IIUM</span> with hands-on delivery across{' '}
          <span className="text-charcoal font-medium">Accenture Technology Malaysia</span> (enterprise
          React/TypeScript/SPFx apps &amp; internal Power BI),{' '}
          <span className="text-charcoal font-medium">ASE Electronics Penang</span> (semiconductor test-floor
          reporting automation &amp; SECS/GEM equipment-data integration), and{' '}
          <span className="text-charcoal font-medium">AEM Enersol</span> (industrial R/Python ETL &amp;
          migration reconciliation).
        </motion.p>

        {/* Primary CTAs + Instant Recruiter Links */}
        <motion.div variants={heroTextReveal} className="flex flex-wrap items-center gap-4 mb-12">
          <Button href="#projects" size="lg">
            See What I Can Build For You
          </Button>
          <Button href="#about" variant="ghost" size="lg">
            How I Work (Story &amp; Process)
          </Button>
          <div className="flex items-center gap-2 sm:ml-2 pt-2 sm:pt-0">
            <a
              href="https://github.com/FaisalHanafi98"
              target="_blank"
              rel="noopener noreferrer"
              className="px-3.5 py-2.5 min-h-[44px] inline-flex items-center gap-2 rounded-lg bg-white border border-cream-darker text-charcoal-light hover:text-blue hover:border-blue/40 font-mono text-xs transition-[color,border-color] duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
            >
              <span>GitHub</span>
            </a>
            <a
              href="https://www.linkedin.com/in/faisalhanafidsa0105/"
              target="_blank"
              rel="noopener noreferrer"
              className="px-3.5 py-2.5 min-h-[44px] inline-flex items-center gap-2 rounded-lg bg-white border border-cream-darker text-charcoal-light hover:text-blue hover:border-blue/40 font-mono text-xs transition-[color,border-color] duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
            >
              <span>LinkedIn</span>
            </a>
            <a
              href="mailto:faisalhanafi.dev@gmail.com"
              className="px-3.5 py-2.5 min-h-[44px] inline-flex items-center gap-2 rounded-lg bg-white border border-cream-darker text-charcoal-light hover:text-blue hover:border-blue/40 font-mono text-xs transition-[color,border-color] duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
            >
              <span>Email Me</span>
            </a>
          </div>
        </motion.div>

        {/* What I Can Do For Your Business — 3 Value Pillars */}
        <motion.div variants={heroTextReveal}>
          <div className="flex items-center justify-between mb-4">
            <p className="font-mono text-xs uppercase tracking-widest text-charcoal-light font-semibold">
              How I Deliver Value to a Business Team
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            {businessPillars.map((pillar) => (
              <div
                key={pillar.step}
                className="p-6 rounded-2xl bg-white/95 border-2 border-cream-darker/80 shadow-sm hover:border-blue/40 transition-[border-color,box-shadow] duration-200 flex flex-col justify-between min-w-0"
              >
                <div>
                  <span
                    className={`inline-block font-mono text-xs font-semibold px-2.5 py-1 rounded-md border mb-3 ${pillar.accent}`}
                  >
                    {pillar.step}
                  </span>
                  <h3 className="text-lg font-bold text-charcoal mb-2 break-words">
                    {pillar.title}
                  </h3>
                  <p className="text-sm text-charcoal-light leading-relaxed mb-4 break-words">
                    {pillar.summary}
                  </p>
                </div>
                <div className="pt-3 border-t border-cream-darker/60 font-mono text-xs text-charcoal font-medium break-words">
                  &#10003; {pillar.proof}
                </div>
              </div>
            ))}
          </div>
        </motion.div>
      </motion.div>
    </section>
  );
}
