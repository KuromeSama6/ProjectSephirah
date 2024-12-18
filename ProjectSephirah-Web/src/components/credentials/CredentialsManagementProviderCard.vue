<script setup lang="ts">
import { Provider, ProviderTokenValidationStatus } from "../../backend/provider/Provider.ts";
import ProviderBadgeSmall from "../home/ProviderBadgeSmall.vue";
import MangaProviderBadge from "../common/provider/MangaProviderBadge.vue";
import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ref } from "vue";
import { useAuthenticationStore } from "../../stores/authenticationStore.ts";
import MaterialIcon from "../common/util/MaterialIcon.vue";
import { useToast } from "primevue/usetoast";
import { useClipboard } from "@vueuse/core";

const auth = useAuthenticationStore();
const toast = useToast();
const clipboard = useClipboard();

const props = defineProps<{
    provider: MangaProvider;
}>();
const authProvider = props.provider.auth;
const credentials = ref(authProvider?.GetCredentialsObject());
const loading = ref({
    authenticate: false,
    validate: false,
    deauthenticate: false,
});

async function Authenticate() {
    loading.value.authenticate = true;
    try {
        var res = await authProvider!.Authenticate(credentials.value);
        if (res.success && res.token) {
            toast.add({ severity: "success", summary: "Success", detail: "Successfully authenticated.", life: 3000 });
            auth.providers[props.provider.id] = res.token;
            auth.Save();

        } else {
            toast.add({ severity: "error", summary: "Authentication Error", detail: res.message, life: 3000 });
        }

    } catch (err: any) {
        toast.add({ severity: "error", summary: "Internal Error", detail: "An internal error occured. There may be more information in the console.", life: 3000 });

    }

    loading.value.authenticate = false;
}

async function Logout() {
    loading.value.deauthenticate = true;
    await authProvider?.Deauthenticate();
    delete auth.providers[props.provider.id];
    auth.Save();
    toast.add({ severity: "success", summary: "Success", detail: "Successfully deauthenticated.", life: 3000 });
    loading.value.deauthenticate = false;
}

async function ValidateToken() {
    loading.value.validate = true;
    const ret = await authProvider?.ValidateToken();
    loading.value.validate = false;
    if (ret == ProviderTokenValidationStatus.VALID) {
        toast.add({ severity: "success", summary: "Success", detail: "Token is valid.", life: 3000 });
    } else if (ret == ProviderTokenValidationStatus.INVALID) {
        toast.add({ severity: "error", summary: "Invalid Token", detail: "Token is invalid. You have been deauthenticated.", life: 3000 });
        delete auth.providers[props.provider.id];
        auth.Save();
    } else {
        toast.add({ severity: "error", summary: "Network Error", detail: "Validation failed due to a network issue. Your token may be still valid.", life: 3000 });
    }
}

</script>

<template>
    <div class="border rounded flex flex-col border-gray-500 p-4">
        <div class="flex gap-1 items-center">
            <div v-if="authProvider" class="w-full">
                <div v-if="!auth.IsAuthenticated(provider)" class="flex flex-col gap-2">
                    <p>Not authenticated. Enter your account credentials of {{ provider.info.name }}.</p>
                    <form @submit.prevent="">
                        <div class="flex flex-col gap-2 w-full">
                            <IftaLabel v-for="field in Object.keys(credentials!)">
                                <InputText :type="field == 'password' ? 'password' : 'text'" v-model="credentials![field]" class="w-full" :disabled="loading.authenticate" />
                                <label>{{ field.toString().toUpperCase() }}</label>
                            </IftaLabel>
                            <div class="text-yellow-300 border-yellow-300 border rounded p-2 flex gap-1 items-center" v-if="authProvider.isProxiedRequest">
                                <MaterialIcon icon="warning" />
                                <p>
                                    Your authentication request with your credentials will be proxied through Project Sephirah servers, and nothing else. We do not store your credentials. Do not enter your credentials if you are uncomfortable us doing so.
                                </p>
                            </div>
                            <Button severity="secondary" outlined label="Authenticate" :loading="loading.authenticate" @click="Authenticate()" type="submit">
                                <template #icon>
                                    <MaterialIcon icon="key" />
                                </template>
                            </Button>
                        </div>
                    </form>
                </div>
                <div class="flex flex-col gap-2" v-else>
                    <p>Token: <code class="bg-gray-700 rounded px-0.5">{{ auth.GetToken(provider)?.toString().substring(0, 6) }}*******</code></p>
                    <div class="flex gap-2 flex-col md:flex-row">
                        <Button severity="secondary" outlined label="Copy Token" @click="clipboard.copy(auth.GetToken(provider))">
                            <template #icon>
                                <MaterialIcon icon="content_paste" />
                            </template>
                        </Button>
                        <Button severity="secondary" outlined label="Validate Token" @click="ValidateToken" :loading="loading.validate">
                            <template #icon>
                                <MaterialIcon icon="check_circle" />
                            </template>
                        </Button>
                        <Button severity="danger" outlined label="Deauthenticate" @click="Logout" :loading="loading.deauthenticate">
                            <template #icon>
                                <MaterialIcon icon="key_off" />
                            </template>
                        </Button>
                    </div>
                </div>
            </div>
            <p v-else>
                No Authentication Required.
            </p>
        </div>
    </div>
</template>

<style scoped>

</style>