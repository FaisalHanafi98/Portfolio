import { motion } from 'framer-motion';

export default function EmailSidebar() {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ delay: 1.5 }}
      className="fixed right-6 bottom-0 hidden md:flex flex-col items-center gap-6"
    >
      <a
        href="mailto:faisalhanafi98@gmail.com"
        className="font-mono text-xs text-charcoal-lighter hover:text-blue transition-colors tracking-widest"
        style={{ writingMode: 'vertical-rl' }}
      >
        faisalhanafi98@gmail.com
      </a>
      <div className="w-px h-24 bg-charcoal-lighter" />
    </motion.div>
  );
}
