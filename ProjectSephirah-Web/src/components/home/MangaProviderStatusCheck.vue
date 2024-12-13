<script setup lang="ts">
import { useMangaProviderStore } from "../../stores/mangaProviderStore.ts";
import { FwbSpinner } from "flowbite-vue";
import Spinner from "../common/util/Spinner.vue";
import MaterialIcon from "../common/util/MaterialIcon.vue";
import { ProviderStatus } from "../../backend/provider/Provider.ts";
import { provide } from "@vue/runtime-core";

const providerStore = useMangaProviderStore();

</script>

<template>
    <div class="justify-items-center">
        <div class="flex gap-2" v-if="Array.from(providerStore.providerStatus.values()).includes(ProviderStatus.Unknown)">
            <FwbSpinner color="white" size="5" />
            <p>Checking Provider Status</p>
        </div>
        <div class="flex gap-1" v-if="Array.from(providerStore.providerStatus.values()).every(c => c == ProviderStatus.Available)">
            <MaterialIcon icon="check_circle" />
            <p>All Providers Online</p>
        </div>
        <div class="flex gap-1 text-yellow-300" v-if="Array.from(providerStore.providerStatus.values()).includes(ProviderStatus.Unavailable)">
            <MaterialIcon icon="warning" />
            <p>
                {{ Array.from(providerStore.providerStatus.values()).filter(c => c == ProviderStatus.Unavailable).length }}
                of {{ providerStore.providerStatus.size }}
                Providers Unavailable
            </p>
        </div>
    </div>
    <Divider />
</template>

<style scoped>

</style>