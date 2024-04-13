<template>
  <v-container class="pa-0 d-flex flex-column justify-center">
    <v-row class="d-flex align-center justify-start ma-2">
      <v-col cols="4">
        <v-row>
          <v-col cols="3" class="d-flex justify-center align-center">
            <v-btn icon="mdi-chevron-left" @click="clickprev" variant="text"></v-btn>
          </v-col>
          <v-col cols="6" class="d-flex justify-center align-center">
            <p>
              <span class="text-h6 font-weight-bold">{{ year }}</span>
              <span class="ms-3 text-h5">{{ listofmonthword[month] }}</span>
            </p>
          </v-col>
          <v-col cols="3" class="d-flex justify-center align-center">
            <v-btn icon="mdi-chevron-right" @click="clicknext" variant="text"></v-btn>
          </v-col>
        </v-row>
      </v-col>
      <v-spacer></v-spacer>
      <v-btn :disabled="role === 'MEMBER'" class="me-3" @click="dialog = true" color="#45566F" variant="outlined" rounded="xl">
        <p>팀 관리</p>
      </v-btn>
      <v-dialog v-model="dialog" persistent width="70%" height="70%">
        <TeamModifyDialog @close-dialog="dialog = false" :team-id="props.teamId" />
      </v-dialog>
      <v-btn
        @click="router.push({ name: 'insertconference', params: { teamid : props.teamId, date : choicedate } })"
        :disabled="role === 'MEMBER'"
        color="#45566F"
        variant="outlined"
        rounded="xl"
        class="mr-10"
      >
        <p>회의 예약</p>
      </v-btn>
    </v-row>
  </v-container>
  <div class="h-85 mt-n1">
    <v-row class="d-flex align-center justify-center ma-0">
      <v-col v-for="i in weekday" class="day-header ma-0" style="flex-grow: 1">
        <div>
          <p class="font-weight-bold">
            {{ dayofweek[i] }}
          </p>
        </div>
      </v-col>
    </v-row>
    <v-row class="d-flex align-center justify-center ma-0 schedule">
      <v-col v-for="day in cal" class="h-100 day pa-2" style="flex-grow: 0">
        <TeamSchedule
          :schedule-date="String(day.year) + '-' + String(day.month + 1).padStart(2, '0') + '-' + String(day.date).padStart(2, '0')"
          :team-id="props.teamId"
          :choice-date="choicedate"
          @click-day="(data) => (choicedate = data)"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { selectTeam } from '@/api/team.js'
import TeamModifyDialog from '../team/TeamModifyDialog.vue'
import TeamSchedule from '../schedule/TeamSchedule.vue'

const router = useRouter()

const props = defineProps({
  teamId: Number
})
const dialog = ref(false)
const role = ref('')

// 달력 날짜 계산
const cal = ref([])

// 기준 일자 (Today)
const nowdate = ref(new Date())

const year = ref(nowdate.value.getFullYear())
const month = ref(nowdate.value.getMonth())
const date = ref(nowdate.value.getDate())
const choicedate = ref(String(year.value) + '-' + String(month.value + 1).padStart(2, '0') + '-' + String(date.value).padStart(2, '0'))
// const day   = ref(nowdate.value.getDay())

// const getWeek = (date) => {
//   const currentDate = date.getDate();
//   const firstDay = new Date(date.setDate(1)).getDay();

//   return Math.ceil((currentDate + firstDay) / 7);
// };

// const week = getWeek(new Date() + '주차');
// console.log(week + "주차");
const select = async () => {
  await selectTeam(
    (response) => {
      role.value = response.data.dataBody.find((data) => data.teamId == props.teamId).role
    },
    (error) => {
      console.log(error)
    }
  )
}

// 팀 전환시 팀원 조회
watch(
  () => props.teamId,
  () => {
    select()
  }
)

// 셀렉터 옵션 및 캘린더 옵션들
const weekday = ref([0, 1, 2, 3, 4, 5, 6])
const dayofweek = ref(['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'])
const listofmonthword = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']

/*-------------------------
// 해당 월 달력 채우기
//-----------------------*/
const makeWeekCalendar = (year, month, date) => {
  const today = new Date(year, month, date).getDay()

  const startOfWeek = new Date(year, month, date)
  startOfWeek.setDate(date - today)
  const endOfWeek = new Date(year, month, date)
  endOfWeek.setDate(date + (6 - today))
  const week = []

  let currentDate = new Date(startOfWeek)
  while (currentDate <= endOfWeek) {
    week.push({
      date: currentDate.getDate(),
      month: currentDate.getMonth(),
      year: currentDate.getFullYear()
    })
    currentDate.setDate(currentDate.getDate() + 1)
  }
  console.log(week)
  return week
}

// 이전주 이동
const clickprev = () => {
  date.value -= 7
  const resource = new Date(year.value, month.value, 0).getDate()
  if (date.value < 0) {
    date.value += resource
    if (month.value === 0) {
      year.value -= 1
      month.value = 11
    } else {
      month.value -= 1
    }
  }
  cal.value = makeWeekCalendar(year.value, month.value, date.value)
}

// 다음주 이동
const clicknext = () => {
  date.value += 7
  const resource = new Date(year.value, month.value + 1, 0).getDate()
  if (date.value > resource) {
    date.value -= resource
    if (month.value === 11) {
      year.value += 1
      month.value = 0
    } else {
      month.value += 1
    }
  }
  cal.value = makeWeekCalendar(year.value, month.value, date.value)
}

onMounted(() => {
  select()
  cal.value = makeWeekCalendar(year.value, month.value, date.value)
})
</script>

<style scoped>
.day-header {
  max-width: calc(100% / 7);
  min-width: calc(100% / 7);
}

.day {
  max-width: calc(100% / 7);
  min-width: calc(100% / 7);
  text-align: center;
  border: 1px solid #e0e0e0;
}
.not_current {
  background-color: #e0e0e0;
}

.schedule {
  height: 94% !important;
}
</style>
