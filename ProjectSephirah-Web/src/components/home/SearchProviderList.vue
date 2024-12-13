<script setup lang="ts">
import ProviderBadgeSmall from "./ProviderBadgeSmall.vue";
import { useMangaProviderStore } from "../../stores/mangaProviderStore.ts";
import MangaProviderBadge from "../common/provider/MangaProviderBadge.vue";

const mangaProviderStore = useMangaProviderStore();
</script>

<template>
    <AccordionHeader>
        <!--                <TextDivider>-->
        <!--                    <div class="text-center w-full">-->
        <!--                        Manga Providers-->
        <!--                    </div>-->
        <!--                    <button>-->
        <!--                        <MaterialIcon icon="expand_circle_down" />-->
        <!--                    </button>-->
        <!--                </TextDivider>-->
        Manga Providers ({{ mangaProviderStore.providers.size }})
    </AccordionHeader>
    <AccordionContent>
        <!--        <div class="grid grid-cols-3 gap-2 lg:grid-cols-4 mb-2">-->
        <!--            <div v-for="provider of mangaProviderStore.providers.values()">-->
        <!--                <ProviderBadgeSmall :provider="provider" class="w-full" />-->
        <!--            </div>-->
        <!--        </div>-->
        <!--        <p class="text-sm text-center">Providers prefixed with <span class="text-red-500">[H]</span> serves mainly Ecchi/NSFW content.</p>-->
        <div class="flex flex-col">
            <MangaProviderBadge v-for="provider of mangaProviderStore.providers.values()" :provider="provider" class="text-gray-300" />
        </div>
    </AccordionContent>
</template>

<style scoped>

</style>