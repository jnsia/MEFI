<template>
    <div class="d-flex pa-3 justify-space-between align-center">
        <p style="font-size: large;">설정</p>
        <font-awesome-icon type="button" @click="close" :icon="['fas', 'xmark']" style="font-size: large;"/>
    </div>
</template>

<script setup>
const emit = defineEmits(['close'])
const close = () => {
    emit('close')
}
</script>

<style scoped>
*{
    background-color: #495464;
    color: white;
}
</style>