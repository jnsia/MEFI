import { InterceptorAxios } from "@/util/http-axios";
import { EventSourcePolyfill, NativeEventSource } from "event-source-polyfill";

const interceptor = InterceptorAxios();
const VITE_APP_API_URL = import.meta.env.VITE_APP_API_URL
const EventSource = EventSourcePolyfill || NativeEventSource

// sse 연결
async function alarmSubscribe(lastEventId) {
    const SSE = new EventSource(
        `${VITE_APP_API_URL}/api/alarm/subscribe?lastEventId=${lastEventId}`,
        { 
            headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}`},
            heartbeatTimeout: 120000
        }
    )
    return SSE;
}

// 알림 전체 조회
async function alarmAll(){
    interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
    try{
        const response = interceptor.get('/alarm/all')
        return response;
    }catch(error){
        throw error;
    }
}

// 알림 전체 읽음
async function alarmReadAll(){
    interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
    await interceptor.patch('/alarm/all').then((res)=>{console.log(res)}).catch((err)=>{console.log(err)})
}

// 알림 읽음
async function alarmReadOne(alarmId){
    interceptor.defaults.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`
    await interceptor.patch(`/alarm/${alarmId}`).then((res)=>{console.log(res)}).catch((err)=>{console.log(err)})
}

export{
    alarmSubscribe,
    alarmAll,
    alarmReadAll,
    alarmReadOne,
}