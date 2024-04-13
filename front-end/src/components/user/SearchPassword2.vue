<template>
    <!-- 이메일 인증 모달창 -->
    <v-sheet class="w-30 ma-auto pa-12 d-flex flex-column justify-center" min-width="500" style="border-radius: 10px;">
        <!-- 문구 -->
        <div class="d-flex justify-center mb-3">
            <span style="font-size:medium;">인증 번호를 입력해주세요</span>
        </div>

        <!-- 입력창 -->
        <v-form @submit.prevent="vertificate" class="d-flex flex-column justify-center">
            <!-- 인증 번호 입력창  -->
            <v-text-field
                label="인증 번호"
                v-model="authCode"
                variant="outlined"
            ></v-text-field>
            <!-- 인증 번호 확인 버튼 -->
            <v-btn rounded="lg" type="sumbit" class="w-100 pa-1" variant="flat" color="#45566F">인증 하기</v-btn>
        </v-form>
    </v-sheet>
</template>

<script setup>
import { ref } from "vue"
import { checkEmailCode } from "@/api/user"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()
// 이메일 인증 정보
const authCode = ref("")

// api 보낼때 필요한 데이터
const email = route.params.email

// 기능 : 인증코드 확인 api
const vertificate = async () => {
    const param = {
        "email":email,
        "authCode":authCode.value,
    }
    await checkEmailCode(param, 
    (res)=>{
        console.log(email)
        router.push({name:"search-password3", params:{email:email}})
    },
    (err)=>{
        alert('다시 인증 코드를 입력해주세요')
    })
}
</script>

<style scoped>
</style>
