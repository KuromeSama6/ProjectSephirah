<script setup lang="ts">
import { useOverlaysStore } from "../../stores/overlaysStore.ts";
import HomeOptionsDropdown from "../home/HomeOptionsDropdown.vue";
import { useRouter } from "vue-router";
import MaterialIcon from "../common/util/MaterialIcon.vue";

const overlays = useOverlaysStore();
const router = useRouter();

interface Option {
    label: string;
    severity?: string;
    icon: string;
    action: () => void;
}

const options: Option[] = [
    {
        label: "Home",
        icon: "home",
        action: () => router.push("/"),
    },
];

</script>

<template>
    <Drawer v-model:visible="overlays.visibility.sidebarMenu" position="right" header="Menu" style="width: 50vw">
        <div class="flex flex-col">
            <Button v-for="option of options" :severity="option.severity || 'secondary'" variant="text">
                <div class="content-start items-center w-full flex gap-1">
                    <MaterialIcon :icon="option.icon" />
                    {{ option.label }}
                </div>
            </Button>
        </div>
    </Drawer>
</template>

<style scoped>

</style>