<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { useMangaProviderStore } from "../../stores/mangaProviderStore.ts";
import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterInfo, MangaChapters, MangaDetails, MangaStatus } from "../../backend/manga/Manga.ts";
import SkeletonBlock from "../../components/common/util/SkeletonBlock.vue";
import { computed, inject, onMounted, ref } from "vue";
import { useToast } from "primevue/usetoast";
import MangaProviderBadge from "../../components/common/provider/MangaProviderBadge.vue";
import MaterialIcon from "../../components/common/util/MaterialIcon.vue";
import MangaStatusBadge from "../../components/manga/details/MangaStatusBadge.vue";
import { useOverlaysStore } from "../../stores/overlaysStore.ts";
import ChapterSelectDialog from "../../components/manga/details/ChapterSelectDialog.vue";
import { SupportedLanguage } from "../../backend/common/Language.ts";

const providerStore = useMangaProviderStore();
const route = inject("mockRoute") as any || useRoute();
const router = useRouter();
const toast = useToast();
const overlays = useOverlaysStore();

const provider: MangaProvider = providerStore.GetProvider(decodeURI(route.params.provider as string));
const mangaId = decodeURI(route.params.mangaId as string);
const language = route.query.lang as SupportedLanguage;

const loading = ref(true);
const manga = ref<MangaDetails>();
const chapters = ref<MangaChapters>();
const error = ref(false);
const chapterSelectVisible = ref(false);

const totalChapters = computed(() => chapters?.value?.groups.map(c => c.count).reduce((a, c) => a + c, 0));
const isSingleChapter = computed(() => totalChapters.value == 1);

async function LoadMangaDetails() {
    try {
        const res = await provider.GetMangaDetails(mangaId, language);
        if (!res) {
            toast.add({ severity: "error", summary: "Load failed", detail: "Failed to load manga details. Reload page to retry.", closable: false, life: 5000 });
            error.value = true;
            return;
        }

        manga.value = res;
        loading.value = false;
    } catch (err: any) {
        error.value = true;
        toast.add({ severity: "error", summary: `Request Error: ${err}`, detail: "The provider may be unavailable. Check console for more details.", closable: false, life: 5000 });
    }
}

function SelectChapter() {
    if (!manga.value) return;
    chapterSelectVisible.value = true;
}

function ReadLatest() {
    if (!chapters.value) return;
    StartReading(chapters.value.groups[0].chapters.slice().reverse()[0]);
    if (route.name == "MangaReader") setTimeout(() => router.go(0), 1);
}

function StartReading(chap: ChapterInfo) {
    router.push(`/${provider.id}/manga/${mangaId}/read/${chap.id}?lang=${language}`);
    if (route.name == "MangaReader") setTimeout(() => router.go(0), 1);
}

onMounted(async () => {
    if (!provider || !mangaId) {
        toast.add({ severity: "error", summary: "Error", detail: "Invalid provider or manga id.", closable: false, life: 5000 });
        return;
    }

    await LoadMangaDetails();
    if (!manga.value) return;

    // load chapters
    chapters.value = await manga.value.chapters();
});

</script>

<template>
    <div class="flex-auto lg:flex-none lg:w-3/4 pt-2 mx-auto">
        <div v-if="error" class="flex items-center justify-center text-red-500 gap-1 text-md border-red-500 border rounded-md p-2 animate-none my-2 mx-5">
            <MaterialIcon icon="error" class="text-xl" />
            There was a problem loading this title. Refresh the page to try again.
        </div>
        <div class="flex flex-col items-center my-2 mx-5 gap-2 animate-pulse" v-if="loading">
            <div class="flex w-full gap-2">
                <SkeletonBlock class="h-64 w-48" />
                <div class="flex flex-col gap-1 flex-auto">
                    <SkeletonBlock class="h-3" />
                    <SkeletonBlock class="h-10" />
                    <SkeletonBlock class="h-5" />
                    <SkeletonBlock class="h-5" />
                    <SkeletonBlock class="h-5" />
                    <SkeletonBlock class="h-5" />
                    <SkeletonBlock class="h-5" />
                </div>
            </div>
            <div class="flex gap-2 w-full mt-2">
                <SkeletonBlock class="flex-auto h-11" />
            </div>
        </div>
        <div class="flex flex-col items-center my-2 mx-5 gap-2" v-if="!loading && manga">
            <div class="flex w-full gap-2 relative fade-in">
                <img class="h-48 w-36 lg:h-64 lg:w-48 object-cover rounded relative" :src="manga.coverUrl">
                <!--                image badges-->
                <div class="absolute top-1 left-1 flex flex-col gap-1">
                    <div class="flex gap-1 bg-opacity-70 bg-black p-0.5">
                        <MangaProviderBadge :provider="provider" />
                    </div>
                    <div class="bg-red-700 rounded-sm px-0.5 flex text-md items-center text-white w-max bg-opacity-90" v-if="provider.info.hentaiDedicated">
                        <MaterialIcon icon="warning" class="text-md" />
                        H
                    </div>
                </div>
                <div class="flex flex-col gap-1 flex-auto">
                    <MangaStatusBadge :manga="manga" />
                    <h3 class="text-2xl font-bold">{{ manga.title }}</h3>
                    <p>{{ manga.author }}</p>
                    <div class="text-white text-xs max-h-full">{{ manga.description }}</div>
                </div>

            </div>

            <div v-if="manga.status == MangaStatus.DMCA_TAKEDOWN" class="mt-2 flex items-center justify-center w-full text-red-500 gap-1 text-md border-red-500 border rounded-md p-2">
                <MaterialIcon icon="block" class="text-xl font-bold" />
                This title is not available on this provider due to DMCA takedown and/or ongoing legal issues. Please check other providers.
            </div>
            <div class="flex gap-2 w-full mt-2" v-else>
                <Button class="flex-auto" severity="primary" @click="ReadLatest" label="Read" v-if="isSingleChapter">
                    <template #icon>
                        <MaterialIcon icon="menu_book" />
                    </template>
                </Button>
                <Button class="flex-auto" severity="primary" @click="ReadLatest" label="Read Latest" v-if="!isSingleChapter">
                    <template #icon>
                        <MaterialIcon icon="history" />
                    </template>
                </Button>
                <Button class="flex-auto" severity="secondary" @click="SelectChapter" :label="`Select Chapter`" v-if="!isSingleChapter">
                    <template #icon>
                        <MaterialIcon icon="menu_book" />
                    </template>
                </Button>
            </div>
        </div>
    </div>

    <!--    Overlays-->
    <ChapterSelectDialog v-model:visible="chapterSelectVisible" :chapters="chapters" @select="StartReading" />
</template>

<style scoped>
.fade-in {
    animation: fadeIn .2s;
}

@keyframes fadeIn {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
</style>