<script setup lang="ts">
const props = defineProps<{
    severity?: string;
}>();
</script>
<template>
    <div :class="['badge', `text-bg-${props.severity || 'primary'}`]">
        <slot></slot>
    </div>
</template>
