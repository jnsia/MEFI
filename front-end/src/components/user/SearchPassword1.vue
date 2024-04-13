<template>
    <v-sheet class="w-30 ma-auto pa-12 d-flex flex-column justify-center" min-width="500" style="border-radius: 10px;">
        <!-- 문구 -->
        <div class="d-flex justify-center mb-3">
            <span style="font-size:medium;">비밀번호를 찾고자하는 이메일을 입력하세요.</span>
        </div>
        
        <v-form class="d-flex flex-column justify-center ">
            <!-- 이메일 입력창 -->
            <v-text-field
                label="이메일"
                v-model="email"
                type="email"
                variant="outlined"
            ></v-text-field>

            <!-- 이메일 api 던지고, url 받은 클라이언트가 사이트로 다시 연결 -->
            <v-btn  rounded="lg" :disabled="email_check" @click="goSearchPassword" class="w-100 pa-1" variant="flat" color="#45566F">다음</v-btn>
        </v-form>
    </v-sheet>
</template>

<script setup>
import { ref, watch } from "vue"
import { useRouter } from "vue-router"
import { sendemailcode } from "@/api/user"

const router = useRouter()

// 입력받은 이메일
const email = ref("")

// 버튼 : 이메일 형식이 맞으면 인증 버튼 활성화
const email_check = ref(true)
watch(
    ()=>email.value,
    ()=>{
        if (regex_email.test(email.value)) {
            email_check.value = false
        }
    }
)

// 기능 : 이메일 정규식 확인
const rule_email = [
    value => !!value || '필수 항목 입니다.',
    value => (value && regex_email.test(value)) || '이메일 주소를 정확히 입력해주세요',
]
const regex_email = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

// 기능 : 이메일 인증 코드 보내는 api
// 기능 : 이메일 인증 코드 입력하는 컴포넌트로 이동
const goSearchPassword = async () => {
    const param = {
        "email":email.value,
    }
    await sendemailcode(param
    ,(res)=>{
        router.push({name:"search-password2", params:{email:email.value}})}
    ,(err)=>{
        if (err.response.request.status==400){
            alert(err.response.data.dataHeader.resultMessage)
        }
    })
}
</script>

<style scoped>
</style>
