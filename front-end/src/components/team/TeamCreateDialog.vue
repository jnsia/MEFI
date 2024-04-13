<template>
    <v-card class="bgcolor-setting w-100 h-100 rounded-lg">
        <v-card-title class="ma-0 pa-0">
            <v-toolbar>
                <v-toolbar-title class="font-weight-bold text-h5">팀 생성</v-toolbar-title>
                <v-spacer></v-spacer>
            </v-toolbar>
        </v-card-title>
        <v-card-text class="h-75">
            <v-row class="h-100 ma-0">
                <v-col cols="5" class="h-100">
                    <v-text-field
                    v-model="teamName"
                    label="팀명"
                    variant="solo"
                    required
                    ></v-text-field>
                    <div class="h-80 w-100 elevation-1 rounded-lg mt-5">
                        <v-toolbar class="rounded-t-lg" height="50">
                            <v-toolbar-title class="font-weight-bold text-h5">멤버 추가</v-toolbar-title>
                            <v-text-field
                                density="compact"
                                v-model="searchName"
                                variant="solo"
                                append-inner-icon="mdi-magnify"
                                single-line
                                hide-details
                                @click:append-inner="onClick"
                                @keyup.enter="onClick"
                                class="me-3"
                            ></v-text-field>
                        </v-toolbar>
                        <div class="w-100 h-cal auto-scroll">
                            <v-list>
                                <v-list-item
                                    v-for="user in searchList"
                                    :value="user.id"
                                    @click="clickUser(user)"
                                    :class="{'selected' : user.isSelect}"
                                    >
                                        <p>{{ user.name + ' / ' + user.email }}</p>
                                </v-list-item>
                            </v-list>
                        </div>
                    </div>
                </v-col>
                <v-col cols="5">
                    <v-text-field
                    v-model="teamDescription"
                    label="팀 설명"
                    variant="solo"
                    required
                    ></v-text-field>
                    <div class="h-80 w-100 elevation-1 rounded-lg mt-5">
                        <v-toolbar class="rounded-t-lg" height="50">
                            <v-toolbar-title class="font-weight-bold text-h5">추가 현황</v-toolbar-title>
                        </v-toolbar>
                        <div class="w-100 h-cal auto-scroll">
                            <v-list>
                                <v-list-item
                                    v-for="selectuser in selectedUsers"
                                    @click="clickUser(selectuser)"
                                    :class="{'selected' : selectuser.isSelect}"
                                    >
                                        <p>{{ selectuser.name + ' / ' + selectuser.email }}</p>
                                </v-list-item>
                            </v-list>
                        </div>
                    </div>
                </v-col>
                <v-col cols="2" class="d-flex flex-column justify-start align-center h-100">
                    <v-btn width="100" class="ma-3" @click="create" color="primary">
                        <p class="font-weight-bold">생성</p>
                    </v-btn>
                    <v-btn @click="addMember" width="100" class="ma-3 mt-15">
                        <v-icon>mdi-account-plus</v-icon>
                        <p class="font-weight-bold">멤버 추가</p>
                    </v-btn>
                    <v-btn @click="excludeMember"  width="100" class="ma-3">
                        <v-icon>mdi-account-minus</v-icon>
                        <p class="font-weight-bold">멤버 제거</p>
                    </v-btn>
                </v-col>
            </v-row>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
            variant="text"
            class="text-h5"
            @click="emit('closeDialog')"
            >
            Close
            </v-btn>

        </v-card-actions>
    </v-card>
</template>

<script setup>
import { ref } from 'vue';
import { createTeam } from '@/api/team.js'
import { userSearch } from '@/api/user.js'

const emit = defineEmits(['closeDialog'])
const searchList = ref([])
const selectedUsers = ref([]);
const searchName = ref('')
const leaderId = ref('')
const teamName = ref('')
const teamDescription = ref('')

const create = async () => {
    if (!teamName.value) {
        alert('팀명을 입력해주세요.')
        return false
    }
    
    const data = {
        leaderId : leaderId.value,
        teamName : teamName.value,
        teamDescription : teamDescription.value,
        members : selectedUsers.value.map((user) => user.id),
    }
    await createTeam(
        data,(response) => {
            emit('closeDialog')
        },
        (error)=>{
            const errorCode = error.response.data.dataHeader?.resultCode
            console.log(errorCode)
        }
    )
  }

const onClick = async () => {
    if ((searchName.value === '') || (searchName.value.replaceAll(' ','') === '')) {
        alert('검색할 이름 혹은 이메일을 입력하세요!.');
        return
    };
    const data = searchName.value
    await userSearch(
        data, (response) => {
            searchList.value = [];
            searchList.value = response.data.dataBody.map((x) => {
                const newData = {
                    isSelect :false,
                    isMember : false,
                    ...x
                }
                return newData
            })
        },
        (error) => {
            console.log(error)
        }
    )
}

const addMember = () => {
    const newList = searchList.value.filter((user) => {
        if (!user.isSelect) {
            return user; // 선택된 사용자만 반환
        }
    });

    const selected = searchList.value.filter((user) => {
        if (user.isSelect) {
            user.isSelect = false;
            user.isMember = true;
            return user; // 선택된 사용자만 반환
        }
    });

    selectedUsers.value = selectedUsers.value.concat(selected);
    searchList.value = newList

};

const excludeMember = () => {
    const newList = selectedUsers.value.filter((user) => {
        if (!user.isSelect) {
            return user; // 선택된 사용자만 반환
        }
    });
    selectedUsers.value = newList
}

const clickUser = (user) => {
    user.isSelect = !user.isSelect
}

</script>

<style scoped>
.selected {
    background-color: aqua;
}
</style>