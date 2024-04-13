<template>
    <v-list-item class="pa-0">
        <div class="d-flex flex-row justify-space-between pa-3" style="border-bottom: 1px solid rgb(196, 196, 196);">
            <div class="d-flex flex-column">
                <span style="font-size: large;">{{ alarm.message }}</span>
                <span style="font-size: small;">{{ createdTime }}</span>
            </div>
        </div>
    </v-list-item>
</template>

<script setup>
import { onMounted } from 'vue';
import { ref } from 'vue';

const props = defineProps({
    alarm:Object,
})

const createdTime = ref("")
onMounted(()=>{
    const array = props.alarm.createdTime.slice(0,10).split('-')
    createdTime.value = array[0] + "년 " + array[1] + "월 " + array[2] + "일"
})
</script>

<style scoped>
</style>