# CalendarScheduler
### 공유 달력 스케쥴러


#### 사용 기술 스택<br>
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white"/></a>
<img src="https://img.shields.io/badge/ThymeLeaf-005F0F?style=flat-square&logo=ThymeLeaf&logoColor=white"/></a>
<img src="https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=MariaDB&logoColor=white"/></a>
<img src="https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=HTML5&logoColor=white"/></a>

#### 주요 기능 및 구현
1. 회원가입 및 로그인
2. 캘린더 생성  (본인 캘린더 및 공유 캘린더)
3. 일정 등록 수정 삭제
4. 일정내용 검색 기능


#### 기능의 세부내용
1. 회원가입 및 로그인
   - 중복아이디 검사, 캘린더 초대코드 검사 등
   - 세션으로 ID 체크
2. 캘린더 생성
   - OPEN API - <a href="https://fullcalendar.io/">FullCalendar</a>사용
   - 회원가입 시 고유 캘린더번호 생성 (고유번호로 캘린더 일정 공유)
3. 일정 등록 수정 삭제
   - 일정 등록: 공유 캘린더 사용자면 가능
   - 일정 수정: 본인 등록 일정에 한해서 수정 가능
   - 일정 삭제: 본인 등록 일정에 한해서 삭제 가능(플래그로 삭제처리)
4. 일정 내용 검색 기능
   - 오늘일자 기준 1년 단위로 제목 및 내용 포함 검색
   - 검색 옵션은 추가 예정
