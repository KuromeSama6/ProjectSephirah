<script setup lang="ts">

import { provide } from "@vue/runtime-core";

const props = defineProps<{
    route: {
        name?: string;
        path?: string;
        query?: object;
        params?: object;
    }
}>();

provide("mockRoute", props.route);
</script>

<template>
    <slot></slot>
</template>

<style scoped>

</style>