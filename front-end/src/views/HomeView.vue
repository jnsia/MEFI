<template>
  <v-layout>
    <!-- 헤더 -->
    <v-app-bar app color="#494550" height="40" class="elevation-0" >
      <HeaderVue :alarms="alarms" @remove-alarm="removeAlarm" @remove-alarms="removeAlarms"/>
    </v-app-bar>
    
    <div class="d-flex w-100 wrapper pa-0 h-cal h-cal-position">
      <!-- 사이드바 -->
      <div class="bg-white border-right h-100 sidebar">
        <ProfileVue />
      </div>
      <!-- 메인 컨텐츠 -->
      <RouterView class="bg-white w-100 h-100 rounded" />
    </div>

  </v-layout>
</template>

<script setup>
import { alarmAll,alarmSubscribe } from '@/api/alarm';
import HeaderVue from '@/components/layout/Header.vue'
import ProfileVue from '@/components/layout/Profile.vue'
import { ref, onMounted } from 'vue';

// 읽은 알림 삭제 처리
const removeAlarm = async (alarmId) => {
  for(let i=0;i<alarms.value.length;i++){
    if(alarms.value[i].id===alarmId){
      alarms.value.splice(i,1);
    }
  }
}

// 알림 전체 읽음 및 배열 삭제
const removeAlarms = () => {
  alarms.value = []
}

// 안읽은 알림
const alarms = ref([])

onMounted(async()=>{
  // 알림 전체 조회
  try{
    const resposne = await alarmAll()
    alarms.value = resposne.data.dataBody
  }catch(error){}

  // SSE 연결 API
  const lastEventId = ref("")
  try {
    const result = await alarmSubscribe(lastEventId.value)
    result.addEventListener("sse", (event) => {
      const eventData = JSON.parse(event.data)
      if (eventData.id !== null) {
        alarms.value = [...alarms.value, eventData]
      }
    })
  } catch (error) {
    console.log(error)
  }
})


</script>

<style> 
.wrapper {
  background-color: #f7f9ff;
}
</style>
