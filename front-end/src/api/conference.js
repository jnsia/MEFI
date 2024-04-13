import { InterceptorAxios } from '@/util/http-axios'

const interceptor = InterceptorAxios()

const createConference = async (param, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.post(`/meeting`, param).then(success).catch(fail)
}
const detailConference = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.get(`/meeting/detail/${conferenceId}`, param).then(success).catch(fail)
}
const getConferenceHistory = async (param, query, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.get(`/meeting/${param}?start=${query}&end=${query}`).then(success).catch(fail)
}
const cancelMeeting = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.patch(`/meeting/cancel/${conferenceId}`, { params: param }).then(success).catch(fail)
}
const doneMeeting = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.patch(`/meeting/done/${conferenceId}`, { params: param }).then(success).catch(fail)
}
const modifyAllMeeting = async (param, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.put(`/meeting/modify/${conferenceId}`, param).then(success).catch(fail)
}
export { createConference, detailConference, getConferenceHistory, cancelMeeting, doneMeeting, modifyAllMeeting }
