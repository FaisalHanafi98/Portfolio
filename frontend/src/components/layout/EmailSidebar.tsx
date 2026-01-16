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
        href="mailto:faisal@example.com"
        className="font-mono text-xs text-slate hover:text-teal transition-colors tracking-widest"
        style={{ writingMode: 'vertical-rl' }}
      >
        faisal@example.com
      </a>
      <div className="w-px h-24 bg-slate" />
    </motion.div>
  );
}
