<template>
  <v-sheet v-if="rendering === true" class="pa-5 d-flex flex-column justify-center alien-center">
    <!-- 정보 조회 및 수정할 수 있는 form -->
    <v-form :disabled="disable">
      <div class="d-flex flex-row justify-center alien-center my-5">
        <label for="photo">
          <img
            :style="{cursor: disable ? 'default' : 'pointer' }"
            :src="previewPhoto"
            style="
              object-fit: cover;
              margin: 5px;
              width: 120px;
              height: 120px;
              border: 0.5px solid #cccccc;
              border-radius: 50%;
            "
          />
        </label>
        <input
          :disabled="disable"
          id="photo"
          type="file"
          ref="photo"
          hidden
          @change="changePhoto"
        />
      </div>

      <v-text-field
        label="email"
        :model-value="email"
        v-model="email"
        variant="outlined"
        disabled="true"
      ></v-text-field>

      <v-text-field
        label="name"
        :model-value="name"
        v-model="name"
        variant="outlined"
      ></v-text-field>

      <v-text-field
        label="position"
        :model-value="position"
        v-model="position"
        variant="outlined"
      ></v-text-field>

      <v-text-field
        label="department"
        :model-value="department"
        v-model="department"
        variant="outlined"
      ></v-text-field>

      <!-- 수정하기 : 누르면 입력창이 활성화 됨 -->
      <div v-if="disable">
        <v-btn @click="toggleDisable" class="w-100" color="#45566F" variant="flat">수정하기</v-btn>
      </div>
      <!-- 저장 : 입력된 정보 저장됨 -->
      <!-- 취소 : 유저 정보는 변경되지 않음. 입력창이 비활성화됨 -->
      <div v-else class="d-flex flex-row justify-space-around">
        <v-btn @click="updateUserinfo" color="#45566F" variant="flat" class="w-40">저장</v-btn>
        <v-btn @click="toggleDisable" color="#45566F" variant="outlined" class="w-40">취소</v-btn>
      </div>
    </v-form>
  </v-sheet>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useUserStore } from '../../../stores/user'
import { userModify } from '../../../api/user'

const store = useUserStore()

// 기능 : 변수 할당 후 렌더링 되도록
const rendering = ref(false)

// 수정될 정보
const email = ref(store.userInfo?.email)
const name = ref(store.userInfo?.name)
const position = ref(store.userInfo?.position)
const department = ref(store.userInfo?.dept)
const imgURL = ref(store.userInfo?.imgURL)

// 이미지 미리보기를 위한 변수
const photo = ref(null)
const previewPhoto = ref('')

// 이미지 선택할 경우 이미지를 URL로 변환 후 previewPhoto 변수에 할당
const changePhoto = () => {
  const image = photo.value.files[0]
  const imageURL = URL.createObjectURL(image)
  previewPhoto.value = imageURL
}

// 변수 선언 및 값 저장
onMounted(() => {
  rendering.value = true
  previewPhoto.value = `data:image/jpeg;base64, ${store.userInfo?.profile}`
})

// 기능 : v-form 비활성화 토글
const disable = ref(true)
const toggleDisable = () => {
  disable.value = !disable.value
}

// 회원 정보를 수정하는 rest api
// 유효성 검사 후 api
const updateUserinfo = async () => {
  if (window.confirm('회원 정보를 수정하시겠습니까?')) {
    const formData = new FormData()
    const profileImage = photo.value.files[0]
    const userModifyAllReqDto = new Blob(
      [
        JSON.stringify({
          name: name.value,
          position: position.value,
          dept: department.value,
          imgUrl: imgURL.value
        })
      ],
      { type: 'application/json' }
    )
    formData.append('profileImg', profileImage)
    formData.append('userModifyAllReqDto', userModifyAllReqDto)
    await userModify(
      formData,
      (res) => {
        store.userInfo = res.data.dataBody
        disable.value = true
      },
      (err) => {
        console.log(err)
      }
    )
  }
}
</script>

<style scoped>
.profile-image {
  object-fit: cover;
  margin: 5px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
}
</style>
