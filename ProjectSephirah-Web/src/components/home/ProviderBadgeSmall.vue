<script setup lang="ts">
import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { useMangaProviderStore } from "../../stores/mangaProviderStore.ts";

const selected = defineModel<boolean>();
const props = defineProps<{
    provider: MangaProvider
}>();
const providerStore = useMangaProviderStore();

</script>

<template>
    <ToggleButton v-model="selected" on-label="" off-label="" :disabled="!providerStore.ProviderAvailable(provider)">
        <template #icon>
            <Avatar :image="`/assets/images/providerIcons/${provider.id}.ico`" />
            <span class="text-red-500" v-if="provider.info.hentaiDedicated">[H]</span>
            <span>{{ provider.info.name }}</span>
        </template>
    </ToggleButton>
</template>

<style scoped>

</style>