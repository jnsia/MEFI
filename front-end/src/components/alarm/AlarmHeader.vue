<template>
    <div class="d-flex justify-space-between pa-2" style="background-color: #45566F; color: white;">
        <div class="px-2" style="font-size: large;">알림</div>
        <div class="d-flex flex-row">
            <!-- 전체 읽음 체크박스 -->
            <div class="d-flex align-center mr-2" @click.stop>
                <div type="button" @click="allread = true"> 전체읽음</div>
                <!-- <label for="checkbox" @click.stop>전체읽음</label> -->
            </div>
            <!-- 닫힘 버튼 -->
            <span @click.stop="close" class="cursor-pointer mx-2" style="font-size: large;">X</span>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from "vue"

// 닫힘
const emit = defineEmits(['close','readAll'])
const close = () => {
    emit('close')
}

// 전체 읽음
const allread = ref(false)
watch(allread, async()=>{
    if(allread.value == true){
        emit('readAll')
    }
})
</script>

<style scoped>
</style>