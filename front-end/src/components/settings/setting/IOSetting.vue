<template>
  <div id="container" @click="getMedia">
    <h2 id="io-title">입출력 설정</h2>
    <div id="io-container">
      <div class="io-items">
        <p class="pb-2">마이크 설정</p>
        <!-- 마이크 선택 -->
        <v-select 
          :items="['마이크1', '마이크2', '마이크3']" 
          density="compact"
          variant="outlined"
          style="max-width: 300px;"
        ></v-select>
        <!-- 마이크 사운드 조절 -->
        <!-- <v-slider v-model="volumeMic" prepend-icon="mdi-volume-high"></v-slider> -->
      </div>
      <div class="io-items">
        <p class="pb-2">스피커 설정</p>
        <!-- 스피커 설정 -->
        <v-select 
          :items="['스피커1', '스피커2', '스피커3']" 
          density="compact"
          variant="outlined"
          style="max-width: 300px;"
        ></v-select>
        <!-- 스피커 사운드 설정 -->
        <!-- <v-slider v-model="volumeSpeaker" prepend-icon="mdi-volume-high"></v-slider> -->
      </div>
      <div class="io-items">
        <p class="pb-2">카메라 설정</p>
        <!-- 카메라 설정 -->
        <v-select 
          :items="['카메라1', '카메라2', '카메라3']" 
          density="compact"
          variant="outlined"
          style="max-width: 300px;"
        ></v-select>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 마이크 볼륨
const volumeMic = ref(10)

// 스피커 볼륨
const volumeSpeaker = ref(90)
</script>

<style scoped>
#container {
  padding: 10px;
  max-width: 400px;
}
#io-title {
  margin: 10px;
}
#io-container {
  flex-direction: column;
  min-width: 240px;
  width: 30vw;
  min-height: 240px;
  padding: 10px;
  margin: 10px;
}
.io-items {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
</style>
