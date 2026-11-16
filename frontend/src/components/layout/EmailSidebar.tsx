import { motion } from 'framer-motion';

export default function EmailSidebar() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.4, duration: 0.35, ease: [0.22, 1, 0.36, 1] }}
      className="fixed right-6 bottom-0 hidden md:flex flex-col items-center gap-6"
    >
      <a
        href="mailto:faisalhanafi98@gmail.com"
        className="font-mono text-xs text-charcoal-light hover:text-blue hover:-translate-y-1 transition-[color,transform] duration-200 ease-out tracking-widest focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue rounded"
        style={{ writingMode: 'vertical-rl' }}
      >
        faisalhanafi98@gmail.com
      </a>
      <div className="w-px h-24 bg-charcoal-lighter" />
    </motion.div>
  );
}
