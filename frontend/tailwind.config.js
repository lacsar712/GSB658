/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: '#4F46E5',  // 现代化主调Indigo
                secondary: '#10B981', // 薄荷绿作为强调
                surface: '#F8FAFC',
            }
        },
    },
    plugins: [],
}
