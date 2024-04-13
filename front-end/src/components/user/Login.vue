<template>
    <v-sheet class="w-30 ma-auto pa-12 d-flex flex-column justify-center" min-width="500" style="border-radius: 10px;">

        <!-- 로그 -->
        <div class="d-flex justify-center">
            <span style="font-size: xx-large; font-weight: bolder;">MEFI</span>
        </div>

        <!-- 입력창 -->
        <v-form @submit.prevent="login" class="d-flex flex-column justify-center">
            <!-- 이메일 입력 -->
            <v-text-field
                label="이메일"
                v-model="email"
                hide-details="auto"
                type="email"
                class="ma-1"
                variant="outlined"
                prepend-inner-icon="mdi-email-outline"
            ></v-text-field>
            
            <!-- 비밀번호 입력 -->
            <v-text-field
                label="비밀번호"
                v-model="password"
                hide-details="auto"
                variant="outlined"
                class="ma-1"
                :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
                :type="visible ? 'text' : 'password'"
                @click:append-inner="visible = !visible"
                prepend-inner-icon="mdi-lock-outline"
            ></v-text-field>

            <!-- 로그인 버튼 -->
            <v-btn type="submit" class="w-100 h-20" variant="flat" color="#45566F">로그인</v-btn>
        </v-form>

        <!-- 비밀번호 찾기 / 회원 가입 -->
        <div class="d-flex flex-row justify-center mt-3">
            <a @click="goSearchPassword" class="cursor-pointer" style="margin: auto 0px;">비밀번호 찾기 </a>
            <span class="ma-2">|</span>
            <a @click="goSignup" class="cursor-pointer" style="margin: auto 0px;">회원가입</a>
        </div>
    </v-sheet>
</template>

<script setup>
import router from "@/router/index.js"
import { useUserStore } from "@/stores/user"
import { ref } from "vue"

// 역할 : login함수 불러오기
const store = useUserStore()

// 입력 정보를 담을 변수
const email = ref('')
const password = ref('')
const visible = ref(false)

// 로그인 함수 : 유효성 검사 후 로그인 정보를 넘겨줌
const login = function(){
    const user = {
        email:email.value,
        password:password.value,
    }
    store.login(user)
}

// 기능 : 회원 가입 페이지로 이동
const goSignup = function(){
    router.push({name:'signup'})
}

// 기능 : 비밀 번호 찾기 페이지로 이동
const goSearchPassword = () => {
    router.push({name:'search-password1'})
}
</script>

<style scoped>
</style>