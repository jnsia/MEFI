import { ref } from 'vue'
import { defineStore } from 'pinia'

// setting store
// 알림, 입출력 설정 저장 
export const useSettingStore = defineStore('setting', () => {
    const conferenceLayout = ref('conference-view1')
    const alarmPermission = ref(true)
    const alarmSound = ref(false)

  return { conferenceLayout, alarmPermission, alarmSound }
  }, { persist: true }
)
