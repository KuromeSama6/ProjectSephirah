<script setup lang="ts">
import { ProviderContentType, useSettingsStore } from "../../../stores/settingsStore.ts";
import { SupportedLanguage, SupportedLanguages } from "../../../backend/common/Language.ts";
import { computed, ref } from "vue";

const settings = useSettingsStore();

interface Option {
    label: string;
    value: ProviderContentType;
}


const options: Option[] = Object.values(ProviderContentType).map((c): Option => ({
    label: c,
    value: c as ProviderContentType,
}));

const value = computed<Option>({
    get: (): Option => ({
        label: settings.search.providerContentFilter,
        value: settings.search.providerContentFilter,
    }),
    set(value: Option) {
        settings.search.providerContentFilter = value.value;
        settings.Save();
    },
});

</script>

<template>
    <slot :value="value" :options="options">
        <Select option-label="label" placeholder="XX" :options="options" v-model="value"></Select>
    </slot>
</template>

<style scoped>

</style>