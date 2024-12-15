import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import Components from "unplugin-vue-components/vite";
import VueI18nPlugin from "@intlify/unplugin-vue-i18n/vite";
import { PrimeVueResolver } from "@primevue/auto-import-resolver";

export default defineConfig({
    server: {
        port: 3000,
    },
    plugins: [
        vue(),
        Components({
            resolvers: [PrimeVueResolver()],
        }),
        VueI18nPlugin({
            include: "./src/locales/**",
            fullInstall: true,
            compositionOnly: true,
        }),
    ],
});
