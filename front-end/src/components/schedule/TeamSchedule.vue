<template>
  <div class="bg-white h-100 w-100" @click="clickday">
    <div class="w-100 h-100 d-flex flex-column">
      <p class="font-weight-bold ma-1 pa-3 rounded-lg" :class="{ clicked: props.scheduleDate === choiceDate }" style="cursor: pointer;">{{ props.scheduleDate }}</p>

      <template v-for="conf in data">
        <div
          v-if="conf.status !== 'CANCELED'"
          @click="router.push({ name: 'detailconference', params: { teamid: props.teamId, conferenceid: conf.id } })"
          class="text-start CONFERENCE ma-1 rounded-lg"
        >
          <p class="font-weight-bold ma-3">{{ conf.title }}</p>
          <p class="font-weight-bold ma-3">{{ conf.callStart.slice(11, 16) }} ~ {{ conf.callEnd.slice(11, 16) }}</p>
        </div>
      </template>
    </div>
  </div>
</template>
<script setup>
import { ref, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { getConferenceHistory } from '@/api/conference'
const emit = defineEmits(['clickDay'])
const props = defineProps({
  teamId: Number,
  scheduleDate: String,
  choiceDate: String
})
const router = useRouter()
const data = ref([])

// 일자 선택
const clickday = () => {
  emit('clickDay', props.scheduleDate)
}

const schedule = async () => {
  const date = props.scheduleDate.replaceAll('-', '')
  await getConferenceHistory(
    props.teamId,
    date,
    (response) => {
      console.log(response.data?.dataBody)
      data.value = response.data?.dataBody.map((conference) => {
        if (conference.title === '') {
          const date = conference.callStart.slice(0, 10)
          const time = conference.callStart.slice(11, 16)
          conference.title = `${date} ${time} 회의`
        }
        return conference
      })
    },
    (error) => {
      const errorCode = error.response.data.dataHeader?.resultCode
      const errorMessage = error.response.data.dataHeader?.resultMessage

      if (errorCode === 'C-001' || errorCode === 'G-001') {
        alert(errorMessage)
        router.replace({ name: 'notFound' })
      }
    }
  )
}

watchEffect(
  (props,
  (newvalue) => {
    schedule()
  })
)
</script>
<style scoped>
.conference-title {
  width: 100%;
  font-size: 12px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: left;
}

.clicked {
  /* 클릭 일자 */
  background-color: rgba(147, 221, 255, 0.207);
}
</style>
