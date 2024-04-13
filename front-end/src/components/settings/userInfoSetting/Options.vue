<template>
    <!-- 선택지 -->
    <v-list v-if="rendering===true">
        <v-list-item class="text-center pa-0" @click="optionChange(1)" :class="{pointColor : color1===true, defaultColor : color1===false}">회원 정보</v-list-item>
        <v-list-item class="text-center pa-0" @click="optionChange(2)" :class="{pointColor : color2===true, defaultColor : color2===false}">비밀 번호 변경</v-list-item>
        <v-list-item class="text-center pa-0" @click="optionChange(3)" :class="{pointColor : color3===true, defaultColor : color3===false}">회원 탈퇴</v-list-item>
    </v-list>
</template>

<script setup>
import { onMounted, ref } from "vue"
const emit = defineEmits(['changeOption'])
const rendering = ref(false)
const color1 = ref(true)
const color2 = ref(false)
const color3 = ref(false)
onMounted(()=>{
    rendering.value = true
})

// style 동적할당 : color 변경

const optionChange = (num)=>{
    if (num==1){
        color1.value = true
        color2.value = false
        color3.value = false
    }else if(num==2){
        color1.value = false
        color2.value = true
        color3.value = false
    }else{
        color1.value = false
        color2.value = false
        color3.value = true
    }
    emit('changeOption',num)
}
</script>

<style scoped>
.pointColor{
    color:white;
    background-color: #45566F;
}
.defaultColor{
    color: black;
    background-color: white;
}
</style>