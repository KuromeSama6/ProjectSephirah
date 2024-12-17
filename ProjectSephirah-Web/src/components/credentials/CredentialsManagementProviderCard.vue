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

const authentication = useAuthenticationStore();
const toast = useToast();
const clipboard = useClipboard();

const props = defineProps<{
    provider: MangaProvider;
}>();
const auth = props.provider.auth;
const credentials = ref(auth?.GetCredentialsObject());
const loading = ref({
    authenticate: false,
    validate: false,
    deauthenticate: false,
});

async function Authenticate() {
    loading.value.authenticate = true;
    try {
        var res = await auth!.Authenticate(credentials.value);
        if (res.success && res.token) {
            toast.add({ severity: "success", summary: "Success", detail: "Successfully authenticated.", life: 3000 });
            authentication.providers[props.provider.id] = res.token;
            authentication.Save();

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
    await auth?.Deauthenticate();
    delete authentication.providers[props.provider.id];
    authentication.Save();
    toast.add({ severity: "success", summary: "Success", detail: "Successfully deauthenticated.", life: 3000 });
    loading.value.deauthenticate = false;
}

async function ValidateToken() {
    loading.value.validate = true;
    const ret = await auth?.ValidateToken();
    loading.value.validate = false;
    if (ret == ProviderTokenValidationStatus.VALID) {
        toast.add({ severity: "success", summary: "Success", detail: "Token is valid.", life: 3000 });
    } else if (ret == ProviderTokenValidationStatus.INVALID) {
        toast.add({ severity: "error", summary: "Invalid Token", detail: "Token is invalid. You have been deauthenticated.", life: 3000 });
        delete authentication.providers[props.provider.id];
        authentication.Save();
    } else {
        toast.add({ severity: "error", summary: "Network Error", detail: "Validation failed due to a network issue. Your token may be still valid.", life: 3000 });
    }
}

</script>

<template>
    <div class="border rounded flex flex-col border-gray-500 p-4">
        <div class="flex gap-1 items-center">
            <div v-if="auth" class="w-full">
                <div v-if="!authentication.IsAuthenticated(provider)" class="flex flex-col gap-2">
                    <p>Not authenticated. Enter your account credentials of {{ provider.info.name }}.</p>
                    <div class="flex flex-col gap-2 w-full">
                        <IftaLabel v-for="field in Object.keys(credentials)">
                            <InputText :type="field == 'password' ? 'password' : 'text'" v-model="credentials[field]" class="w-full" :disabled="loading.authenticate" />
                            <label>{{ field.toString().toUpperCase() }}</label>
                        </IftaLabel>
                        <Button severity="secondary" outlined label="Authenticate" :loading="loading.authenticate" @click="Authenticate()">
                            <template #icon>
                                <MaterialIcon icon="key" />
                            </template>
                        </Button>
                    </div>
                </div>
                <div class="flex flex-col gap-2" v-else>
                    <p>Token: <code class="bg-gray-700 rounded px-0.5">{{ authentication.GetToken(provider)?.toString().substring(0, 6) }}*******</code></p>
                    <div class="flex gap-2">
                        <Button severity="secondary" outlined label="Copy Token" @click="clipboard.copy(authentication.GetToken(provider))">
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