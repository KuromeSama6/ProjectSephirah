<script setup lang="ts">
import MaterialIcon from "../common/util/MaterialIcon.vue";
import { useRouter } from "vue-router";
import SearchLanguageSelector from "./selectors/SearchLanguageSelector.vue";
import SearchContentRatingSelector from "./selectors/SearchContentRatingSelector.vue";

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
        <div class="flex flex-col gap-1">
            <InputGroup>
                <InputText type="text" v-model="kw" :placeholder="placeholder" />
                <Button label="Go" type="submit" :disabled="!kw">
                    <template #icon>
                        <MaterialIcon icon="Search" />
                    </template>
                </Button>
            </InputGroup>
            <InputGroup>
                <IftaLabel>
                    <SearchLanguageSelector />
                    <label>Search in...</label>
                </IftaLabel>
                <IftaLabel>
                    <SearchContentRatingSelector />
                    <label>Provider Content Filter</label>
                </IftaLabel>
            </InputGroup>
        </div>
    </form>
</template>

<style scoped>

</style>