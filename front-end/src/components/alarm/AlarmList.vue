<template>
    <v-card class="w-30 ma-0 pa-0 d-flex flex-column justify-center" min-width="400" rounded="lg">
        <AlarmHeader @close="closeModal" @read-all="readAll"></AlarmHeader> 
        
            <!-- alarms 있을때 -->
            <v-table v-if="props.alarms?.length > 0" class="ma-0 pa-0" height="300px">
                <v-list class="pa-0" v-for="alarm in props.alarms" :key="alarm.id">
                    <AlarmOne :alarm="alarm" class="cursor-pointer" @click.stop="openAlarm(alarm)"></AlarmOne>
                </v-list>
                <v-dialog v-model="modalAlarm" activator="parent" width="auto">
                    <AlarmMain :alarm="propsAlarm" @close="closeAlarm"></AlarmMain>
                </v-dialog>
            </v-table>

            <!-- alarms 없을때 -->
            <div v-else class="d-flex flex-column justify-center" style="min-height: 100px;">
                <span class="text-center">알림이 없습니다</span>
            </div>
        
    </v-card>
</template>

<script setup>
import AlarmOne from './AlarmOne.vue';
import AlarmHeader from './AlarmHeader.vue';
import AlarmMain from './alarmDetail/AlarmMain.vue';
import { ref } from 'vue';
import { alarmReadAll, alarmReadOne } from '@/api/alarm'

const emit = defineEmits(['close', 'removeAlarm', 'removeAlarms'])

// 알림 상세 조회
const openAlarm = async (alarm) => {
    modalAlarm.value = true
    propsAlarm.value = alarm
    
    // 알림 읽음 처리 API
    await alarmReadOne(alarm.id)
}

// 단일 알림 조회 모달창 닫기
const closeAlarm = (alarmId) => {
    modalAlarm.value = false

    // front alarms remove
    console.log(alarmId)
    emit('removeAlarm', alarmId)
}

// 알림 모달 제어
const modalAlarm = ref(false)

// 알림 상세 조회
const propsAlarm = ref({
    id:0,
    message:'',
    sender:'',
    createdTime:''
})

// 알림 정보
const props = defineProps({
    alarms:Array,
})

// 안 읽은 알림 전체 조회 창 닫기
const closeModal = () => {
    emit('close')
}

// 알림 전체 읽음
const readAll = async () =>{
    await alarmReadAll ();

    emit('removeAlarms');
}
</script>

<style scoped>
</style>