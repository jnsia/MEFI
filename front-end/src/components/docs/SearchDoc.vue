<template>
  <v-sheet
    height="100%"
    width="100%"
    rounded="lg"
    border
    elevation="0"
    :class="[isDragged ? 'dragged' : '', 'px-4', 'py-2']"
    @dragenter.prevent="onDragenter"
    @dragover.prevent="onDragenter"
    @dragleave.prevent="onDragleave"
    @drop.prevent="onDrop"
  >
    <v-infinite-scroll v-if="fileList.length + addedFileList.length > 0" height="35vh">
      <!-- 업로드된 리스트 -->
      <div class="d-flex justify-space-between align-center file-upload-list" v-for="file in fileList" :key="file.fileName">
        <a class="file-name">{{ file.fileName }}</a>
        <div class="d-flex my-1">
          <v-btn class="px-2 py-1 mx-1 border" size="sm" @click="saveFile(file.fileName)">다운</v-btn>
          <v-btn v-if="role === 'LEADER'" class="px-2 py-1 border" size="sm" @click="eraseFile(file.fileName)">삭제</v-btn>
        </div>
      </div>
      <!-- 업로드할 리스트 -->
      <div class="d-flex justify-space-between file-upload-list" v-for="addedFile in addedFileList" :key="addedFile.fileName">
        <a>{{ addedFile.name }}</a>
        <div class="d-flex">
          <v-btn class="px-2 py-1 border"  size="sm" @click="removeFile(addedFile.name)">삭제</v-btn>
        </div>
      </div>
      <template v-slot:loading>{{ props.documentState.state === 'modify' ? "추가할 문서를 여기로 옮겨주세요." : "" }}</template>
    </v-infinite-scroll>
    <div v-else class="d-flex h-100 flex-column align-center justify-center">
      <div>관련 문서가 없습니다.</div>
      <div>{{ props.documentState.state === 'modify' ? "추가할 문서를 여기로 옮겨주세요." : "" }}</div>
    </div>
  </v-sheet>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getFiles, createFile, downloadFile, deleteFile } from '@/api/file.js'
import { selectTeam } from '@/api/team';
import router from '@/router';

const route = useRoute()
const teamId = ref(route.params?.teamid)
const conferenceId = ref(route.params?.conferenceid)
const props = defineProps({
  documentState: Object
})

// API 호출 함수
const fileList = ref([])
const addedFileList = ref([])

const isDragged = ref(false)

const role = ref(false)

const fetchFiles = () => {
  getFiles(
    {},
    conferenceId.value,
    (response) => {
      fileList.value = response.data.dataBody
    },
    (error) => {
      console.log(error)
    }
  )
}

const uploadFile = (file) => {
  const fileName = sanitizeFilename(file.name)
  const uploadFile = new File([file], fileName, {type: 'Blob'})
  console.log(uploadFile)
  
  const formData = new FormData()

  const fileRequestDto = new Blob(
    [
      JSON.stringify({
        teamId: teamId.value,
        conferenceId: conferenceId.value,
        fileName: 'ATTACHMENT.pdf',
        type: 'ATTACHMENT'
      })
    ],
    { type: 'application/json' }
  )

  formData.append('file', uploadFile)
  formData.append('fileRequestDto', fileRequestDto)

  createFile(
    formData,
    () => {
      console.log(`upload success`)
    },
    (error) => {
      console.log(error)
    }
  )
}

function sanitizeFilename(filename) {
    const sanitizedFilename = filename.replace(/[<>[\\\]^`{|}"]/g, '_');
    return sanitizedFilename;
}

const saveFile = (fileName) => {
  const originFileName = fileName

  downloadFile(
    {
      fileName: originFileName
    },
    conferenceId.value,
    (response) => {
      // Blob 파일 형식을 URL 객체로 변환
      const filepath = URL.createObjectURL(response.data)

      // 임의의 a 태그 생성 후 href 속성에 URL 객체 할당
      const element = document.createElement('a')
      element.setAttribute('href', filepath)
      element.setAttribute('download', originFileName)
      document.body.appendChild(element)

      // 생성한 a 태그 클릭
      element.click()

      // a 태그와 URL 객체 삭제
      document.body.removeChild(element)
      // URL.revokeObjectUrl(filepath)
    },
    (error) => {
      console.log(error)
    }
  )
}

const eraseFile = (fileName) => {
  deleteFile(
    {
      fileName: fileName
    },
    conferenceId.value,
    (response) => {
      alert('문서가 삭제되었습니다.')
      fetchFiles()
    },
    (error) => {
      console.log(error)
    }
  )
}

const removeFile = (fileName) => {
  addedFileList.value = addedFileList.value.filter((addedFile) => addedFile.name !== fileName)
}

const emit = defineEmits(['changeDone'])

watch(
  () => props.documentState.state,
  () => {
    console.log(props.documentState.state)
    if (props.documentState.state === 'detail') {
      fetchFiles()
    }

    if (props.documentState.state === 'done') {
      conferenceId.value = props.documentState.conferenceId

      addedFileList.value.forEach((addedFile) => {
        uploadFile(addedFile)
      })

      addedFileList.value = []
    }
  }
)

const checkRole = async () => {
  await selectTeam(
    (response) => {
      role.value = response.data.dataBody.find((data) => data.teamId == teamId.value).role
    },
    (error) => {
      console.log(error)
    }
  )
}

onMounted(() => {
  console.log(props.documentState.state)
  if (props.documentState.state !== 'create') {
    fetchFiles()
  }
  checkRole()
})

// 드래그 앤 드롭을 사용하지 않고 수동으로 파일을 넣는 함수
// const onFileChange = (event) => {
//   const files = event.target.files[0]
//   console.log(files)
//   addedFileList.value.push(files)
// }

const onDragenter = (event) => {
  isDragged.value = true
}

const onDragleave = (event) => {
  isDragged.value = false
}

const onDrop = (event) => {
  if (props.documentState.state === 'detail') {
    alert('권한이 없거나 수정 중이 아닙니다.')
    return
  }

  isDragged.value = false
  const files = event.dataTransfer.files[0]
  addedFileList.value.push(files)
}
</script>

<style scoped>
.dragged {
  border: 1px dashed #495464;
  background-color: #f7f9ff;
  opacity: 0.5;
}

.file-name {
  width: 75%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

::-webkit-scrollbar {
  width: 0px; /* 스크롤바의 너비 설정 */
}

::-webkit-scrollbar-thumb {
  background-color: #888; /* 스크롤바의 색상 설정 */
  border-radius: 10px; /* 스크롤바의 모서리 반경 설정 */
}
</style>
