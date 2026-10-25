/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#0E8388', // Roamly Deep Teal
          dark: '#2DD4BF',
        },
        secondary: {
          DEFAULT: '#008080', // Roamly Teal Accent
          dark: '#5EEAD4',
        },
        mint: {
          DEFAULT: '#E6F4F1',
          dark: '#14383B',
        },
        navy: {
          DEFAULT: '#0B1928',
          card: '#152332',
        },
        background: {
          light: '#F4F8FA',
          dark: '#0B1928',
        },
        surface: {
          light: '#FFFFFF',
          dark: '#152332',
          variant: '#1B2A38',
        }
      },
      fontFamily: {
        serif: ['Outfit', 'Plus Jakarta Sans', 'Inter', 'serif'],
        sans: ['Plus Jakarta Sans', 'Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
  darkMode: 'class',
}
