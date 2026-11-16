import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Tag from '../ui/Tag';
import { slideUp } from '../../lib/animations';
import { projectImages } from '../../lib/projectImages';
import { projectNarratives } from '../../lib/projectNarratives';
import type { ProjectSummary } from '../../types';

interface FeaturedProjectProps {
  project: ProjectSummary;
  index: number;
  showTechnicalDepth?: boolean;
}

export default function FeaturedProject({
  project,
  index,
  showTechnicalDepth = false,
}: FeaturedProjectProps) {
  const imageSrc = projectImages.get(project.slug);
  const narrative = projectNarratives.get(project.slug);
  const [imageError, setImageError] = useState(false);
  const showImage = Boolean(imageSrc && !imageError);

  return (
    <motion.article
      variants={slideUp}
      className="bg-white rounded-2xl border-2 border-cream-darker/90 shadow-sm hover:border-blue/40 transition-[border-color,box-shadow] duration-200 overflow-hidden"
    >
      {/* Top Bar: Project Index, Business Domain & Timeline */}
      <div className="px-6 py-4 bg-cream-dark/60 border-b border-cream-darker flex flex-wrap items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2.5">
          <span className="font-mono text-xs font-bold px-2.5 py-1 rounded-md bg-charcoal text-cream">
            CASE STUDY 0{index + 1}
          </span>
          {narrative && (
            <span className="font-mono text-xs font-semibold text-blue bg-blue/10 px-3 py-1 rounded-full border border-blue/20">
              {narrative.businessPillar}
            </span>
          )}
          {narrative?.audienceTag && (
            <span className="text-xs text-charcoal-light font-medium">
              · {narrative.audienceTag}
            </span>
          )}
        </div>
        {project.period && (
          <span className="font-mono text-xs text-charcoal-light px-2.5 py-0.5 rounded-full bg-white border border-cream-darker">
            {project.period}
          </span>
        )}
      </div>

      {/* Main Content Grid */}
      <div className="p-6 md:p-8 grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Left Column: Title, Headline Summary & 3-Step Business Story */}
        <div className="lg:col-span-7 min-w-0">
          <Link
            to={`/projects/${project.slug}`}
            className="inline-block rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
          >
            <h3 className="text-2xl md:text-3xl font-bold text-charcoal hover:text-blue transition-colors mb-3 break-words">
              {project.title}
            </h3>
          </Link>

          <p className="text-charcoal-light text-base leading-relaxed mb-6 break-words">
            {project.shortDescription}
          </p>

          {narrative ? (
            <div className="space-y-4 mb-6">
              {/* 1. What It's About */}
              <div className="p-4 rounded-xl bg-cream/60 border border-cream-darker">
                <p className="font-mono text-xs uppercase tracking-wider font-bold text-pink mb-1">
                  01 / What This Project Is About (The Problem)
                </p>
                <p className="text-sm text-charcoal leading-relaxed break-words">
                  {narrative.whatItIs}
                </p>
              </div>

              {/* 2. What I Did */}
              <div className="p-4 rounded-xl bg-cream/60 border border-cream-darker">
                <p className="font-mono text-xs uppercase tracking-wider font-bold text-blue mb-1">
                  02 / What I Did (How I Solved It)
                </p>
                <p className="text-sm text-charcoal leading-relaxed break-words">
                  {narrative.whatIDid}
                </p>
              </div>

              {/* 3. Why It Matters / Business Result */}
              <div className="p-4 rounded-xl bg-green/5 border-2 border-green/30">
                <p className="font-mono text-xs uppercase tracking-wider font-bold text-green mb-1">
                  03 / Why People Care (The Business &amp; Human Result)
                </p>
                <p className="text-sm text-charcoal font-medium leading-relaxed break-words">
                  {narrative.whyItMatters}
                </p>
              </div>

              {/* Optional Engineering Depth Drawer (for Tech Leads) */}
              {showTechnicalDepth && (
                <div className="p-4 rounded-xl bg-charcoal text-cream border border-charcoal-light">
                  <p className="font-mono text-xs uppercase tracking-wider font-semibold text-yellow mb-2">
                    Under the Hood — Engineering &amp; Architecture Proof
                  </p>
                  <ul className="space-y-1.5 text-xs text-cream/90 leading-relaxed">
                    {narrative.engineeringHighlights.map((item, idx) => (
                      <li key={idx} className="flex items-start gap-2">
                        <span className="text-yellow shrink-0" aria-hidden="true">&#9656;</span>
                        <span>{item}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          ) : null}

          {/* Tech Tags & Action Links */}
          <div className="flex flex-wrap gap-2 mb-6">
            {project.technologies.map((tech) => (
              <Tag key={tech} size="sm">
                {tech}
              </Tag>
            ))}
          </div>

          <div className="flex flex-wrap items-center gap-4 pt-2 border-t border-cream-darker/60">
            <Link
              to={`/projects/${project.slug}`}
              className="btn-secondary text-xs px-5 py-2.5 min-h-[44px]"
              aria-label={`Read full case study for ${project.title}`}
            >
              <span>Read Full Case Study</span>
              <svg className="w-4 h-4 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
              </svg>
            </Link>

            {narrative?.githubUrl && (
              <a
                href={narrative.githubUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="btn-primary text-xs px-4 py-2.5 min-h-[44px]"
              >
                <span>View Source on GitHub</span>
              </a>
            )}
          </div>
        </div>

        {/* Right Column: Visual Preview + Quick Impact Card */}
        <div className="lg:col-span-5 min-w-0 space-y-4">
          <Link
            to={`/projects/${project.slug}`}
            className="block relative group rounded-xl focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue"
          >
            <div className="relative overflow-hidden rounded-xl border-2 border-cream-darker/80 shadow-sm transition-[border-color,box-shadow] duration-200 ease-out group-hover:shadow-md group-hover:border-blue/60 bg-cream/40">
              {showImage ? (
                <img
                  src={imageSrc}
                  alt={project.title}
                  onError={() => setImageError(true)}
                  className="w-full aspect-video object-cover object-top transition-transform duration-300 ease-out group-hover:scale-[1.02]"
                  loading="lazy"
                />
              ) : (
                <div className="w-full aspect-video bg-cream-dark/70 flex flex-col items-center justify-center p-6 text-center">
                  <span className="text-xs font-mono uppercase tracking-widest text-blue font-semibold mb-2">
                    {narrative?.businessPillar ?? 'System Architecture'}
                  </span>
                  <span className="text-base text-charcoal font-bold mb-3 break-words max-w-sm">
                    {project.title}
                  </span>
                  <div className="flex flex-wrap justify-center gap-1.5">
                    {project.technologies.slice(0, 4).map((tech) => (
                      <span
                        key={tech}
                        className="text-xs font-mono px-2.5 py-0.5 rounded-full bg-white text-charcoal-light border border-cream-darker"
                      >
                        {tech}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </Link>

          {narrative && (
            <div className="p-5 rounded-xl bg-cream/50 border border-cream-darker">
              <p className="font-mono text-xs uppercase tracking-wider text-charcoal-light font-semibold mb-1.5">
                10-Second Recruiter Takeaway
              </p>
              <p className="text-sm font-semibold text-charcoal leading-relaxed break-words">
                {narrative.headlineOutcome}
              </p>
            </div>
          )}
        </div>
      </div>
    </motion.article>
  );
}
