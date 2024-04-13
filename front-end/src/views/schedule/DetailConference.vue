<template>
  <v-card class="h-100 pa-5">
    <v-card-title class="d-flex align-center pa-2">
      <p class="text-h5 font-weight-black">회의 상세 정보</p>
      <v-spacer></v-spacer>
      <template v-if="role === 'LEADER'">
        <template v-if="documentState.state === 'modify'">
          <v-btn @click="modifyConference" color="#45566F" rounded="xl">회의 정보 수정하기</v-btn>
        </template>
        <template v-else>
          <v-btn class="mx-1" color="#45566F" rounded="xl" @click="documentState.state = 'modify'">회의 정보 수정하기</v-btn>
          <v-btn class="mx-1" color="#45566F" rounded="xl" variant="outlined" @click="cancelConference" >회의 예약 삭제하기</v-btn>
          <v-btn
            class="mx-1"
            color="#45566F" rounded="xl" variant="outlined"
            @click="
              router.push({
                name: 'conference',
                params: { teamid: teamId, conferenceid: conferenceId }
              })
            "
            >회의 시작하기</v-btn
          >
        </template>
      </template>
      <template v-else>
        <v-btn
          color="#45566F" rounded="xl" variant="outlined"
          @click="
            router.push({
              name: 'conference',
              params: { teamid: teamId, conferenceid: conferenceId }
            })
          "
          >회의 참여하기</v-btn
        >
      </template>
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
            v-model="selectvalue"
            :items="typeschedule"
            hide-selected="1"
            variant="outlined"
            density="compact"
            hide-details="true"
            disabled
          ></v-select>
        </v-col>
        <v-col cols="6">
          <v-row>
            <v-col cols="3">
              <v-select
                v-model="selectSh"
                :items="starthours"
                variant="outlined"
                density="compact"
                hide-details="true"
                :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
              ></v-select>
            </v-col>
            <v-col cols="3">
              <v-select
                v-model="selectSm"
                :items="startmins"
                variant="outlined"
                density="compact"
                hide-details="true"
                :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
              ></v-select>
            </v-col>
            <v-col cols="3">
              <v-select
                v-model="selectEh"
                :items="endhours"
                variant="outlined"
                density="compact"
                hide-details="true"
                :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
              ></v-select>
            </v-col>
            <v-col cols="3">
              <v-select
                v-model="selectEm"
                :items="endmins"
                variant="outlined"
                density="compact"
                hide-details="true"
                :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
              ></v-select>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <p>제목</p>
        </v-col>
        <v-col cols="6">
          <p>설명</p>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-text-field
            v-model="title"
            label="title"
            variant="outlined"
            density="compact"
            required
            :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
          ></v-text-field>
        </v-col>
        <v-col cols="6">
          <v-text-field
            v-model="description"
            label="description"
            variant="outlined"
            density="compact"
            required
            :disabled="role === 'MEMBER' || documentState.state !== 'modify'"
          ></v-text-field>
        </v-col>
      </v-row>
    </v-card-item>
    <v-card-title class="d-flex align-center pa-2">
      <p class="text-h5 font-weight-black">회의 관련 문서</p>
    </v-card-title>
    <div class="pa-3">
      <SearchDoc :documentState="documentState" :changeDone="changeDone" />
    </div>
  </v-card>
</template>
<script setup>
import SearchDoc from '@/components/docs/SearchDoc.vue'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { detailConference, cancelMeeting, modifyAllMeeting } from '@/api/conference.js'
import { selectTeam } from '@/api/team.js'

const starthours = ref(['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21'])
const startmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])
const endhours = ref(['09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22'])
const endmins = ref(['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'])
const typeschedule = ref(['회의'])
const selectvalue = ref('회의')
const route = useRoute()
const router = useRouter()
const teamId = ref(route.params?.teamid)
const conferenceId = ref(route.params?.conferenceid)

const documentState = ref({
  state: 'detail',
  conferenceId: conferenceId.value,
  role: ''
})

const title = ref('')
const description = ref('')
const date = ref('')
const selectSh = ref('')
const selectSm = ref('')
const selectEh = ref('')
const selectEm = ref('')
const role = ref('')

const modifyConference = () => {
  const conferenceModifyAllReqDto = {
    callStart: date.value + 'T' + selectSh.value + ':' + selectSm.value + ':00.000Z',
    callEnd: date.value + 'T' + selectEh.value + ':' + selectEm.value + ':00.000Z',
    title: title.value,
    description: description.value
  }

  modifyAllMeeting(
    conferenceModifyAllReqDto,
    conferenceId.value,
    (response) => {
      documentState.value.state = 'done'
      router.push({ name: 'team', params: { id: teamId.value } })
    },
    (error) => {
      const errorCode = error.response.data.dataHeader?.resultCode
      const errorMessage = error.response.data.dataHeader?.resultMessage
      if (errorCode === 'G-006') {
        alert(errorMessage)
        router.replace({ name: 'notFound' })
      }
      if (errorCode === "C-001") {
        alert('이미 종료됐거나 취소된 회의입니다.')
      }
      if (errorCode === 'S-002') {
        alert('중복된 시간이 존재합니다. 시간을 확인해 주세요.')
      }
      else {
        console.log(error)
        alert('잘못된 입력이 존재합니다.')
      }
    }
  )
}

const cancelConference = () => {
  cancelMeeting(
    { conferenceId: conferenceId.value },
    conferenceId.value,
    (response) => {
      console.log(response.data?.dataBody)
      router.push({ name: 'team', params: { id: teamId.value } })
    },
    (error) => {
      console.log(error)
      if (errorCode === "C-001") {
        alert('이미 종료됐거나 취소된 회의입니다.')
      }
    }
  )
}

const getConferenceDetail = () => {
  detailConference(
    { conferenceId: conferenceId.value },
    conferenceId.value,
    (response) => {
      const responseData = response.data?.dataBody
      console.log(responseData)

      title.value = responseData?.title
      description.value = responseData?.description
      date.value = responseData?.callStart.slice(0, 10)

      const callStart = responseData?.callStart.slice(11).split(':')

      selectSh.value = callStart[0]
      selectSm.value = callStart[1]

      const callEnd = responseData?.callEnd.slice(11).split(':')
      selectEh.value = callEnd[0]
      selectEm.value = callEnd[1]
    },
    (error) => {
      const errorCode = error.response.data.dataHeader?.resultCode
      const errorMessage = error.response.data.dataHeader?.resultMessage

      if (errorCode === 'C-001' || errorCode === 'G-006') {
        alert(errorMessage)
        router.replace({ name: 'notFound' })
      }
    }
  )
}

const checkRole = async () => {
  await selectTeam(
    (response) => {
      role.value = response.data.dataBody.find((data) => data.teamId == teamId.value).role
      documentState.value.role = role.value
    },
    (error) => {
      console.log(error)
    }
  )
}

onMounted(() => {
  checkRole()
  getConferenceDetail()
})
</script>
<style lang="scss" scoped></style>
