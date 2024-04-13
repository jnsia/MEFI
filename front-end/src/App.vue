<template>
    <v-app>
        <RouterView/>
    </v-app>
</template>

<script setup>
import { RouterView, useRouter } from "vue-router";
import { updateToken } from "./api/user";

const router = useRouter()

// login
// 동작 : refreshtoken 없으면 바로 login page / 있으면 updateToken api 날려주고, 에러 발생하면 login page로 이동
router.beforeEach((to, from, next) => {
    // refresh token이 있으면, access Token 재발급 요청함
    // 재발급 되면, res 로 빠짐
    // 재발금 안되면, err로 빠짐
    if (localStorage.getItem('refreshToken') !== null) {
        updateToken(
            (res) => {
                localStorage.setItem('accessToken', res.data.dataBody.accessToken)
                next()
            },
            (err) => {
                localStorage.clear();
                
                if (to.name != 'login' &&
                    to.name != 'signup' &&
                    to.name != 'email' &&
                    to.name != 'search-password1' &&
                    to.name != 'search-password2' &&
                    to.name != 'search-password3') {
                    next({name:'login'})
                }
                else{
                    next()
                }
            }
        )
    } 
    // refresh Token 없으면, 무조건 로그인 페이지
    // 로그인 / 회원가입 / 이메일 인증 / 비밀번호 찾기 페이지가 아니면, 로그인 페이지로 이동
    // 이미 위의 해당 페이지 중 하나에 위치에 있다면, 이동 안함
    else {
        if (to.name != 'login' &&
            to.name != 'signup' &&
            to.name != 'email' &&
            to.name != 'search-password1' &&
            to.name != 'search-password2' &&
            to.name != 'search-password3') {
            next({name:'login'})
        }
        else{
            next()
        }
    }
})
</script>

<style scoped>
</style>
