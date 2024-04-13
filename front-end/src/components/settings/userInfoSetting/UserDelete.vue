<template>
    <!-- 회원 탈퇴 창 -->
    <v-sheet class="pa-5" min-height="300">
        <div class="d-flex flex-column justify-center alien-center pa-3">
            <span class="text-center my-10" style="font-weight: bold;">회원 탈퇴 하시겠습니까?</span>
            <v-text-field
                label="현재 비밀번호"
                variant="outlined"
                type="password"
                v-model="password"
            ></v-text-field>
            <div class="d-flex flex-row justify-space-around alien-center">
                <v-btn @click="Delete()" color="#45566F" variant="flat" class="w-40" rounded="lg">예</v-btn>
                <v-btn @click="goOut()" variant="outlined" class="w-40" rounded="lg">아니오</v-btn>
            </div>
        </div>
    </v-sheet>
</template>

<script setup>
import { userDelete } from '@/api/user';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter()
const password = ref('')

// 회원 탈퇴하는 함수
const Delete = async () => {
    const param = {
        "currentPassword" : password.value
    }
    await userDelete(param,
    (res)=>{router.push({name:"login"})},
    (err)=>{console.log(err)})
}

// 목적 : 상위 컴포넌트로 전달
// 기능 : 해당 모달창 닫기
const emit = defineEmits(['close'])
const goOut = () => {
    emit('close')
}
</script>

<style scoped>
</style>
