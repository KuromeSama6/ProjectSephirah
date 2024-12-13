<script setup lang="ts">
import Card from "primevue/card";
import { useRouter } from "vue-router";
import MaterialIcon from "../../components/common/util/MaterialIcon.vue";

const router = useRouter();
</script>
<template>
    <div class="flex flex-auto justify-center w-full h-max pt-48">
        <div class="flex flex-col items-center gap-2">
            <h1 class="text-2xl font-bold">Not Found</h1>
            <p>Nothing on this page.</p>
            <Button text @click="router.go(-1)" class="p-2">
                <MaterialIcon icon="arrow_back" />
                Go Back
            </Button>
        </div>
    </div>

</template>
