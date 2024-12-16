<script setup lang="ts">
import { useOverlaysStore } from "../../../stores/overlaysStore.ts";
import MaterialIcon from "../../common/util/MaterialIcon.vue";
import SkeletonBlock from "../../common/util/SkeletonBlock.vue";
import { ChapterInfo, MangaChapters, MangaDetails } from "../../../backend/manga/Manga.ts";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useSettingsStore } from "../../../stores/settingsStore.ts";

const router = useRouter();
const props = defineProps<{
    chapters: MangaChapters | undefined;
    current?: string;
}>();
const settings = useSettingsStore();

const visible = defineModel<boolean>("visible");
const sortAscending = computed({
    get: () => settings.chapterSelectSortAscending,
    set: (val: boolean) => {
        settings.chapterSelectSortAscending = val;
        settings.Save();
        console.log(settings.$state);
    },
});

const emit = defineEmits<{
    (event: "select", chapter: ChapterInfo): void;
}>();

function StartReading(chap: ChapterInfo) {
    emit("select", chap);
}

</script>

<template>
    <Drawer v-model:visible="visible" header="Select Chapter" position="bottom" style="height: 80vh;">
        <template #header>
            <div class="flex items-center gap-2 w-full pr-2">
                <h1 class="text-2xl font-bold">Manga Details</h1>
                <Button severity="secondary" variant="text" class="" @click="sortAscending = !sortAscending">
                    <MaterialIcon icon="swap_vert" class="text-xl" />
                    {{ sortAscending ? "Ascending" : "Descending" }}
                </Button>
            </div>
        </template>

        <div class="flex flex-col gap-2 animate-pulse" v-if="!chapters">
            <div class="flex gap-2 w-full">
                <SkeletonBlock class="h-11 flex-auto" />
                <SkeletonBlock class="h-11 flex-auto" />
            </div>
            <div class="grid grid-cols-5 lg:grid-cols-10 gap-2">
                <SkeletonBlock class="h-11" v-for="_ of Array(30)" />
            </div>
        </div>
        <Tabs :value="0" class="mb-2" v-else scrollable>
            <TabList v-if="chapters?.groups.length! > 1">
                <Tab v-for="(group, i) of chapters?.groups" :value="i">{{ group.name || (i == 0 ? "Default" : `Group ${i}`) }} ({{ group.count }})</Tab>
            </TabList>
            <TabPanels>
                <TabPanel v-for="(group, i) of chapters?.groups" :value="i">
                    <div class="flex flex-col gap-2">
                        <div class="flex gap-2 w-full">
                            <Button class="flex-auto" @click="StartReading(group.chapters[group.chapters.length - 1])">
                                <MaterialIcon icon="history" />
                                Latest (#{{ group.count }})
                            </Button>
                            <Button class="flex-auto" severity="secondary" v-if="group.count > 1" @click="StartReading(group.chapters[0])">
                                <MaterialIcon icon="first_page" />
                                First
                            </Button>
                        </div>
                        <div class="grid grid-cols-5 lg:grid-cols-10 gap-2">
                            <Button :severity="chap.id == current ? 'primary' : 'secondary'" v-for="chap in sortAscending ? group.chapters : group.chapters.slice().reverse()" :disabled="chap.id == current" outlined @click="StartReading(chap)">
                                <div v-if="chap.title && chap.index != undefined" class="text-md flex flex-col">
                                    <span class="text-gray-400 text-xs"> #{{ chap.index + 1 }} </span>
                                    <span> {{ chap.title }} </span>
                                </div>
                                <div v-else>
                                    <span> {{ chap.index }} </span>
                                </div>
                            </Button>
                        </div>
                    </div>
                </TabPanel>
            </TabPanels>
        </Tabs>
    </Drawer>
</template>

<style scoped>

</style>