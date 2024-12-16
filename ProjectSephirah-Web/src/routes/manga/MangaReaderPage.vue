<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { useMangaProviderStore } from "../../stores/mangaProviderStore.ts";
import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { computed, onMounted, onUnmounted, ref } from "vue";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import { ChapterDetails, ChapterInfo, MangaChapters, MangaDetails } from "../../backend/manga/Manga.ts";
import { useMangaReaderStore } from "../../stores/mangaReaderStore.ts";
import MangaReaderToolbar from "../../components/manga/reader/MangaReaderToolbar.vue";
import MangaProviderBadge from "../../components/common/provider/MangaProviderBadge.vue";
import { FwbSpinner } from "flowbite-vue";
import ChapterSelectDialog from "../../components/manga/details/ChapterSelectDialog.vue";
import { useToast } from "primevue/usetoast";
import MockRouteWrapper from "../../components/common/util/MockRouteWrapper.vue";
import MangaDetailsPage from "./MangaDetailsPage.vue";
import MaterialIcon from "../../components/common/util/MaterialIcon.vue";

const router = useRouter();
const route = useRoute();
const providersStore = useMangaProviderStore();
const mangaStore = useMangaReaderStore();
const toast = useToast();

const provider: MangaProvider = providersStore.GetProvider(decodeURI(route.params.provider as string));
const mangaId = decodeURI(route.params.mangaId as string);
const chapterId = route.params.chapterId as string;
const language = route.query.lang as SupportedLanguage;

const chapter = ref<ChapterDetails>();
const loaded = ref(false);
const error = ref(false);
const images = ref<string[]>();
const hideTopbar = ref(false);
const loadedImages = ref(0);

const chapterSelectVisible = ref(false);
const mangaDetailsVisible = ref(false);
const chapterData = ref<MangaChapters>();

let lastScrollPosition = window.scrollY;

function GetScrollPercentage() {
    const scrollTop = window.scrollY;
    const scrollHeight = document.documentElement.scrollHeight;
    const clientHeight = document.documentElement.clientHeight;

    // Calculate scroll percentage
    return scrollTop / (scrollHeight - clientHeight);
}

function HandleScroll() {
    const currentScrollPosition = GetScrollPercentage();
    if (currentScrollPosition === 0 || currentScrollPosition >= 1) {
        // Show the toolbar if the page is at the top
        hideTopbar.value = false;
    } else if (currentScrollPosition > lastScrollPosition) {
        // Hide the toolbar if scrolling down
        hideTopbar.value = true;
    } else if (currentScrollPosition < lastScrollPosition) {
        // Show the toolbar if scrolling up
        hideTopbar.value = false;
    }

    lastScrollPosition = currentScrollPosition;
}

function ToggleList() {
    chapterSelectVisible.value = !chapterSelectVisible.value;
    if (chapterSelectVisible.value && !chapterData.value) {
        LoadChaptersData();
    }
}

function ReadPrev() {
    if (!chapter.value?.prevChapter) return;
    router.push(`/${provider.id}/manga/${mangaId}/read/${chapter.value.prevChapter}?lang=${language}`);
    setTimeout(() => router.go(0), 100);
}

function ReadNext() {
    if (!chapter.value?.nextChapter) return;
    router.push(`/${provider.id}/manga/${mangaId}/read/${chapter.value.nextChapter}?lang=${language}`);
    setTimeout(() => router.go(0), 100);
}

function JumpToChapter(chap: ChapterInfo) {
    router.push(`/${provider.id}/manga/${mangaId}/read/${chap.id}?lang=${language}`);
    setTimeout(() => router.go(0), 100);
}

async function LoadChaptersData() {
    var manga = await provider.GetMangaDetails(mangaId, language);
    if (manga == null) {
        chapterSelectVisible.value = true;
        toast.add({ severity: "error", summary: "Error", detail: "Failed to load chapters", closable: false, life: 3000 });
        return;
    }

    chapterData.value = await manga.chapters();

    if (chapterData.value == null) {
        chapterSelectVisible.value = true;
        toast.add({ severity: "error", summary: "Error", detail: "Failed to load chapters", closable: false, life: 3000 });
    }
}

async function Load() {
    mangaStore.current.chapter = undefined;

    // load details
    const chapterDetails = await provider.GetChapterDetails(mangaId, chapterId, language);
    if (chapterDetails == null) {
        error.value = true;
        return;
    }
    chapter.value = chapterDetails;
    mangaStore.current.chapter = chapterDetails;

    // images
    const links = await chapterDetails.images();
    if (links == null) {
        error.value = true;
        return;
    }

    loaded.value = true;
    images.value = links!;
}

onMounted(() => {
    Load();

    window.addEventListener("scroll", HandleScroll);
});

onUnmounted(() => {
    window.removeEventListener("scroll", HandleScroll);
});

</script>

<template>

    <div class="fixed w-full toolbar transition-transform duration-200 ease-in" :class="[{ 'topbar-collapsed': hideTopbar }]">
        <div class="flex w-full p-2 gap-1 items-center bg-pink-500 text-white min-h-8 backdrop-blur bg-opacity-80">
            <div class="text-lg flex gap-1 items-center w-full" v-if="chapter">
                <b>{{ chapter.manga.title }}</b>
                &middot;
                <span class="text-sm">{{ chapter.title || `#${chapter.index}` }}</span>
                <MangaProviderBadge :provider="chapter.provider" class="ml-auto" />
            </div>
            <div v-else-if="error" class="flex items-center gap-1">
                <MaterialIcon icon="error" />
                Load Failed
            </div>
            <FwbSpinner color="white" size="6" v-else />
        </div>
        <div class="flex w-full p-0.5 gap-1 items-center bg-yellow-300 text-white backdrop-blur bg-opacity-80" v-if="chapter?.imageCacheLength!">
            <div class="text-xs flex gap-1 items-center w-full" v-if="chapter">
                <MaterialIcon class="text-xs" icon="error" />
                Images from this provider are cached for up to {{ Math.round(chapter.imageCacheLength / 86400) }} days and may not be the most up-to-date.
            </div>
        </div>
    </div>
    <div v-if="loaded && !error" class="justify-center flex">
        <div @click="hideTopbar = !hideTopbar" class="flex flex-col w-full lg:w-[45vw] md:w-[60vw] xl:w-[35vw]">
            <p class="opacity-0">a</p>
            <img v-for="src of images" :data-src="src" class="lazyload" @load="++loadedImages">
        </div>
    </div>
    <div v-if="error" class="flex w-full h-[100vh] items-center justify-center">
        <div class="border rounded p-4 border-red-500 mx-4 flex flex-col gap-2">
            <h1 class="text-xl font-bold text-red-500">There was an error.</h1>
            <p>
                Failed to load chapter. There may be more information in the console.
            </p>
            <div class="flex gap-2">
                <Button severity="secondary" @click="router.go(0)">
                    <MaterialIcon icon="refresh" />
                    Retry
                </Button>
            </div>
        </div>
    </div>
    <div class="fixed flex flex-col w-full bottom-0 z-10 backdrop-blur backdrop-blur bg-opacity-80 bg-gray-900 h-50 transition-transform duration-200 ease-in" :class="[{ 'bottombar-collapsed': hideTopbar }]">
        <div class="flex justify-center gap-2 w-full mt-2" v-if="GetScrollPercentage() >= 1 && loadedImages < images!.length">
            <FwbSpinner color="white" size="6" />
            <p>Loading more images</p>
        </div>
        <MangaReaderToolbar @toggleList="ToggleList" @toggleDetails="mangaDetailsVisible = !mangaDetailsVisible" @prev="ReadPrev" @next="ReadNext" :disable-prev="!chapter?.prevChapter" :disable-next="!chapter?.nextChapter" />
    </div>

    <ChapterSelectDialog :chapters="chapterData" v-model:visible="chapterSelectVisible" @select="JumpToChapter" :current="chapter?.id" />
    <Drawer position="bottom" v-model:visible="mangaDetailsVisible" header="Manga Details" style="height: 80vh;">
        <template #header>
            <div class="flex items-center gap-2 w-full pr-2">
                <h1 class="text-2xl font-bold">Manga Details</h1>
                <Button severity="secondary" variant="text" class="ms-auto" @click="router.push(`/${provider.id}/manga/${mangaId}/details?lang=${language}`)">
                    <MaterialIcon icon="open_in_full" class="text-xl" />
                </Button>
            </div>
        </template>
        <MockRouteWrapper :route="{
            name: 'MangaReader',
            params: {
                provider: provider.id,
                mangaId: mangaId
            },
            query: {
                lang: language
            }
        }">
            <MangaDetailsPage />
        </MockRouteWrapper>
    </Drawer>
</template>

<style scoped>
.topbar-collapsed {
    transform: translateY(-100%);
}

.bottombar-collapsed {
    transform: translateY(100%);
}


</style>