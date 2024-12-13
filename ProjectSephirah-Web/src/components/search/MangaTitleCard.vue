<script setup lang="ts">
import { MangaInfo } from "../../backend/manga/Manga.ts";
import MangaProviderBadge from "../common/provider/MangaProviderBadge.vue";
import MaterialIcon from "../common/util/MaterialIcon.vue";
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useSettingsStore } from "../../stores/settingsStore.ts";
import { set } from "js-cookie";

const props = defineProps<{
    manga: MangaInfo;
    directMatch?: boolean;
    nsfwFilter?: boolean;
}>();
const router = useRouter();
const settings = useSettingsStore();

const isEcchi = props.manga.provider.info.hentaiDedicated;
const applyNsfwFilter = computed(() => props.nsfwFilter && isEcchi);

</script>

<template>
    <div class="flex items-center border rounded-lg shadow flex-row border-gray-700 relative">
        <!--        H Warning -->
        <div class="bg-red-700 absolute top-1 left-1 z-10 rounded-sm px-0.5 flex text-sm items-center bg-opacity-90 text-white" v-if="isEcchi">
            <MaterialIcon icon="warning" class="text-sm" />
            H
        </div>
        <img class="object-cover rounded-t-lg h-full w-16 lg:w-32 rounded-none rounded-s-lg" :class="{
            'grayscale': applyNsfwFilter,
            'blur-[2px]': applyNsfwFilter,
            'contrast-[5]': applyNsfwFilter,
            'brightness-[1.2]': applyNsfwFilter,
        }" :src="manga.coverUrl" alt="">
        <div class="flex flex-col justify-between pl-2 leading-normal">
            <div class="flex gap-1">
                <MangaProviderBadge :provider="manga.provider" />
                <span v-if="directMatch" class="font-bold text-blue-500">&middot; Direct Match</span>
            </div>
            <h5 class="lg:text-2xl text-lg font-bold tracking-tight text-white">{{ manga.title }}</h5>
            <p class="font-normal text-gray-400">{{ manga.author }}</p>
        </div>
        <div class="flex flex-col ml-auto px-2 gap-2">
            <Button outlined class="h-7 lg:h-auto">
                <MaterialIcon icon="import_contacts" />
                Read
            </Button>
            <Button outlined class="h-7 lg:h-auto" severity="info" @click="router.push(`/${manga.provider.id}/manga/${manga.id}/details?lang=${settings.searchLangauge}`)">
                <MaterialIcon icon="more_horiz" />
                Details
            </Button>
        </div>
    </div>
</template>

<style scoped>

</style>