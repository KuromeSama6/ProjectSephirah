import { createPinia } from "pinia";
import PrimeVue from "primevue/config";
import { createApp } from "vue";
import App from "./App.vue";
import "./assets/css/base.css";
import "./assets/css/main.css";
import "./assets/css/tailwind.css";
import "./assets/scss/main.scss";
import i18n from "./i18n";
import router from "./router.ts";
import { theme } from "./theme";
import "../node_modules/flowbite-vue/dist/index.css";
import { ToastService, Tooltip } from "primevue";
import "lazysizes";

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);
app.use(router);

app.use(i18n);
app.use(PrimeVue, {
    theme: {
        preset: theme,
    },
    options: {
        cssLayer: {
            name: "primevue",
            order: "tailwind-base, primevue, tailwind-utilities",
        },
        darkModeSelector: ".app-mode-dark",
    },
});
app.use(ToastService);
app.directive("tooltip", Tooltip);

app.mount("#app");
