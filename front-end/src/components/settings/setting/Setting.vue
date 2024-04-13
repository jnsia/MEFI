<template>
  <div>
    <!-- setting header -->
    <Header @close="close"></Header>

    <main id="setting-body">
      <!-- 선택지 -->
      <OptionsVue id="body-option" @change-option="optionChange" />
      <div id="body-content">
        <!-- 알림 설정 -->
        <AlarmSettingVue v-if="option === 1" />
        <!-- 입출력 설정 -->
        <IOSettingVue v-if="option === 2" />
      </div>
    </main>
  </div>
</template>

<script setup>
import Header from './Header.vue'
import OptionsVue from './Options.vue'
import AlarmSettingVue from './AlarmSetting.vue'
import IOSettingVue from './IOSetting.vue'
import { ref } from 'vue'

// 옵션 선택지 변수
const option = ref(1)
// 옵션 변경
const optionChange = (num) => {
  option.value = num
}
// 상위 컴포넌트에 전달
// 기능 : 해당 모달창 닫기
const emit = defineEmits(['close'])
const close = () => {
  emit('close')
}
</script>

<style scoped>
#setting-body {
  display: flex;
}
#body-option {
  min-width: 120px;
}
#body-content {
  display: flex;
}
</style>
