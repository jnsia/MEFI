<template>
    <v-card>
       <HeaderVue @close="close"></HeaderVue> 
       <BodyVue :alarm="props.alarm" @close="close"></BodyVue>
    </v-card>
</template>

<script setup>
import BodyVue from "./Body.vue"
import HeaderVue from "./Header.vue"
const props = defineProps({
    alarm:Object,
})
const emit = defineEmits(['close'])
const close = () => {
    emit('close',props.alarm.id)
}
</script>

<style scoped>
</style>