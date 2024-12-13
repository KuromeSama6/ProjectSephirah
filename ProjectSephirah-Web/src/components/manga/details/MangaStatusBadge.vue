<script setup lang="ts">
import { MangaDetails, MangaInfo, MangaStatus } from "../../../backend/manga/Manga.ts";
import { ChapterInfo } from "../../../backend/manga/Manga.ts";
import MaterialIcon from "../../common/util/MaterialIcon.vue";
import { computed } from "vue";

const props = defineProps<{
    manga: MangaDetails;
}>();


</script>

<template>

    <div class="flex gap-1 items-center">
        <MaterialIcon icon="trip_origin" class="text-sm" :class="{
            'text-blue-400': manga.status == MangaStatus.UPDATING,
            'text-green-400': manga.status == MangaStatus.FINISHED,
            'text-pink-400': manga.status == MangaStatus.SINGLE,
            'text-red-500': manga.status == MangaStatus.TERMINATED,
            'text-red-700': manga.status == MangaStatus.DMCA_TAKEDOWN,
            'text-gray-500': manga.status == MangaStatus.UNKNOWN
        }" />
        <div>
            <h1 class="text-sm" v-if="manga.status == MangaStatus.UPDATING">Ongoing</h1>
            <h1 class="text-sm" v-if="manga.status == MangaStatus.FINISHED">Complete</h1>
            <h1 class="text-sm" v-if="manga.status == MangaStatus.SINGLE">Single Chapter</h1>
            <h1 class="text-sm" v-if="manga.status == MangaStatus.TERMINATED">Terminated</h1>
            <h1 class="text-sm" v-if="manga.status == MangaStatus.UNKNOWN">Status Unknown</h1>
            <h1 class="text-sm text-red-500" v-if="manga.status == MangaStatus.DMCA_TAKEDOWN">DMCA Takedown</h1>
        </div>
        <p class="font-extrabold">&middot;</p>
        <p v-if="typeof manga.latestChapter == 'string'">{{ manga.latestChapter }}</p>
        <p v-else>#{{ (manga.latestChapter as ChapterInfo).index }}</p>
        <p class="font-extrabold">&middot;</p>
        <p>{{ manga.latestUpdate.toLocaleDateString() }}</p>
    </div>
</template>

<style scoped>

</style>