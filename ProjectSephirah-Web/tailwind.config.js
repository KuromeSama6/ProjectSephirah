/** @type {import("tailwindcss").Config} */
import colors from "tailwindcss/colors";
import flowbitePlugin from "flowbite/plugin";

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
        flowbitePlugin,
    ],
};
