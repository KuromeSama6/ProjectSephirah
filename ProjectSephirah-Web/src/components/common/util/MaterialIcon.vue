<script setup lang="ts">
import { defineProps } from "vue";

const props = defineProps({
    icon: String,
});
</script>

<template>
    <span class="material-symbols-outlined">
        {{ props.icon }}
    </span>
</template>
