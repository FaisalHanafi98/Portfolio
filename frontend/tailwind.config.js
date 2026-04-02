/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Cream notebook palette
        cream: {
          DEFAULT: '#FFF8F0',
          dark: '#F5EDE3',
          darker: '#EBE1D5',
        },
        charcoal: {
          DEFAULT: '#2D3436',
          light: '#636E72',
          lighter: '#B2BEC3',
        },
        // WCAG AA-compliant accent colors
        blue: {
          DEFAULT: '#0984E3',
          light: '#74B9FF',
          dark: '#0767B2',
        },
        pink: {
          DEFAULT: '#E84393',
          light: '#FD79A8',
          dark: '#C0277A',
        },
        green: {
          DEFAULT: '#00B894',
          light: '#55EFC4',
          dark: '#009473',
        },
        purple: {
          DEFAULT: '#6C5CE7',
          light: '#A29BFE',
          dark: '#5541D9',
        },
        coral: {
          DEFAULT: '#E17055',
          light: '#FAB1A0',
        },
        yellow: {
          DEFAULT: '#FDCB6E',
          dark: '#E5A716',
        },

        // Legacy aliases — maps old dark-theme tokens to cream equivalents
        // so existing components don't break during migration
        navy: {
          DEFAULT: '#FFF8F0',     // was dark bg → now cream
          light: '#F5EDE3',       // was card bg → now cream-dark
          lighter: '#EBE1D5',     // was border → now cream-darker
        },
        'off-white': '#2D3436',   // was light text → now charcoal (primary text)
        'grey-muted': '#636E72',  // was secondary text → now charcoal-light
        'grey-subtle': '#B2BEC3', // was sketch lines → now charcoal-lighter
        slate: {
          DEFAULT: '#636E72',     // was secondary text
          light: '#2D3436',       // was primary text → now charcoal
          dark: '#B2BEC3',        // was dim text → now lighter
        },
        teal: {
          DEFAULT: '#0984E3',     // map old teal accent → blue
          dark: '#0767B2',
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
        hand: [
          'Caveat',
          'cursive',
        ],
      },
      fontSize: {
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
