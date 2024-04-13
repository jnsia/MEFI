import { InterceptorAxios } from "@/util/http-axios";

const interceptor = InterceptorAxios();

async function setHeaders() {
    interceptor.defaults.headers["Authorization"] = 'Bearer ' + localStorage.getItem("accessToken");
}

// 팀목록 조회 profile.vue에 물려있음
async function selectTeam(success, fail){
    await setHeaders();
    await interceptor.get(`/team`).then(success).catch(fail);
}

// 팀생성 teamcreateDialog.vue에 물려있음
async function createTeam(param, success, fail){
    await setHeaders();
    await interceptor.post(`/team`, param).then(success).catch(fail);
}

// 팀원 조회 TeamPage.vue에 물려있음
async function selectTeamMate(param, success, fail){
    await setHeaders();
    await interceptor.get(`/team/${param}`).then(success).catch(fail);
}

// 팀원 추가 API 
async function addTeamMate(param, success, fail){
    await setHeaders();
    await interceptor.post(`/team/${param.teamid}/${param.userid}`).then(success).catch(fail);
}

// 팀원 삭제 API
async function excludeTeamMate(param, success, fail){
    await setHeaders();
    await interceptor.delete(`/team/${param.teamid}/${param.userid}`).then(success).catch(fail);
}

// 리더 위임
async function changeLeader(param, success, fail){
    await setHeaders();
    await interceptor.patch(`/team/${param.teamid}/${param.userid}`).then(success).catch(fail);
}

// 팀상세조회
async function detailTeam(param, success, fail){
    await setHeaders();
    await interceptor.get(`/team/detail/${param}`).then(success).catch(fail);
}

// 팀상세정보수정
async function modifyTeam(param, success, fail){
    await setHeaders();
    await interceptor.patch(`/team/${param.teamId}`, param.data).then(success).catch(fail);
}

// 팀삭제
async function deleteTeam(param, success, fail){
    await setHeaders();
    await interceptor.delete(`/team/${param}`).then(success).catch(fail);
}

export {
  selectTeam,
  createTeam,
  selectTeamMate,
  addTeamMate,
  excludeTeamMate,
  changeLeader,
  detailTeam,
  modifyTeam,
  deleteTeam
}
