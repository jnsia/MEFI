<template>
  <!-- 마우스 hover 시 username 출력 -->
  <div @mouseover="handleMouseOver" @mouseout="handleMouseOut">
    <div class="username" :style="{'display': isHovered ? 'block' : 'none'}">{{ clientData }}</div>
    <video id="video" ref="video" autoplay></video>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted, computed } from 'vue'

const video = ref(null)
const isHovered = ref(false)

const props = defineProps({
  streamManager: Object
})

onMounted(() => {
  if (props.streamManager) {
    props.streamManager.addVideoElement(video.value)
  }
})

const clientData = computed(() => {
  const clientData = getConnectionData()
  return clientData
})

const getConnectionData = () => {
  const { connection } = props.streamManager.stream
  return connection.data.split('"')[3]
}

const handleMouseOver = () => {
	isHovered.value = true;
}

const handleMouseOut = () => {
	isHovered.value = false;
}
</script>

<style scoped>
#video {
  width: 100%;
  height: 100%;
  /* 가로 1.35 | 세로 1 */
  aspect-ratio: 1.35;
  border-radius: 10px;
}

/* 마우스 hover 시 username 출력  */
.username {
  position: relative;
  left: 5px;
  top: 5px;
  height: 0px;
  color: white;
  font-size: 12px;
  text-shadow: 3px 4px 5px black;
  background-color: rgba(0, 0, 0, 0.55);
}
</style>
