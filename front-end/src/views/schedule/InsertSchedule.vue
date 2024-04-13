<template>
  <v-card class="pa-5">
    <v-card-title class="d-flex align-center pa-2 my-2">
      <p class="text-h5 font-weight-black">일정 등록</p>
      <v-spacer></v-spacer>
      <v-btn @click="create" color="#45566F" rounded="xl">일정 등록</v-btn>
    </v-card-title>
    <v-card-item class="pa-3">
      <v-row>
        <v-col cols="6">
          <p>일정 종류</p>
        </v-col>
        <v-col cols="3">
          <p>시작 시간</p>
        </v-col>
        <v-col cols="3">
          <p>종료 시간</p>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-select label="Choice Type" v-model="selecttype" :items="typeschedule" variant="outlined" density="compact" hide-details="true"></v-select>
        </v-col>
        <v-col cols="6">
          <v-row>
            <v-col cols="3">
              <v-select v-model="selectSh" :items="starthours" variant="outlined" density="compact" hide-details="true"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectSm" :items="startmins" variant="outlined" density="compact" hide-details="true"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectEh" :items="endhours" variant="outlined" density="compact" hide-details="true"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectEm" :items="endmins" variant="outlined" density="compact" hide-details="true"></v-select>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <p>요약</p>
        </v-col>
        <v-col cols="6">
          <p>설명</p>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-text-field v-model="summary" label="summary" density="compact" variant="outlined" required></v-text-field>
        </v-col>
        <v-col cols="6">
          <v-text-field v-model="description" label="Description" density="compact" variant="outlined" required></v-text-field>
        </v-col>
      </v-row>
    </v-card-item>
  </v-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createSchedule } from '@/api/schedule.js'

const router = useRouter()
const starthours = ref(['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21'])
const startmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])
const endhours = ref(['09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22'])
const endmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])
const typeschedule = ref(['출장'])
const selecttype = ref('출장')
const selectSh = ref('08')
const selectSm = ref('00')
const selectEh = ref('22')
const selectEm = ref('00')
const summary = ref('')
const description = ref('')

const props = defineProps({
  date: String
})


const create = async () => {
  if (!summary.value || !description.value) {
    alert('일정 정보를 입력해주세요.')
    return false
  }
  if (selecttype.value === null) {
    alert('일정 타입을 선택해주세요.')
    return false
  }
  if (!selectSh.value || !selectSh.value || !selectSh.value || !selectSh.value) {
    alert('일정 시간을 설정해주세요.')
    return false
  }
  
  if (new Date(props.date + ' ' + selectSh.value +':'+ selectSm.value) >= new Date(props.date + ' ' + selectEh.value +':'+ selectEm.value)) {
    alert('일정 시간에 오류가 있습니다. 확인 후 생성하세요!')
    return false
  }
  const data = {
    startedTime: props.date + 'T' + selectSh.value + ':' + selectSm.value + ':00.000Z',
    endTime: props.date + 'T' + selectEh.value + ':' + selectEm.value + ':00.000Z',
    type: selecttype.value === '회의' ? 'CONFERENCE' : 'BUSINESSTRIP',
    summary: summary.value,
    description: description.value
  }
  await createSchedule(
    data,
    (response) => {
      console.log(response)
      router.back()
    },
    (error) => {
      const errorCode = error.response.data.dataHeader?.resultCode
      const errorMessage = error.response.data.dataHeader?.resultMessage
      if (errorCode === 'G-006') {
        alert(errorMessage)
        router.replace({ name: 'notFound' })
      }
      if (errorCode === 'S-002') {
        alert('중복된 시간이 존재합니다. 시간을 확인해 주세요.')
      }
      else {
        alert('잘못된 입력이 존재합니다.')
      }
    }
  )
}
</script>

<style lang="scss" scoped></style>
