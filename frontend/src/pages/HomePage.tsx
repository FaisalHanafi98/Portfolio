import { useEffect } from 'react';
import { motion } from 'framer-motion';
import HeroSection from '../components/sections/HeroSection';
import AboutSection from '../components/sections/AboutSection';
import ProjectsSection from '../components/sections/ProjectsSection';
import SkillsSection from '../components/sections/SkillsSection';
import ExperienceSection from '../components/sections/ExperienceSection';
import ContactSection from '../components/sections/ContactSection';
import { pageTransition } from '../lib/animations';
import { buildAnalyticsPayload, postAnalyticsEvent } from '../lib/analytics';

export default function HomePage() {
  useEffect(() => {
    const payload = buildAnalyticsPayload({
      eventName: 'page_view',
      projectSlug: '__site__',
      sectionKey: 'hero',
    });
    void postAnalyticsEvent(payload);
  }, []);
  return (
    <motion.div
      variants={pageTransition}
      initial="initial"
      animate="animate"
      exit="exit"
    >
      <HeroSection />
      <AboutSection />
      <ProjectsSection />
      <SkillsSection />
      <ExperienceSection />
      <ContactSection />
    </motion.div>
  );
}
