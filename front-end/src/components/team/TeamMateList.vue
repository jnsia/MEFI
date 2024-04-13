<template>
  <div class="bg-white h-100 w-100 elevation-3 pa-0 rounded-lg">
    <v-toolbar height="40" class="elevation-1 rounded-t-lg" color="#ffffff">
      <v-toolbar-title>
        <p class="font-weight-bold text-start text-h7">팀원 목록</p>
      </v-toolbar-title>
    </v-toolbar>
    <div class="mt-1">
      <v-list v-if="members">
        <v-list-item
            v-for="member in members"
          >
          <p class="font-weight">{{ member.name }}</p>
        </v-list-item>
      </v-list>
    </div>
    <!-- <template v-slot:loading></template> -->
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue';
import { selectTeamMate } from '@/api/team.js';

const props = defineProps({
  teamId: Number
});

const members = ref([])

const selectmember = async () => {
    await selectTeamMate(
        props.teamId,(response) => {
            members.value = response.data.dataBody
        },
        (error)=>{
            console.log(error)
        }
    )
  }

// 최초 생성시 팀원 조회
onMounted(() => {
  selectmember();
});

// 팀 전환시 팀원 조회
watchEffect((props, (newvalue) => {
  selectmember();
}))


</script>

<style scoped>

</style>