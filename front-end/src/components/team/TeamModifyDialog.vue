<template>
    <v-card class="bgcolor-setting w-100 h-100 rounded-lg">
        <v-card-title class="ma-0 pa-0">
            <v-toolbar>
                <v-toolbar-title class="font-weight-bold text-h5">팀 정보 수정</v-toolbar-title>
                <v-spacer></v-spacer>
            </v-toolbar>
        </v-card-title>
        <v-card-text class="h-75">
            <v-row class="h-100 ma-0">
                <v-col cols="5" class="h-100">
                    <v-text-field
                    v-model="teamName"
                    label="팀명"
                    variant="outlined"
                    required
                    hide-details
                    ></v-text-field>
                    <div class="h-80 w-100 elevation-1 rounded-lg mt-5">
                        <v-toolbar class="rounded-t-lg" height="50">
                            <v-toolbar-title class="font-weight-bold text-h5">팀원 목록</v-toolbar-title>
                        </v-toolbar>
                        <div class="w-100 h-cal auto-scroll">
                            <v-list>
                                <v-list-item
                                    v-for="user in members"
                                    :title="user.name + ' / ' + user.email"
                                    :value="user.id"
                                    max-height="30"
                                    @click="excludememberdata = user.id"
                                    >
                                </v-list-item>
                            </v-list>
                        </div>
                    </div>
                </v-col>
                <v-col cols="5">
                    <v-text-field
                    v-model="teamDescription"
                    label="팀 설명"
                    variant="outlined"
                    required
                    hide-details
                    ></v-text-field>
                    <div class="h-80 w-100 elevation-1 rounded-lg mt-5">
                        <v-toolbar class="rounded-t-lg" height="50">
                            <v-toolbar-title class="font-weight-bold text-h5">검색 목록</v-toolbar-title>
                            <v-text-field
                            density="compact"
                                variant="outlined"
                                v-model="searchName"
                                append-inner-icon="mdi-magnify"
                                single-line
                                hide-details
                                class="me-3 bg-white"
                                @click:append-inner="search"
                                @keyup.enter="search"
                            ></v-text-field>
                        </v-toolbar>
                        <div class="w-100 h-cal auto-scroll">
                            <v-list>
                            <v-list-item
                                v-for="user in searchList"
                                :title="user.name + ' / ' + user.email"
                                :value="user.id"
                                @click="addmemberdata = user.id"
                                >
                            </v-list-item>
                        </v-list>
                        </div>
                    </div>
                </v-col>
                <v-col cols="2" class="d-flex flex-column justify-start align-center h-100">
                    <v-btn width="100" class="ma-3" @click="updateTeam" variant="outlined" rounded="xl">팀정보 수정</v-btn>
                    <v-dialog
                        v-model="dialog"
                        persistent
                        width="auto"
                        >
                        <template v-slot:activator="{ props }">
                            <v-btn
                            color="primary"
                            rounded="xl"
                            v-bind="props"
                            width="100" 
                            class="ma-3"
                            :disabled="excludememberdata === null"
                            >
                            팀장 위임
                            </v-btn>
                        </template>
                        <v-card>
                            <v-card-text>팀장을 위임할 경우, 해당 팀에 대해 다시 위임받기 전까진 일반 멤버로 머물게 됩니다. 진행하시겠습니까?</v-card-text>
                            <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn
                                width="100" class="ma-3"
                                color="green-darken-1"
                                variant="text"
                                @click="dialog = false"
                            >
                                취소
                            </v-btn>
                            <v-btn
                                color="green-darken-1"
                                variant="text"
                                @click="change"
                            >
                                진행
                            </v-btn>
                            </v-card-actions>
                        </v-card>
                        </v-dialog>
                    <!-- <v-btn width="100" class="ma-3" @click="change">팀장 위임</v-btn> -->
                    <v-btn width="100" class="ma-3" @click="addMember" color="#45566F" variant="outlined" rounded="xl">팀원 추가</v-btn>
                    <v-btn width="100" class="ma-3" @click="excludeMember" color="#45566F" variant="outlined" rounded="xl">팀원 삭제</v-btn>
                    <v-btn width="100" class="ma-3" color="red-darken-2" @click="removeTeam" rounded="xl">팀 삭제</v-btn>
                </v-col>
            </v-row>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
            variant="text"
            class="text-h5"
            @click="close"
            >
            Close
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { selectTeamMate, addTeamMate, excludeTeamMate, changeLeader, detailTeam, deleteTeam, modifyTeam } from '@/api/team.js';
import { userSearch } from '@/api/user.js'
import { useUserStore } from "@/stores/user"
import router from '@/router';

const props = defineProps({
  teamId: Number
});

const addmemberdata = ref(null)
const excludememberdata = ref(null)
const teamName = ref('')
const teamDescription = ref('')
const emit = defineEmits(['closeDialog'])

const searchList = ref([])
const searchName = ref('')
const members = ref([])
const store = useUserStore()
const dialog = ref(false)

// 팀원 조회
const selectmember = async () => {
    await selectTeamMate(
        props.teamId,(response) => {
            members.value = response.data.dataBody.filter(member => {
                if (member.email !== store.userInfo.email)
                    return member
            })
        },
        (error)=>{
            console.log(error)
        }
    )
}

// 사용자검색
const search = async () => {
    if ((searchName.value === '') || (searchName.value.replaceAll(' ','') === '')) {
        alert('검색할 이름 혹은 이메일을 입력하세요!.');
        return
    };
    const data = searchName.value
    await userSearch(
        data, (response) => {
            searchList.value = response.data.dataBody.filter(member => {
                return !members.value.some(selectedMember => selectedMember.email === member.email) && member.email !== store.userInfo.email;
            });
        },
        (error) => {
            console.log(error)
        }
    )
}

// 멤버추가
const addMember = async () => {
    if (addmemberdata.value === null) return;
    const data = {
        teamid: props.teamId,
        userid: addmemberdata.value
    }
    await addTeamMate(
        data,(response) => {
            selectmember();
            searchList.value = searchList.value.filter(member => member.id !== addmemberdata.value);
            addmemberdata.value = null;
        },
        (error)=>{
            console.log(error)
        }
    )
};

// 멤버추방
const excludeMember = async () => {
    if (excludememberdata.value === null) return;
    const data = {
        teamid: props.teamId,
        userid: excludememberdata.value
    }
    await excludeTeamMate(
        data,(response) => {
            search()
            members.value = members.value.filter(member => member.id !== excludememberdata.value);
            excludememberdata.value = null
        },
        (error)=>{
            console.log(error)
        }
    )
};

// 리더위임
const change = async () => {
    if (excludememberdata.value === null) {
        alert('위임받을 팀원을 선택하세요.');
        return;
    }
    const data = {
        teamid: props.teamId,
        userid: excludememberdata.value
    }
    await changeLeader(
        data,(response) => {
            dialog.value = false;
            close();
        },
        (error)=>{
            console.log(error)
        }
    )
}

// 팀 상세정보 조회
const selectTeamDetail= async () => {
    await detailTeam(
        props.teamId,(response) => {
            teamName.value = response.data.dataBody.teamName
            teamDescription.value = response.data.dataBody.teamDescription
        },
        (error)=>{
            console.log(error)
        }
    )
}
// 팀 정보 수정
const updateTeam = async () => {
    if (!teamName.value) {
        alert('팀명을 입력해주세요.')
        return false
    }

    const param = {
        teamId : props.teamId,
        data : {
            name : teamName.value,
            description : teamDescription.value,
        }
    }
    await modifyTeam(
        param,(response) => {
            console.log('modify', response.data.dataBody)
            close();
        },
        (error)=>{
            console.log(error)
        }
    )
}
// 팀 삭제
const removeTeam = async () => {
    await deleteTeam(
        props.teamId,(response) => {
            console.log('delete', response.data.dataBody)
            close();
        },
        (error)=>{
            console.log(error)
        }
    )
}

// 최초 생성시 팀원 조회
onMounted(() => {
    selectmember();
    selectTeamDetail();
});

const close = () => {
    emit('closeDialog')
}
</script>

<style scoped>
.selected {
    background-color: aqua;
}
</style>