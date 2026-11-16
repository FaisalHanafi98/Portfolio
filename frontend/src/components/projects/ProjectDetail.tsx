import { useState } from 'react';
import { motion } from 'framer-motion';
import Tag from '../ui/Tag';
import Button from '../ui/Button';
import { staggerContainer, slideUp } from '../../lib/animations';
import { projectImages } from '../../lib/projectImages';
import SketchUnderline from '../ui/SketchUnderline';
import type { ProjectDetail as ProjectDetailType } from '../../types';

interface ProjectDetailProps {
  project: ProjectDetailType;
}

export default function ProjectDetail({ project }: ProjectDetailProps) {
  const localImage = projectImages.get(project.slug);
  const remoteImage = project.images?.find((image) => image.url && !image.url.includes('placeholder.com'));
  const heroImage = localImage ?? remoteImage?.url;
  const [imageError, setImageError] = useState(false);

  return (
    <motion.article
      variants={staggerContainer}
      initial="hidden"
      animate="visible"
      className="max-w-4xl mx-auto min-w-0"
    >
      {/* Header */}
      <motion.header variants={slideUp} className="mb-10">
        <div className="flex flex-wrap items-center gap-3 mb-3">
          {project.period && (
            <span className="font-mono text-xs font-medium text-blue bg-blue/10 px-3 py-1 rounded-full">
              {project.period}
            </span>
          )}
          <span className="font-hand text-charcoal-light text-lg">
            Engineering Case Study
          </span>
        </div>
        <h1 className="text-3xl sm:text-4xl md:text-5xl font-bold text-charcoal mb-4 break-words">
          {project.title}
        </h1>
        <SketchUnderline width={200} className="text-blue/40" delay={0.2} />
        <p className="text-lg md:text-xl text-charcoal-light mt-4 leading-relaxed break-words">
          {project.shortDescription}
        </p>
      </motion.header>

      {/* Technologies */}
      {project.technologies && project.technologies.length > 0 && (
        <motion.div variants={slideUp} className="flex flex-wrap gap-2.5 mb-10">
          {project.technologies.map((tech) => (
            <Tag key={tech.id}>
              {tech.name}
            </Tag>
          ))}
        </motion.div>
      )}

      {/* Project Image */}
      {heroImage && !imageError && (
        <motion.div variants={slideUp} className="mb-12">
          <div className="rounded-xl overflow-hidden border-2 border-cream-darker/60 shadow-sm bg-white">
            <img
              src={heroImage}
              alt={(localImage ? undefined : remoteImage?.altText) || project.title}
              onError={() => setImageError(true)}
              className="w-full aspect-video object-cover object-top"
            />
          </div>
        </motion.div>
      )}

      {/* Problem & Solution */}
      {(project.problemStatement || project.solution) && (
        <motion.div
          variants={slideUp}
          className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12"
        >
          {project.problemStatement && (
            <section className="bg-white p-6 rounded-xl border border-cream-darker/60 shadow-sm min-w-0">
              <p className="font-mono text-xs uppercase tracking-wider text-pink font-semibold mb-2">
                01 / Context &amp; Problem
              </p>
              <h2 className="text-xl font-semibold text-charcoal mb-3">
                The Problem
              </h2>
              <p className="text-charcoal-light leading-relaxed break-words">
                {project.problemStatement}
              </p>
            </section>
          )}

          {project.solution && (
            <section className="bg-white p-6 rounded-xl border border-cream-darker/60 shadow-sm min-w-0">
              <p className="font-mono text-xs uppercase tracking-wider text-green font-semibold mb-2">
                02 / Approach &amp; Delivery
              </p>
              <h2 className="text-xl font-semibold text-charcoal mb-3">
                The Solution
              </h2>
              <p className="text-charcoal-light leading-relaxed break-words">
                {project.solution}
              </p>
            </section>
          )}
        </motion.div>
      )}

      {/* Architecture Decisions */}
      {project.architectureDecisions && project.architectureDecisions.length > 0 && (
        <motion.section variants={slideUp} className="mb-12 bg-white p-6 md:p-8 rounded-xl border border-cream-darker/60 shadow-sm">
          <p className="font-mono text-xs uppercase tracking-wider text-blue font-semibold mb-2">
            03 / System Design &amp; Trade-offs
          </p>
          <h2 className="text-2xl font-semibold text-charcoal mb-4">
            Architecture Decisions
          </h2>
          <ul className="space-y-3">
            {project.architectureDecisions.map((decision, index) => (
              <li key={index} className="flex items-start gap-3 text-charcoal-light leading-relaxed">
                <span className="text-blue mt-1 shrink-0" aria-hidden="true">&#9656;</span>
                <span className="min-w-0 break-words">{decision}</span>
              </li>
            ))}
          </ul>
        </motion.section>
      )}

      {/* Tech Stack */}
      {project.techStack && Object.keys(project.techStack).length > 0 && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-charcoal mb-6">
            Tech Stack
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {Object.entries(project.techStack).map(([layer, techs]) => (
              <div key={layer} className="bg-white p-6 rounded-xl border border-cream-darker/60 min-w-0">
                <h3 className="text-lg font-semibold text-blue mb-3 font-hand">{layer}</h3>
                <div className="flex flex-wrap gap-2">
                  {techs.map((t) => (
                    <span key={t} className="text-sm text-charcoal-light bg-cream-dark px-3 py-1 rounded-full break-words">
                      {t}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </motion.section>
      )}

      {/* Challenges */}
      {project.challenges && project.challenges.length > 0 && (
        <motion.section variants={slideUp} className="mb-12 bg-white p-6 md:p-8 rounded-xl border border-cream-darker/60 shadow-sm">
          <p className="font-mono text-xs uppercase tracking-wider text-pink font-semibold mb-2">
            04 / Engineering Constraints
          </p>
          <h2 className="text-2xl font-semibold text-charcoal mb-4">
            Challenges
          </h2>
          <ul className="space-y-3">
            {project.challenges.map((challenge, index) => (
              <li key={index} className="flex items-start gap-3 text-charcoal-light leading-relaxed">
                <span className="text-pink mt-1 shrink-0" aria-hidden="true">&#9656;</span>
                <span className="min-w-0 break-words">{challenge}</span>
              </li>
            ))}
          </ul>
        </motion.section>
      )}

      {/* Cost Justification & Full Description */}
      <motion.div variants={slideUp} className="grid grid-cols-1 gap-6 mb-12">
        {project.costJustification && (
          <section className="bg-cream-dark/60 p-6 rounded-xl border border-cream-darker min-w-0">
            <p className="font-mono text-xs uppercase tracking-wider text-charcoal font-semibold mb-2">
              05 / Operational Reality &amp; Outcome
            </p>
            <h2 className="text-xl font-semibold text-charcoal mb-2">
              Cost &amp; Infrastructure
            </h2>
            <p className="text-charcoal-light leading-relaxed break-words">
              {project.costJustification}
            </p>
          </section>
        )}

        <section className="bg-white p-6 rounded-xl border border-cream-darker/60 min-w-0">
          <h2 className="text-xl font-semibold text-charcoal mb-3">
            About This Project
          </h2>
          <div className="text-charcoal-light leading-relaxed space-y-4 break-words">
            <p>{project.fullDescription}</p>
          </div>
        </section>
      </motion.div>

      {/* Links */}
      <motion.div variants={slideUp} className="flex flex-wrap gap-4">
        {project.githubUrl && (
          <Button href={project.githubUrl} external>
            <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
            </svg>
            View Code
          </Button>
        )}
        {project.liveUrl && (
          <Button href={project.liveUrl} variant="secondary" external>
            <svg className="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
            Live Demo
          </Button>
        )}
      </motion.div>
    </motion.article>
  );
}
