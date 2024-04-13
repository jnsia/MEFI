<template>
    <div>
        <div class="d-flex flex-column justify-center alien-center pa-6">
            <div class="d-flex flex-row alien-center mb-1" style="border: 1px solid rgb(199, 199, 199); border-radius: 10px;">
                <div class="ma-0 pa-3" style="text-align: center; width: 80px; border-top-left-radius: 10px; border-bottom-left-radius: 10px; background-color: rgb(213, 213, 213);">작성자</div>
                <div class="pa-3">{{ props.alarm.sender }}</div>
            </div>
            <div class="d-flex flex-row alien-center" style="border: 1px solid rgb(199, 199, 199); border-radius: 10px;">
                <div class="ma-0 pa-3" style="text-align: center; width: 80px; border-top-left-radius: 10px; border-bottom-left-radius: 10px; background-color: rgb(213, 213, 213);">작성 일자</div>
                <div class="pa-3">{{ createdTime }}</div>
            </div>
        </div>
        <div style="min-height: 300px; padding: 30px; display: flex;">
            <span style="padding: auto; margin: auto;">{{ props.alarm.message }}</span>
        </div>
        <div class="d-flex flex-row justify-space-around pa-5">
            <v-btn @click.stop="read" color="#45566F" variant="flat" class="w-40">확인</v-btn>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { ref } from 'vue';

const props = defineProps({
    alarm:Object
})
const emit = defineEmits(['close'])
const read = () => {
    emit('close')
}

const createdTime = ref("")
onMounted(()=>{
    const array = props.alarm.createdTime.slice(0,10).split('-')
    const array2 = props.alarm.createdTime.slice(11, 16).split(':')
    createdTime.value = array[0] + "년 " + array[1] + "월 " + array[2] + "일"
    if (array2[0]>12){
        createdTime.value += '  오후 '
        createdTime.value += array2[0]-12 + '시 ' + array2[1] + '분'
    }
    else{
        createdTime.value += '  오전 '
        createdTime.value += String(Number(array2[0])) + '시 ' + array2[1] + '분'
    }
    
})
</script>

<style scoped>
</style>