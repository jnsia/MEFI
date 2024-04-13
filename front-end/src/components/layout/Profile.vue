<template>
  <v-infinite-scroll v-if="store.userInfo !== null" class="w-100 h-100 d-flex flex-column  pa-3">
    <!-- 회원 정보 -->
    <div class="d-flex mb-6 align-center">
      <div class="d-flex">
        <!-- 이미지 -->
        <img
          class="rounded-circle"
          width="55px"
          height="55px"
          :src="userphoto"
          alt="profile-image"
        />
      </div>
      <!-- 회원 정보 -->
      <div class="w-75 pl-2">
        <p class="font-weight-bold ml-1" style="font-size: 15px;">{{ userInfo.name }}</p>
        <p class="ml-1" style="color: rgb(155, 155, 155); font-size: 13px;">{{ userInfo.email }}</p>
        <p class="ml-1" style="color: rgb(155, 155, 155); font-size: 13px;">
          <span>{{ userInfo.dept }}</span> | 
          <span>{{ userInfo.position }}</span>
        </p>
      </div>
    </div>

    <!-- 개인 일정 조회 -->
    <v-btn 
      class="elevation-0 my-2 mb-7"
      style="font-weight: bolder; border: 1px solid rgb(148, 148, 148); height: 50px;"
      @click="router.push({ name: 'main' })"
      rounded="lg">
      <font-awesome-icon :icon="['far', 'calendar']" style="font-size: large;"/>
      <span style="margin-left: 10px;">개인일정조회</span>
    </v-btn>

    <!-- 팀 목록 및 팀 일정 조회 -->
    <div>
      <!-- 팀 목록 타이틀 및 생성 -->
      <div class="d-flex mx-4 justify-space-between align-center" >
        <p style="font-weight: bold; font-size: 18px;">팀 목록</p>
        <font-awesome-icon :icon="['fas', 'plus']" @click="dialog=true" style="cursor: pointer;"/>
        <v-dialog v-model="dialog" persistent width="70%" height="70%">
          <TeamCreateDialog @close-dialog="dialog = false"/>
        </v-dialog>
      </div>

      <!-- 팀 목록 -->
      <div>
        <v-list v-if="teams">
          <v-list-item
              v-for="team in teams"
              :key="team"
              :title="team.teamName"
              :value="team.teamId"
              @click="goTeamPage(team.teamId)"
              style="border-radius: 10px;"
              color="primary"
              class="ma-2"
            ></v-list-item>

            
        </v-list>
      </div>
    </div>
    <template v-slot:loading></template>
  </v-infinite-scroll>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import TeamCreateDialog from '@/components/team/TeamCreateDialog.vue'
import { useRouter } from 'vue-router'
import { selectTeam } from '@/api/team.js'
import { useUserStore } from "@/stores/user"
const store = useUserStore()
const userInfo = ref(store.userInfo)
const userphoto = ref(`data:image/jpeg;base64, ${store.userInfo?.profile}`)

const dialog = ref(false)
const router = useRouter()
const teams = ref([])

watch(()=> store.userInfo, () => {
  userInfo.value = store.userInfo
  userphoto.value = `data:image/jpeg;base64, ${store.userInfo?.profile}`
  console.log(userInfo.value)
})

const select = async () => {
    await selectTeam(
        (response) => {
            teams.value = response?.data.dataBody
        },
        (error)=>{
            console.log(error)
        }
    )
}

watch(() => dialog.value, () => {
  select()
})

onMounted(() => {
  if (store.userInfo !== null) {
    select()
  }
});

const goTeamPage = (id) => {
  console.log('check')
  router.push({name: 'team', params: { id }})
}
</script>

<style scoped>
::-webkit-scrollbar {
  display: none;
}
</style>

