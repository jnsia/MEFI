import { InterceptorAxios } from '@/util/http-axios'

const interceptor = InterceptorAxios()

const makeSession = async (param, teamId, conferenceId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor
    .post(`/openvidu/api/sessions/${teamId}?conferenceId=${conferenceId}`, param)
    .then(success)
    .catch(fail)
}

const makeToken = async (param, teamId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor
    .post(`/openvidu/api/sessions/${teamId}/connections?sessionId=${param.sessionId}`)
    .then(success)
    .catch(fail)
}

const checkDone = async (sessionId, success, fail) => {
  interceptor.defaults.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken')
  await interceptor.get(`/openvidu/api/sessions/${sessionId}/`, {}).then(success).catch(fail)
}

export { makeToken, makeSession, checkDone }
