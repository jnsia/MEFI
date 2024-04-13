<template>
  <div id="container">
    <!-- 알림 설정 -->
    <h3 id="alarm-title">알림 설정</h3>
    <div id="alarm-container">
      <!-- 알림 설정 -->
      <div class="alarm-items">
        <p style="margin-right: 40px;">알림 설정</p>
        <!-- 알림 on/off -->
        <v-switch
          v-model="alarm"
          hide-details
          inline
          color="primary"
          style="position: absolute; right: 40px;"
        ></v-switch>
      </div>
      <!-- 알림 소리 설정 -->
      <div class="alarm-items">
        <p style="margin-right: 40px;">알림 소리</p>
        <!-- 소리 on/off -->
        <v-switch
          v-model="sound"
          hide-details
          inline
          color="primary"
          style="position: absolute; right: 40px;"
        ></v-switch>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useSettingStore } from '@/stores/setting';

const store = useSettingStore();

// 알림 on off를 담을 변수
const alarm = ref(store.alarmPermission)

// 소리 on off를 담을 변수
const sound = ref(store.alarmSound)

// 작동 : 해당 아이콘 클릭 시 toggle됨
// 기능 : alarm 설정 on off
watch(()=> alarm.value, () =>{
  store.alarmPermission = !store.alarmPermission
})

// 작동 : 해당 아이콘 클릭 시 toggle됨
// 기능 : sound 설정 on off
watch(()=> alarm.value, () =>{
  store.alarmSound = !store.alarmSound
})
</script>

<style scoped>
#container {
  padding: 10px;
  max-width: 400px;
}
#alarm-title {
  margin: 10px;
}
#alarm-container {
  flex-direction: column;
  min-width: 240px;
  width: 30vw;
  min-height: 240px;
  padding: 0 10px;
}
.alarm-items {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}
</style>
