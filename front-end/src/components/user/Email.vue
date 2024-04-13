<template>
    <!-- 이메일 인증 모달창 -->
    <v-sheet class="w-30 ma-auto d-flex flex-column justify-center" min-width="500" style="border-radius: 10px;">
        <!-- 헤더 : close 버튼 -->
        <div style="display: flex; flex-direction: row; justify-content: space-between;" class="pr-5 pt-5">
            <span></span>
            <span @click="close" style="cursor: pointer;"><font-awesome-icon :icon="['fas', 'xmark']" style="font-size:x-large;"/></span>
        </div>

        <!-- 입력창 -->
        <v-form @submit.prevent="vertificate" class="d-flex flex-column justify-center pa-10 pt-0">
            <!-- 문구 -->
            <div class="d-flex justify-center mb-3">
                <span style="font-size:medium;">인증 번호를 입력해주세요</span>
            </div>

            <!-- 인증 번호 입력창  -->
            <v-text-field
                label="인증 번호"
                v-model="authCode"
                hide-details="auto"
                type="email"
                variant="outlined"
                class="mb-5"
            ></v-text-field>

            <!-- 인증 번호 확인 버튼 -->
            <v-btn type="sumbit" class="w-100" variant="flat" color="#45566F">인증 하기</v-btn>
        </v-form>
    </v-sheet>
</template>

<script setup>
import { ref } from "vue"
import { checkEmailCode } from "@/api/user"

// 이메일 인증 정보
const authCode = ref("")

// api 보낼때 필요한 데이터
const props = defineProps({
    email:String,
})

// 기능 : 인증번호 확인 api
// successs : accessToken 저장 및 모달창 닫기
// fail : 인증 번호를 다시 확인해주세요
const vertificate = async () => {
    // api : 인증번호 비교
    const param = {
        "email":props.email,
        "authCode":authCode.value,
    }
    await checkEmailCode(param, 
    (res)=>{
        // localStorage.setItem('accessToken', res.data.dataBody)
        emit('success')
    },
    (err)=>{
        if(err.response.status == 400){
            alert(err.response.data.dataHeader.resultMessage)
        }
        console.log(err)
    })
}

// 모달창 닫기
const emit = defineEmits(['close', 'success'])
const close = () => {
    emit('close')
}
</script>

<style scoped>
</style>