<template>
  <div id="video-container" v-if="props.streamManager">
    <OvVideo id="video" :stream-manager="props.streamManager" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import OvVideo from './OvVideo.vue'

const props = defineProps({
  streamManager: Object
})
</script>

<style scoped>
</style>
