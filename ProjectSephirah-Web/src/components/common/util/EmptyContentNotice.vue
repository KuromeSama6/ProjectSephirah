<script setup lang="ts"></script>
<template>
    <div class="card">
        <div class="flex flex-col gap-2 items-center mt-4">
            <slot name="icon">
                <MaterialIcon
                    icon="hide_source"
                    class="text-6xl text-surface-400" />
            </slot>
            <h3 class="text-2xl">
                <slot name="title">Nothing Here</slot>
            </h3>
            <p class="text-muted">
                <slot name="content">There is nothing to display.</slot>
            </p>
        </div>
    </div>
</template>
