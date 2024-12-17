<script setup lang="ts">
import { useOverlaysStore } from "../../stores/overlaysStore.ts";
import { useSettingsStore } from "../../stores/settingsStore.ts";
import { ref } from "vue";
import MaterialIcon from "../common/util/MaterialIcon.vue";

const overlays = useOverlaysStore();
const settings = useSettingsStore();

const visible = ref(!settings.legalTermsAccepted);

function AgreeAndContinue() {
    settings.legalTermsAccepted = true;
    settings.Save();
    visible.value = false;
}

</script>

<template>
    <Drawer position="bottom" :dismissable="false" :showCloseIcon="false" v-model:visible="visible" style="height: 90vh;">
        <div class="flex flex-col items-center gap-2 m-4">
            <h1 class="text-gray-400">This site is available in other languages.</h1>
            <h1 class="flex items-center gap-1">
                <MaterialIcon icon="warning" />
                Disclaimer
            </h1>
            <h1 class="font-bold text-2xl text-pink-400">You must be of age 18 and above.</h1>
            <h1 class="font-bold text-2xl underline text-pink-400">No Exceptions.</h1>
            <p>
                By clicking "continue" and/or using this service, you acknowledge that you are of age 18 and above.
            </p>
            <p>
                This website is intended for adults only and may contain material that is not suitable for minors. By accessing this site, you certify that:
                <ul class="list-disc">
                    <li>You are at least 18 years of age (or the legal age of majority in your jurisdiction).</li>
                    <li>You are accessing this website voluntarily and acknowledge that some content may include explicit material intended for mature audiences.</li>
                    <li>Viewing this material is legal in your community and does not violate any local, state, or federal laws.</li>
                </ul>
            </p>
            <p class="text-gray-400">
                This website fully complies with the record-keeping requirements of 18 U.S. Code § 2257 and its associated regulations. All models, actors, actresses, and other persons depicted in any visual depiction of actual or simulated sexually explicit conduct appearing on this website were over the age of eighteen (18) years at the time of the creation of such depictions.
            </p>
            <p class="text-gray-400">
                This website operates in accordance with the laws of its hosting jurisdiction. It is your sole responsibility to ensure that accessing and viewing content on this site complies with the laws of your local jurisdiction. By accessing this site, you waive any claims against the site owner or operators for violations of local laws or regulations.

                The site owner and operators assume no responsibility for the actions of users who fail to comply with their local legal requirements.
            </p>
            <divider />
            <h1 class="flex items-center gap-1">
                <MaterialIcon icon="cookie" />
                Cookies Policy
            </h1>
            <p class="text-gray-400">
                This site uses cookies to store important information such as your authentication status and preferences. By clicking "continue" and/or using this site, you agree to the use of such cookies.
            </p>
            <Divider />
            <h1 class="flex items-center gap-1">
                <MaterialIcon icon="public" />
                Information Disclosure Policy & Open-Source Statements
            </h1>
            <p class="text-gray-400">
                Project Sephirah is composed of a frontend web application (the site you are viewing right now), as well as a backend API server. A "provider" is any external service or source (i.e. a manga viewing site like Mangadex) that Project Sephirah uses to provide its services. Project Sephirah does not host any media (including but not limited to images and videos) on its server; however, we may cache or store responses from providers that contains metadata to improve the performance of
                the site.
            </p>
            <p class="text-gray-400">
                The backend server is solely used to proxy requests to some providers that cannot be directly accessed by the frontend application directly (i.e. due to CORS restrictions). The backend server does not store any user data, and all requests are made anonymously. The backend server may cache responses from providers to improve the performance of the site. <span
                class="text-white underline">We never send your credentials from providers anywhere, even the backend server.</span> We apologize if we are unable to add or implement a provider due to this limitation.
            </p>
            <p class="text-gray-400">
                Project Sephirah operates on a Vue.js web app and a Spring Boot backend server, both of which are open-source projects. The source code for this project is available on GitHub.
            </p>
            <Divider />
            <p class="text-md font-bold text-yellow-300">By clicking "continue" and/or proceeding to use this site, you acknowledge and agree to all the terms and disclaimers mentioned above, as well as any modifications or amendments to it in the future thereof. If otherwise, you will immediatly discontinue use of this site.</p>
            <Button @click="AgreeAndContinue" class="w-full">Agree and Continue</Button>
        </div>
    </Drawer>
</template>

<style scoped>

</style>