<script setup lang="ts">

import HomeOptionsDropdown from "../../home/HomeOptionsDropdown.vue";
import SearchBarMain from "../../search/SearchBarMain.vue";
</script>

<template>
    <div class="flex-auto lg:flex-none lg:w-2/4 pt-2 mx-auto">
        <div class="flex flex-col items-center my-2 mx-5 gap-2">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped>

</style>