<template>
    <v-app class="bg-main background-image">
        <!-- 로그인 / 회원가입 페이지로 넘어감 -->
        <RouterView></RouterView>
    </v-app>
</template>

<script setup>
import { RouterView, useRouter } from "vue-router";

// 가장 처음에는 login 컴포넌트가 뜨도록 되어 있음
const router = useRouter()
router.push({name:'login'})
</script>

<style scoped>
.background-image {
    background-color: #9CD8E2;
    background-image: url('../../assets/LoginBackground.jpg');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: 100% 100px;
}
</style>