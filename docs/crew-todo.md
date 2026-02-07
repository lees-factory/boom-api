## 크루 도메인 구현 TODO

### ~~1. 크루 일정 참여 API~~ (완료)

- [x] `CrewScheduleParticipant` 도메인 모델
- [x] `crew_schedule_participant` 테이블 DDL
- [x] `POST /api/v1/crews/{crewId}/schedules/{scheduleId}/participate` - 일정 참여
- [x] `GET /api/v1/crews/{crewId}/schedules/{scheduleId}/participants` - 참여자 목록
- [x] 크루 멤버 권한 확인 (GUEST 제외)
- [x] 중복 참여 체크
- [x] 일정 존재 + crewId 일치 검증
- [x] 참여 시 활동 점수 +10 반영

---

### ~~2. joinCrew 검증 로직~~ (완료)

- [x] 중복 가입 검증 → `CREW_ALREADY_JOINED` 에러
- [x] 정원 초과 검증 → `CREW_MEMBER_LIMIT_EXCEEDED` 에러
- [x] 크루 미존재 검증 → `CREW_NOT_FOUND` 에러

---

### ~~3. 활동 점수 계산 로직~~ (완료)

- [x] `ActivityScoreUpdater` 컴포넌트 (점수 상수 + 반영 메서드)
- [x] `MemberRepository.incrementActivityScore()` → `@Modifying` 원자적 UPDATE
- [x] 일정 참여 시 +10 (`CrewService.participateSchedule`)
- [x] 암장 입장 시 +5 (`GymService.enterUser`)
- [x] 크루 랭킹: 크루원 평균 활동 점수 기반 조회 (`CrewRankingInfo`, `CrewRankingResponse`)
- [ ] 피드 게시 +3 (피드 기능 확장 후)
- [ ] 댓글/좋아요 +1 (피드 기능 확장 후)

---

### ~~4. 암장 참여횟수 점수 반영~~ (완료)

- [x] `GymService.enterUser()` 에서 활동 점수 +5 반영
- [x] `GymService.enterUserWithoutLocationCheck()` (TEST) 에서도 동일 반영

### ~~5. 뱃지 목록~~ (완료)
```
🧗 클라이머 배지 (단순화 최종본)
📆 암장출석 누적
코드    배지명    이모지    조건
ATTEND_10    초크 묻었다    🪨    출석 10회
ATTEND_30    벽이 익숙해졌다    🧗    출석 30회
ATTEND_100    여기 집이야    🏠    출석 100회
ATTEND_300    거의 직원    🧑‍💼    출석 300회
ATTEND_999    999 클럽    😈    출석 999회
🔁 암장 연속 출석 (스트릭) 한곳이아니여도 됨
코드    배지명    이모지    조건
STREAK_7    끊기지 않는다    🔥    7일 연속
STREAK_30    벽이 일상    🧱    30일 연속
🗓 주간 루틴
코드    배지명    이모지    조건
WEEK_3    주 3은 기본    📅    주 3일 출석
WEEK_5    주 5일 벽친자    🧗‍♂️    주 5일 출석
WEEK_7    주 7일 지박령    🪦    주 7일 출석
🌅 시간대
코드    배지명    이모지    조건
EARLY    일찍이    🌅    05~09시 출석
NIGHT    올빼미    🌙    22~02시 출석
🚪 방문 패턴
코드    배지명    이모지    조건
OPENER    오프너    🚪    오픈 30분 이내
WANDERER    떠돌이    🧳    48h 내 2개 암장
```

