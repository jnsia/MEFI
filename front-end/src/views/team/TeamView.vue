<template>
  <div class="w-100 h-100">
    <v-row class="ma-0 pa-0 w-100 h-100">
      <v-col cols="9" class="w-100 h-100 d-flex justify-center">
        <v-container class="w-100 h-100">
          <TeamCalendarVue :team-id="Number(props.id)" />
        </v-container>
      </v-col>
      <v-col cols="3" class="w-100 h-100">
        <v-container class="w-100 h-100 d-flex flex-column">
          <TeamMateList :team-id="Number(props.id)" />
        </v-container>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import TeamCalendarVue from '@/components/calendar/TeamCalendar.vue'
import Schedule from '@/components/schedule/TeamSchedule.vue'
import TeamMateList from '@/components/team/TeamMateList.vue'

const props = defineProps({
  id: Number
})
</script>

<style scoped></style>
