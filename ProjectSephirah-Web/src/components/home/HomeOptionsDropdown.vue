<script setup lang="ts">

import ProviderBadgeSmall from "./ProviderBadgeSmall.vue";
import SearchProviderList from "./SearchProviderList.vue";
</script>

<template>
    <div class="w-full">
        <Accordion value="">
            <AccordionPanel value="0">
                <SearchProviderList />
            </AccordionPanel>
        </Accordion>
    </div>
</template>

<style scoped>

</style>