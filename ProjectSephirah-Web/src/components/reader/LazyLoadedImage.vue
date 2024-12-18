<script setup lang="ts">
import { onBeforeMount, onBeforeUnmount, onMounted, ref } from "vue";
import { useElementVisibility } from "@vueuse/core";
import { ImagePostProcessData } from "../../backend/manga/Manga.ts";

const props = defineProps<{
    src: string;
    placeholder?: string;
    postProcessor?: (data: ImagePostProcessData) => Promise<string>;
}>();

const imageRef = ref<HTMLImageElement | null>(null);
const source = ref<string>(props.src);
const rawSrc = ref<string>(props.placeholder!);
const postProcessing = ref(false);
const blobUrl = ref<string>("");

const emit = defineEmits<{
    (event: "load"): void;
}>();

function OnLoadStart(e: Event) {
    if (props.postProcessor) {
        postProcessing.value = true;
        Load();
    }
}

function OnPostProcessLoadFinished() {
    URL.revokeObjectURL(blobUrl.value);
}

async function Load() {
    // post process
    if (props.postProcessor) {
        const res = GetImageBase64();

        if (res) {
            const ret = await props.postProcessor({
                imageBase64: res.imageBase64,
                url: source.value,
                originalSize: res.originalSize,
            });
            // console.log(ret);
            if (ret) {
                blobUrl.value = ret;
                rawSrc.value = ret;
                postProcessing.value = false;
                emit("load");
                // URL.revokeObjectURL(ret);
            } else {
                rawSrc.value = "https://placehold.co/1441x2048/000/F00?text=Postprocess%20Error&font=source-sans-pro";
            }
        }
    }
}

function GetImageBase64(): {
    imageBase64: string;
    originalSize: { width: number; height: number };
} {
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
    if (ctx) {
        // Set canvas dimensions to match the image
        const displayedWidth = imageRef.value?.scrollWidth!;
        const displayedHeight = imageRef.value?.scrollHeight!;
        const originalWidth = imageRef.value?.naturalWidth!;
        const originalHeight = imageRef.value?.naturalHeight!;

        canvas.width = originalWidth;
        canvas.height = originalHeight;
        // Draw the image onto the canvas
        ctx.drawImage(imageRef.value as any, 0, 0, originalWidth, originalHeight);

        // Convert the canvas content to a Base64 string
        // console.log(canvas.toDataURL("image/png"));
        return {
            imageBase64: canvas.toDataURL("image/png"),
            originalSize: { width: originalWidth, height: originalHeight },
        }; // You can change 'image/png' to 'image/jpeg' if needed
    }

    return {
        imageBase64: "",
        originalSize: { width: 0, height: 0 },
    };
}

</script>

<template>
    <img ref="imageRef" alt="" :src="rawSrc" :data-src="source" class="lazyload" @lazyloaded="OnLoadStart" @load="OnPostProcessLoadFinished" :crossorigin="postProcessor ? 'anonymous' : undefined" />
    <img v-if="postProcessing" src="https://placehold.co/1441x2048/000/FFF?text=Postprocessing%20Image&font=source-sans-pro" alt="" />
</template>

<style scoped>

</style>