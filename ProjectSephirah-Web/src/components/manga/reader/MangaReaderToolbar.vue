<script setup lang="ts">

import MaterialIcon from "../../common/util/MaterialIcon.vue";
import MangaProviderBadge from "../../common/provider/MangaProviderBadge.vue";
import { FwbSpinner } from "flowbite-vue";
import { useRouter } from "vue-router";

const props = defineProps<{
    disableNext: boolean;
    disablePrev: boolean;
}>();

const emit = defineEmits<{
    (event: "toggleList"): void;
    (event: "toggleDetails"): void;
    (event: "next"): void;
    (event: "prev"): void;
}>();

const router = useRouter();

</script>

<template>
    <div class="flex w-full p-2 gap-1 items-center">
        <Button severity="secondary" outlined @click="emit('prev')" :disabled="disablePrev">
            <MaterialIcon icon="arrow_back" />
        </Button>
        <div class="m-auto flex gap-1">
            <Button severity="secondary" outlined @click="router.push('/')">
                <MaterialIcon icon="home" />
            </Button>

            <Button severity="secondary" outlined>
                <MaterialIcon icon="search" />
            </Button>
            <Button severity="primary" outlined>
                <MaterialIcon icon="favorite" />
            </Button>

            <Button severity="secondary" outlined @click="emit('toggleDetails')">
                <MaterialIcon icon="info" />
            </Button>
            <Button severity="secondary" outlined @click="emit('toggleList')">
                <MaterialIcon icon="list" />
            </Button>
        </div>
        <Button severity="secondary" outlined class="self-end" @click="emit('next')" :disabled="disableNext">
            <MaterialIcon icon="arrow_forward" />
        </Button>
    </div>
</template>

<style scoped>

</style>