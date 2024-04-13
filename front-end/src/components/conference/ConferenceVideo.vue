<template>
  <div>
    <v-btn v-if="selectedVideo" class="cancleButton bg-grey-darken-4" @click="cancleExpand"
      ><font-awesome-icon :icon="['fas', 'xmark']" style="color: #ffffff" />
    </v-btn>
    <!-- 내 카메라가 켜졌을 때 화상회의 열기 -->
    <div id="session" v-if="mainStreamManager">
      <!-- isSide ref 변수에 따라 class를 동적 할당하여 스타일 변경 -->
      <div id="video-container" ref="videos" :class="['bg-grey-darken-4', isSide ? 'videos' : 'upSideVideos']">
        <UserVideo
          :stream-manager="mainStreamManager"
          :class="[
            isSide ? 'video' : 'upSideVideo',
            // 마이크 입력을 인식하면 클래스 적용
            onSpeak ? 'toRight' : '',
            selectedVideo === 'mainStream' ? 'selectedVideo' : ''
          ]"
          @click="expandVideo('mainStream')"
        />
        <UserVideo
          v-for="sub in subscribers"
          :class="[
            isSide ? 'video' : 'upSideVideo',
            // 말하고 있는 참가자에 따라 class 속성 변경
            onSpeakSub.includes(sub.stream.connection.connectionId) ? 'toRight' : '',
            selectedVideo === sub.stream.connection.connectionId ? 'selectedVideo' : ''
          ]"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="expandVideo(sub.stream.connection.connectionId)"
        />
      </div>
      <v-overlay persistent :scrim="false" no-click-animation :model-value="chatOverlay" class="bg-transparent align-end justify-end">
        <div class="w-100 bg-transparent" elevation="0">
          <v-btn size="large" class="bg-blue-grey-darken-4 rounded-lg" elevation="0" @click="exitChatBox" style="position: absolute; right: 0">
            <font-awesome-icon :icon="['fas', 'xmark']" style="color: #ffffff" />
          </v-btn>
        </div>
        <v-infinite-scroll load id="chatBox" ref="chatBox" class="bg-blue-grey-darken-4 pt-10 px-5 rounded-t-lg" width="400px" height="50vh">
          <div v-for="chat in chats" class="d-flex justify-end">
            <div style="font-size: 16px" :class="['chat-box', chat.split('-')[0] === userEmail ? 'right-chat' : 'left-chat']">{{ chat.split('-')[0] === userEmail ? chat.split(':')[1] : chat.split('-')[1] }}</div>
          </div>
          <template v-slot:loading></template>
        </v-infinite-scroll>
        <v-divider :thickness="1"></v-divider>
        <div class="d-flex bg-blue-grey-darken-4 px-4 align-center rounded-b-lg">
          <v-textarea
            class="bg-blue-grey-darken-4 mt-4 mr-2"
            auto-grow
            rows="1"
            row-height="1"
            density="compact"
            v-model="chatInput"
            @keydown.enter="sendChat(chatInput)"
          ></v-textarea>
          <v-btn @click="sendChat(chatInput)" rounded="lg">SEND</v-btn>
        </div>
      </v-overlay>
    </div>
    <!-- 내 카메라 아직 켜지지 않았으면 로딩 스피너 출력 -->
    <div v-else class="loading bg-grey-darken-4">
      <v-progress-circular indeterminate size="64" width="6"></v-progress-circular>
    </div>
  </div>
</template>
<script setup>
import { OpenVidu } from 'openvidu-browser'
import { ref, onBeforeUnmount, onMounted, defineProps, onUpdated, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { checkDone, makeToken, makeSession } from '@/api/video'
import { detailConference } from '@/api/conference'
import { useUserStore } from '@/stores/user'
import UserVideo from './UserVideo.vue'
const router = useRouter()
const props = defineProps({
  videoStatus: Object
})
const userStore = useUserStore()
const route = useRoute()
const teamId = ref(route.params?.teamid)
const conferenceId = ref(route.params?.conferenceid)
// 1, 2번 layout은 true | 3번 layout은 false
const isSide = ref(true)
// 본인 마이크 입력 인식
const onSpeak = ref(false)
// 말하고 있는 참가자 id 배열
const onSpeakSub = ref([])
const chatInput = ref('')
const chats = ref([])
const selectedVideo = ref('')
const chatOverlay = ref(false)
const chatBox = ref(null)
const OVCamera = ref(null)
const OVScreen = ref(null)
const sessionCamera = ref(undefined)
const sessionScreen = ref(undefined)
const mainStreamManager = ref(null)
const publisher = ref(null)
const publisherScreen = ref(null)
const subscribers = ref([])
// Join form
const sessionId = ref(`meficonference-${teamId.value}-${conferenceId.value}`)
const createdSessionId = ref('')
const userEmail = ref(userStore?.userInfo.email)
const userName = ref(userStore?.userInfo.name)
onMounted(() => {
  joinSession()
  // 레이아웃에 따라 ref 변수 변경
  changeOverlay()
})
onUpdated(() => {
  // 레이아웃에 따라 ref 변수 변경
  changeOverlay()
})
const expandVideo = (connectionId) => {
  selectedVideo.value = connectionId
}
const cancleExpand = () => {
  selectedVideo.value = ''
}
// 레이아웃에 따라 ref 변수 변경
const changeOverlay = () => {
  if (props.videoStatus.layoutType.slice(-1) === '3') {
    isSide.value = false
  } else {
    isSide.value = true
  }
}
// 카메라 온/오프 여부 확인
watch(
  () => props.videoStatus.cameraStatus,
  () => {
    publisher.value.publishVideo(props.videoStatus.cameraStatus)
  }
)
// 마이크 온/오프 여부 확인
watch(
  () => props.videoStatus.voiceStatus,
  () => {
    publisher.value.publishAudio(props.videoStatus.voiceStatus)
  }
)
// 채팅방 오픈 여부 확인
watch(
  () => props.videoStatus.chatLayout,
  () => {
    chatOverlay.value = !chatOverlay.value
  }
)
// 레이아웃 변경 여부 확인
watch(
  () => props.videoStatus.layoutType,
  () => {
    changeOverlay()
  }
)
// 화면 공유 여부 확인
watch(
  () => props.videoStatus.screenShared,
  () => {
    publishScreenShare()
  }
)
// 세션 나가기 여부 확인
watch(
  () => props.videoStatus.leaveSession,
  () => {
    leaveSession()
  }
)

// 팀장이 회의 종료하였을 경우 팀원들을 세션에서 방출함
watch(
  () => props.videoStatus.conferenceDone,
  () => {
    if (props.videoStatus.conferenceDone) {
      sessionCamera.value
        .signal({
          data: `conferenceDone`,
          to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
          type: 'conferenceDone' // The type of message (optional)
        })
        .catch((error) => {
          console.error(error)
        })
    }
  }
)

// 채팅 보내는 함수
const sendChat = (content) => {
  // 입력값이 없을 경우 pass
  if (!content) return
  // session signal 메서드를 통해
  // 세션 내의 참가자에게 'chat' 타입의 시그널을 임의의 데이터와 보냄
  sessionCamera.value
    .signal({
      data: `${userEmail.value}-${userName.value}: ${content}`,
      to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
      type: 'chat' // The type of message (optional)
    })
    .then(() => {
      chatInput.value = ''
    })
    .catch((error) => {
      console.error(error)
    })
}
// 세션 참가
const joinSession = () => {
  OVCamera.value = new OpenVidu()
  OVScreen.value = new OpenVidu()
  // 마이크 입력 빈도와 제한 조절
  OVCamera.value.setAdvancedConfiguration({
    publisherSpeakingEventsOptions: {
      interval: 100, // Frequency of the polling of audio streams in ms (default 100)
      threshold: -50 // Threshold volume in dB (default -50)
    }
  })
  // 카메라 세션과 화면 공유 세션을 따로 생성
  sessionCamera.value = OVCamera.value.initSession()
  sessionScreen.value = OVScreen.value.initSession()
  // 카메라 스트림 생성
  sessionCamera.value.on('streamCreated', ({ stream }) => {
    if (stream.typeOfVideo == 'CAMERA') {
      const subscriber = sessionCamera.value.subscribe(stream)
      subscribers.value.push(subscriber)
    }
  })
  // 화면 공유 스트림 생성
  sessionScreen.value.on('streamCreated', ({ stream }) => {
    if (stream.typeOfVideo == 'SCREEN') {
      // Subscribe to the Stream to receive it. HTML video will be appended to element with 'container-screens' id
      const subscriberScreen = sessionScreen.value.subscribe(stream)
      subscribers.value.push(subscriberScreen)
    }
  })
  // 카메라 스트림 삭제
  sessionCamera.value.on('streamDestroyed', ({ stream }) => {
    const index = subscribers.value.indexOf(stream.streamManager, 0)
    if (index >= 0) {
      subscribers.value.splice(index, 1)
    }
  })
  // 화면 공유 스트림 삭제
  sessionScreen.value.on('streamDestroyed', ({ stream }) => {
    const index = subscribers.value.indexOf(stream.streamManager, 0)
    if (index >= 0) {
      subscribers.value.splice(index, 1)
    }
  })
  sessionCamera.value.on('exception', ({ exception }) => {
    console.warn(exception)
  })
  // 마이크 입력 시작 이벤트
  sessionCamera.value.on('publisherStartSpeaking', (event) => {
    onSpeak.value = true
    // 말하고 있는 사람 connection id를 onSpeakSub 배열에 삽입
    onSpeakSub.value.push(event.connection.connectionId)
  })
  // 마이크 입력 종료 이벤트
  sessionCamera.value.on('publisherStopSpeaking', (event) => {
    onSpeak.value = false
    // onSpeakSub 배열에서 마이크 입력이 없는 connection id를 삭제
    onSpeakSub.value.splice(event.connection.connectionId, 1)
  })
  // 미디어 서버와 카메라 세션을 연결
  getToken(sessionId.value).then((token) => {
    sessionCamera.value
      .connect(token, JSON.stringify({ clientData: userName.value }))
      .then(() => {
        const newPublisher = OVCamera.value.initPublisher(undefined, {
          audioSource: undefined,
          videoSource: undefined,
          publishAudio: true,
          publishVideo: true,
          resolution: '640x480',
          frameRate: 30,
          insertMode: 'APPEND',
          mirror: false
        })
        mainStreamManager.value = newPublisher
        publisher.value = newPublisher
        sessionCamera.value.publish(publisher.value)
      })
      .catch((error) => {
        console.log('There was an error connecting to the session:', error.code, error.message)
      })
  })
  // 미디어 서버와 화면 공유 세션을 연결
  getToken(sessionId.value).then((tokenScreen) => {
    // Create a token for screen share
    sessionScreen.value
      .connect(tokenScreen, JSON.stringify({ clientData: userName.value }))
      .then(() => {
        console.log('Session screen connected')
      })
      .catch((error) => {
        console.warn('There was an error connecting to the session for screen share:', error.code, error.message)
      })
  })
  // type이 chat인 signal을 받을 때 chats 배열에 data 삽입
  sessionCamera.value.on('signal:chat', (event) => {
    chats.value.push(event.data)
    // 새로운 채팅이 들어오면 스크롤을 가장 아래로 당김
    const chatBox = document.querySelector('#chatBox')
    chatBox.scrollTo(0, chatBox.scrollHeight)
  })

  sessionCamera.value.on('signal:conferenceDone', (event) => {
    alert('회의가 종료되었습니다.')
    leaveSession()
  })

  window.addEventListener('beforeunload', leaveSession)
}

// 세션 퇴장
const leaveSession = async () => {
  if (sessionCamera.value) sessionCamera.value.disconnect()
  if (sessionScreen.value) sessionScreen.value.disconnect()
  sessionCamera.value = null
  sessionScreen.value = null
  mainStreamManager.value = null
  publisher.value = null
  publisherScreen.value = null
  subscribers.value = []
  OVCamera.value = null
  checkConferenceDone(sessionId.value)
}

// 화면 공유
const publishScreenShare = () => {
  // 화면 공유 초기 설정
  const publisherScreen = OVScreen.value.initPublisher(undefined, {
    audioSource: undefined,
    videoSource: 'screen',
    resolution: '640x480',
    frameRate: 30,
    insertMode: 'APPEND',
    mirror: false
  })
  // 화면 공유 권한이 있을 경우
  publisherScreen.once('accessAllowed', (event) => {
    // 화면 공유를 중지하면
    // 화면 공유 스트림 삭제 후 카메라 스트림 생성
    publisherScreen.stream
      .getMediaStream()
      .getVideoTracks()[0]
      .addEventListener('ended', () => {
        console.log('User pressed the "Stop sharing" button')
        sessionScreen.value.unpublish(publisherScreen)
        sessionCamera.value.publish(publisher.value)
      })
    // 카메라 스트림 삭제 및 화면 공유 스트림 생성
    sessionCamera.value.unpublish(publisher.value)
    sessionScreen.value.publish(publisherScreen)
  })
  publisherScreen.once('accessDenied', (event) => {
    console.error('Screen Share: Access Denied')
  })
}

const getConferenceDetail = async () => {
  await detailConference(
    { conferenceId: conferenceId.value },
    conferenceId.value,
    (response) => {
      const responseData = response.data?.dataBody
      createdSessionId.value = responseData?.thumbnailUrl
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

const getToken = async (sessionId) => {
  if (props.videoStatus.role === 'LEADER') {
    await createSession(sessionId)
    return await createToken(createdSessionId.value)
  } else {
    await getConferenceDetail()
    return await createToken(createdSessionId.value)
  }
}

const createSession = async (sessionId) => {
  await makeSession(
    { customSessionId: sessionId },
    teamId.value,
    conferenceId.value,
    (response) => {
      createdSessionId.value = response?.data.dataBody
    },
    (error) => {
      const errorCode = error.response.data.dataHeader?.resultCode

      if (errorCode === 'G-003') {
        alert('회의가 아직 생성되지 않았습니다.')
        router.go(-1)
      }
    }
  )
}

const createToken = async (sessionId) => {
  let createdToken

  await makeToken(
    { sessionId: sessionId },
    teamId.value,
    (response) => {
      createdToken = response.data?.dataBody.token
    },
    (error) => {
      if (error.response.status === 404) {
        alert('아직 회의가 시작되지 않았습니다.')
        router.go(-1)
      }
    }
  )
  return createdToken
}

// 회의가 종료되었는지 확인하는 메서드
// response.status가 200이면 회의 진행 중
// response.status가 503이면 회의 종료
const checkConferenceDone = async (sessionId) => {
  checkDone(
    createdSessionId.value,
    (response) => {
      // 회의 종료 시 상위 컴포넌트에 알림
      if (response.status === 204) {
        emit('endConference')
      } else {
        router.push({ name: 'team', params: { id: teamId.value } })
      }
    },
    (error) => {
      console.error(error)
    }
  )
}

const emit = defineEmits(['endConference', 'exitChatBox'])
const exitChatBox = () => {
  emit('exitChatBox')
}

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', leaveSession)
})
// const updateMainVideoStreamManager = (stream) => {
//   if (mainStreamManager.value === stream) return (mainStreamManager.value = stream)
// }
</script>
<style scoped>
/* 1, 2번 레이아웃 비디오 컨테이너 스타일 */
.videos {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  background-color: black;
  gap: 10px;
  max-width: 600px;
}
/* 1, 2번 레이아웃 비디오 스타일 */
.video {
  display: flex;
  width: 45%;
  height: 100%;
}
/* 3번 레이아웃 비디오 컨테이너 스타일 */
.upSideVideos {
  display: flex;
  justify-content: center;
  top: 0;
  background-color: black;
  padding: 0 10px;
  gap: 10px;
}
/* 3번 레이아웃 비디오 스타일 */
.upSideVideo {
  display: flex;
  height: 100px;
}
.selectedVideo {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  background-color: black;
}
.cancleButton {
  position: fixed;
  width: 0px;
  right: 10px;
  bottom: 10px;
  z-index: 2;
}
/* 화면 크기에 따라 스타일 조정 */
@media (max-width: 720px) {
  .videos {
    flex-direction: column;
  }
  .video {
    width: 100%;
  }
}
.loading {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.left-chat {
  padding: 10px 20px;
  margin: 10px 0;
  color: black;
  background-color: white;
  border-radius: 10px;
  max-width: 200px;
  margin-right: auto;
  word-break: break-all;
}

.right-chat {
  padding: 10px 20px;
  margin: 10px 0;
  color: black;
  background-color: white;
  border-radius: 10px;
  max-width: 200px;
  word-break: break-all;
}

/* 캠돌리기 파티 */
@keyframes rotate {
  to {
    transform: rotate(360deg);
  }
}
/* 캠 크기 증가 애니메이션 */
@keyframes sizeup {
  to {
    transform: scale(1.2);
  }
}
.toRight {
  /* animation: rotate 2s linear infinite; */
  /* animation: sizeup linear; */
  border: 2px solid #b2ff59;
  border-radius: 10px;
}
::-webkit-scrollbar {
  width: 0px; /* 스크롤바의 너비 설정 */
}
::-webkit-scrollbar-thumb {
  background-color: #888; /* 스크롤바의 색상 설정 */
  border-radius: 10px; /* 스크롤바의 모서리 반경 설정 */
}
</style>
