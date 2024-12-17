<script setup lang="ts">
import MaterialIcon from "../components/common/util/MaterialIcon.vue";
import SearchProviderList from "../components/home/SearchProviderList.vue";
import HomeOptionsDropdown from "../components/home/HomeOptionsDropdown.vue";
import HomeMangaProviderStatusCheck from "../components/home/MangaProviderStatusCheck.vue";
import { ref } from "vue";
import { useRouter } from "vue-router";
import SearchBarMain from "../components/search/SearchBarMain.vue";
import { SephirahAPI } from "../backend/api/SephirahAPI.ts";
import PageContainer from "../components/common/util/PageContainer.vue";

const quickFilterOptions = [
    "All", "Non-H", "H-Only",
];
const kw = ref<string>("");
const router = useRouter();

</script>

<template>
    <PageContainer>
        <h1 class="font-bold text-3xl">Project Sephirah</h1>

        <SearchBarMain v-model="kw" @search="router.push(`/search?kw=${encodeURI(kw!)}`)" />
        <!--            <HomeMangaProviderStatusCheck />-->
        <HomeOptionsDropdown />

        <p>
            Project Sephirah aims to provide a centralized platform for reading manga from all sources, from a
            convienent web interface.
        </p>
        <p class="text-xs">
            Project Sephirah is a non-profit project. All source code is dedicated to the public domain under the
            Unlicense, available
            on Github. All manga content is provided by the respective manga providers. Project Sephirah does not
            host any manga content and is not
            affiliated with any manga provider.
        </p>
    </PageContainer>
</template>

<style scoped>

</style>