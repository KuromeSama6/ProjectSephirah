<script setup lang="ts">
import { Provider } from "../../../backend/provider/Provider.ts";
import { MangaProvider } from "../../../backend/manga/MangaProvider.ts";

const props = defineProps<{
    provider: MangaProvider;
}>();
</script>

<template>
    <div class="flex gap-1 items-center">
        <img class="w-4 h-4" :src="`/assets/images/providerIcons/${provider.id}.ico`" :alt="provider.id" />
        <p class="text-sm">{{ provider.info.name }}</p>
    </div>
</template>

<style scoped>

</style>