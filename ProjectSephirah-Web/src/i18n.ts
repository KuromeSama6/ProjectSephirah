import {createI18n} from "vue-i18n";
import en from "./locales/en.json";
import sc from "./locales/sc.json";

const i18n = createI18n({
    locale: "en",
    messages: {
        en,
        sc
    }
});

export default i18n;