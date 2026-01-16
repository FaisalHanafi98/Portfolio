-- Technologies
INSERT INTO technologies (id, name, category, icon_url) VALUES
(1, 'Laravel', 'Backend', NULL),
(2, 'PHP', 'Backend', NULL),
(3, 'MySQL', 'Database', NULL),
(4, 'JavaScript', 'Frontend', NULL),
(5, 'HTML/CSS', 'Frontend', NULL),
(6, 'TypeScript', 'Frontend', NULL),
(7, 'React', 'Frontend', NULL),
(8, 'SPFx', 'Frontend', NULL),
(9, 'Fluent UI', 'Frontend', NULL),
(10, 'Spring Boot', 'Backend', NULL),
(11, 'Java', 'Backend', NULL),
(12, 'PostgreSQL', 'Database', NULL),
(13, 'Tailwind CSS', 'Frontend', NULL),
(14, 'Framer Motion', 'Frontend', NULL),
(15, 'Python', 'Backend', NULL),
(16, 'SpaCy', 'AI/ML', NULL),
(17, 'NLP', 'AI/ML', NULL),
(18, 'R', 'Data', NULL),
(19, 'Power BI', 'Data', NULL),
(20, 'AWS', 'Cloud', NULL),
(21, 'Azure', 'Cloud', NULL),
(22, 'Git', 'Tools', NULL),
(23, 'Docker', 'Tools', NULL),
(24, 'Vite', 'Tools', NULL);

-- Projects
INSERT INTO projects (id, slug, title, short_description, full_description, problem_statement, solution, thumbnail_url, featured, display_order, github_url, live_url, period, metric_database_tables, metric_user_roles, metric_lines_of_code, metric_complexity_reduction, metric_recognition) VALUES
(1, 'creams', 'CREAMS Rehabilitation Management System',
'Enterprise-grade Laravel system for rehabilitation centers with comprehensive case management, RBAC, and reporting capabilities.',
'CREAMS (Community-based REhabilitAtions Management System) is a comprehensive web-based system designed to streamline operations at rehabilitation centers. The system manages the entire rehabilitation lifecycle from patient registration through discharge, including session scheduling, progress tracking, and competency assessments. Built with Laravel 10 and MySQL, it demonstrates enterprise-level architecture with robust security, data integrity, and scalability considerations.',
'Rehabilitation centers relied on paper-based systems and fragmented digital tools, leading to inefficient case management, lost documentation, difficulty tracking patient progress across sessions, and challenges in generating compliance reports for stakeholders.',
'Built an enterprise-grade management system with role-based access control supporting 6 distinct user roles, each with granular permissions. Implemented comprehensive modules for patient management, session scheduling with therapist competency matching, progress tracking with visual analytics, and automated report generation in PDF/Excel formats.',
NULL, true, 1, 'https://github.com/FaisalHanafi/creams', NULL, '2023-2024', 50, 6, NULL, NULL, 'Gold Medal - Best FYP'),

(2, 'auto-recruit', 'Auto-Recruit Pipeline Automation',
'Enterprise recruitment automation system built with SPFx and React, featuring 6-role RBAC and 95% complexity reduction.',
'Auto-Recruit is a sophisticated recruitment pipeline automation system designed to transform manual hiring processes into a streamlined, automated workflow. Built on SharePoint Framework (SPFx) with React and TypeScript, it demonstrates enterprise-grade patterns including comprehensive role-based access control, state management, and performance optimization.',
'Manual recruitment processes involved repetitive data entry, inconsistent candidate tracking across spreadsheets, difficulty coordinating between HR, hiring managers, and interviewers, and lack of visibility into pipeline metrics.',
'Developed a full-featured recruitment automation system with 6-role RBAC (23 granular permissions), candidate pipeline tracking, automated notifications, interview scheduling, and analytics dashboards. Achieved 68% improvement in page load times through component optimization and lazy loading.',
NULL, true, 2, NULL, NULL, '2025', NULL, 6, 15000, '95%', NULL),

(3, 'portfolio', 'Portfolio Platform',
'Dynamic full-stack portfolio demonstrating Spring Boot API development and modern React frontend with animations.',
'This portfolio platform itself serves as a demonstration of full-stack development capabilities. Built with Spring Boot 3 for the backend API and React with TypeScript for the frontend, it showcases modern development practices including RESTful API design, type-safe frontend development, and purposeful animation implementation.',
'Static portfolio sites cannot demonstrate live coding ability or backend skills. Needed a platform that proves technical capability through its own implementation.',
'Created a dynamic, API-driven portfolio with Spring Boot backend serving structured JSON, React frontend with Framer Motion animations, and responsive design. The codebase itself serves as a code sample for potential employers.',
NULL, false, 3, 'https://github.com/FaisalHanafi/portfolio', NULL, '2025', NULL, NULL, NULL, NULL, NULL),

(4, 'tezer', 'TeZer Text Summarizer',
'NLP-powered extractive text summarization tool built with Python and SpaCy.',
'TeZer is an extractive text summarization tool that uses natural language processing to identify and extract the most important sentences from input text. Built with Python and SpaCy, it demonstrates understanding of NLP concepts including tokenization, sentence scoring, and text preprocessing.',
'Reading long documents and articles is time-consuming. Needed an automated way to extract key information while preserving the essential meaning.',
'Developed an extractive summarization algorithm using sentence scoring based on word frequency, position, and length. Implemented configurable summary length and text preprocessing for optimal results.',
NULL, false, 4, 'https://github.com/FaisalHanafi/tezer', NULL, '2023', NULL, NULL, NULL, NULL, NULL);

-- Project Technologies (Many-to-Many)
INSERT INTO project_technologies (project_id, technology_id) VALUES
-- CREAMS
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
-- Auto-Recruit
(2, 6), (2, 7), (2, 8), (2, 9),
-- Portfolio
(3, 10), (3, 11), (3, 12), (3, 7), (3, 6), (3, 13), (3, 14),
-- TeZer
(4, 15), (4, 16), (4, 17);

-- Project Features
INSERT INTO project_features (project_id, feature) VALUES
-- CREAMS
(1, '6-role RBAC with granular permissions (Admin, Manager, Therapist, Counselor, Staff, Viewer)'),
(1, 'Comprehensive patient lifecycle management from registration to discharge'),
(1, 'Session scheduling with therapist competency matching'),
(1, 'Progress tracking with visual analytics and milestone indicators'),
(1, 'Automated PDF/Excel report generation for compliance and stakeholders'),
(1, 'Audit logging for all sensitive operations'),
-- Auto-Recruit
(2, '6-role RBAC system with 23 granular permissions'),
(2, 'Candidate pipeline tracking with stage-based workflows'),
(2, 'Automated email notifications for status updates'),
(2, 'Interview scheduling with calendar integration'),
(2, 'Analytics dashboard with recruitment metrics'),
(2, '68% page load improvement through optimization'),
-- Portfolio
(3, 'RESTful API with Spring Boot and OpenAPI documentation'),
(3, 'Type-safe React frontend with strict TypeScript'),
(3, 'Framer Motion animations with reduced motion support'),
(3, 'Responsive design tested across multiple breakpoints'),
(3, 'Dark/light theme toggle with system preference detection'),
-- TeZer
(4, 'Extractive summarization using sentence scoring'),
(4, 'Configurable summary length (percentage or sentence count)'),
(4, 'Text preprocessing and cleaning'),
(4, 'Support for multiple input formats');

-- Skill Categories
INSERT INTO skill_categories (id, name, display_order) VALUES
(1, 'Languages', 1),
(2, 'Frontend', 2),
(3, 'Backend', 3),
(4, 'Data & Databases', 4),
(5, 'Cloud & DevOps', 5),
(6, 'Tools', 6);

-- Skills
INSERT INTO skills (id, name, proficiency, icon_url, display_order, category_id) VALUES
-- Languages
(1, 'TypeScript', 4, NULL, 1, 1),
(2, 'JavaScript', 4, NULL, 2, 1),
(3, 'PHP', 4, NULL, 3, 1),
(4, 'Python', 3, NULL, 4, 1),
(5, 'Java', 3, NULL, 5, 1),
(6, 'R', 3, NULL, 6, 1),
(7, 'SQL', 4, NULL, 7, 1),
-- Frontend
(8, 'React', 4, NULL, 1, 2),
(9, 'HTML/CSS', 5, NULL, 2, 2),
(10, 'Tailwind CSS', 4, NULL, 3, 2),
(11, 'Framer Motion', 3, NULL, 4, 2),
(12, 'SPFx', 4, NULL, 5, 2),
-- Backend
(13, 'Spring Boot', 3, NULL, 1, 3),
(14, 'Laravel', 4, NULL, 2, 3),
(15, 'Node.js', 3, NULL, 3, 3),
(16, 'REST APIs', 4, NULL, 4, 3),
-- Data & Databases
(17, 'MySQL', 4, NULL, 1, 4),
(18, 'PostgreSQL', 3, NULL, 2, 4),
(19, 'Power BI', 3, NULL, 3, 4),
(20, 'Data Analysis', 4, NULL, 4, 4),
-- Cloud & DevOps
(21, 'AWS', 3, NULL, 1, 5),
(22, 'Azure', 3, NULL, 2, 5),
(23, 'Docker', 2, NULL, 3, 5),
-- Tools
(24, 'Git', 4, NULL, 1, 6),
(25, 'Vite', 4, NULL, 2, 6),
(26, 'VS Code', 5, NULL, 3, 6);

-- Experiences
INSERT INTO experiences (id, company, role, location, start_date, end_date, description, display_order) VALUES
(1, 'Accenture Technology Malaysia', 'Packaged App Development Associate', 'Kuala Lumpur, Malaysia', '2025-03-01', NULL,
'Working on enterprise application development projects, focusing on React, TypeScript, and SharePoint technologies. Contributing to client-facing solutions with emphasis on code quality, performance optimization, and modern development practices.', 1),

(2, 'AEM Energy Solutions', 'Data Analyst Intern', 'Kuala Lumpur, Malaysia', '2023-08-01', '2024-03-31',
'Conducted data analysis for oil and gas risk assessment projects using R programming. Developed data visualization dashboards, performed statistical analysis, and contributed to technical reports for clients.', 2);

-- Experience Highlights
INSERT INTO experience_highlights (experience_id, highlight) VALUES
-- Accenture
(1, 'Developing enterprise applications using React and TypeScript'),
(1, 'Building SharePoint solutions with SPFx framework'),
(1, 'Implementing performance optimizations achieving significant load time improvements'),
(1, 'Collaborating with cross-functional teams in Agile environment'),
-- AEM Energy
(2, 'Analyzed oil and gas data for risk assessment using R programming'),
(2, 'Created data visualization dashboards for client presentations'),
(2, 'Performed statistical analysis and data cleaning on large datasets'),
(2, 'Contributed to technical documentation and reports');
