<template>
  <div>
    <div class="notfound">
      <img class="notfound-img" src='@/assets/NotFoundImage.jpg' width="100%"/>
      <div class="text-box">
        <h1 style="font-size: 52px; color: white">404 Not Found</h1>
        <p style="font-size: 18px; color: rgb(159, 159, 159)">3초 후 메인 페이지로 이동합니다</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

setTimeout(() => {
  router.push({ name: 'main' })
}, 3000)
</script>

<style scoped>
.notfound {
  width: 100vw;
  height: 100vh;
  margin: 0 auto;
  display: flex;
  justify-content: center;
}

.text-box {
  position: absolute;
  top: 40px;
  height: 0px;
  text-align: center;
}

.notfound-img {
  object-fit: cover;
}
</style>
