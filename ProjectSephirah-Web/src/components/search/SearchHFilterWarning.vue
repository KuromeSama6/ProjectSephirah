<script setup lang="ts">

import MaterialIcon from "../common/util/MaterialIcon.vue";
</script>

<template>

    <div class="flex gap-1 text-xs lg:text-sm mb-2 text-yellow-300 items-center">
        <MaterialIcon icon="warning" />
        <p>
            Some cover images blurred due to Ecchi/NSFW content.
            <RouterLink to="" class="text-pink-400 underline">Disable</RouterLink>
            &middot;
            <RouterLink to="" class="text-pink-400 underline">Change settings</RouterLink>
        </p>
    </div>
</template>

<style scoped>

</style>