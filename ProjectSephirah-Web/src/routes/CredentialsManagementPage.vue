<script setup lang="ts">
import PageContainer from "../components/common/util/PageContainer.vue";
import CredentialsManagementProviderCard from "../components/credentials/CredentialsManagementProviderCard.vue";
import { useMangaProviderStore } from "../stores/mangaProviderStore.ts";
import { computed } from "vue";
import { MangaProvider } from "../backend/manga/MangaProvider.ts";
import { useAuthenticationStore } from "../stores/authenticationStore.ts";
import MaterialIcon from "../components/common/util/MaterialIcon.vue";

const providers = useMangaProviderStore();
const auth = useAuthenticationStore();

const providerList = computed(() => Array.from(providers.providers.values())
    .filter(c => c.auth)
    .sort((a, b) => GetSortKey(a) - GetSortKey(b))
    .reverse());

function GetSortKey(provider: MangaProvider): number {
    if (provider.auth) {
        return 1;
    }

    return 0;
}

</script>

<template>
    <PageContainer>
        <h1 class="font-bold text-3xl">Credentials Manager</h1>
        <p>Manage your credentials for different providers.</p>
        <Accordion :value="[]" multiple class="w-full">
            <AccordionPanel v-for="provider of providerList" :value="provider.id">
                <AccordionHeader>
                    <div class="flex gap-1 items-center">
                        <img class="w-4 h-4" :src="`/assets/images/providerIcons/${provider.id}.ico`" :alt="provider.id" />
                        <p class="text-lg text-white font-bold">{{ provider.info.name }}</p>
                        <MaterialIcon icon="key" class="ms-2 text-green-500" v-if="auth.IsAuthenticated(provider)" />
                        <MaterialIcon icon="key_off" class="ms-2 text-gray-400a" v-else />
                    </div>
                </AccordionHeader>
                <AccordionContent>
                    <CredentialsManagementProviderCard :provider="provider" />
                </AccordionContent>
            </AccordionPanel>
        </Accordion>
        <p class="mt-5 text-sm">Providers not listed here requires no authentication.</p>
        <p class="text-xs">Your credentials are only sent to the servers of corresponding providers, and nowhere else. We never sent your credentials to our backend servers.</p>
    </PageContainer>
</template>

<style scoped>

</style>