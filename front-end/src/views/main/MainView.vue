<template>
  <div class="w-100 h-100">
    <v-row class="ma-0 pa-0 h-100 w-100">
      <v-col cols="9" class="w-100 h-100 d-flex justify-center">
        <v-container class="w-100 h-100">
          <CalendarVue @click-day="(date, data) => dataCheck(date, data)"/>
        </v-container>
      </v-col>
      <v-col cols="3" class="w-100 h-100">
        <v-container class="w-100 h-100 d-flex flex-column">
          <Schedule :schedule-data="schedule" :schedule-date="catchdate"/>
        </v-container>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import CalendarVue from '@/components/calendar/Calendar.vue'
import Schedule from '@/components/schedule/InvSchedule.vue'


const windowWidth = ref(window.innerWidth)
const windowHeight = ref(window.innerHeight)
const schedule = ref([])
const catchdate = ref('')

const dataCheck = (date, data) => {
  catchdate.value = date
  schedule.value = data
  console.log(date, data)
}

window.addEventListener('resize', function () {
  windowWidth.value = window.innerWidth
})
</script>
<style scoped></style>
