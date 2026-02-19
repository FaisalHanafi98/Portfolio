import { motion } from 'framer-motion';
import Tag from '../ui/Tag';
import Button from '../ui/Button';
import { staggerContainer, slideUp } from '../../lib/animations';
import type { ProjectDetail as ProjectDetailType } from '../../types';

interface ProjectDetailProps {
  project: ProjectDetailType;
}

export default function ProjectDetail({ project }: ProjectDetailProps) {
  return (
    <motion.article
      variants={staggerContainer}
      initial="hidden"
      animate="visible"
      className="max-w-4xl mx-auto"
    >
      {/* Header */}
      <motion.header variants={slideUp} className="mb-12">
        <p className="font-mono text-teal mb-4">{project.period}</p>
        <h1 className="text-4xl md:text-5xl font-bold text-slate-light mb-4">
          {project.title}
        </h1>
        <p className="text-xl text-slate">{project.shortDescription}</p>
      </motion.header>

      {/* Project Image */}
      {project.images && project.images.length > 0 && (
        <motion.div variants={slideUp} className="mb-12">
          <div className="rounded-lg overflow-hidden">
            <img
              src={project.images[0].url}
              alt={project.images[0].altText}
              className="w-full"
            />
          </div>
        </motion.div>
      )}

      {/* Technologies */}
      <motion.div variants={slideUp} className="flex flex-wrap gap-3 mb-12">
        {project.technologies.map((tech) => (
          <Tag key={tech.id}>
            {tech.name}
          </Tag>
        ))}
      </motion.div>

      {/* Problem Statement */}
      {project.problemStatement && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-4">
            The Problem
          </h2>
          <p className="text-slate leading-relaxed">{project.problemStatement}</p>
        </motion.section>
      )}

      {/* Solution */}
      {project.solution && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-4">
            The Solution
          </h2>
          <p className="text-slate leading-relaxed">{project.solution}</p>
        </motion.section>
      )}

      {/* Architecture Decisions */}
      {project.architectureDecisions && project.architectureDecisions.length > 0 && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-4">
            Architecture Decisions
          </h2>
          <ul className="space-y-3">
            {project.architectureDecisions.map((decision, index) => (
              <li key={index} className="flex items-start gap-3 text-slate">
                <span className="text-teal mt-1">▹</span>
                {decision}
              </li>
            ))}
          </ul>
        </motion.section>
      )}

      {/* Tech Stack */}
      {project.techStack && Object.keys(project.techStack).length > 0 && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-6">
            Tech Stack
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {Object.entries(project.techStack).map(([layer, techs]) => (
              <div key={layer} className="bg-navy-light p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-teal mb-3">{layer}</h3>
                <div className="flex flex-wrap gap-2">
                  {techs.map((t) => (
                    <span key={t} className="text-sm text-slate bg-navy px-3 py-1 rounded-full">
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
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-4">
            Challenges
          </h2>
          <ul className="space-y-3">
            {project.challenges.map((challenge, index) => (
              <li key={index} className="flex items-start gap-3 text-slate">
                <span className="text-coral mt-1">▹</span>
                {challenge}
              </li>
            ))}
          </ul>
        </motion.section>
      )}

      {/* Cost Justification */}
      {project.costJustification && (
        <motion.section variants={slideUp} className="mb-12">
          <h2 className="text-2xl font-semibold text-slate-light mb-4">
            Cost & Infrastructure
          </h2>
          <p className="text-slate leading-relaxed">{project.costJustification}</p>
        </motion.section>
      )}

      {/* Full Description */}
      <motion.section variants={slideUp} className="mb-12">
        <h2 className="text-2xl font-semibold text-slate-light mb-4">
          About This Project
        </h2>
        <div className="text-slate leading-relaxed space-y-4">
          <p>{project.fullDescription}</p>
        </div>
      </motion.section>

      {/* Links */}
      <motion.div variants={slideUp} className="flex gap-4">
        {project.githubUrl && (
          <Button href={project.githubUrl} external>
            <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 24 24">
              <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
            </svg>
            View Code
          </Button>
        )}
        {project.liveUrl && (
          <Button href={project.liveUrl} variant="secondary" external>
            <svg className="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
            Live Demo
          </Button>
        )}
      </motion.div>
    </motion.article>
  );
}
