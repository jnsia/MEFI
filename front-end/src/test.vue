<script>
import { ref, onMounted, onUnmounted } from "vue";
import axios from "axios";

const pushedAlarm = ref('');
let eventSource;
export default {
  setup(){
    // 컴포넌트가 마운트될 때 실행되는 로직
    onMounted(() => {
      // 실행할 메서드를 ref로 선언
      const intervalMethod = ref("");

      // setInterval을 사용하여 1초마다 intervalMethod를 호출
      intervalMethod.value = setInterval(() => {
        // 실행할 로직 작성
        console.log("EventSource state : ", eventSource.readyState != EventSource.CLOSED);
      }, 3000);
    });

    // 컴포넌트가 언마운트될 때 setInterval 해제
    onUnmounted(() => {
      clearInterval(intervalMethod.value);
      eventSource.close();
      console.log('Connection to server closed.');
    });
  },

  created() {
    this.setupSSE();
  },
  methods:{
    setupSSE() {

      // userId
      eventSource = new EventSource('http://localhost:8080/api/alarm?userId=1');
      console.log("EventSource : ", eventSource);

      eventSource.addEventListener("sse",(event)=>{
        console.log(event.data);
      })

      eventSource.onerror = (error) => {
        console.error('Error occurred:', error);
        eventSource.close();
      };
      eventSource.onopen = () => {
        console.log('Connection to server opened.');
      };
    },

    sendAlarm() {
      // userId
      const url = `http://localhost:8080/api/alarm/send?userId=1`;
      
      // header
      const lastEventId = '';
      const headers = {
        'Last-Event-Id' : lastEventId
      };

      axios.post(url, null, { headers })
        .then(response => {
          console.log('POST 요청 성공 : ', response);
        })
        .catch(error => {
          console.error('POST 요청 중 오류 발생 : ', error);
        });
    }

  }
}
</script>

<template>
  <main>
    <h1>SSE Notifications</h1>
    <button @click="sendAlarm"> 알림 생성 버튼 </button>
    <h2>전송된 알람</h2>
    <div>{{ pushedAlarm }}</div>
    <!-- <h2 id="alarm"></h2> -->
  </main>
</template>
