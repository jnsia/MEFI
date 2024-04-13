<template>
  <v-container class="pa-0 d-flex flex-column justify-center">
    <!-- 달력 년/월 표시 및 일정 추가 버튼 -->
    <v-row class="d-flex align-center justify-start ma-2">
      <v-col cols="4">
        <v-row>
          <v-col cols="3" class="d-flex justify-center align-center">
            <v-btn icon="mdi-chevron-left" @click="clickprev" variant="text"></v-btn>
          </v-col>
          <v-col cols="6" class="d-flex justify-center align-center">
            <p>
              <span class="text-h6 font-weight-bold">{{ year }}</span>
              <span class="ms-3 text-h5">{{ listofmonthword[month] }}</span>
            </p>
          </v-col>
          <v-col cols="3" class="d-flex justify-center align-center">
            <v-btn icon="mdi-chevron-right" @click="clicknext" variant="text"></v-btn>
          </v-col>
        </v-row>
      </v-col>
      <v-spacer></v-spacer>
      <v-btn @click="router.push({ name: 'insertschedule', params: {date: choicedate}})" 
        color="#45566F"
        variant="outlined"
        rounded="xl"
        class="mr-10"
      >
        <p>+ 일정 생성</p>
      </v-btn>
    </v-row>

    <!-- 달력 -->
    <div class="mt-n1">

      <!-- 요일 -->
      <v-row class="d-flex align-center justify-center ma-0">
        <v-col v-for="i in weekday" class="day-header ma-0" :key="i">
          <div>
            <p class="font-weight-bold">
              {{ dayofweek[i] }}
            </p>
          </div>
        </v-col>
      </v-row>

      <!-- 날짜 -->
      <v-row v-for="week in cal" :key="week" class="d-flex align-center justify-center ma-0">
        <v-col v-for="i in weekday" 
          class="day pa-0 cursor-pointer"
          :key="i"
          @click="clickday(week[i])"
          :class="[week[i]['type']]"
        >
          <div class="ma-3 rounded-lg w-15 text-center" :class="{ 'clicked': week[i]['fulldate'] === choicedate }">
            <span>{{ week[i]['date'] }}</span>
          </div>

          <!-- 일정 -->
          <template v-for="(item, index) in week[i]['event']" >
            <div v-if="index < 2" class="text-start my-1 mx-2 px-2 rounded-lg" :class="item.type" :key="index">
              <p class="text-algin-start schedule-name">{{ item.summary }}</p>
            </div>
          </template>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { selectSchedule } from '@/api/schedule.js';

const router = useRouter();
const eventdata = ref([]);

const emit = defineEmits(['clickDay'])

// 기준 일자 (Today)
const nowdate = ref(new Date())
const year  = ref(nowdate.value.getFullYear())
const month = ref(nowdate.value.getMonth())
const date = ref(nowdate.value.getDate())
const choicedate = ref(String(year.value) +'-'+ String(month.value+1).padStart(2,'0') +'-' + String(date.value).padStart(2,'0'))
// 셀렉터 옵션 및 캘린더 옵션들
const weekday = ref([0, 1, 2, 3, 4, 5, 6])
const dayofweek = ref(['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'])
const listofmonthword = ['January','February','March','April','May','June','July','August','September','October','November', 'December']
/*-------------------------
// 해당 월 달력 채우기
//-----------------------*/
const makedate = (year, month) => {
  // 이전 달, 현재 달, 다음 달 구분 및 계산용 변수
  let prevenddate = new Date(year, month, 0).getDate();
  let current = 1
  let next = 1

  // 현재 달력에 표시하기 위한 일자수 계산용 변수
  const startdate = new Date(year, month, 1).getDay();
  const enddate = new Date(year, month + 1, 0).getDate();
  const new_list = Array(42).fill(null).map(() => ({ type: 'current', date: 0, fulldate: '', event:[] }));

  // 이전, 다음 년도 및 월 관리
  const fullprevyear   = month == 0 ?  year - 1 : year
  const fullprevmonth  = month == 0 ?  12 : month
  const fullmonth      = month + 1
  const fullnextyear   = month == 11 ? year + 1 : year
  const fullnextmonth  = month == 11 ? 1 : month + 2

  // 이전 달 
  for (let i = startdate - 1; i >= 0 ; i--) {
    new_list[i]['type'] = 'not_current'
    new_list[i]['fulldate'] = String(fullprevyear) +'-'+ String(fullprevmonth).padStart(2,'0') +'-'+ String(prevenddate).padStart(2,'0')
    new_list[i]['date'] = String(prevenddate--).padStart(2,'0')
  }

  // 현재 달
  for (let j = startdate; j < enddate + startdate; j++) {
    new_list[j]['fulldate'] = String(year) +'-'+ String(fullmonth).padStart(2,'0') +'-'+ String(current).padStart(2,'0')
    new_list[j]['date'] = String(current++).padStart(2,'0')
  }

  // 다음달
  for (let k = enddate + startdate; k < 42; k++) {
    new_list[k]['type'] = 'not_current' 
    new_list[k]['fulldate'] = String(fullnextyear) +'-'+ String(fullnextmonth).padStart(2,'0') +'-'+ String(next).padStart(2,'0')
    new_list[k]['date'] = String(next++).padStart(2,'0')
  }
  return makecalendar(new_list);
};

const makecalendar = (list) => {
    // 달력 표시 데이터
    const result = [];
    // 일자 데이터 주차별 나누기
    for (let i = 0; i < 42; i += 7) {
        result.push(list.slice(i, i + 7));
    }
    
    cal.value = result
}

const schedule = async () => {
  const s = cal.value[0][0]['fulldate'].replace(/-/gi, '');
  const e = cal.value[5][6]['fulldate'].replace(/-/gi, '');
  const data = 'start=' + s + '&end=' + e
  await selectSchedule(
      data,(response) => {
        eventdata.value = response.data.dataBody
        // cal.value = mergeEvents(cal.value, eventdata.value)
        console.log(response.data.dataBody)
      },
      (error)=>{
          console.log(error)
      }
  )
}

const mergeEvents = (firstArray, secondArray) => {
    // 결과를 저장할 배열
    const result = [];
    // firstArray를 순회하면서 각 배열의 요소들에 대해 작업을 수행
    firstArray.forEach(subArray => {
        const mergedSubArray = []; // 각 subArray에 대해 새로운 배열을 생성
        // subArray의 각 객체에 대해 작업을 수행
        subArray.forEach(obj => {
            // 객체를 복사하여 변경하지 않고 유지하기 위해 spread operator 사용
            const newObj = { ...obj };
            // 해당 객체의 'fulldate'를 기반으로 secondArray에서 데이터 찾기
            const matchingEvents = secondArray.filter(item => {
                const time = item.startedTime.slice(0,10);
                return time === newObj.fulldate;
            });
            
            // 찾은 데이터를 'event' 배열에 추가
            newObj.event = matchingEvents;
            
            // 수정된 객체를 새로운 배열에 추가
            mergedSubArray.push(newObj);
        });
        
        // 수정된 subArray를 결과 배열에 추가
        result.push(mergedSubArray);
    });
    
    return result;
};

// 달력 날짜 계산
const cal = ref([])

onMounted(async () => {
  makedate(year.value, month.value)
  await schedule()
  cal.value = mergeEvents(cal.value, eventdata.value)

  const data = cal.value.flatMap(week => {
    // 현재 날짜를 'yyyy-mm-dd' 형식으로 가져옵니다.
    const currentDate = String(new Date().getFullYear()) + '-' + 
                        String(new Date().getMonth() + 1).padStart(2, 0) + '-' + 
                        String(new Date().getDate()).padStart(2, 0);
    
    // 주(week) 내에서 현재 날짜와 일치하는 요소를 찾습니다.
    return week.filter(day => day.fulldate === currentDate);
  })[0]
  console.log(data.fulldate)
  emit('clickDay', data.fulldate, data.event)
})

// 이전달 이동
const clickprev = async () => {
  if (month.value === 0) {
    year.value -= 1
    month.value = 11
  } else {
    month.value -= 1
  }
  makedate(year.value, month.value)
  await schedule()
  cal.value = mergeEvents(cal.value, eventdata.value)
}

// 다음달 이동
const clicknext = async () => {
  if (month.value === 11) {
    year.value += 1
    month.value = 0
  } else {
    month.value += 1
  }
  makedate(year.value, month.value)
  await schedule()
  cal.value = mergeEvents(cal.value, eventdata.value)
}

// 일자 선택
const clickday = (data) => {
  choicedate.value = data.fulldate;
  emit('clickDay', data.fulldate, data.event)
}
</script>

<style scoped>
.day-header {
  max-width: calc(100%/7);
  min-width: calc(100%/7);
}
.day {
    max-width: calc(100%/7);
    min-width: calc(100%/7);
    min-height: 115px;
    max-height: 115px;
    border: 1px solid #e0e0e0;
}
.not_current {
  /* 해당 month의 day가 아닌 day */
  background-color: #f7f7f7;
  color: #d6d6d6;
}

.clicked {
  /* 클릭 일자 */
  background-color: rgba(0, 110, 255, 0.5);
  color: #ffffff;
}

.schedule-name {
  width: 100%;
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