<template>
    <!-- 회원 가입 -->
    <v-sheet class="w-30 ma-auto pa-12 d-flex flex-column justify-center" min-width="500" style="border-radius: 10px;">

        <!-- 로그 -->
        <div class="d-flex justify-center mb-5">
            <span style="font-weight:bolder;" class="text-h3">MEFI</span>
        </div>

        <!-- 회원 가입 입력창 -->
        <v-form @submit.prevent="signup" class="d-flex flex-column justify-center">

            <!-- 이메일 입력창 -->
            <div class="d-flex flex-row justify-center align-center">
                <v-text-field
                    label="이메일"
                    v-model="email"
                    variant="outlined"
                    type="email"
                    :rules="rule_email"
                    class="mb-1"
                ><!-- 이메일 인증 모달창 -->
                    <template v-slot:append-inner>    
                        <font-awesome-icon v-if="email_success===true" :icon="['fas', 'circle-check']" :style="{color: email_success ? '#4CAF50' : '#BDBDBD'}" style="font-size: x-large;"/>
                        <v-btn v-show="email_success===false && email_check===false" variant="text"  @click="emailCheck()" >
                            <span>인증</span>
                            <v-dialog persistent v-model="emailDialog">
                                <Email @close="emailDialog = false" :email="email" @success="emailSuccess"></Email>
                            </v-dialog>
                        </v-btn>
                    </template>
                </v-text-field>
            </div>

            <!-- 비밀 번호 입력창 -->
            <v-text-field
                label="비밀 번호"
                type="password"
                v-model="password"
                :rules="rule_pass"
                variant="outlined"
                class="mb-1"
            ></v-text-field>

            <!-- 비밀번호 확인 입력창 -->
            <v-text-field
                label="비밀 번호 확인"
                type="password"
                v-model="password_check"
                :rules="rule_pass_check"
                variant="outlined"
                class="mb-1"
            ></v-text-field>

            <!-- 이름 입력창 -->
            <v-text-field
                label="이름"
                v-model="name"
                :rules="rule_name"
                variant="outlined"
                class="mb-1"
            ></v-text-field>

            <!-- 부서 입력창 -->
            <v-text-field
                label="부서"
                v-model="dept"
                variant="outlined"
                class="mb-1"
            ></v-text-field>

            <!-- 직급 입력창 -->
            <v-text-field
                label="직급"
                v-model="position"
                variant="outlined"
                class="mb-1"
            ></v-text-field>

            <!-- 회원 가입 버튼 -->
            <v-btn type="submit" class="w-100" style="height: 40px;" variant="flat" color="#45566F" rounded="lg">회원 가입</v-btn>
        </v-form>
    </v-sheet>
</template>

<script setup>
import { useUserStore } from "@/stores/user"
import { ref, watch } from "vue"
import Email from "./Email.vue";
import { sendEmailCode } from "@/api/user"
const store = useUserStore();

// 회원가입 시, 입력 받을 정보
const email = ref('')
const password = ref('')
const password_check = ref('')
const name = ref('')
const dept = ref('')
const position = ref('')

// 기능 : 이메일 인증 버튼
// 작동 원리 : 입력 형식 맞으면 true, 아니면 false
const email_check = ref(true)
watch(
    ()=>email.value,
    ()=>{
        if (regex_email.test(email.value)) {
            email_check.value = false
        }
        else{
            email_check.value = true
            email_success.value = false
        }
    }
)

// 이메일 인증 모달창 on/off
const emailDialog = ref(false)

// 유효성 검사
// 이메일 : 필수항목 / 이메일 형식
const rule_email = [
    value => !!value || '필수 항목 입니다.',
    value => (value && regex_email.test(value)) || '이메일 주소를 정확히 입력해주세요',
]
// 비밀번호 : 필수항목 / 영문숫자특수문자(8-16) 
const rule_pass = [
    value => !!value || '필수 항목 입니다.',
    value => (value && regex_pass.test(value)) || '영문, 숫자, 특수문자가 조합하여 입력해주세요(8-16자)',
]
// 비밀번호확인 : 필수항목 / 비밀번호 일치 여부
const rule_pass_check = [
    value => !!value || '필수 항목 입니다.',
    value => (value&&value==password.value) || '비밀번호가 일치하지 않습니다'
]
// 이름 : 필수항목
const rule_name = [
    value => !!value || '필수 항목 입니다.',
]

// 정규식
// 이메일 : asdf@asdf.com 이메일 형식
const regex_email = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
// 비밀번호 : 영문숫자특수문자(8-16) 
const regex_pass =  /^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)\-_=+]).{8,16}$/

// 회원 가입 api
const signup = async function(){
    // 유효성 검사
    if ( !email.value || !regex_email.test(email.value) || 
        !password.value || !regex_pass.test(password.value) ||
        !password_check.value || password_check.value!=password.value ||
        !name.value){
        alert("모두 입력해 주세요")
    }
    else{
        const userInfo = {
            email:email.value,
            password:password.value,
            name:name.value,
            dept:dept.value,
            position:position.value,
        }
        await store.signup(userInfo)
    }
}

// 이메일 인증 코드 보내기
const emailCheck = async () => {
    const param = {
        "email": email.value
    }
    await sendEmailCode(param, 
    (res)=>{
        emailDialog.value = true
    },(err)=>{
        console.log(err)
        if(err.response.data.dataHeader.resultCode=="U-003"){
            alert(err.response.data.dataHeader.resultMessage)
        }
    })
}

// 이메일 인증 코드 확인
const email_success = ref(false)
const emailSuccess = () => {
    emailDialog.value = false
    email_success.value = true
    email_check.value = true
}
</script>

<style scoped>
</style>