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
