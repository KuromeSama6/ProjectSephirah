<script setup lang="ts">

import MaterialIcon from "../util/MaterialIcon.vue";
import { useRouter } from "vue-router";
import SearchLangaugeSelector from "../provider/SearchLangaugeSelector.vue";

const kw = defineModel<string>();
const props = withDefaults(defineProps<{
    placeholder?: string;
}>(), {
    placeholder: "Search for a title or an ID",
});

const emit = defineEmits<{
    (event: "search", kw: string): void;
}>();

</script>

<template>
    <form @submit.prevent="emit('search', kw!)" class="w-full">
        <InputGroup>
            <InputGroupAddon>
                <IftaLabel>
                    <SearchLangaugeSelector />
                    <label>Search in...</label>
                </IftaLabel>
            </InputGroupAddon>
            <InputText type="text" v-model="kw" :placeholder="placeholder" />
            <Button label="Go" type="submit" :disabled="!kw">
                <template #icon>
                    <MaterialIcon icon="Search" />
                </template>
            </Button>
        </InputGroup>
    </form>
</template>

<style scoped>

</style>