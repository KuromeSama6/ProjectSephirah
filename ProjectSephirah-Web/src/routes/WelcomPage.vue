<script setup lang="ts">

import HomeOptionsDropdown from "../components/home/HomeOptionsDropdown.vue";
import MaterialIcon from "../components/common/util/MaterialIcon.vue";
</script>

<template>
    <div class="flex-auto lg:flex-none lg:w-2/4 pt-2 mx-auto">
        <div class="flex flex-col items-center my-2 mx-5 gap-2">
            <h1 class="font-bold text-3xl">Project Sephirah</h1>

            <p>
                Project Sephirah aims to provide a centralized platform for reading manga from all sources, from a
                convienent web interface.
            </p>
            <Divider />
            <p class="text-xs">
                Project Sephirah is a non-profit project. All source code is dedicated to the public domain under the
                Unlicense, available
                on Github. All manga content is provided by the respective manga providers. Project Sephirah does not
                host any manga content and is not
                affiliated with any manga provider.
            </p>
        </div>
    </div>
</template>

<style scoped>

</style>