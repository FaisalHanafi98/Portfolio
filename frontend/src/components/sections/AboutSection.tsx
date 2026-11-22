import { useRef, useState } from 'react';
import type { KeyboardEvent } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import AnimatedSection from '../ui/AnimatedSection';
import { slideUp } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';

interface StorySlide {
  id: string;
  chapter: string;
  shortTab: string;
  title: string;
  subtitle: string;
  story: string;
  processStep: string;
  processDetail: string;
  businessValue: string;
  tags: string[];
  accentColor: string;
  badgeClass: string;
}

const storySlides: StorySlide[] = [
  {
    id: 'core-habit',
    chapter: 'Chapter 01 / 05',
    shortTab: '01. The Core Habit',
    title: 'Spot the Friction, Build the Structure',
    subtitle: 'How I approach every team and business problem',
    story:
      'Every organization has a workflow people quietly dread — copying 35 fields between legacy windows, chasing candidate approvals across email threads, or fixing the same broken spreadsheet every Friday. I notice where a process is painful to use, and I build the system that makes it effortless.',
    processStep: 'Step 1: Observe the Real Workflow First',
    processDetail:
      'Before touching code or schemas, I trace where human time is wasted, where handoffs drop records, and what the end user actually needs on their screen.',
    businessValue:
      'You get an engineer who cares about whether the tool actually saves your team time — not someone who just closes tickets in isolation.',
    tags: ['Workflow Discovery', 'Process Redesign', 'User-First Engineering'],
    accentColor: 'text-blue',
    badgeClass: 'bg-blue/10 text-blue border-blue/30',
  },
  {
    id: 'dirty-data',
    chapter: 'Chapter 02 / 05',
    shortTab: '02. Data & ETL',
    title: 'Make Messy Data Trustworthy First',
    subtitle: 'AEM Enersol & Enterprise BI Foundation',
    story:
      'Dashboards and AI are useless if nobody trusts the numbers underneath. At AEM Enersol, we faced 5,687 historical petroleum Excel sheets across 43 inconsistent templates — where even a strikethrough font meant a technical value had been superseded.',
    processStep: 'Step 2: Profile, Standardize & Reconcile',
    processDetail:
      'I use R, Python, SQL, Regex, and Power Query to profile raw sources, catch formatting semantics (like strikethroughs or same-day refunds), and reconcile before-and-after migration counts.',
    businessValue:
      'Decision-makers and analysts get clean, standardized datasets and Power BI views they can act on without second-guessing the underlying math.',
    tags: ['R & Python ETL', 'SQL & Schema Mapping', 'Power BI (M & DAX)', 'Data QA/QC'],
    accentColor: 'text-purple',
    badgeClass: 'bg-purple/10 text-purple border-purple/30',
  },
  {
    id: 'enterprise-scale',
    chapter: 'Chapter 03 / 05',
    shortTab: '03. Enterprise Apps',
    title: 'Build Software That Survives Real Scale',
    subtitle: 'Accenture Technology Malaysia Delivery',
    story:
      'At Accenture, I was tasked with building a SharePoint & React recruitment platform despite having zero prior React experience. When our early prototype grew into a sluggish 2,520-line file, I worked with the team to refactor it into 50+ modular components and 10 service layers.',
    processStep: 'Step 3: Separate State, Services & Validation',
    processDetail:
      'By splitting state into focused contexts, enforcing strict TypeScript types, and engineering server-side pagination around SharePoint’s 5,000-item limit, we scaled smoothly to 20,000+ candidate records.',
    businessValue:
      'Your business gets software that stays fast as data grows — and a clean, modular codebase that any developer on your team can maintain.',
    tags: ['React & TypeScript', 'SPFx & Fluent UI', 'RBAC & Validation', 'Performance Tuning'],
    accentColor: 'text-pink',
    badgeClass: 'bg-pink/10 text-pink border-pink/30',
  },
  {
    id: 'constrained-automation',
    chapter: 'Chapter 04 / 05',
    shortTab: '04. Floor Automation',
    title: 'Automate Under Real-World Constraints',
    subtitle: 'ASE Electronics Semiconductor Test Operations',
    story:
      'On ASE’s cleanroom test floor in Penang, strict security meant we couldn’t install external libraries or query databases directly — yet staff spent 45 minutes manually compiling remnant buffer reports and tracking 400+ electrical testers.',
    processStep: 'Step 4: Deliver With Zero Extra Dependencies',
    processDetail:
      'Working strictly with installed Excel, 64-bit VBA7, native Win32 APIs, and SECS/GEM equipment signals, I automated legacy query clients, reconciled 400+ testers, and supported equipment state/downtime tracking.',
    businessValue:
      'Even in locked-down corporate or factory environments, I find safe, zero-license-cost ways to eliminate repetitive manual work.',
    tags: ['Excel VBA7 & Win32', 'SECS/GEM Context', 'Inventory Reconciliation', 'Hardware & PC Builds'],
    accentColor: 'text-green',
    badgeClass: 'bg-green/10 text-green border-green/30',
  },
  {
    id: 'guardrailed-ai',
    chapter: 'Chapter 05 / 05',
    shortTab: '05. Guardrailed AI',
    title: 'Why Accuracy & Protection Are Personal to Me',
    subtitle: 'RinggitSense, BAiTI, CREAMS & Sen2Nal',
    story:
      'Losing money to a scam earlier in life made financial clarity and data protection deeply personal to me. I saw firsthand what happens when systems are opaque, permissions are sloppy, or tools present guesswork as fact.',
    processStep: 'Step 5: Deterministic Rules First, Bounded AI Second',
    processDetail:
      'In my full-stack and AI systems, I enforce database-level tenant isolation (PostgreSQL RLS / CentreScope), exact decimal arithmetic, and explicit [NEEDS INPUT] boundaries whenever AI encounters ambiguous data.',
    businessValue:
      'You get modern AI and full-stack capabilities engineered with accountability, privacy, and audit trails built in from day one.',
    tags: ['Spring Boot & FastAPI', 'Laravel & MySQL', 'PostgreSQL FORCE RLS', 'Multi-Agent Claude & FinBERT'],
    accentColor: 'text-blue',
    badgeClass: 'bg-blue/10 text-blue border-blue/30',
  },
];

const processSteps = [
  {
    step: '01',
    name: 'Diagnose the Bottleneck',
    desc: 'Sit with the real workflow to find where manual effort, spreadsheets, or handoffs break down.',
  },
  {
    step: '02',
    name: 'Clean & Model the Data',
    desc: 'Profile raw inputs, handle edge-case formatting, and lock in a schema that preserves business truth.',
  },
  {
    step: '03',
    name: 'Build Within Constraints',
    desc: 'Whether it’s Spring Boot, FastAPI, SPFx, or locked-down cleanroom VBA, build for the reality on the ground.',
  },
  {
    step: '04',
    name: 'Verify & Hand Over',
    desc: 'Prove parity against real records, write automated checks, and document clearly so teams can own it.',
  },
];

export default function AboutSection() {
  const [activeIndex, setActiveIndex] = useState(0);
  const chapterTabs = useRef<(HTMLButtonElement | null)[]>([]);
  const currentSlide = storySlides[activeIndex];

  const handleChapterKeyDown = (event: KeyboardEvent<HTMLButtonElement>, index: number) => {
    const chapterForKey: Record<string, number> = {
      ArrowLeft: (index - 1 + storySlides.length) % storySlides.length,
      ArrowRight: (index + 1) % storySlides.length,
      Home: 0,
      End: storySlides.length - 1,
    };
    const nextIndex = chapterForKey[event.key];
    if (nextIndex === undefined) return;
    event.preventDefault();
    setActiveIndex(nextIndex);
    chapterTabs.current[nextIndex]?.focus();
  };

  const handlePrev = () => {
    setActiveIndex((prev) => (prev === 0 ? storySlides.length - 1 : prev - 1));
  };

  const handleNext = () => {
    setActiveIndex((prev) => (prev === storySlides.length - 1 ? 0 : prev + 1));
  };

  return (
    <AnimatedSection
      id="about"
      className="py-24 px-6 bg-white/90 border-y-2 border-cream-darker/80 relative"
    >
      <div className="max-w-5xl mx-auto">
        {/* Section Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-10 gap-4">
          <div>
            <div className="inline-flex items-center gap-2 font-mono text-xs uppercase tracking-widest text-blue bg-blue/10 px-3 py-1 rounded-full mb-3">
              <span>01 / Story &amp; Engineering Process</span>
            </div>
            <div>
              <motion.h2
                variants={slideUp}
                className="text-3xl md:text-4xl font-bold text-charcoal mb-3 inline-block"
              >
                How I Think, Work &amp; Build
              </motion.h2>
            </div>
            <SketchUnderline width={160} className="text-blue/50" delay={0.2} />
          </div>
          <p className="text-sm text-charcoal-light max-w-md font-mono">
            Swipe or click through the 5 chapters below to see how my work evolved from data extraction to enterprise software and automation.
          </p>
        </div>

        {/* Interactive Chapter Tabs */}
        <div
          role="tablist"
          aria-label="Professional story chapters"
          className="flex items-center gap-2 overflow-x-auto pb-3 mb-6"
        >
          {storySlides.map((slide, idx) => {
            const isSelected = idx === activeIndex;
            return (
              <button
                key={slide.id}
                type="button"
                role="tab"
                ref={(tab) => { chapterTabs.current[idx] = tab; }}
                tabIndex={isSelected ? 0 : -1}
                aria-selected={isSelected}
                aria-controls={`story-panel-${slide.id}`}
                id={`story-tab-${slide.id}`}
                onClick={() => setActiveIndex(idx)}
                onKeyDown={(event) => handleChapterKeyDown(event, idx)}
                className={`px-4 py-2.5 min-h-[44px] rounded-xl font-mono text-xs whitespace-nowrap border transition-[background-color,color,border-color,transform] duration-150 ease-out active:scale-[0.98] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue ${
                  isSelected
                    ? 'bg-charcoal text-cream border-charcoal font-semibold shadow-sm'
                    : 'bg-cream/70 text-charcoal-light border-cream-darker hover:bg-cream-dark hover:text-charcoal'
                }`}
              >
                {slide.shortTab}
              </button>
            );
          })}
        </div>

        {/* Main Story Carousel Card */}
        <div
          className="bg-cream/60 rounded-2xl border-2 border-cream-darker p-6 md:p-10 shadow-sm relative overflow-hidden"
          onKeyDown={(e) => {
            if (e.key === 'ArrowLeft') handlePrev();
            if (e.key === 'ArrowRight') handleNext();
          }}
        >
          {/* Progress Bar */}
          <div className="w-full h-1.5 bg-cream-darker rounded-full overflow-hidden mb-8" aria-hidden="true">
            <div
              className="h-full bg-blue transition-[width] duration-200 ease-out rounded-full"
              style={{ width: `${((activeIndex + 1) / storySlides.length) * 100}%` }}
            />
          </div>

          <AnimatePresence mode="wait">
            <motion.div
              key={currentSlide.id}
              id={`story-panel-${currentSlide.id}`}
              role="tabpanel"
              tabIndex={0}
              aria-labelledby={`story-tab-${currentSlide.id}`}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.22, ease: [0.22, 1, 0.36, 1] }}
              className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start"
            >
              {/* Left Column: Narrative Story */}
              <div className="lg:col-span-7 min-w-0">
                <div className="flex flex-wrap items-center gap-3 mb-3">
                  <span
                    className={`font-mono text-xs font-semibold px-3 py-1 rounded-full border ${currentSlide.badgeClass}`}
                  >
                    {currentSlide.chapter}
                  </span>
                  <span className="font-mono text-xs text-charcoal-light">
                    {currentSlide.subtitle}
                  </span>
                </div>

                <h3 className="text-2xl md:text-3xl font-bold text-charcoal mb-4 break-words">
                  {currentSlide.title}
                </h3>

                <p className="text-charcoal text-base md:text-lg leading-relaxed mb-6 break-words">
                  {currentSlide.story}
                </p>

                <div className="flex flex-wrap gap-2">
                  {currentSlide.tags.map((tag) => (
                    <span
                      key={tag}
                      className="font-mono text-xs px-3 py-1 rounded-full bg-white border border-cream-darker text-charcoal-light"
                    >
                      {tag}
                    </span>
                  ))}
                </div>
              </div>

              {/* Right Column: Process & Business Value Breakdown */}
              <div className="lg:col-span-5 space-y-4 min-w-0">
                <div className="bg-white p-5 rounded-xl border border-cream-darker shadow-sm">
                  <p className={`font-mono text-xs uppercase tracking-wider font-semibold mb-1.5 ${currentSlide.accentColor}`}>
                    {currentSlide.processStep}
                  </p>
                  <p className="text-sm text-charcoal-light leading-relaxed break-words">
                    {currentSlide.processDetail}
                  </p>
                </div>

                <div className="bg-white p-5 rounded-xl border-2 border-charcoal/15 shadow-sm">
                  <p className="font-mono text-xs uppercase tracking-wider font-semibold text-charcoal mb-1.5">
                    Why This Matters to an Employer
                  </p>
                  <p className="text-sm text-charcoal font-medium leading-relaxed break-words">
                    {currentSlide.businessValue}
                  </p>
                </div>
              </div>
            </motion.div>
          </AnimatePresence>

          {/* Carousel Controls Footer */}
          <div className="flex flex-wrap items-center justify-between gap-4 mt-8 pt-6 border-t border-cream-darker">
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={handlePrev}
                aria-label="Previous story chapter"
                className="px-4 py-2 min-h-[44px] rounded-lg bg-white border border-cream-darker text-charcoal font-mono text-xs font-medium hover:border-blue hover:text-blue active:scale-[0.98] transition-[color,border-color,transform] duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
              >
                &larr; Previous
              </button>
              <button
                type="button"
                onClick={handleNext}
                aria-label="Next story chapter"
                className="px-4 py-2 min-h-[44px] rounded-lg bg-charcoal text-cream font-mono text-xs font-medium hover:bg-charcoal-light active:scale-[0.98] transition-[background-color,transform] duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
              >
                Next Chapter &rarr;
              </button>
            </div>

            <div className="flex items-center gap-2">
              {storySlides.map((slide, idx) => (
                <button
                  key={slide.id}
                  type="button"
                  onClick={() => setActiveIndex(idx)}
                  aria-label={`Go to ${slide.shortTab}`}
                  className="p-2 min-w-[32px] min-h-[32px] flex items-center justify-center rounded-full focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue"
                >
                  <span
                    className={`block rounded-full transition-all duration-200 ${
                      idx === activeIndex ? 'w-6 h-2 bg-blue' : 'w-2 h-2 bg-charcoal-lighter'
                    }`}
                  />
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* 4-Step Professional Workflow Strip */}
        <div className="mt-12">
          <p className="font-mono text-xs uppercase tracking-widest text-charcoal-light font-semibold mb-4">
            My 4-Step Engineering Process on Every Project
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {processSteps.map((item) => (
              <div
                key={item.step}
                className="p-5 rounded-xl bg-cream/40 border border-cream-darker/80 min-w-0"
              >
                <div className="font-mono text-xs font-bold text-blue mb-1">
                  STEP {item.step}
                </div>
                <h4 className="text-base font-bold text-charcoal mb-1.5 break-words">
                  {item.name}
                </h4>
                <p className="text-xs text-charcoal-light leading-relaxed break-words">
                  {item.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </AnimatedSection>
  );
}
