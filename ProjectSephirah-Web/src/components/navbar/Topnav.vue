<script setup lang="ts">
import { RouterLink, useRoute } from "vue-router";
import MaterialIcon from "../common/util/MaterialIcon.vue";
import { useI18n } from "vue-i18n";
import { useMangaReaderStore } from "../../stores/mangaReaderStore.ts";
import { computed } from "vue";
import { FwbSpinner } from "flowbite-vue";
import MangaProviderBadge from "../common/provider/MangaProviderBadge.vue";
import { useOverlaysStore } from "../../stores/overlaysStore.ts";

const t = useI18n().t;
const route = useRoute();
const mangaReader = useMangaReaderStore();
const overlays = useOverlaysStore();

const currentChapter = computed(() => mangaReader.current.chapter);

</script>

<template>
    <!--    <div class="flex gap-2 py-0.5 px-2 bg-primary-500 text-white" v-if="route.path == '/manga/read'">-->
    <!--        <div class="text-lg flex gap-1 items-center w-full" v-if="currentChapter">-->
    <!--            <b>{{ currentChapter.manga.title }}</b>-->
    <!--            &middot;-->
    <!--            <span class="text-sm">{{ currentChapter.title || `#${currentChapter.index}` }}</span>-->
    <!--            <MangaProviderBadge :provider="currentChapter.provider" class="ml-auto" />-->
    <!--        </div>-->
    <!--        <FwbSpinner color="white" size="6" v-else />-->
    <!--    </div>-->
    <div class="flex gap-2 py-4 lg:py-1 px-2 bg-primary-500 text-white items-center" v-if="route.name != 'MangaReader'">
        <div v-if="route.path == '/welcome'" class="m-auto">
            <p class="text-xl font-bold">Project Sephirah - Welcome</p>
        </div>
        <div v-else>
            <RouterLink to="/">
                <p class="text-xl font-bold lg:hidden">Sephirah</p>
                <p class="text-xl font-bold hidden lg:block">Project Sephirah</p>
            </RouterLink>
        </div>
        <div class="ml-auto gap-5 items-center hidden lg:flex" v-if="route.path != '/welcome'">
            <RouterLink to="/" class="flex gap-0.5">
                <MaterialIcon icon="home" />
                {{ t("component.common.topnav.option.home") }}
            </RouterLink>
            <RouterLink to="" class="flex gap-0.5">
                <MaterialIcon icon="deployed_code" />
                Providers
            </RouterLink>
            <RouterLink to="/credentials" class="flex gap-0.5">
                <MaterialIcon icon="key" />
                Credentials
            </RouterLink>
            <RouterLink to="" class="flex gap-0.5">
                <MaterialIcon icon="shelf_position" />
                Bookshelf
            </RouterLink>
            <RouterLink to="" class="flex gap-0.5">
                <MaterialIcon icon="settings" />
                Options
            </RouterLink>
        </div>
        <div class="ml-auto gap-2 items-center flex lg:hidden" v-if="route.path != '/welcome'">
            <Button outlined @click="overlays.visibility.sidebarMenu = true">
                <template #icon>
                    <MaterialIcon icon="menu" />
                </template>
            </Button>
        </div>
    </div>
</template>

<style scoped>

</style>