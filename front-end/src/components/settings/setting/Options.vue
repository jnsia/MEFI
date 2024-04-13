<template>
  <div class="d-flex flex-column border-right">
    <!-- 선택지 1 : 알림 설정 -->
    <div :class="['option-item', option == 1 && 'select-item']" @click="optionChange(1)">
      알림 설정
    </div>
    <!-- 선택지 2 : 입출력 설정 -->
    <div :class="['option-item', option == 2 && 'select-item']" @click="optionChange(2)">
      입출력 설정
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const option = ref(1)

// 목적 : 상위 컴포넌트에 전달
// 기능 : 선택한 선택지를 반영
const emit = defineEmits(['changeOption'])
const optionChange = (num) => {
  emit('changeOption', num)
  option.value = num
}
</script>

<style scoped>
.option-item {
  text-align: center;
  padding: 20px;
  cursor: pointer;
}

.select-item {
  background-color: #616c7c;
  color: white;
}
</style>
