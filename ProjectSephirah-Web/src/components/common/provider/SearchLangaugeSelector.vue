<script setup lang="ts">
import { useSettingsStore } from "../../../stores/settingsStore.ts";
import { SupportedLanguage, SupportedLanguages } from "../../../backend/common/Language.ts";
import { computed, ref } from "vue";

const settings = useSettingsStore();

interface Option {
    label: string;
    value: SupportedLanguage;
}

const options: Option[] = SupportedLanguages.map((c): Option => ({
    label: c.toUpperCase(),
    value: c as SupportedLanguage,
}));

const value = computed<Option>({
    get: (): Option => ({
        label: (settings.searchLangauge as String).toUpperCase(),
        value: settings.searchLangauge,
    }),
    set(value: Option) {
        settings.searchLangauge = value.value;
        settings.Save();
    },
});

</script>

<template>
    <slot :value="value" :options="options">
        <Select option-label="label" placeholder="XX" :options="options" v-model="value" class="uppercase"></Select>
    </slot>
</template>

<style scoped>

</style>