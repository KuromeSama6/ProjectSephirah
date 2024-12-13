<script setup lang="ts">
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import { useSingletonsStore } from "../../../stores/singletonsStore";
import ChapterSelectDialog from "../../manga/details/ChapterSelectDialog.vue";
import { useOverlaysStore } from "../../../stores/overlaysStore.ts";

const singletons = useSingletonsStore();
const toast = useToast();
const overlays = useOverlaysStore();

singletons.toastSingleton = toast;
</script>
<template>
    <ChapterSelectDialog />
    <Toast position="bottom-right" />
</template>
