import { InterceptorAxios } from '@/util/http-axios'
import { localAxios } from '@/util/http-commons'

const local = localAxios()
const interceptor = InterceptorAxios()

async function userLogin(param, success, fail) {
  await local.post(`/users/login`, param).then(success).catch(fail)
}
async function userLogout(success, fail) {
  await interceptor.post('/users/logout').then(success).catch(fail)
}
async function userSignup(param, success, fail) {
  await local.post(`/users`, param).then(success).catch(fail)
}
async function sendEmailCode(param, success, fail) {
  await local.post(`/users/join/auth`, param).then(success).catch(fail)
}
async function sendemailcode(param, success, fail) {
  await local.post(`/users/pwd/auth`, param).then(success).catch(fail)
}
async function checkEmailCode(param, success, fail) {
  await local.post(`/users/auth/check`, param).then(success).catch(fail)
}
async function passwordFind(param, success, fail) {
  await local.patch(`/users/pwd/recovery`, param).then(success).catch(fail)
}
async function passwordChange(param, success, fail) {
  interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
  await interceptor.patch(`/users/pwd`, param).then(success).catch(fail)
}
async function updateToken(success, fail) {
  local.defaults.headers.Authorization = `Bearer ${localStorage.getItem('refreshToken')}`
  await local.post(`/token`).then(success).catch(fail)
}
async function userSearch(param, success, fail) {
  await local.get(`/users/search/${param}`, param).then(success).catch(fail)
}
async function userModify(param, success, fail) {
  interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
  interceptor.defaults.headers['Content-Type'] = 'multipart/form-data'
  await interceptor.put(`/users/info`, param).then(success).catch(fail)
}
async function userDelete(param, success, fail) {
  interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
  await interceptor.delete(`/users`, { data: param }).then(success).catch(fail)
}
export {
  userLogin,
  userSignup,
  updateToken,
  sendEmailCode,
  checkEmailCode,
  userSearch,
  userModify,
  userDelete,
  sendemailcode,
  passwordFind,
  passwordChange,
  userLogout
}
