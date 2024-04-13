import { InterceptorAxios } from "@/util/http-axios";

// const local = localAxios();
const interceptor = InterceptorAxios();

async function setHeaders() {
    interceptor.defaults.headers["Authorization"] = 'Bearer ' + localStorage.getItem("accessToken");
}

async function selectSchedule(query, success, fail){
    await setHeaders();
    await interceptor.get(`/schedule?${query}`).then(success).catch(fail);
}

async function selectScheduleDetail(param, success, fail){
    await setHeaders();
    await interceptor.get(`/schedule/detail/${param}`).then(success).catch(fail);
}

async function selectTeamSchedule(param, query, success, fail){
    await setHeaders();
    await interceptor.get(`/schedule/${param}?day=${query}`).then(success).catch(fail);
}

async function createSchedule(param, success, fail){
    await setHeaders();
    await interceptor.post(`/schedule`, param).then(success).catch(fail);
}

async function deleteSchedule(param, success, fail){
    await setHeaders();
    await interceptor.delete(`/schedule/${param}`).then(success).catch(fail);
}

async function modifySchedule(param, success, fail){
    await setHeaders();
    await interceptor.patch(`/schedule/${param.id}`, param.data).then(success).catch(fail);
}


export { selectSchedule, createSchedule, deleteSchedule, modifySchedule, selectTeamSchedule, selectScheduleDetail }