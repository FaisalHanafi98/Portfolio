import { motion } from 'framer-motion';
import Button from '../ui/Button';
import { staggerContainer, heroTextReveal, letterReveal } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import { SketchStar } from '../ui/SketchDoodles';

export default function HeroSection() {
  const nameText = 'Faisal Hanafi.';

  return (
    <section id="intro" className="min-h-screen flex items-center justify-center px-6 pt-20 relative">
      {/* Decorative sketch star */}
      <div className="absolute top-32 right-12 text-pink/30 hidden lg:block">
        <SketchStar delay={2} />
      </div>

      <motion.div
        className="max-w-4xl relative"
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
        <motion.p
          variants={heroTextReveal}
          className="font-hand text-blue text-xl md:text-2xl mb-5"
        >
          Hi, my name is
        </motion.p>

        {/* Name with letter-by-letter reveal */}
        <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold text-charcoal mb-2">
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

        {/* Hand-drawn underline under name */}
        <div className="mb-6">
          <SketchUnderline width={150} className="text-blue/40" delay={1.2} />
        </div>

        <motion.h2
          variants={heroTextReveal}
          className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold text-charcoal-light mb-6"
        >
          I build systems around data, software, and decisions.
        </motion.h2>

        <motion.p
          variants={heroTextReveal}
          className="text-charcoal-light max-w-2xl text-lg mb-12 leading-relaxed"
        >
          I&apos;m a <span className="text-charcoal font-medium">data science graduate</span> who
          grew into software engineering through enterprise delivery, financial curiosity, and a
          habit of caring deeply about how systems handle real people, real data, and real money.
          {' '}Currently building at{' '}
          <a
            href="https://www.accenture.com"
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue hover:text-blue-dark link-underline transition-colors"
          >
            Accenture
          </a>
          , while exploring better tools for analytics, AI, and financial clarity.
        </motion.p>

        <motion.div variants={heroTextReveal} className="flex gap-4 flex-wrap">
          <Button href="#projects" size="lg">
            View My Work
          </Button>
          <Button href="#about" variant="ghost" size="lg">
            Learn More
          </Button>
        </motion.div>

        {/* Engineering Proof Tape */}
        <motion.div
          variants={heroTextReveal}
          className="mt-14 p-5 md:p-6 rounded-2xl bg-white/85 border-2 border-cream-darker/60 backdrop-blur-sm shadow-sm grid grid-cols-2 md:grid-cols-4 gap-6"
        >
          <div className="min-w-0">
            <div className="font-mono text-2xl md:text-3xl font-bold text-charcoal">Gold FYP</div>
            <div className="text-xs font-medium text-charcoal-light uppercase tracking-wider mt-1">
              CREAMS Case Management (IIUM)
            </div>
          </div>
          <div className="min-w-0">
            <div className="font-mono text-2xl md:text-3xl font-bold text-charcoal">Enterprise</div>
            <div className="text-xs font-medium text-charcoal-light uppercase tracking-wider mt-1">
              Accenture SPFx &amp; BI Delivery
            </div>
          </div>
          <div className="min-w-0">
            <div className="font-mono text-2xl md:text-3xl font-bold text-charcoal">Full-Stack</div>
            <div className="text-xs font-medium text-charcoal-light uppercase tracking-wider mt-1">
              Spring Boot + Laravel + React
            </div>
          </div>
          <div className="min-w-0">
            <div className="font-mono text-2xl md:text-3xl font-bold text-charcoal">ML &amp; Data</div>
            <div className="text-xs font-medium text-charcoal-light uppercase tracking-wider mt-1">
              Financial NLP &amp; ETL Validation
            </div>
          </div>
        </motion.div>
      </motion.div>

      {/* Scroll indicator */}
      <motion.div
        aria-hidden="true"
        className="absolute bottom-8 left-1/2 -translate-x-1/2 hidden sm:flex flex-col items-center gap-2"
        initial={{ opacity: 0, y: -6 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.8, duration: 0.4, ease: [0.22, 1, 0.36, 1] }}
      >
        <span className="text-charcoal-light text-xs font-mono tracking-wider">SCROLL</span>
        <div className="w-[1px] h-10 bg-gradient-to-b from-charcoal-light/60 to-transparent" />
      </motion.div>
    </section>
  );
}
