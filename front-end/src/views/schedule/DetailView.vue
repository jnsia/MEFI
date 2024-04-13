<template>
  <template v-if="type === 'schedule'">
    <ScheduleDialog/>
  </template>
  <template v-else>
    <ConferenceDialog/>
  </template>
</template>

<script setup>
import { ref, defineProps } from 'vue';
import ScheduleDialog from '@/components/dialog/ScheduleDialog.vue';
import ConferenceDialog from '@/components/dialog/ConferenceDialog.vue';
// defineProps를 사용하여 동적으로 전달된 props를 가져옵니다.
const props = defineProps({
  type:String,
  scheduleId: String
});

const type = ref(props.type)


</script>
  
  <style>
  /* 스타일을 추가할 수 있습니다. */
  </style>
  