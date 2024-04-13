<template>
  <v-app v-if="userStore.userInfo !== null" class="bg-grey-darken-4">
    <div :class="['conference-view', layoutType]">
      <v-infinite-scroll id="conference-video" class="ma-0 pa-0" tag="div" :height="layoutType.slice(-1) === '3' ? '' : '90vh'">
        <ConferenceVideo
          v-if="videoStatus.role"
          :videoStatus="videoStatus"
          @end-conference="changeConferenceState"
          @exit-chatBox="changeChatOverlay"
        />
        <template v-slot:loading></template>
      </v-infinite-scroll>
      <div id="conference-document">
        <ConferenceDocument :conferenceState="conferenceState" />
      </div>
    </div>
    <v-bottom-sheet inset v-model="sheet" :scrim="false" >
      <template v-slot:activator="{ props }">
        <div class="text-center" style="position: fixed; bottom: 0; right: 50vw; width: 0px">
          <FontAwesomeIcon v-bind="props" type="button" class="bg-grey-darken-4 pa-1 rounded-xl" :icon="['fas', 'chevron-up']" />
        </div>
      </template>
      <v-list class="d-flex justify-space-around px-2 bg-grey-darken-4 rounded-lg">
        <v-list-item type="button" align="center" @click="changeCameraStatus">
          <font-awesome-icon v-if="videoStatus.cameraStatus" :icon="['fas', 'video']" style="color: #ffffff" />
          <font-awesome-icon v-else :icon="['fas', 'video-slash']" style="color: #ffffff" />
          <p class="text-overline">카메라</p>
        </v-list-item>
        <v-list-item type="button" align="center" @click="changeVoiceStatus">
          <font-awesome-icon v-if="videoStatus.voiceStatus" :icon="['fas', 'microphone']" style="color: #ffffff" />
          <font-awesome-icon v-else :icon="['fas', 'microphone-slash']" style="color: #ffffff" />
          <p class="text-overline">마이크</p>
        </v-list-item>
        <v-list-item type="button" align="center" @click="videoStatus.screenShared = !videoStatus.screenShared">
          <font-awesome-icon :icon="['fas', 'share']" style="color: #ffffff" />
          <p class="text-overline">화면공유</p>
        </v-list-item>
        <v-list-item id="layout-activator" type="button" class="text-center">
          <font-awesome-icon :icon="['fas', 'table-cells-large']" style="color: #ffffff" />
          <p class="text-overline">레이아웃</p>
          <v-menu activator="#layout-activator">
            <v-list class="text-center bg-grey-darken-4">
              <v-list-item @click="changeOverlay('conference-view1')">1번</v-list-item>
              <v-list-item @click="changeOverlay('conference-view2')">2번</v-list-item>
              <v-list-item @click="changeOverlay('conference-view3')">3번</v-list-item>
            </v-list>
          </v-menu>
        </v-list-item>
        <v-list-item type="button" class="text-center" @click="changeChatOverlay">
          <font-awesome-icon :icon="['fas', 'comments']" style="color: #ffffff" />
          <p class="text-overline">채팅창</p>
        </v-list-item>
        <v-list-item v-if="role === 'LEADER'" type="button" class="text-center" @click="doneConference">
          <font-awesome-icon :icon="['fas', 'person-running']" style="color: #ffffff" />
          <p class="text-overline">회의 종료</p>
        </v-list-item>
        <v-list-item v-else type="button" class="text-center" @click="videoStatus.leaveSession = !videoStatus.leaveSession">
          <font-awesome-icon :icon="['fas', 'person-running']" style="color: #ffffff" />
          <p class="text-overline">나가기</p>
        </v-list-item>
      </v-list>
    </v-bottom-sheet>
  </v-app>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSettingStore } from '@/stores/setting'
import ConferenceVideo from '@/components/conference/ConferenceVideo.vue'
import ConferenceDocument from '@/components/conference/ConferenceDocument.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { selectTeamMate, selectTeam } from '@/api/team'
import { doneMeeting, detailConference } from '@/api/conference'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const settingStore = useSettingStore()
const teamId = ref(route.params?.teamid)
const conferenceId = ref(route.params?.conferenceid)
const teamMembers = ref([])
const role = ref('')

// bottom sheet 변수
const sheet = ref(false)
// overlay 설정 변수
const layoutType = ref(settingStore.conferenceLayout)
const conferenceState = ref(true)
const videoStatus = ref({
  layoutType: settingStore.conferenceLayout,
  screenShared: false,
  cameraStatus: true,
  voiceStatus: true,
  leaveSession: false,
  chatLayout: false,
  conferenceDone: false,
  role: '',
  createdSessionId: ''
})

const changeCameraStatus = () => {
  videoStatus.value.cameraStatus = !videoStatus.value.cameraStatus
}
const changeVoiceStatus = () => {
  videoStatus.value.voiceStatus = !videoStatus.value.voiceStatus
}
const changeChatOverlay = () => {
  videoStatus.value.chatLayout = !videoStatus.value.chatLayout
}
const changeConferenceState = () => {
  conferenceState.value = false
}
// overlay 설정 변경 메서드
const changeOverlay = (layout) => {
  layoutType.value = layout
  videoStatus.value.layoutType = layout
  settingStore.conferenceLayout = layout
}

const doneConference = () => {
  doneMeeting(
    { conferenceId: conferenceId.value },
    conferenceId.value,
    (response) => {
      const responseData = response.data?.dataBody
      
      if (responseData === 'Success') {
        videoStatus.value.conferenceDone = true
      }
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

const selectmember = async () => {
  await selectTeamMate(
    teamId.value,
    (response) => {
      if (response.data.dataBody !== null) {
        teamMembers.value = response.data.dataBody
      }
    },
    (error) => {
      console.log(error)
    }
  )
}

const checkRole = async () => {
  await selectTeam(
    (response) => {
      role.value = response.data.dataBody.find((data) => data.teamId == teamId.value).role
      videoStatus.value.role = role.value
    },
    (error) => {
      console.log(error)
    }
  )
}

onMounted(async () => {
  await checkRole()
  await selectmember()


  // 이용자가 팀의 멤버가 맞는지 확인하는 메서드
  const isIncluded = teamMembers.value.filter((teamMember) => {
    return teamMember.email === userStore.userInfo?.email
  })

  if (isIncluded.length === 0) {
    router.replace({ name: 'notFound' })
  }

})
</script>
<style scoped>
/* 레이아웃 배치 공통 스타일 */
.conference-view {
  display: flex;
}
/* 1번 레이아웃 배치 스타일 */
.conference-view1 {
  justify-content: center;
  margin: 40px 0;
}
/* 2번 레이아웃 배치 스타일 */
.conference-view2 {
  flex-direction: row-reverse;
  justify-content: center;
  margin: 40px 0;
}
/* 3번 레이아웃 배치 스타일 */
.conference-view3 {
  flex-direction: column;
  align-items: center;
}
#conference-video {
  display: flex;
  margin: 0 20px;
  border-radius: 10px;
}
#conference-document {
  margin: 0 20px;
  width: 60%;
  background-color: white;
}
/* 사용하지 말 것 */
@keyframes rotate {
  to {
    transform: rotate(360deg);
    transform: scale(1.5);
  }
}
.toRight {
  animation: rotate 3s linear infinite;
  transform-origin: 50% 50%;
}
::-webkit-scrollbar {
  width: 0px; /* 스크롤바의 너비 설정 */
}
::-webkit-scrollbar-thumb {
  background-color: #888; /* 스크롤바의 색상 설정 */
  border-radius: 10px; /* 스크롤바의 모서리 반경 설정 */
}
</style>
