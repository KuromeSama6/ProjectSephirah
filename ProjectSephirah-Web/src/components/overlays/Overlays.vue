<script setup lang="ts">
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import { useSingletonsStore } from "../../stores/singletonsStore.ts";
import ChapterSelectDialog from "../manga/details/ChapterSelectDialog.vue";
import { useOverlaysStore } from "../../stores/overlaysStore.ts";
import SidebarMenuOverlay from "./SidebarMenuOverlay.vue";
import LegalTermsPopupOverlay from "./LegalTermsPopupOverlay.vue";

const singletons = useSingletonsStore();
const toast = useToast();
const overlays = useOverlaysStore();

singletons.toastSingleton = toast;
</script>
<template>
    <SidebarMenuOverlay />
    <LegalTermsPopupOverlay />
    <Toast position="bottom-right" />
</template>
