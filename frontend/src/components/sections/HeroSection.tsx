import { motion } from 'framer-motion';
import Button from '../ui/Button';
import { staggerContainer, heroTextReveal, letterReveal } from '../../lib/animations';
import SketchUnderline from '../ui/SketchUnderline';
import { SketchArrow, SketchStar } from '../ui/SketchDoodles';

export default function HeroSection() {
  const nameText = "Faisal Hanafi.";

  return (
    <section id="intro" className="min-h-screen flex items-center justify-center px-6 pt-20 relative">
      {/* Decorative sketch star - top right */}
      <div className="absolute top-32 right-12 text-coral/30 hidden lg:block">
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
          className="font-mono text-teal mb-5 text-sm md:text-base"
        >
          Hi, my name is
        </motion.p>

        {/* Name with letter-by-letter reveal */}
        <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold text-off-white mb-2">
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
          <SketchUnderline width={150} className="text-teal/40" delay={1.2} />
        </div>

        <motion.h2
          variants={heroTextReveal}
          className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold text-grey-muted mb-6"
        >
          I build things for the web.
        </motion.h2>

        <motion.p
          variants={heroTextReveal}
          className="text-grey-muted max-w-xl text-lg mb-12 leading-relaxed"
        >
          I'm a <span className="text-off-white font-medium">software engineer</span> and{' '}
          <span className="text-off-white font-medium">data enthusiast</span>.
          I build systems that scale and solutions that matter.
          {' '}Currently at{' '}
          <a
            href="https://www.accenture.com"
            target="_blank"
            rel="noopener noreferrer"
            className="text-teal hover:text-teal-dark link-underline transition-colors"
          >
            Accenture
          </a>
          .
        </motion.p>

        <motion.div variants={heroTextReveal} className="flex gap-4 flex-wrap">
          <Button href="#projects" size="lg">
            View My Work
          </Button>
          <Button href="#about" variant="ghost" size="lg">
            Learn More
          </Button>
        </motion.div>

        {/* Decorative arrow pointing to CTA */}
        <motion.div
          initial={{ opacity: 0, x: -10 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 2, duration: 0.5 }}
          className="absolute -left-16 top-[480px] text-teal/40 hidden xl:block"
        >
          <SketchArrow delay={2.2} />
        </motion.div>
      </motion.div>

      {/* Scroll indicator */}
      <motion.div
        className="absolute bottom-12 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2"
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 2.5, duration: 0.8, repeat: Infinity, repeatType: 'reverse' }}
      >
        <span className="text-grey-muted text-xs font-mono tracking-wider">SCROLL</span>
        <div className="w-[1px] h-12 bg-gradient-to-b from-grey-muted to-transparent" />
      </motion.div>
    </section>
  );
}
