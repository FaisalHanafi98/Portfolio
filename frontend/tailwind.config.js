/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // New greyish-casual color palette
        charcoal: {
          DEFAULT: '#1e1e2e',      // Main background (was navy #0a192f)
          light: '#2a2a3e',         // Card backgrounds (was navy-light #112240)
          lighter: '#3a3a4a',       // Borders, subtle accents (was navy-lighter #233554)
        },
        'off-white': '#e8e6e3',     // Primary text (was slate-light #ccd6f6)
        'grey-muted': '#a0a0a0',    // Secondary text (was slate #8892b0)
        'grey-subtle': '#4a4a5a',   // Sketch elements, dividers
        teal: {
          DEFAULT: '#5eead4',       // Soft muted teal (was bright #64ffda)
          dark: '#4fd1c5',
        },
        coral: '#f0a0a0',           // Secondary accent (dusty rose/coral)

        // Legacy aliases for gradual migration (will remove these later)
        navy: {
          DEFAULT: '#1e1e2e',
          light: '#2a2a3e',
          lighter: '#3a3a4a',
        },
        slate: {
          light: '#e8e6e3',
          DEFAULT: '#a0a0a0',
          dark: '#4a4a5a',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'system-ui',
          'sans-serif',
        ],
        mono: [
          'JetBrains Mono',
          'Fira Code',
          'SF Mono',
          'Consolas',
          'monospace',
        ],
        // Optional: Add a casual/rounded font for special headings
        // display: ['Poppins', 'Inter', 'sans-serif'],
      },
      fontSize: {
        // Slightly adjusted scale for better hierarchy
        'xs': ['0.75rem', { lineHeight: '1rem' }],
        'sm': ['0.875rem', { lineHeight: '1.25rem' }],
        'base': ['1rem', { lineHeight: '1.5rem' }],
        'lg': ['1.125rem', { lineHeight: '1.75rem' }],
        'xl': ['1.25rem', { lineHeight: '1.75rem' }],
        '2xl': ['1.5rem', { lineHeight: '2rem' }],
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }],
        '4xl': ['2.25rem', { lineHeight: '2.5rem' }],
        '5xl': ['3rem', { lineHeight: '1.16' }],
        '6xl': ['3.75rem', { lineHeight: '1.1' }],
        '7xl': ['4.5rem', { lineHeight: '1.05' }],
      },
      letterSpacing: {
        tighter: '-0.02em',
        tight: '-0.01em',
        normal: '0',
        wide: '0.01em',
        wider: '0.02em',
        widest: '0.1em',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'slide-up': 'slideUp 0.5s ease-out',
        'slide-down': 'slideDown 0.3s ease-out',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideDown: {
          '0%': { opacity: '0', transform: 'translateY(-10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
    },
  },
  plugins: [],
};
