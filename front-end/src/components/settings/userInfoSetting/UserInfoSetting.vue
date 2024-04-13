<template>
    <v-sheet min-width="500">
        <!-- 헤더 -->
        <HeaderVue :option="option" @close="close" class="d-flex flex-row justify-space-between pa-3"></HeaderVue>
        <v-row no-gutters>
            <!-- 선택지 -->
            <v-col cols="3"><OptionsVue @change-option="optionChange"></OptionsVue></v-col>
            <v-col style="border-left: 1px solid #c4c4c4">
                <!-- 선택지에 따른 컴포넌트 -->
                <!-- 회원 정보 조회 및 수정 -->
                <UserInfoVue v-if="option===1"></UserInfoVue>
                <!-- 비밀번호 변경 -->
                <ChangePwdVue v-if="option===2" @close="close"></ChangePwdVue>
                <!-- 회원 탈퇴 -->
                <UserDelete v-if="option===3" @close="close"></UserDelete>
            </v-col>
        </v-row>
    </v-sheet>
</template>

<script setup>
import HeaderVue from "./Header.vue";
import OptionsVue from "./Options.vue";
import UserInfoVue from "./UserInfo.vue";
import ChangePwdVue from "./ChangePwd.vue";
import UserDelete from "./UserDelete.vue";
import { ref } from "vue";

// 선택지 ( 기본 첫번째꺼 클릭되어 있음)
const option = ref(1)
const optionChange = (num)=>{
    option.value = num
}

// 모달창 닫기
const emit = defineEmits(['close'])
const close = () => {
    emit('close')
}
</script>

<style scoped>
</style>