<script setup lang="ts">
import MaterialIcon from "../components/common/util/MaterialIcon.vue";
import SearchProviderList from "../components/home/SearchProviderList.vue";
import HomeOptionsDropdown from "../components/home/HomeOptionsDropdown.vue";
import HomeMangaProviderStatusCheck from "../components/home/MangaProviderStatusCheck.vue";
import { useRoute, useRouter } from "vue-router";
import { computed, onMounted, ref } from "vue";
import SearchBarMain from "../components/common/search/SearchBarMain.vue";
import { ProviderStatus } from "../backend/provider/Provider.ts";
import { FwbSpinner } from "flowbite-vue";
import { useMangaProviderStore } from "../stores/mangaProviderStore.ts";
import { MangaDetails, MangaInfo } from "../backend/manga/Manga.ts";
import { MangaProvider } from "../backend/manga/MangaProvider.ts";
import MangaTitleCard from "../components/search/MangaTitleCard.vue";
import ProviderBadgeSmall from "../components/home/ProviderBadgeSmall.vue";
import MangaProviderBadge from "../components/common/provider/MangaProviderBadge.vue";
import Fuse from "fuse.js";
import { FuzzySearchUtil } from "../backend/util/FuzzySearchUtil.ts";
import { sify } from "chinese-conv";
import SearchHFilterWarning from "../components/search/SearchHFilterWarning.vue";
import SearchMangaList from "../components/search/SearchMangaList.vue";
import { useSettingsStore } from "../stores/settingsStore.ts";
import { SupportedLanguage } from "../backend/common/Language.ts";

const router = useRouter();
const route = useRoute();
const searchText = ref<string>(decodeURI(route.query.kw as string));
var kw = decodeURI(route.query.kw as string);
const providerStore = useMangaProviderStore();
const settings = useSettingsStore();

const expandedAccordions = ref<string[]>([]);

// Search

interface SearchResult {
    provider: MangaProvider;
    results?: MangaInfo[];
    error: boolean;
    warningText?: string;
    icon: string;
    colorClass?: string;
}

const results = ref<Record<string, SearchResult>>({});
const titles = computed((): MangaInfo[] => Object.values(results.value)
    .filter(c => c.results)
    .flatMap(c => c.results!));
const searchCompleted = computed(() => Object.keys(results.value).length >= providerStore.providers.size);

const resultlessProviders = computed(() => Object.values(results.value)
    .filter(c => !c.error && !c.results));
const providerErrors = computed(() => Object.values(results.value)
    .filter(c => c.error));

function BeginSearch() {
    const searchLanguage = settings.searchLangauge;
    kw = searchText.value;
    if (kw === "") return;

    if ((route.query.kw as string) !== searchText.value) {
        router.push({ query: { kw: searchText.value } });
    }

    expandedAccordions.value = [];
    results.value = {};

    for (let provider of providerStore.providers.values()) {
        if (!provider.info.supportedSearchLanguages.includes(searchLanguage)) {
            results.value[provider.id] = ({
                provider: provider,
                error: false,
                icon: "subtitles_off",
                warningText: "Language Not Supported",
            });
            expandedAccordions.value.push("1");
            continue;
        }

        SearchSingle(provider, searchLanguage);
    }
}

async function SearchSingle(provider: MangaProvider, language: SupportedLanguage) {
    try {
        const res = await provider.Search(kw, language);

        if (res == null) {
            results.value[provider.id] = ({
                provider: provider,
                error: true,
                icon: "error",
                colorClass: "text-red-500",
                warningText: "Request Error",
            });
            return;
        }

        if (res.length > 0) {
            results.value[provider.id] = ({
                provider: provider,
                results: res,
                error: false,
                icon: "check",
                colorClass: "text-green-500",
            });
            expandedAccordions.value.push("0");
            expandedAccordions.value.push("4");

        } else {
            results.value[provider.id] = ({
                provider: provider,
                error: false,
                icon: "radio_button_unchecked",
                warningText: "No Results",
                colorClass: "text-gray-500",
            });
            expandedAccordions.value.push("1");
        }

    } catch (error: any) {
        results.value[provider.id] = ({
            provider: provider,
            error: true,
            icon: "error",
            colorClass: "text-red-500",
            warningText: "Network Error",
        });
        expandedAccordions.value.push("2");
    }

}

const orderedSearchResults = computed<MangaInfo[]>(() => {
    // fuzzy sort
    const fuse = new Fuse(titles.value, {
        keys: [
            {
                name: "title",
                getFn: (obj: { title: string }) => sify(obj.title),
            },
        ],
        threshold: 0.3,
        includeScore: true,
    });

    const ret = fuse.search(kw);
    return ret.map(c => c.item);
});


onMounted(async () => {
    BeginSearch();
});

</script>

<template>
    <div class="flex-auto lg:flex-none lg:w-2/4 pt-2 mx-auto">
        <div class="flex flex-col items-center my-2 mx-5 gap-2">
            <SearchBarMain v-model="searchText" @search="BeginSearch()" />
            <div class="w-full">
                <div v-if="!searchCompleted" class="flex">
                    <!--                Search Progress -->
                    <div class="flex gap-2">
                        <FwbSpinner color="white" size="5" />
                        <p>Search in progress</p>
                    </div>
                    <div class="ml-auto">
                        <p>
                            {{ Object.keys(results).length }} / {{ providerStore.providerStatus.size }}
                        </p>
                    </div>
                </div>
                <div class="flex gap-1 text-yellow-300" v-if="searchCompleted && titles.length == 0">
                    <MaterialIcon icon="warning" />
                    <p>
                        Search gave no relevant results.
                    </p>
                </div>
            </div>
            <div class="w-full">
                <Accordion multiple :value="expandedAccordions">
                    <AccordionPanel value="0">
                        <AccordionHeader>
                            <MaterialIcon icon="target" />
                            Direct Hits ({{ orderedSearchResults.length }})
                        </AccordionHeader>
                        <AccordionContent>
                            <div class="flex gap-1 text-xs lg:text-sm mb-2 text-gray-500 items-center">
                                <MaterialIcon icon="info" />
                                <p>
                                    Direct hits are results that are most likely to be relevant to your search and your preferred reading language. Check the "Potentially Relevant" section if the title you are looking for is not here.
                                    <RouterLink to="" class="text-pink-400 underline">Change reading language</RouterLink>
                                </p>
                            </div>
                            <SearchMangaList :kw="kw" :list="orderedSearchResults" />
                        </AccordionContent>
                    </AccordionPanel>
                    <AccordionPanel value="4">
                        <AccordionHeader>
                            <MaterialIcon icon="radio_button_checked" />
                            Potentially Relevant ({{ titles.length - orderedSearchResults.length }})
                        </AccordionHeader>
                        <AccordionContent>
                            <div class="flex gap-1 text-xs lg:text-sm mb-2 text-gray-500 items-center">
                                <MaterialIcon icon="info" />
                                <p>
                                    These titles may be relevant to your search but are not as likely to be as the "Direct Hits" section. These titles may have been suggested to you by the provider based on your search query.
                                </p>
                            </div>
                            <SearchMangaList :kw="kw" :list="titles" :display-predicate="manga => !orderedSearchResults.includes(manga)" />
                        </AccordionContent>
                    </AccordionPanel>
                    <AccordionPanel value="1">
                        <AccordionHeader>
                            <MaterialIcon icon="radio_button_unchecked" />
                            Resultless Providers ({{ resultlessProviders.length }})
                        </AccordionHeader>
                        <AccordionContent>
                            <div class="flex gap-1 text-xs lg:text-sm mb-2 text-gray-500 items-center">
                                <MaterialIcon icon="info" />
                                These providers did not return any results from your search.
                            </div>
                            <div class="flex flex-col gap-2">
                                <div v-for="result of resultlessProviders">
                                    <div class="flex gap-1">
                                        <MangaProviderBadge :provider="result.provider" />
                                        <div class="ml-auto flex gap-1" :class="[
                                            result.colorClass || 'text-yellow-300'
                                        ]">
                                            <MaterialIcon :icon="result.icon" />
                                            <p>{{ result.warningText }}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </AccordionContent>
                    </AccordionPanel>
                    <AccordionPanel value="2">
                        <AccordionHeader>
                            <MaterialIcon icon="error" />
                            Provider Errors ({{ providerErrors.length }})
                        </AccordionHeader>
                        <AccordionContent>
                            <div class="flex gap-1 text-xs lg:text-sm mb-2 text-gray-500 items-center">
                                <MaterialIcon icon="info" />
                                These providers are either unavailable or encountered an error while searching for your query. Providers marked as "unavailable" may be refreshed by refreshing the page.
                            </div>
                            <div class="flex flex-col gap-2">
                                <div v-for="result of providerErrors">
                                    <div class="flex gap-1">
                                        <MangaProviderBadge :provider="result.provider" />
                                        <div class="ml-auto flex gap-1" :class="[
                                            result.colorClass || 'text-red-500'
                                        ]">
                                            <MaterialIcon :icon="result.icon" />
                                            <p>{{ result.warningText }}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </AccordionContent>
                    </AccordionPanel>
                </Accordion>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>