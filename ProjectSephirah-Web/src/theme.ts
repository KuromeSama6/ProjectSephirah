import { definePreset } from "@primevue/themes";
import Aura from "@primevue/themes/aura";

export const theme = definePreset(Aura, {
    semantic: {
        primary: {
            50: "{pink.50}",
            100: "{pink.100}",
            200: "{pink.200}",
            300: "{pink.300}",
            400: "{pink.400}",
            500: "{pink.500}",
            600: "{pink.600}",
            700: "{pink.700}",
            800: "{pink.800}",
            900: "{pink.900}",
            950: "{pink.950}",
        },
        colorScheme: {
            light: {
                primary: {
                    color: "{zinc.50}",
                    inverseColor: "#ffffff",
                    hoverColor: "{pink.400}",
                    activeColor: "{pink.400}",
                    borderColor: "{pink.400}",
                },
            },
            dark: {
                primary: {
                    color: "{zinc.50}",
                    inverseColor: "{zinc.950}",
                    hoverColor: "{zinc.100}",
                    activeColor: "{zinc.200}",
                },
                highlight: {
                    background: "rgba(250, 250, 250, .16)",
                    focusBackground: "rgba(250, 250, 250, .24)",
                    color: "rgba(255,255,255, .87)",
                    focusColor: "rgba(255,255,255, .87)",
                },
            },
        },
        menubar: {
            border: {
                radius: "5",
            },
        },
        content: {
            borderRadius: "{border.radius.md}",
        },
    },
    components: {
        menubar: {
            borderRadius: "{border.radius.none}",
        },
    },
});
