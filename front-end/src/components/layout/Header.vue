<template>
  <v-toolbar color="#495464" class="w-100 d-flex align-center">
    <v-toolbar-title class="text-center" @click="router.push({name:'main'})">
      <p class="font-weight-bold text-center text-h4 w-10">MEFI</p>
    </v-toolbar-title>
    <v-spacer></v-spacer>

    <!-- 알림 -->
    <v-btn variant="text" size="40" stacked>
      <v-badge :content="props.alarms.length" color="#E53935">
        <v-icon>mdi-bell-outline</v-icon>
      </v-badge>
      <v-menu activator="parent" v-model="modalAlarm" width="auto">
        <AlarmListVue :alarms="props.alarms" @close="modalAlarm = !modalAlarm" @remove-alarm="removeAlarm" @remove-alarms="removeAlarms"></AlarmListVue>
      </v-menu>
    </v-btn>

    <!-- 설정 -->
    <v-btn variant="text" size="40">
      <font-awesome-icon :icon="['fas', 'gear']" style="font-size: x-large"/>
      <v-dialog v-model="modalSetting" activator="parent" width="auto">
        <v-card>
          <Setting @close="modalSetting = !modalSetting"></Setting>
        </v-card>
      </v-dialog>
    </v-btn>

    <!-- user 설정 -->
    <!-- user 설정 drawer -->
    <v-btn variant="text" size="40" @click.stop="modalUserSetting = !modalUserSetting">
      <font-awesome-icon :icon="['fas', 'user']" style="font-size: x-large"/>
      <v-menu location="buttom" activator="parent">
        <v-list class="overflow-hidden ma-0">
          <!-- 상태 정보 -->
          <v-list-item @click.stop="modalUserStatus = !modalUserStatus">
            상태 정보
            <UserStateSetting @status-change="changeStatus"></UserStateSetting>
          </v-list-item>
          <!-- 회원 정보 조회 및 수정 -->
          <v-list-item style="cursor: pointer;">
            회원 정보
            <v-dialog v-model="modalUserInfo" activator="parent" width="auto">
              <UserInfoSettingVue @close="modalUserInfo = !modalUserInfo"></UserInfoSettingVue>
            </v-dialog>
          </v-list-item>
          <!-- 로그 아웃 -->
          <v-list-item @click="goLogout">로그아웃</v-list-item>
        </v-list>
      </v-menu>
    </v-btn>
  </v-toolbar>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../../stores/user'
import Setting from '../../components/settings/setting/Setting.vue'
import UserInfoSettingVue from '../settings/userInfoSetting/UserInfoSetting.vue'
import UserStateSetting from '../settings/userStateSetting/UserStateSetting.vue'
import AlarmListVue from '../alarm/AlarmList.vue'
import { useRouter } from 'vue-router'
const router = useRouter();
const store = useUserStore();

const emit = defineEmits(['removeAlarm', 'removeAlarms'])

// 읽은 알림 삭제 처리
const removeAlarm = (alarmId) => {
  emit('removeAlarm', alarmId)
}

// 전체 알림 읽음 삭제 처리
const removeAlarms = () => {
  emit('removeAlarms')
}

// 로그아웃
// store logout 함수에 api 추가 및 router.push({name:'home'})
const goLogout = () => {
  store.logout()
}

// 알람 모달창
const modalAlarm = ref(false)
// 설정 모달창
const modalSetting = ref(false)
// 회원 설정 모달창
const modalUserSetting = ref(false)
// 회원 상태 drawer
const modalUserStatus = ref(false)
// 회원 정보 모달창
const modalUserInfo = ref(false)

// 회원 상태 변화 반영 및 모달창 제어
const changeStatus = (color) => {
  store.userInfo.status = color
  modalUserStatus.value = false
} 

// 알림 정보
const props = defineProps({
  alarms:Array,
})

</script>

<style scoped>
.v-list {
  width: 150px;
  overflow: hidden !important;
}
#profile {
  overflow: visible;
}
.green {
  color: green;
}
.red {
  color: red;
}
.yellow {
  color: yellow;
}
.white {
  color: white;
}
</style>
