import { InterceptorAxios } from '@/util/http-axios'

const interceptor = InterceptorAxios()

const getFiles = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.get(`/file/all/${conferenceId}`).then(success).catch(fail)
}

const createFile = async (param, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  interceptor.defaults.headers['Content-Type'] = 'multipart/form-data'
  await interceptor.post(`/file`, param).then(success).catch(fail)
}

const downloadFile = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  // 바이너리 데이터를 response로 받기 위한 설정
  interceptor.defaults['responseType'] = 'blob'
  await interceptor
    .get(`/file/download/${conferenceId}`, { params: param })
    .then(success)
    .catch(fail)
}

const deleteFile = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.delete(`/file/${conferenceId}`, { params: param }).then(success).catch(fail)
}

export { getFiles, createFile, downloadFile, deleteFile }
