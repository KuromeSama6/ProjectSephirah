<script setup lang="ts">
import { MangaInfo } from "../../backend/manga/Manga.ts";
import { FuzzySearchUtil } from "../../backend/util/FuzzySearchUtil.ts";
import SearchHFilterWarning from "./SearchHFilterWarning.vue";
import MangaTitleCard from "./MangaTitleCard.vue";
import { computed } from "vue";

const props = defineProps<{
    kw: string;
    list: MangaInfo[];
    displayPredicate?: (manga: MangaInfo) => boolean;
}>();

const coverFilteredTitles = computed(() => {
    return props.list.filter(c => c.provider.info.hentaiDedicated);
});

const emit = defineEmits<{
    (event: "read", manga: MangaInfo): void;
}>();

</script>

<template>
    <SearchHFilterWarning v-if="coverFilteredTitles.length > 0" />
    <div class="flex flex-col gap-2">
        <div v-for="manga of list">
            <MangaTitleCard :manga="manga" :direct-match="FuzzySearchUtil.NormalizeString(manga.title) == kw" :nsfw-filter="true" v-if="!displayPredicate || displayPredicate(manga)" @click="emit('read', manga)" />
        </div>
    </div>
</template>

<style scoped>

</style>