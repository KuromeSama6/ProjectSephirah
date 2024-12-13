import { defineStore } from "pinia";
import { ConfirmationOptions } from "primevue/confirmationoptions";
import { ToastServiceMethods } from "primevue/toastservice";

interface State {
    toastSingleton?: ToastServiceMethods;
    confirmSingleton?: {
        require: (option: ConfirmationOptions) => void;
        close: () => void;
    };
}

export const useSingletonsStore = defineStore("singletonsStore", {
    state: (): State => ({}),
});
