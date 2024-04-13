<template>
  <v-card class="pa-5">
    <v-card-title class="d-flex align-center pa-2">
      <p class="text-h5 font-weight-black">일정 상세</p>
      <v-spacer></v-spacer>
      <div v-if="disabled===true">
        <v-btn v-if="selecttype === '출장'" color="#45566F" class="mx-1" @click="disabled = !disabled" rounded="xl">수정</v-btn>
        <v-btn v-if="selecttype === '출장'" color="#45566F" class="mx-1" @click="deletes" variant="outlined"  rounded="xl">삭제</v-btn>
      </div>
      <div v-else>
        <v-btn class="mx-1" @click="modify" color="#45566F" rounded="xl">저장</v-btn>
        <v-btn color="#45566F" class="mx-1" @click="back" variant="outlined" rounded="xl">취소</v-btn>
      </div>
      
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
          <v-select
            label="Choice Type"
            :items="typeschedule"
            variant="outlined"
            density="compact"
            hide-details="true"
            v-model="selecttype"
            :disabled="disabled"
          ></v-select>
        </v-col>
        <v-col cols="6">
          <v-row>
            <v-col cols="3">
              <v-select v-model="selectSh" :items="starthours" variant="outlined" density="compact" hide-details="true" :disabled="disabled"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectSm" :items="startmins" variant="outlined" density="compact" hide-details="true" :disabled="disabled"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectEh" :items="endhours" variant="outlined" density="compact" hide-details="true" :disabled="disabled"></v-select>
            </v-col>
            <v-col cols="3">
              <v-select v-model="selectEm" :items="endmins" variant="outlined" density="compact" hide-details="true" :disabled="disabled"></v-select>
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
          <v-text-field v-model="summary" label="Summary" variant="outlined" required :disabled="disabled"></v-text-field>
        </v-col>
        <v-col cols="6">
          <v-text-field v-model="description" label="Description" variant="outlined" required :disabled="disabled"></v-text-field>
        </v-col>
      </v-row>
    </v-card-item>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { deleteSchedule, modifySchedule, selectScheduleDetail } from '@/api/schedule'

const router = useRouter()

const starthours = ref(['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21'])
const startmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])
const endhours = ref(['09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22'])
const endmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])

const disabled = ref(true)
const typeschedule = ref(['회의', '출장'])
const selecttype = ref('')
const selectSh = ref('08')
const selectSm = ref('00')
const selectEh = ref('22')
const selectEm = ref('00')
const summary = ref('')
const description = ref('')

const st_typeschedule = ref(['회의', '출장'])
const st_selectSh = ref('08')
const st_selectSm = ref('00')
const st_selectEh = ref('22')
const st_selectEm = ref('00')
const st_summary = ref('')
const st_description = ref('')

const props = defineProps({
  scheduleid: Number,
  date: String
})

const detail = async () => {
  await selectScheduleDetail(
    props.scheduleid,
    (response) => {
      console.log(response.data.dataBody)
      const data = response.data.dataBody
      selectSh.value = data.startedTime.slice(11, 13)
      selectSm.value = data.startedTime.slice(14, 16)
      selectEh.value = data.endTime.slice(11, 13)
      selectEm.value = data.endTime.slice(14, 16)
      selecttype.value = data.type === 'BUSINESSTRIP' ? '출장' : '회의'
      summary.value = data.summary
      description.value = data.description

      st_typeschedule.value = data.type === 'BUSINESSTRIP' ? '출장' : '회의'
      st_selectSh.value = data.startedTime.slice(11, 13)
      st_selectSm.value = data.startedTime.slice(14, 16)
      st_selectEh.value = data.endTime.slice(11, 13)
      st_selectEm.value = data.endTime.slice(14, 16)
      st_summary.value = data.summary
      st_description.value = data.description

    },
    (error) => {
      console.log(error)
    }
  )
}


const modify = async () => {
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
    type: typeschedule.value == '회의' ? 'CONFERENCE' : 'BUSINESSTRIP',
    summary: summary.value,
    description: description.value
  }
  const param = {
    id: props.scheduleid,
    data: data
  }
  await modifySchedule(
    param,
    (response) => {
      console.log(response)
      router.push({name:'main'})
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
      if (errorCode === 'S-003') {
        alert('팀 회의 일정은 개인 수정이 불가능합니다.')
      }
    }
  )
}

const deletes = async () => {
  if(window.confirm('일정을 삭제하시겠습니까?')){
    await deleteSchedule(
      props.scheduleid,
      (response) => {
        console.log(response)
        router.back()
      },
      (error) => {
        console.log(error)
      }
    )
  }
}

onMounted(() => {
  detail()
})


const back = () => {
  selecttype.value = st_typeschedule.value
  selectSh.value = st_selectSh.value
  selectSm.value = st_selectSm.value
  selectEh.value = st_selectEh.value
  selectEm.value = st_selectEm.value
  summary.value = st_summary.value
  description.value = st_description.value
  disabled.value = !disabled.value
}

</script>

<style lang="scss" scoped></style>
