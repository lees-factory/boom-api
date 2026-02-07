-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.crew (
                             id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                             created_at timestamp without time zone,
                             updated_at timestamp without time zone,
                             description character varying,
                             max_member_count integer NOT NULL,
                             name character varying,
                             CONSTRAINT crew_pkey PRIMARY KEY (id)
);
CREATE TABLE public.crew_member (
                                    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                    created_at timestamp without time zone,
                                    updated_at timestamp without time zone,
                                    crew_id bigint NOT NULL,
                                    member_id bigint NOT NULL,
                                    role character varying CHECK (role::text = ANY (ARRAY['LEADER'::character varying, 'MEMBER'::character varying, 'GUEST'::character varying]::text[])),
  CONSTRAINT crew_member_pkey PRIMARY KEY (id)
);
CREATE TABLE public.gym (
                            id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                            name character varying NOT NULL,
                            address character varying,
                            latitude double precision NOT NULL,
                            longitude double precision NOT NULL,
                            crowd_level character varying NOT NULL DEFAULT 'NORMAL'::character varying,
                            created_at timestamp with time zone DEFAULT now(),
                            updated_at timestamp with time zone DEFAULT now(),
                            CONSTRAINT gym_pkey PRIMARY KEY (id)
);
CREATE TABLE public.member (
                               id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                               created_at timestamp without time zone,
                               updated_at timestamp without time zone,
                               email character varying,
                               name character varying,
                               provider character varying CHECK (provider::text = ANY (ARRAY['GOOGLE'::character varying, 'APPLE'::character varying, 'KAKAO'::character varying]::text[])),
  role character varying NOT NULL CHECK (role::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying]::text[])),
  social_id character varying,
  profile_image character varying,
  CONSTRAINT member_pkey PRIMARY KEY (id)
);
CREATE TABLE public.member_token (
                                     id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                     member_id bigint NOT NULL UNIQUE,
                                     refresh_token character varying NOT NULL,
                                     expiration_date_time timestamp without time zone NOT NULL,
                                     created_at timestamp without time zone DEFAULT now(),
                                     updated_at timestamp without time zone DEFAULT now(),
                                     CONSTRAINT member_token_pkey PRIMARY KEY (id)
);

-- [2026-02-04] Gym 테이블 스키마 변경 (max_capacity, current_count 추가)
ALTER TABLE public.gym ADD COLUMN max_capacity integer NOT NULL DEFAULT 50;
ALTER TABLE public.gym ADD COLUMN current_count integer NOT NULL DEFAULT 0;

-- [2026-02-04] GymVisit (방문 기록) 테이블 추가
-- 유저의 중복 입장을 방지하고, 입/퇴장 이력을 관리하기 위함
CREATE TABLE public.gym_visit (
                                  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                  gym_id bigint NOT NULL,
                                  member_id bigint NOT NULL,
                                  status character varying NOT NULL CHECK (status::text = ANY (ARRAY['ADMISSION'::character varying, 'EXIT'::character varying]::text[])),
    admitted_at timestamp without time zone NOT NULL,
    exited_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT gym_visit_pkey PRIMARY KEY (id)
);
-- 빠른 조회를 위해 인덱스 추가 (특정 유저가 특정 암장에 현재 입장 중인지 확인용)
CREATE INDEX idx_gym_visit_check ON public.gym_visit (member_id, gym_id, status);

-- [2026-02-05] GymVisit 테이블에 만료 시간 컬럼 추가 (앱에서 연장 기능용)
-- 입장 시 기본 3시간 후 만료, 연장 시 3시간씩 추가
ALTER TABLE public.gym_visit ADD COLUMN expires_at timestamp without time zone NOT NULL DEFAULT now();
-- 오래된 방문 조회를 위한 인덱스 추가 (매일 새벽 스케줄러에서 24시간 이상 지난 방문 정리용)
CREATE INDEX idx_gym_visit_stale ON public.gym_visit (status, admitted_at) WHERE status = 'ADMISSION';

-- [2026-02-06] GymActiveVisit (현재 입장 상태) 테이블 추가
-- 방문 기록(gym_visit)과 현재 상태(gym_active_visit)를 분리하여:
-- 1. 통계용 히스토리 보존 (gym_visit에 항상 INSERT)
-- 2. "이미 입장 중" 버그 방지 (유저당 1개 row만 존재, UNIQUE 제약)
-- 3. 현재 입장 조회 성능 향상 (작은 테이블에서 조회)
CREATE TABLE public.gym_active_visit (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    gym_id bigint NOT NULL,
    member_id bigint NOT NULL UNIQUE,
    admitted_at timestamp without time zone NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT gym_active_visit_pkey PRIMARY KEY (id)
);
-- 유저별 현재 입장 조회용 인덱스 (UNIQUE 제약으로 자동 생성되지만 명시적으로 추가)
CREATE UNIQUE INDEX idx_active_visit_member ON public.gym_active_visit (member_id);
-- 암장별 입장 유저 목록 조회용 인덱스
CREATE INDEX idx_active_visit_gym ON public.gym_active_visit (gym_id);
-- 만료된 입장 정리용 인덱스 (스케줄러에서 사용)
CREATE INDEX idx_active_visit_expires ON public.gym_active_visit (expires_at);

-- [2026-02-07] CrewSchedule (크루 일정) 테이블 추가
-- 크루 내 일정 관리 (암장 선택 가능, LEADER/MEMBER만 등록 가능)
-- gym_id는 nullable로, 특정 암장 모임이 아닌 경우 NULL
CREATE TABLE public.crew_schedule (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    crew_id bigint NOT NULL,
    gym_id bigint,
    title character varying NOT NULL,
    description character varying NOT NULL,
    scheduled_at timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT crew_schedule_pkey PRIMARY KEY (id)
);
-- 크루별 일정 조회용 인덱스
CREATE INDEX idx_crew_schedule_crew ON public.crew_schedule (crew_id, scheduled_at);

-- [2026-02-07] Anti-Goinmul Ranking System
-- 멤버 활동 점수 추가
ALTER TABLE member ADD COLUMN activity_score INT DEFAULT 0;

-- 크루 온도(랭킹용) 추가. 멤버들의 활동 점수 평균을 캐싱하거나 주기적으로 업데이트
ALTER TABLE crew ADD COLUMN activity_score DOUBLE PRECISION DEFAULT 0.0;
ALTER TABLE crew ADD COLUMN latitude double precision;
ALTER TABLE crew ADD COLUMN longitude double precision;
ALTER TABLE crew ADD COLUMN address character varying;

-- [2026-02-07] 크루 멤버 수 비정규화 컬럼 추가
-- 크루 목록/랭킹 조회 시 매번 COUNT 서브쿼리 방지
ALTER TABLE crew ADD COLUMN member_count integer NOT NULL DEFAULT 0;
-- 기존 데이터 보정 (member_count를 실제 crew_member 수로 업데이트)
UPDATE crew SET member_count = (
    SELECT COUNT(*) FROM crew_member cm WHERE cm.crew_id = crew.id
);

-- [2026-02-08] CrewScheduleParticipant (크루 일정 참여) 테이블 추가
-- 크루 일정에 대한 참여 기록 관리
CREATE TABLE public.crew_schedule_participant (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    schedule_id bigint NOT NULL,
    member_id bigint NOT NULL,
    participated_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT crew_schedule_participant_pkey PRIMARY KEY (id)
);
-- 동일 일정에 동일 유저 중복 참여 방지
CREATE UNIQUE INDEX idx_schedule_participant_unique ON public.crew_schedule_participant (schedule_id, member_id);
-- 일정별 참여자 목록 조회용 인덱스
CREATE INDEX idx_schedule_participant_schedule ON public.crew_schedule_participant (schedule_id, participated_at);

-- [2026-02-08] MemberBadge (클라이머 뱃지) 테이블 추가
-- gym_visit 히스토리 기반 업적 뱃지 (마이페이지 진입 시 on-demand 계산)
CREATE TABLE public.member_badge (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    member_id bigint NOT NULL,
    badge_type character varying NOT NULL,
    acquired_at timestamp without time zone NOT NULL,
    notified boolean NOT NULL DEFAULT false,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT member_badge_pkey PRIMARY KEY (id)
);
-- 동일 뱃지 중복 획득 방지
CREATE UNIQUE INDEX idx_member_badge_unique ON public.member_badge (member_id, badge_type);
-- 특정 유저의 뱃지 목록 조회용 인덱스
CREATE INDEX idx_member_badge_member ON public.member_badge (member_id);
