<script setup lang="ts">
import Topnav from "./components/navbar/Topnav.vue";
import { onMounted } from "vue";
import { useMangaProviderStore } from "./stores/mangaProviderStore.ts";
import { useRoute } from "vue-router";
import Components from "unplugin-vue-components/vite";
import Overlays from "./components/common/overlay/Overlays.vue";

const providerStore = useMangaProviderStore();
const route = useRoute();

onMounted(() => {
    // providerStore.CheckProviders();
});
</script>

<template>
    <Overlays />
    <Topnav />
    <RouterView v-slot="{Component}">
        <component :is="Component" />
    </RouterView>
    <!--    <Topnav v-if="route.path == '/manga/read'" />-->
</template>

<style scoped>

</style>