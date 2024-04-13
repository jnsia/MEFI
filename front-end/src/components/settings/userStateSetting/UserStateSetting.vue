<template>
    <!-- 회원 현재 상태 변경 -->
    <v-menu location="button" activator="parent">
        <v-list class="overflow-hidden ma-0">
            <!-- green -->
            <v-list-item @click="changeStatus('green')" class="d-flex flex-row justify-space-between">
                <font-awesome-icon :icon="['fas', 'circle']" style="color: green;"/>
                <span>온라인</span>
            </v-list-item>
            <!-- white -->
            <v-list-item @click="changeStatus('white')" class="d-flex flex-row justify-space-between">
                <font-awesome-icon :icon="['fas', 'circle']" style="color: white; border: 1px solid rgb(186, 186, 186); border-radius: 50%;"/>
                <span>오프라인</span>
            </v-list-item>
            <!-- red -->
            <v-list-item @click="changeStatus('red')" class="d-flex flex-row justify-space-between">
                <font-awesome-icon :icon="['fas', 'circle']" style="color: red;"/>
                <span>방해 금지</span>
            </v-list-item>
            <!-- yellow -->
            <v-list-item @click="changeStatus('yellow')" class="d-flex flex-row justify-space-between">
                <font-awesome-icon :icon="['fas', 'circle']" style="color: yellow;"/>
                <span>다른 용무 중</span>
            </v-list-item>
        </v-list>
    </v-menu>
</template>

<script setup>
// 목적 : 상위 컴포넌트로 변화를 전달
// 기능 : 상태 클릭시, 해당하는 상태로 변경
const emit = defineEmits(['statusChange'])
const changeStatus = (color) => {
    emit('statusChange', color)
}
</script>

<style scoped>
</style>