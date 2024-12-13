/** @type {import("tailwindcss").Config} */
const colors = require("tailwindcss/colors");

export default {
    content: [
        "./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}",
        "node_modules/flowbite-vue/**/*.{js,jsx,ts,tsx,vue}",
        "node_modules/flowbite/**/*.{js,jsx,ts,tsx}",
    ],
    theme: {
        extend: {},
        colors: {
            transparent: "transparent",
            current: "currentColor",
            black: colors.black,
            white: colors.white,
            gray: colors.gray,
            emerald: colors.emerald,
            indigo: colors.indigo,
            yellow: colors.yellow,
            red: colors.red,

            primary: colors.pink,
        },
    },
    plugins: [
        require("flowbite/plugin"),
    ],
};
