# SoolNote

주류 테이스팅 기록 서비스

사용자가 주류 정보를 조회하고,
테이스팅 노트를 작성하며,
좋아요 및 댓글 기반으로 소통할 수 있는 웹 서비스입니다.

## Tech Stack

### Frontend
- Vue 3
- Vue Router
- Pinia
- Axios
- Vite

### Backend
- Spring Boot
- Spring Security
- MyBatis
- Redis
- MySQL

### Infra
- AWS S3

- ## 주요 기능

### 사용자
- 회원가입 / 로그인
- 이메일 인증
- 비밀번호 재설정
- 세션 기반 인증

### 주류
- 주류 목록 조회
- 카테고리 검색
- 정렬 및 필터링
- 좋아요 기능

### 테이스팅 노트
- 노트 작성 / 수정 / 삭제
- 별점 및 맛 프로파일
- 이미지 업로드
- 신고 기능

### 관리자
- 주류 관리
- 카테고리 관리
- 맛 프로파일 관리
- 신고 관리
- 통계 관리

- ## 시스템 구조

- Front Office (Vue 3)
- Back Office (JSP + jqGrid)
- REST API 기반 통신
- Session 기반 인증 처리
- Redis 캐시 적용
- AWS S3 이미지 저장

## ERD

<img width="2080" height="1092" alt="soolNoteERD" src="https://github.com/user-attachments/assets/a881ffbd-6656-48db-a6cb-e6f2545b0784" />

